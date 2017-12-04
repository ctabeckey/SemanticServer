package com.paypal.credit.workflowcommand;

import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.commandprovider.AbstractCommandProvider;
import com.paypal.credit.core.commandprovider.CommandInstantiationToken;
import com.paypal.credit.core.commandprovider.CommandProvider;
import com.paypal.credit.core.commandprovider.exceptions.CommandInstantiationException;
import com.paypal.credit.core.commandprovider.exceptions.InvalidTokenException;
import com.paypal.credit.core.applicationbridge.ProductTypeRoutingToken;
import com.paypal.credit.core.semantics.CommandClassSemantics;
import com.paypal.credit.workflow.RSProcessorContext;
import com.paypal.credit.workflowcommand.factory.IocWorkflowFactory;
import com.paypal.utility.ParameterCheckUtility;
import com.paypal.utility.ThreeMemberCompoundKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A CommandProvider implementation that builds a WorkflowCommand
 * implementation.
 */
public class WorkflowCommandProvider
    extends AbstractCommandProvider
    implements CommandProvider {
    /** */
    private final static Logger LOGGER = LoggerFactory.getLogger(WorkflowCommandProvider.class);

    /**
     *
     */
    public WorkflowCommandProvider() {

    }

    /** Inject a factory to create Workflow instances */
    private IocWorkflowFactory workflowFactory = new IocWorkflowFactory();

    /**
     * Cache the token instances. WorkflowType instances may require a resource
     * file or network access and are therefore expensive to acquire.
     */
    private final Map<ThreeMemberCompoundKey<RoutingToken, CommandClassSemantics, Class<?>>, WorkflowCommandInstantiationToken>
            tokenCache = new ConcurrentHashMap<>();

    /**
     * Cache the WorkflowCommand instances.
     */
    private final Map<WorkflowCommandInstantiationToken, Workflow<?,?>> workflowCache = new ConcurrentHashMap<>();

    /**
     * TODO: Externalize the creation
     */
    private final ProcessorContextFactory processorContextFactory = new ProcessorContextFactoryImpl();

    public ProcessorContextFactory getProcessorContextFactory() {
        return processorContextFactory;
    }

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
     * @param resultType
     * @return
     */
    @Override
    public CommandInstantiationToken findCommand(
            final RoutingToken routingToken,
            final CommandClassSemantics commandClassSemantics,
            final Class<?>[] parameterTypes,
            final Class<?> resultType) {
        ThreeMemberCompoundKey key = new ThreeMemberCompoundKey<RoutingToken, CommandClassSemantics, Class<?>>(
                routingToken, commandClassSemantics, resultType
        );
        WorkflowCommandInstantiationToken token = tokenCache.get(key);
        if (token == null) {
            Workflow workflowType = null;
            URL workflowUrl = getWorkflowDefinitionLocation(routingToken, commandClassSemantics);

            if (workflowUrl != null) {
                try {
                    workflowType = workflowFactory.getOrCreate(workflowUrl);
                    if (workflowType != null) {
                        token = new WorkflowCommandInstantiationToken(this, workflowType, routingToken, resultType);
                        tokenCache.put(key, token);
                    } else {
                        LOGGER.error(String.format("Error creating workflow [%s]", workflowUrl.toString()));
                    }
                } catch (IOException | JAXBException| ContextInitializationException | WorkflowContextException x) {
                    LOGGER.error(String.format("Error when finding command [%s] %s @ %s (%s)", resultType, workflowType, workflowUrl.toString(), routingToken), x);
                }
            }
        }

        return token;
    }

    /**
     *
     * @param instantiationToken
     * @param parameters
     * @return
     * @throws CommandInstantiationException
     */
    @Override
    public Callable<?> createCommand(
            final CommandInstantiationToken instantiationToken,
            final Object[] parameters)
            throws CommandInstantiationException, InvalidTokenException {

        ParameterCheckUtility.checkParameterNotNull(instantiationToken, "instantiationToken");
        if (!(instantiationToken instanceof WorkflowCommandInstantiationToken)) {
            throw new InvalidTokenException(instantiationToken.getClass(), WorkflowCommandInstantiationToken.class);
        }
        WorkflowCommandInstantiationToken token = (WorkflowCommandInstantiationToken)instantiationToken;
        Workflow workflow = workflowCache.get(token);

        Class<?> contextClass = token.getWorkflow().getContextClass();
        if (contextClass == null) {
            contextClass = RSProcessorContext.class;
        }

        RSProcessorContext processorContext = getProcessorContextFactory().createContext(contextClass, parameters);

        if (workflow == null) {
            try {

                workflow = token.getWorkflow();
                workflow.validate();
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
                return new URL("rsc:workflows/" + workflowIdentifier + ".xml");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

}
