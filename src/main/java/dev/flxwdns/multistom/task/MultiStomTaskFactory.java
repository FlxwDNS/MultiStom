package dev.flxwdns.multistom.task;

import dev.flxwdns.multistom.task.invoker.MultiStomTaskInvoker;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import net.minestom.server.timer.Task;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Accessors(fluent = true)
@Getter
public final class MultiStomTaskFactory {
    private final Path tasksFolder;

    private final List<MultiStomTask> tasks;

    @SneakyThrows
    public MultiStomTaskFactory() {
        this.tasksFolder = Path.of("tasks");
        this.tasks = new ArrayList<>();

        if (!this.tasksFolder.toFile().exists()) {
            log.info("Creating tasks folder...");
            Files.createDirectory(this.tasksFolder);
        }

        var tasksFiles = this.tasksFiles();
        tasksFiles.forEach(it -> {
            this.tasks.add(MultiStomTaskInvoker.invoke(it.toPath()));
        });

        log.info("Found {} {}.", tasksFiles.size(), tasksFiles.size() == 1 ? "task" : "tasks");
        this.tasks.forEach(it -> {
            var environment = it.environment();
            log.info(" - {} (v{}) made by {}", it.getClass().getSimpleName(), environment.version(), Arrays.toString(environment.authors()));
        });
        if(!tasks.isEmpty()) {
            log.info("");
        }
    }

    private List<File> tasksFiles() {
        return Arrays.stream(this.tasksFolder.toFile().listFiles()).filter(it -> it.getName().endsWith(".jar")).toList();
    }
}