package net.weichware.jbdaotest.uiserver;

import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;


public class ErrorHandler extends ErrorPageErrorHandler {
    @Override
    protected void generateAcceptableResponse(Request baseRequest, HttpServletRequest request, HttpServletResponse response, int code, String message, String mimeType) throws IOException {
        if (isRestRequest(request)) {
            baseRequest.setHandled(true);
            Writer writer = getAcceptableWriter(baseRequest, request, response);
            if (null != writer) {
                response.setContentType(MimeTypes.Type.APPLICATION_JSON.asString());
                response.setStatus(code);
                handleErrorPage(request, writer, code, message);
            }
        } else {
            super.generateAcceptableResponse(baseRequest, request, response, code, message, mimeType);
        }
    }

    @Override
    protected Writer getAcceptableWriter(Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (isRestRequest(request)) {
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            return response.getWriter();
        } else {
            return super.getAcceptableWriter(baseRequest, request, response);
        }
    }

    @Override
    protected void writeErrorPage(HttpServletRequest request, Writer writer, int code, String message, boolean showStacks) throws IOException {
        if (isRestRequest(request)) {
            writer.write(message);
        } else {
            super.writeErrorPage(request, writer, code, message, showStacks);
        }
    }

    private boolean isRestRequest(HttpServletRequest request) {
        return request.getServletPath().startsWith("/api/");
    }

}