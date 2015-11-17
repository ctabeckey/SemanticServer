package com.paypal.credit.workflowcommand;

import com.paypal.credit.core.commandprocessor.Command;
import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.commandprovider.CommandInstantiationToken;
import com.paypal.credit.core.commandprovider.CommandInstantiationTokenImpl;
import com.paypal.credit.core.commandprovider.CommandProvider;
import com.paypal.credit.core.commandprovider.CommandLocationTokenRankedSet;
import com.paypal.credit.core.commandprovider.exceptions.CommandInstantiationException;
import com.paypal.credit.core.commandprovider.exceptions.InvalidTokenException;
import com.paypal.credit.core.processorbridge.ProductTypeRoutingToken;
import com.paypal.credit.core.semantics.CommandClassSemantics;
import com.paypal.credit.core.utility.TwoMemberCompoundKey;
import com.paypal.credit.workflowcommand.workflow.WorkflowType;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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
    private final Map<TwoMemberCompoundKey<RoutingToken, CommandClassSemantics>, CommandInstantiationTokenImpl>
            tokenCache = new ConcurrentHashMap<>();

    /**
     * Cache the WorkflowCommand instances.
     */
    private final Map<CommandInstantiationTokenImpl, WorkflowCommand<?,?>>
            commandCache = new ConcurrentHashMap<>();

    /**
     *
     * @param routingToken
     * @param commandClassSemantics
     * @param parameters
     * @param resultType
     * @return
     */
    @Override
    public CommandLocationTokenRankedSet findCommands(
            final RoutingToken routingToken,
            final CommandClassSemantics commandClassSemantics,
            final Class<?>[] parameters,
            final Class<?> resultType) {
        final CommandLocationTokenRankedSet locationTokenRankedSet = new CommandLocationTokenRankedSet(
                routingToken,
                commandClassSemantics,
                parameters,
                resultType);

        TwoMemberCompoundKey key =
                new TwoMemberCompoundKey<RoutingToken, CommandClassSemantics>(routingToken, commandClassSemantics);
        CommandInstantiationTokenImpl token = tokenCache.get(key);
        if (token == null) {
            WorkflowType workflowType = null;
            URL workflowUrl = findWorkflow(routingToken, commandClassSemantics);

            if (workflowUrl != null) {
                try {
                    workflowType = WorkflowReader.create(workflowUrl);
                    token = new CommandInstantiationTokenImpl(this, new WorkflowCommandFactory(workflowType));
                    tokenCache.put(key, token);
                } catch (JAXBException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }

        if (token != null) {
            locationTokenRankedSet.add(token);
        }

        return locationTokenRankedSet;
    }

    /**
     *
     * @param routingToken
     * @param instantiationToken
     * @param parameters
     * @return
     * @throws CommandInstantiationException
     */
    @Override
    public Command<?> createCommand(
            final RoutingToken routingToken,
            final CommandInstantiationToken instantiationToken,
            final Object[] parameters)
            throws CommandInstantiationException, InvalidTokenException {

        WorkflowCommand command = null;

        if (instantiationToken instanceof CommandInstantiationTokenImpl) {
            CommandInstantiationTokenImpl token = (CommandInstantiationTokenImpl)instantiationToken;

            command = commandCache.get(token);

            if (command == null) {
                try {
                    command = (WorkflowCommand) token.getFactory().create(routingToken, parameters);
                    commandCache.put(token, command);
                } catch (Exception e) {
                    throw new CommandInstantiationException(token.getFactory().getClass().getName(), e);
                }
            }
        } else {
            throw new InvalidTokenException(instantiationToken.getClass(), CommandInstantiationTokenImpl.class);
        }

        return command;
    }

    /**
     *
     * @param routingToken
     * @param commandClassSemantics
     * @return
     */
    private URL findWorkflow(
            final RoutingToken routingToken,
            final CommandClassSemantics commandClassSemantics) {
        if (routingToken instanceof ProductTypeRoutingToken) {
            ProductTypeRoutingToken productTypeRouting = (ProductTypeRoutingToken)routingToken;

            String productType = productTypeRouting.getProductType().toLowerCase();
            String command = commandClassSemantics.toBaseString();

            String workflowIdentifier = String.format("%s_%s", command, productType);

            try {
                URL url = new URL("rsc:workflows/" + workflowIdentifier + ".xml");
                URLConnection conn = url.openConnection();
                conn.connect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // the workflow definition is not available that implements the
                // routing token and command requested
                return null;
            }
        }
        return null;
    }

}
