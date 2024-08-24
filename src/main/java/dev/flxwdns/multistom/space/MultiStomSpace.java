package dev.flxwdns.multistom.space;

import dev.flxwdns.multistom.template.MultiStomTemplate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minestom.server.instance.Instance;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@Accessors(fluent = true)
public final class MultiStomSpace {
    private final String name;
    private final MultiStomTemplate template;

    private Instance spawnInstance;

    private final List<Instance> instances = new ArrayList<>();
}
