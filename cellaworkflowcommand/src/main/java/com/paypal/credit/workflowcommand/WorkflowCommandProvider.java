package com.paypal.credit.workflowcommand;

import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.commandprovider.CommandInstantiationToken;
import com.paypal.credit.core.commandprovider.CommandProvider;
import com.paypal.credit.core.commandprovider.exceptions.CommandInstantiationException;
import com.paypal.credit.core.commandprovider.exceptions.InvalidTokenException;
import com.paypal.credit.core.processorbridge.ProductTypeRoutingToken;
import com.paypal.credit.core.semantics.CommandClassSemantics;
import com.paypal.credit.utility.ParameterCheckUtility;
import com.paypal.credit.utility.ThreeMemberCompoundKey;
import com.paypal.credit.workflow.RSProcessorContext;
import com.paypal.credit.workflow.Workflow;
import com.paypal.credit.workflow.factory.WorkflowFactory;

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
implements CommandProvider {
    /**
     *
     */
    public WorkflowCommandProvider() {

    }

    /** Inject a factory to create Workflow instances */
    private WorkflowFactory workflowFactory = new WorkflowFactory();

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
                workflowType = workflowFactory.getOrCreate(workflowUrl);
                token = new WorkflowCommandInstantiationToken(this, workflowType, routingToken, resultType);
                tokenCache.put(key, token);
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
