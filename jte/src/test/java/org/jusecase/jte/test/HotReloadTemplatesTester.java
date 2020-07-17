package org.jusecase.jte.test;

import org.junit.jupiter.api.Test;
import org.jusecase.jte.TemplateEngine;
import org.jusecase.jte.resolve.DirectoryCodeResolver;

import java.nio.file.Path;

public class HotReloadTemplatesTester {
    @Test
    public void run() {
        DirectoryCodeResolver codeResolver = new DirectoryCodeResolver(Path.of("src", "test", "resources", "benchmark"));
        TemplateEngine templateEngine = TemplateEngine.create(codeResolver, Path.of("jte"));

        codeResolver.startTemplateFilesListenerBlocking(templateEngine, templates -> {
            System.out.println("Invalidated " + templates);
            for (String template : templates) {
                templateEngine.prepareForRendering(template);
            }
        });
    }
}
