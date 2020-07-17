package org.jusecase.jte.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jusecase.jte.TemplateEngine;
import org.jusecase.jte.compile.IoUtils;
import org.jusecase.jte.output.FileOutput;
import org.jusecase.jte.output.StringOutput;
import org.jusecase.jte.resolve.DirectoryCodeResolver;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class TemplateEngine_HotReloadTest {

    public static final String TEMPLATE = "test.jte";

    private Path tempDirectory;
    private TemplateEngine templateEngine;

    @BeforeEach
    public void setUp() throws IOException {
        tempDirectory = Files.createTempDirectory("temp-code");
        templateEngine = TemplateEngine.create(new DirectoryCodeResolver(tempDirectory));
    }

    @AfterEach
    public void tearDown() {
        IoUtils.deleteDirectoryContent(tempDirectory);
    }

    @Test
    public void template() {
        whenFileIsWritten(TEMPLATE, "@param String name\nHello ${name}!");
        thenTemplateOutputIs("Hello hot reload!");

        whenFileIsWritten(TEMPLATE, "@param String name\nHello ${name}!!!");
        thenTemplateOutputIs("Hello hot reload!!!");
    }

    @Test
    public void tagUsedByTemplate() {
        whenFileIsWritten("tag/name.jte", "@param String name\nHello ${name}!");
        whenFileIsWritten(TEMPLATE, "@param String name\n@tag.name(name)");
        thenTemplateOutputIs("Hello hot reload!");

        whenFileIsWritten("tag/name.jte", "@param String name\nHello ${name}!!!");
        thenTemplateOutputIs("Hello hot reload!!!");
    }

    private void thenTemplateOutputIs(String expected) {
        StringOutput output = new StringOutput();
        templateEngine.render(TEMPLATE, "hot reload", output);
        assertThat(output.toString()).isEqualTo(expected);
    }

    private void whenFileIsWritten(String name, String content) {
        try (FileOutput fileOutput = new FileOutput(tempDirectory.resolve(name))) {
            fileOutput.writeContent(content);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}