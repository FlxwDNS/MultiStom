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

    public void runSpace(MultiStomTask task) {
        var space = new MultiStomSpace(task.environment().prefix() + "-" + ThreadLocalRandom.current().nextInt(100000, 999999), task.environment().type());

        log.info("Running space {} with type {}", space.name(), space.type());

        task.enable(space);
        this.spaces.add(space);
    }
}
