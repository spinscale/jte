package org.jusecase.jte.compile;

import org.jusecase.jte.TemplateException;
import org.jusecase.jte.internal.ClassInfo;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClassFilesCompiler {
    public static void compile(String[] files, List<String> compilePath, Path classDirectory, Map<String, ClassInfo> templateByClassName) {
        List<String> additionalArguments = new ArrayList<>();

        if (compilePath != null && !compilePath.isEmpty()) {
            additionalArguments.add("--class-path");
            additionalArguments.add(String.join(File.pathSeparator, compilePath));
        }

        String modulePath = System.getProperty("jdk.module.path");
        if (modulePath != null && !modulePath.isBlank()) {
            additionalArguments.add("--module-path");
            additionalArguments.add(modulePath);

            additionalArguments.add("--add-modules");
            additionalArguments.add("org.jusecase.jte.runtime,org.jusecase.jte");
        }

        if (additionalArguments.isEmpty()) {
            runCompiler(files, classDirectory, templateByClassName);
        } else {
            String[] args = new String[files.length + additionalArguments.size()];
            for (int i = 0; i < additionalArguments.size(); i++) {
                args[i] = additionalArguments.get(i);
            }
            System.arraycopy(files, 0, args, additionalArguments.size(), files.length);

            runCompiler(args, classDirectory, templateByClassName);
        }


        if (false) {
            if (compilePath != null && !compilePath.isEmpty()) {
                String[] args = new String[files.length + 2];
                args[0] = "--class-path";
                args[1] = String.join(File.pathSeparator, compilePath);
                System.arraycopy(files, 0, args, 2, files.length);

                runCompiler(args, classDirectory, templateByClassName);
            } else {
                String[] args = new String[files.length + 6];
                args[0] = "--class-path";
                args[1] = System.getProperty("java.class.path"); // TODO null check
                args[2] = "--module-path";
                args[3] = System.getProperty("jdk.module.path"); // TODO null check
                args[4] = "--add-modules";
                args[5] = "org.jusecase.jte.runtime,org.jusecase.jte";
                System.arraycopy(files, 0, args, 6, files.length);

                runCompiler(args, classDirectory, templateByClassName);
            }
        }
    }

    private static void runCompiler(String[] args, Path classDirectory, Map<String, ClassInfo> templateByClassName) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

        int result = compiler.run(null, null, new PrintStream(errorStream, true, StandardCharsets.UTF_8), args);
        if (result != 0) {
            String errors = errorStream.toString(StandardCharsets.UTF_8);
            throw new TemplateException(getErrorMessage(errors, classDirectory, templateByClassName));
        }
    }

    private static String getErrorMessage(String errors, Path classDirectory, Map<String, ClassInfo> templateByClassName) {
        try {
            String absolutePath = classDirectory.toAbsolutePath().toString();
            int classBeginIndex = errors.indexOf(absolutePath) + absolutePath.length() + 1;
            int classEndIndex = errors.indexOf(".java:");
            String className = errors.substring(classBeginIndex, classEndIndex).replace(File.separatorChar, '.');

            int lineStartIndex = classEndIndex + 6;
            int lineEndIndex = errors.indexOf(':', lineStartIndex);
            int javaLine = Integer.parseInt(errors.substring(lineStartIndex, lineEndIndex));

            ClassInfo templateInfo = templateByClassName.get(className);
            int templateLine = templateInfo.lineInfo[javaLine - 1] + 1;

            return "Failed to compile template, error at " + templateInfo.name + ":" + templateLine + "\n" + errors;
        } catch (Exception e) {
            return "Failed to compile template, error at\n" + errors;
        }
    }
}
