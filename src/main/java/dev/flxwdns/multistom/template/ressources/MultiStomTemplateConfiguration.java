package dev.flxwdns.multistom.template.ressources;

import dev.flxwdns.multistom.template.type.MultiStomTemplateType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public final class MultiStomTemplateConfiguration {
    private final String name;
    private final int minOnline;

    private final MultiStomTemplateType type;
}
