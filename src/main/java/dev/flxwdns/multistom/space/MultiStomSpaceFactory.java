package dev.flxwdns.multistom.space;

import dev.flxwdns.multistom.space.type.MultiStomSpaceState;
import dev.flxwdns.multistom.template.MultiStomTemplate;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Accessors(fluent = true)
public final class MultiStomSpaceFactory {
    private final List<MultiStomSpace> spaces;

    public MultiStomSpaceFactory() {
        this.spaces = new ArrayList<>();
    }

    public MultiStomSpace execute(MultiStomTemplate template) {
        var space = new MultiStomSpace(template.configuration().name() + "-" + spaces.stream()
                .filter(it -> it.name().startsWith(template.configuration().name() + "-"))
                .toList()
                .size(), template);

        log.info("Starting space {}...", space.name());
        template.tasks().forEach(it -> it.spaceState(space, MultiStomSpaceState.CONNECTED));
        this.spaces.add(space);

        return space;
    }
}
