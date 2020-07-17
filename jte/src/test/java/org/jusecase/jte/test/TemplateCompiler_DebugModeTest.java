package org.jusecase.jte.test;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.jusecase.jte.compile.TemplateCompiler;

import static org.assertj.core.api.Assertions.assertThat;

public class TemplateCompiler_DebugModeTest {
    @Test
    public void ensureDebugModeIsOff() {
        Assertions.assertThat(TemplateCompiler.DEBUG).describedAs("Do not deploy debug builds to maven central!").isFalse();
    }
}