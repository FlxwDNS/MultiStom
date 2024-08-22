package dev.flxwdns.multistom.instance;

import dev.flxwdns.multistom.MultiStom;
import dev.flxwdns.multistom.task.type.MultiStomTaskType;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;

public final class MultiStomInstanceFactory {

    public MultiStomInstanceFactory() {
        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, event -> {
            var lobbyTask = MultiStom.instance().taskFactory().tasks()
                    .stream()
                    .filter(it -> it.environment().type().equals(MultiStomTaskType.LOBBY))
                    .findFirst()
                    .orElse(null);
            if(lobbyTask != null) {
                if(lobbyTask.instances().isEmpty()) {
                    event.getPlayer().kick(Component.text("No lobby instance available!"));
                    return;
                }
                event.setSpawningInstance(lobbyTask.instances().getFirst());
                return;
            }
            event.getPlayer().kick(Component.text("No lobby task available!"));
        });
    }

}
