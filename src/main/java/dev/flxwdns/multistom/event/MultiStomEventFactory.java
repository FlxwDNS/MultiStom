package dev.flxwdns.multistom.event;

import dev.flxwdns.multistom.task.MultiStomTask;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.trait.InstanceEvent;

import java.util.function.Consumer;

public final class MultiStomEventFactory {

    public <T extends InstanceEvent> void listen(MultiStomTask task, Class<T> eventType, Consumer<T> listener) {
        MinecraftServer.getGlobalEventHandler().addListener(eventType, t -> {
            if(task.instances().stream().anyMatch(it -> it.equals(t.getInstance()))) {
                listener.accept(t);
            }
        });
    }

}
