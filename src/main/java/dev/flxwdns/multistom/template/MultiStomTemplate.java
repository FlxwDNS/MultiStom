package dev.flxwdns.multistom.template;

import dev.flxwdns.multistom.task.MultiStomTask;
import dev.flxwdns.multistom.template.ressources.MultiStomTemplateConfiguration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public final class MultiStomTemplate {
    private final String folderName;
    private final MultiStomTemplateConfiguration configuration;

    private final List<MultiStomTask> tasks = new ArrayList<>();
}
