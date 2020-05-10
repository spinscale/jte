package org.jusecase.kte.internal;

interface ParameterParserVisitor {
    void onImport(String importClass);
    void onParameter(String parameter);
}
