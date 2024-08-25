package dev.flxwdns.multistom;

import dev.appolo.server.server.AppoloServer;
import dev.flxwdns.multistom.event.MultiStomEventFactory;
import dev.flxwdns.multistom.instance.MultiStomInstanceFactory;
import dev.flxwdns.multistom.space.command.MultiStomCommand;
import dev.flxwdns.multistom.template.MultiStomTemplateFactory;
import dev.flxwdns.multistom.space.MultiStomSpaceFactory;
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

    private final MultiStomTemplateFactory templateFactory;
    private final MultiStomTaskFactory taskFactory;
    private final MultiStomSpaceFactory spaceFactory;
    private final MultiStomEventFactory eventFactory;
    private final MultiStomInstanceFactory instanceFactory;

    public MultiStom() {
        instance = this;

        /*System.setIn(new PrintStream("System.in") {
            @Override
            public void println(String x) {
                log.info(x);
            }
        });*/

        var server = new AppoloServer(log::info);
        server.run();

        this.spaceFactory = new MultiStomSpaceFactory();
        this.eventFactory = new MultiStomEventFactory();
        this.instanceFactory = new MultiStomInstanceFactory();
        this.templateFactory = new MultiStomTemplateFactory();
        this.taskFactory = new MultiStomTaskFactory();

        this.templateFactory.templates()
                .stream()
                .filter(it -> it.configuration().minOnline() > 0)
                .forEach(template -> {
                    for (int i = 0; i < template.configuration().minOnline(); i++) {
                        this.spaceFactory.execute(template);
                    }
                });

        new MultiStomCommand();

        var startup = System.currentTimeMillis() - Long.valueOf(System.getProperty("multistom.startup"));
        log.info("MultiStom has been started! Took {}ms ({}s)", startup, TimeUnit.MILLISECONDS.toSeconds(startup));
    }
}
