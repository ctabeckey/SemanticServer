package com.paypal.credit.workflowcommand;

import com.paypal.credit.core.commandprocessor.Command;
import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.commandprovider.CommandInstantiationToken;
import com.paypal.credit.core.commandprovider.CommandInstantiationTokenImpl;
import com.paypal.credit.core.commandprovider.CommandProvider;
import com.paypal.credit.core.commandprovider.CommandLocationTokenRankedSet;
import com.paypal.credit.core.commandprovider.exceptions.CommandInstantiationException;
import com.paypal.credit.core.processorbridge.ProductTypeRoutingToken;
import com.paypal.credit.core.semantics.CommandClassSemantics;
import com.paypal.credit.core.utility.URLFactory;
import com.paypal.credit.workflowcommand.workflow.WorkflowType;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * A CommandProvider implementation that builds a WorkflowCommand
 * implementation.
 */
public class WorkflowCommandProvider
implements CommandProvider {
    @Override
    public CommandLocationTokenRankedSet findCommands(
            final RoutingToken routingToken,
            final CommandClassSemantics commandClassSemantics,
            final Class<?>[] parameters,
            final Class<?> resultType) {
        final CommandLocationTokenRankedSet locationTokenRankedSet = new CommandLocationTokenRankedSet(
                routingToken,
                commandClassSemantics.toString(),
                parameters,
                resultType);

        URL workflowUrl = findWorkflow(routingToken, commandClassSemantics);

        if (workflowUrl != null) {
            WorkflowType workflowType = null;
            try {
                workflowType = WorkflowReader.create(workflowUrl);
                locationTokenRankedSet.add(this, new WorkflowCommandFactory(workflowType));
            } catch (JAXBException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return locationTokenRankedSet;
    }

    @Override
    public Command<?> createCommand(
            final RoutingToken routingToken,
            final CommandInstantiationToken instantiationToken,
            final Object[] parameters)
            throws CommandInstantiationException {

        if (instantiationToken instanceof CommandInstantiationTokenImpl) {
            CommandInstantiationTokenImpl token = (CommandInstantiationTokenImpl)instantiationToken;
            try {
                Command command = token.getFactory().create(routingToken, parameters);
                return command;
            } catch (Exception e) {
                throw new CommandInstantiationException(token.getFactory().getClass().getName(), e);
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
