package dev.flxwdns.multistom.instance;

import dev.flxwdns.multistom.MultiStom;
import dev.flxwdns.multistom.space.MultiStomSpace;
import dev.flxwdns.multistom.task.MultiStomTask;
import dev.flxwdns.multistom.task.type.MultiStomTaskType;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.InstanceContainer;

public final class MultiStomInstanceFactory {

    public MultiStomInstanceFactory() {
        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, event -> {
            var lobbyTask = MultiStom.instance().spaceFactory().spaces()
                    .stream()
                    .filter(it -> it.type().equals(MultiStomTaskType.LOBBY))
                    .findFirst()
                    .orElse(null);
            if(lobbyTask != null) {
                if(lobbyTask.instances().isEmpty()) {
                    event.getPlayer().kick(Component.text("§8[§cmultistom§8] §cNo lobby instance available!"));
                    return;
                }
                event.setSpawningInstance(lobbyTask.instances().getFirst());
                return;
            }
            event.getPlayer().kick(Component.text("§8[§cmultistom§8] §cNo lobby space available!"));
        });
    }

    public InstanceContainer create(MultiStomSpace space) {
        var container = MinecraftServer.getInstanceManager().createInstanceContainer();
        MinecraftServer.getInstanceManager().registerInstance(container);

        space.instances().add(container);
        return container;
    }
}
