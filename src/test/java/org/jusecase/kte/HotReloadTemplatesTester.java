package org.jusecase.kte;

import org.junit.jupiter.api.Test;
import org.jusecase.kte.resolve.DirectoryCodeResolver;

import java.nio.file.Path;

public class HotReloadTemplatesTester {
    @Test
    void name() {
        DirectoryCodeResolver codeResolver = new DirectoryCodeResolver(Path.of("src", "test", "resources", "benchmark"));
        TemplateEngine templateEngine = new TemplateEngine(codeResolver, Path.of("kte"));

        codeResolver.enableHotReloadBlocking(templateEngine, templates -> {
            System.out.println("Invalidated " + templates);
            for (String template : templates) {
                templateEngine.prepareForRendering(template);
            }
        });
    }
}
