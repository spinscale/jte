package org.jusecase.kte;

import org.junit.jupiter.api.Test;
import org.jusecase.kte.internal.TemplateCompiler;
import org.jusecase.kte.resolve.DirectoryCodeResolver;

import java.nio.file.Path;

public class PreCompileTemplatesTester {
    @Test
    void precompile() {
        Path outputDirectory = Path.of("kte");
        FileUtil.deleteAllIn(outputDirectory);
        TemplateCompiler compiler = new TemplateCompiler(new DirectoryCodeResolver(Path.of("src", "test", "resources", "benchmark")), outputDirectory);
        compiler.precompileAll();
    }
}
