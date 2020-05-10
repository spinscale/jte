package org.jusecase.kte.internal;

import org.jusecase.kte.TemplateOutput;

public interface Template<Model> {
    void render(Model model, TemplateOutput output);
}
