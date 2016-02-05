package com.paypal.credit.xlator;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by cbeckey on 2/3/16.
 */
@ApplicationPath("/statements")
public class StatementXlatorApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(Statement.class);
        return classes;
    }

    /**
     *
     */
    @Path("/{statement-id}")
    public static class Statement
    {
        private AmazonSQSClient sqs;

        public Statement() {
            sqs = new AmazonSQSClient();
        }

        @GET
        @Produces("application/json")
        public String getStatement(@PathParam("statement-id") final String statementId)
        {
            SendMessageRequest sendMessage = new SendMessageRequest()
                    .withQueueUrl("getstatement")
                    .withMessageBody(statementId);

            sqs.sendMessage(sendMessage);

            return "\"text\" : \"hello world\"";
        }
    }

}
