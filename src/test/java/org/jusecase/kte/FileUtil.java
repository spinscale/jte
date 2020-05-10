package org.jusecase.kte;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class FileUtil {
    public static void deleteAllIn(Path root) {
        if (Files.exists(root)) {
            try {
                //noinspection ResultOfMethodCallIgnored
                Files.walk(root)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
