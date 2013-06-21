package org.apache.tiles.request.handlebars;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tiles.request.Request;
import org.apache.tiles.request.render.Renderer;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;

public class HandlebarsRenderer implements Renderer {
    private final TemplateLoader loader;
    private final Handlebars handlebars;
    private Pattern acceptPattern;

    HandlebarsRenderer(String prefix, String suffix) {
        if (prefix != null && suffix != null) {
            this.loader = new ClassPathTemplateLoader(prefix, suffix);
        } else {
            this.loader = new ClassPathTemplateLoader();
        }
        this.handlebars = new Handlebars(loader);
    }

    @Override
    public void render(String path, Request request) throws IOException {
        Template template = handlebars.compile(path);
        Writer writer = request.getWriter();
        template.apply(buildScope(request), writer);
    }

    @Override
    public boolean isRenderable(String path, Request request) {
        if (path == null) {
            return false;
        }
        if (acceptPattern != null) {
            final Matcher matcher = acceptPattern.matcher(path);
            return matcher.matches();
        }
        return true;
    }

    public final void setAcceptPattern(Pattern acceptPattern) {
        this.acceptPattern = acceptPattern;
    }

    public Pattern getAcceptPattern() {
        return acceptPattern;
    }

    protected Map<String, Object> buildScope(Request request) {
        Map<String, Object> scope = new HashMap<String, Object>();
        List<String> availableScopes = request.getAvailableScopes();
        for (int i = availableScopes.size() - 1; i >= 0; --i) {
            scope.putAll(request.getContext(availableScopes.get(i)));
        }
        return scope;
    }

}
