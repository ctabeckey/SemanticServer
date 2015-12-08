package com.paypal.credit.workflowcommand;

import com.paypal.credit.core.commandprocessor.Command;
import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.commandprovider.CommandInstantiationToken;
import com.paypal.credit.core.commandprovider.CommandProvider;
import com.paypal.credit.core.commandprovider.exceptions.CommandInstantiationException;
import com.paypal.credit.core.commandprovider.exceptions.InvalidTokenException;
import com.paypal.credit.core.processorbridge.ProductTypeRoutingToken;
import com.paypal.credit.core.semantics.CommandClassSemantics;
import com.paypal.credit.core.utility.ParameterCheckUtility;
import com.paypal.credit.core.utility.TwoMemberCompoundKey;
import com.paypal.credit.workflow.RSProcessorContext;
import com.paypal.credit.workflowcommand.workflow.WorkflowType;

import javax.xml.bind.JAXBException;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A CommandProvider implementation that builds a WorkflowCommand
 * implementation.
 */
public class WorkflowCommandProvider
implements CommandProvider {
    /**
     * Cache the token instances. WorkflowType instances may require a resource
     * file or network access and are therefore expensive to acquire.
     */
    private final Map<TwoMemberCompoundKey<RoutingToken, CommandClassSemantics>, WorkflowCommandInstantiationToken>
            tokenCache = new ConcurrentHashMap<>();

    /**
     * Cache the WorkflowCommand instances.
     */
    private final Map<WorkflowCommandInstantiationToken, Workflow<?,?>> workflowCache = new ConcurrentHashMap<>();

    /**
     * The publisher can be used to select a specific CommandProvider if
     * there are multiple Command implementations available.
     *
     * @return
     */
    @Override
    public String getPublisher() {
        return "workflow";
    }

    /**
     *
     * @param routingToken
     * @param commandClassSemantics
     * @param parameterTypes
     * @param parameterAnnotations
     * @param resultType
     * @return
     */
    @Override
    public CommandInstantiationToken findCommand(
            final RoutingToken routingToken,
            final CommandClassSemantics commandClassSemantics,
            final Class<?>[] parameterTypes,
            final Annotation[][] parameterAnnotations,
            final Class<?> resultType) {
        TwoMemberCompoundKey key = new TwoMemberCompoundKey<RoutingToken, CommandClassSemantics>(
                routingToken, commandClassSemantics
        );
        WorkflowCommandInstantiationToken token = tokenCache.get(key);
        if (token == null) {
            WorkflowType workflowType = null;
            URL workflowUrl = getWorkflowDefinitionLocation(routingToken, commandClassSemantics);

            if (workflowUrl != null) {
                try {
                    workflowType = WorkflowReader.create(workflowUrl);
                    token = new WorkflowCommandInstantiationToken(this, workflowType, routingToken, resultType);
                    tokenCache.put(key, token);
                } catch (JAXBException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }

        return token;
    }

    /**
     *
     * @param instantiationToken
     * @param parameters
     * @param parameterAnnotations
     * @return
     * @throws CommandInstantiationException
     */
    @Override
    public Command<?> createCommand(
            final CommandInstantiationToken instantiationToken,
            final Object[] parameters,
            final Annotation[][] parameterAnnotations)
            throws CommandInstantiationException, InvalidTokenException {

        ParameterCheckUtility.checkParameterNotNull(instantiationToken, "instantiationToken");
        if (!(instantiationToken instanceof WorkflowCommandInstantiationToken)) {
            throw new InvalidTokenException(instantiationToken.getClass(), WorkflowCommandInstantiationToken.class);
        }
        WorkflowCommandInstantiationToken token = (WorkflowCommandInstantiationToken)instantiationToken;
        Workflow workflow = workflowCache.get(token);

        String contextClassName = token.getWorkflowType().getContextClass();
        if (contextClassName == null || contextClassName.isEmpty()) {
            contextClassName = RSProcessorContext.class.getName();
        }

        RSProcessorContext processorContext = createContext(contextClassName, parameters, parameterAnnotations);

        if (workflow == null) {
            try {
                workflow = WorkflowFactory.create(
                        processorContext.getClass(),
                        token.getWorkflowType(),
                        token.getResultType()
                );
                workflowCache.put(token, workflow);
            } catch (Exception e) {
                throw new CommandInstantiationException(
                        String.format("%s", ((WorkflowCommandInstantiationToken) instantiationToken).getRoutingToken().toString()),
                        e);
            }
        }

        return new WorkflowCommand<>(workflow, processorContext);
    }

    /**
     *
     * @param contextClassName
     * @param parameters
     * @param parameterAnnotations
     * @return
     */
    private RSProcessorContext createContext(final String contextClassName, final Object[] parameters, final Annotation[][] parameterAnnotations)
            throws CommandInstantiationException {
        RSProcessorContext result;

        if (contextClassName != null && !contextClassName.isEmpty()) {
            try {
                result = (RSProcessorContext)(Class.forName(contextClassName)).newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | ClassCastException x) {
                x.printStackTrace();
                throw new CommandInstantiationException("WorkflowCommandProvider", x);
            }

        } else {
            result = new RSProcessorContext();
        }

        for (int index=0; index < parameters.length; ++index) {
            WorkflowContextMapping mapping = getWorkflowContextMapping(parameterAnnotations[index]);
            String propertyName = mapping == null ? parameters[index].toString() : mapping.value();

            result.put(propertyName, parameters[index]);
        }

        return result;
    }

    /**
     *
     * @param parameterAnnotations
     * @return
     */
    private WorkflowContextMapping getWorkflowContextMapping(final Annotation[] parameterAnnotations) {
        if(parameterAnnotations != null && parameterAnnotations.length > 0) {
            for (Annotation parameterAnnotation : parameterAnnotations) {
                if (parameterAnnotation instanceof WorkflowContextMapping) {
                    return (WorkflowContextMapping)parameterAnnotation;
                }
            }
        }

        return null;
    }

    /**
     *
     * @param routingToken
     * @param commandClassSemantics
     * @return
     */
    private URL getWorkflowDefinitionLocation(
            final RoutingToken routingToken,
            final CommandClassSemantics commandClassSemantics) {
        if (routingToken instanceof ProductTypeRoutingToken) {
            ProductTypeRoutingToken productTypeRouting = (ProductTypeRoutingToken)routingToken;

            String productType = productTypeRouting.getProductType().toLowerCase();
            String command = commandClassSemantics.toBaseString();

            String workflowIdentifier = String.format("%s_%s", command, productType);

            try {
                URL url = new URL("rsc:workflows/" + workflowIdentifier + ".xml");
                if (WorkflowReader.exists(url)) {
                    return url;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

}
