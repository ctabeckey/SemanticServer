package com.paypal.credit.workflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.paypal.credit.json.Graphdataschema;
import com.paypal.credit.workflowcommand.WorkflowReader;
import com.paypal.credit.workflowcommand.workflow.schema.WorkflowType;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.IOException;

/**
 * Created by cbeckey on 12/14/15.
 */
public class WorkflowDataSource
    extends HttpServlet {
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        String workflowId = getWorkflowId(req);

        resp.setContentType("application/json");

        String workflowResource = workflowId + ".xml";

        Graphdataschema graph = null;
        try {
            WorkflowType workflow =
                    WorkflowReader.create(Thread.currentThread().getContextClassLoader().getResourceAsStream(workflowResource));

            GraphdataschemaBuilder builder = new GraphdataschemaBuilder();
            builder.withWorkflow(workflow);
            graph = builder.build();
        }
        catch (JAXBException jaxbX) {
            throw new ServletException(jaxbX);
        }

        if (graph != null) {
            ObjectMapper mapper = new ObjectMapper();
            if (mapper.canSerialize(graph.getClass())) {
                ObjectWriter writer = mapper.writer().forType(graph.getClass());
                writer.writeValue(resp.getOutputStream(), graph);
            } else {
                throw new ServletException(graph.getClass().getName() + " cannot be serialized.");
            }
        }
    }

    @Override
    protected void doPut(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        String workflowId = getWorkflowId(req);
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        String workflowId = getWorkflowId(req);
    }

    @Override
    protected void doDelete(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        String workflowId = getWorkflowId(req);
    }

    private String getWorkflowId(HttpServletRequest request) {
        String extraPath = request.getPathInfo();

        return extraPath.isEmpty() || !extraPath.startsWith("/") ?
                extraPath :
                extraPath.substring(1);
    }

}
