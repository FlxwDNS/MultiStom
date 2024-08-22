package dev.flxwdns.multistom.task;

import dev.flxwdns.multistom.space.MultiStomSpace;
import dev.flxwdns.multistom.task.annotation.MultiStomTaskEnvironment;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minestom.server.instance.Instance;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(fluent = true)
public abstract class MultiStomTask {
    private MultiStomTaskEnvironment environment;

    public void enable(MultiStomSpace space) {
    }

    public void disable() {
    }
}
