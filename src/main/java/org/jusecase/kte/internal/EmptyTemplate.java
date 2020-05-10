package org.jusecase.kte.internal;

import org.jusecase.kte.TemplateOutput;

final class EmptyTemplate implements Template<Object> {
    public static final EmptyTemplate INSTANCE = new EmptyTemplate();

    @Override
    public void render(Object o, TemplateOutput output) {
    }
}
