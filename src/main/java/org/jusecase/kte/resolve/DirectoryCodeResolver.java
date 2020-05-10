package org.jusecase.kte.resolve;

import com.sun.nio.file.SensitivityWatchEventModifier;
import org.jusecase.kte.CodeResolver;
import org.jusecase.kte.TemplateEngine;
import org.jusecase.kte.internal.TemplateCompiler;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DirectoryCodeResolver implements CodeResolver {
    private final Path root;

    public DirectoryCodeResolver(Path root) {
        this.root = root;
    }

    @Override
    public String resolve(String name) {
        try {
            return Files.readString(root.resolve(name));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public List<String> resolveAllTemplateNames() {
        try (Stream<Path> stream = Files.walk(root)) {
            return stream
                    .filter(p -> !Files.isDirectory(p))
                    .map(p -> root.relativize(p).toString().replace('\\', '/'))
                    .filter(s -> !s.startsWith(TemplateCompiler.TAG_DIRECTORY) && !s.startsWith(TemplateCompiler.LAYOUT_DIRECTORY))
                    .filter(s -> s.endsWith(".kte"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to resolve all templates in " + root, e);
        }
    }

    @SuppressWarnings("unused") // Not used by unit tests, but depending projects
    public void enableHotReload(TemplateEngine templateEngine, Consumer<List<String>> onTemplatesInvalidated) {
        Thread reloadThread = new Thread(() -> enableHotReloadBlocking(templateEngine, onTemplatesInvalidated));
        reloadThread.setName("kte-reloader");
        reloadThread.setDaemon(true);
        reloadThread.start();
    }

    public void enableHotReloadBlocking(TemplateEngine templateEngine, Consumer<List<String>> onTemplatesInvalidated) {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {

            Files.walk(root).filter(p -> Files.isDirectory(p)).forEach(p -> {
                try {
                    p.register(watchService, new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_MODIFY}, SensitivityWatchEventModifier.HIGH);
                } catch (IOException e) {
                    throw new UncheckedIOException("Failed to register watch service for hot reload!", e);
                }
            });

            WatchKey watchKey;
            while ((watchKey = watchService.take()) != null) {
                try {
                    List<WatchEvent<?>> events = watchKey.pollEvents();
                    for (WatchEvent<?> event : events) {
                        String eventContext = event.context().toString();
                        if (!eventContext.endsWith(".kte")) {
                            continue;
                        }

                        Path file = root.relativize((Path) watchKey.watchable()).resolve(eventContext);

                        Path absoluteFile = root.resolve(file);
                        if (absoluteFile.toFile().length() <= 0) {
                            continue;
                        }

                        String name = file.toString().replace('\\', '/');

                        List<String> invalidatedTemplates = templateEngine.invalidate(name);
                        if (onTemplatesInvalidated != null) {
                            onTemplatesInvalidated.accept(invalidatedTemplates);
                        }
                    }
                } finally {
                    watchKey.reset();
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to watch page content", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
