package org.apache.tiles.request.handlebars;

import java.io.IOException;
import java.io.Writer;

import org.apache.tiles.request.Request;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;

public class HandlebarsJavaRenderer extends HandlebarsRenderer {
    private final TemplateLoader loader;
    private final Handlebars handlebars;

    HandlebarsJavaRenderer(String prefix, String suffix) {
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
}
