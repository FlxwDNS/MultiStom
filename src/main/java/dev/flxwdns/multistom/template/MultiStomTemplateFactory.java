package dev.flxwdns.multistom.template;

import dev.flxwdns.multistom.GsonUtility;
import dev.flxwdns.multistom.template.ressources.MultiStomTemplateConfiguration;
import dev.flxwdns.multistom.template.type.MultiStomTemplateType;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Accessors(fluent = true)
public final class MultiStomTemplateFactory {
    private final List<MultiStomTemplate> templates;

    @SneakyThrows
    public MultiStomTemplateFactory() {
        this.templates = new ArrayList<>();

        var tasksFolder = Path.of("tasks");
        if (!tasksFolder.toFile().exists()) {
            log.info("Creating tasks folder...");
            Files.createDirectory(tasksFolder);
        }
        tasksFolder.resolve("global").toFile().mkdirs();

        for (File file : tasksFolder.toFile().listFiles()) {
            if(!file.isFile() && !file.getName().equalsIgnoreCase("global")) {
                var path = tasksFolder.resolve(file.getName());
                GsonUtility.writeIfNotExists(new MultiStomTemplateConfiguration(file.getName().toUpperCase().charAt(0) + file.getName().toLowerCase().substring(1), 0, MultiStomTemplateType.GAME), path);
                this.templates.add(new MultiStomTemplate(file.getName(), GsonUtility.read(MultiStomTemplateConfiguration.class, path)));
            }
        }
    }
}
