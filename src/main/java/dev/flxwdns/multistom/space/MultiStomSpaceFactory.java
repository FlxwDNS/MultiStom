package dev.flxwdns.multistom.space;

import dev.flxwdns.multistom.task.MultiStomTask;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Getter
@Accessors(fluent = true)
public final class MultiStomSpaceFactory {
    private final List<MultiStomSpace> spaces;

    public MultiStomSpaceFactory() {
        this.spaces = new ArrayList<>();
    }

    public MultiStomSpace runSpace(MultiStomTask task) {
        var space = new MultiStomSpace(task.environment().prefix() + "-" + spaces.stream()
                .filter(it -> it.name().startsWith(task.environment().prefix() + "-"))
                .toList()
                .size(), task.environment().type());

        log.info("Running space {} with type {}", space.name(), space.type());

        task.spaceConnect(space);
        this.spaces.add(space);
        return space;
    }
}
