package dev.flxwdns.multistom.event;

import dev.flxwdns.multistom.space.MultiStomSpace;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.trait.InstanceEvent;

import java.util.function.Consumer;

public final class MultiStomEventFactory {

    public <T extends InstanceEvent> void listen(MultiStomSpace space, Class<T> eventType, Consumer<T> listener) {
        MinecraftServer.getGlobalEventHandler().addListener(eventType, t -> {
            if(space.instances().stream().anyMatch(it -> it.equals(t.getInstance()))) {
                listener.accept(t);
            }
        });
    }
}
