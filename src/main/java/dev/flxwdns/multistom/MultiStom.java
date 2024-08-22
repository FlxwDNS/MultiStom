package dev.flxwdns.multistom;

import dev.appolo.server.server.AppoloServer;
import dev.flxwdns.multistom.event.MultiStomEventFactory;
import dev.flxwdns.multistom.instance.MultiStomInstanceFactory;
import dev.flxwdns.multistom.task.MultiStomTaskFactory;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Getter
@Slf4j
@Accessors(fluent = true)
public final class MultiStom {
    @Getter
    private static MultiStom instance;

    private final MultiStomTaskFactory taskFactory;
    private final MultiStomEventFactory eventFactory;
    private final MultiStomInstanceFactory instanceFactory;

    public MultiStom() {
        instance = this;

        var server = new AppoloServer(log::info);
        server.run();

        this.taskFactory = new MultiStomTaskFactory();
        this.eventFactory = new MultiStomEventFactory();
        this.instanceFactory = new MultiStomInstanceFactory();

        var startup = System.currentTimeMillis() - Long.valueOf(System.getProperty("multistom.startup"));
        log.info("MultiStom has been started! Took {}ms ({}s)", startup, TimeUnit.MILLISECONDS.toSeconds(startup));
    }
}
