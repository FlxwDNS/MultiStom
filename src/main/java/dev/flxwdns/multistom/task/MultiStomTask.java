package dev.flxwdns.multistom.task;

import dev.flxwdns.multistom.space.MultiStomSpace;
import dev.flxwdns.multistom.task.annotation.MultiStomTaskEnvironment;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public abstract class MultiStomTask {
    private MultiStomTaskEnvironment environment;

    public void spaceConnect(MultiStomSpace space) {
    }

    public void spaceDisconnect() {
    }
}
