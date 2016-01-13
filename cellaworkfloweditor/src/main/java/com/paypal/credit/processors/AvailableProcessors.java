package com.paypal.credit.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.paypal.credit.json.Availableprocessorschema;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AvailableProcessors extends HttpServlet {
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        Availableprocessorschema response = null;

        response = new Availableprocessorschema();
        List<Availableprocessorschema.Processor> processors = new ArrayList<>();



        response.setProcessors(processors);

        if (response != null) {
            ObjectMapper mapper = new ObjectMapper();
            if (mapper.canSerialize(response.getClass())) {
                ObjectWriter writer = mapper.writer().forType(response.getClass());
                writer.writeValue(resp.getOutputStream(), response);
            } else {
                throw new ServletException(response.getClass().getName() + " cannot be serialized.");
            }
        }
    }
}