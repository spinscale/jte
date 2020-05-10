package org.jusecase.kte;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jusecase.kte.output.StringOutput;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class TemplateEngine_OutputEscapingTest {

    Path root = Path.of("kte");
    DummyCodeResolver dummyCodeResolver = new DummyCodeResolver();
    TemplateEngine templateEngine = new TemplateEngine(dummyCodeResolver, root);

    StringOutput stringOutput = new StringOutput();
    SecureOutput secureOutput = new SecureOutput(stringOutput);

    @BeforeEach
    void setUp() {
        FileUtil.deleteAllIn(root);
    }

    @Test
    void outputEscaping() {
        dummyCodeResolver.givenCode("template.kte", "@param model:String\n" +
                "Hello ${model}, ${1}, ${1.0f}");

        templateEngine.render("template.kte", "Model", secureOutput);

        assertThat(stringOutput.toString()).isEqualTo("Hello <cleaned>Model</cleaned>, 1, 1.0");
    }

    @Test
    void alreadyEscaped() {
        dummyCodeResolver.givenCode("template.kte", "@param model:String\n" +
                "Hello $safe{model}, ${1}, ${1.0f}");

        templateEngine.render("template.kte", "Model", secureOutput);

        assertThat(stringOutput.toString()).isEqualTo("Hello Model, 1, 1.0");
    }

    static class SecureOutput implements TemplateOutput {

        private final TemplateOutput output;

        SecureOutput(TemplateOutput output) {
            this.output = output;
        }

        @Override
        public void writeSafeContent(String value) {
            output.writeSafeContent(value);
        }

        @Override
        public void writeUnsafeContent(String value) {
            output.writeSafeContent("<cleaned>" + value + "</cleaned>");
        }
    }
}
