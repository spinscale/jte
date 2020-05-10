package org.jusecase.kte.internal;

import java.util.ArrayList;
import java.util.List;

final class TemplateParameterParser {
    List<String> importClasses = new ArrayList<>();

    int lastIndex;
    String className;
    String instanceName;

    public void parse(String templateCode) {
        lastIndex = new ParameterParser(templateCode, new ParameterParserVisitor() {
            @Override
            public void onImport(String importClass) {
                importClasses.add(importClass);
            }

            @Override
            public void onParameter(String parameter) {
                String[] params = parameter.split(":");
                className = params[1];
                instanceName = params[0];
            }
        }).parse();
    }
}
