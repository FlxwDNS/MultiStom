package dev.flxwdns.multistom.task;

import dev.flxwdns.multistom.MultiStom;
import dev.flxwdns.multistom.template.MultiStomTemplate;
import dev.flxwdns.multistom.task.invoker.MultiStomTaskInvoker;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Slf4j
@Accessors(fluent = true)
@Getter
public final class MultiStomTaskFactory {
    private final Path tasksFolder;

    //private final Map<MultiStomTemplate, List<MultiStomTask>> tasks;

    @SneakyThrows
    public MultiStomTaskFactory() {
        this.tasksFolder = Path.of("tasks");

        if (!this.tasksFolder.toFile().exists()) {
            log.info("Creating tasks folder...");
            Files.createDirectory(this.tasksFolder);
        }

        MultiStom.instance().templateFactory().templates().forEach(it -> {
            var tasks = this.templateTasks(it);
            tasks.forEach(task -> {
                it.tasks().add(MultiStomTaskInvoker.invoke(task.toPath()));
            });
        });

        log.info("Found {} {}.", MultiStom.instance().templateFactory().templates().size(), MultiStom.instance().templateFactory().templates().size() == 1 ? "template" : "templates");

        MultiStom.instance().templateFactory().templates().forEach(template -> {
            log.info("Template: {}", template.configuration().name());
            template.tasks().forEach(task -> {
                var environment = task.environment();
                log.info(" - {} (v{}) made by {}", task.getClass().getSimpleName(), environment.version(), Arrays.toString(environment.authors()));
            });
        });
    }

    private List<File> templateTasks(MultiStomTemplate template) {
        var files = this.tasksFolder.resolve(template.folderName()).toFile().listFiles();
        if(files == null) {
            throw new RuntimeException("No tasks found for template " + template.configuration().name());
        }
        var tasks = new ArrayList<>(Arrays.stream(files).filter(it -> it.getName().endsWith(".jar")).toList());
        tasks.addAll(Arrays.stream(this.tasksFolder.resolve("global").toFile().listFiles()).filter(it -> it.getName().endsWith(".jar")).toList());
        return tasks;
    }
}