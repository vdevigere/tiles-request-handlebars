package org.apache.tiles.request.handlebars;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.tiles.request.Request;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

public class HandlebarsRhinoRenderer extends HandlebarsRenderer {

    private String prefix;
    private String suffix;
    private static final String HANDLEBARSJS_LIB = "/handlebars-v1.1.0.js";
    private static final String RENDER_TEMPLATE = "var template = Handlebars.compile(template);template(context);";
    private Scriptable scope;
    private final String handlerbarLib = getFileContent(HANDLEBARSJS_LIB);

    HandlebarsRhinoRenderer(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
        Context context = Context.enter();
        scope = context.initStandardObjects();
        context.evaluateString(scope, handlerbarLib, "somefile", 1, null);
        Context.exit();
    }

    @Override
    public void render(String path, Request request) throws IOException {
        // Start Context
        Context context = Context.enter();

        // Create javascript context from request map.
        Map<String, Object> requestMap = buildScope(request);
        NativeObject handlebarContext = new NativeObject();
        for (Map.Entry<String, Object> entry : requestMap.entrySet()) {
            handlebarContext.defineProperty(entry.getKey(), entry.getValue(), NativeObject.READONLY);
        }
        scope.put("context", scope, handlebarContext);

        // Read template file from file system.
        StringBuilder strb = new StringBuilder(prefix);
        String templatePath = strb.append("/").append(path).append(suffix).toString();
        String template = getFileContent(templatePath);
        scope.put("template", scope, template);

        // Evaluate template against context
        Object result = context.evaluateString(scope, RENDER_TEMPLATE, "somefile", 1, null);

        Writer writer = request.getWriter();
        writer.write(Context.toString(result));
        Context.exit();
    }

    private String getFileContent(String fileName) {
        try {
            InputStream is = this.getClass().getResourceAsStream(fileName);
            return IOUtils.toString(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
