package dev.flxwdns.multistom.instance;

import dev.flxwdns.multistom.MultiStom;
import dev.flxwdns.multistom.space.MultiStomSpace;
import dev.flxwdns.multistom.template.type.MultiStomTemplateType;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.network.packet.server.play.PlayerInfoRemovePacket;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket;

import java.util.EnumSet;
import java.util.List;

@Slf4j
public final class MultiStomInstanceFactory {

    public MultiStomInstanceFactory() {
        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, event -> {
            var lobbyTask = MultiStom.instance().spaceFactory().spaces()
                    .stream()
                    .filter(it -> it.template().configuration().type().equals(MultiStomTemplateType.LOBBY))
                    .findFirst()
                    .orElse(null);
            if (lobbyTask != null) {
                if (lobbyTask.instances().isEmpty()) {
                    event.getPlayer().kick(Component.text("§8[§cmultistom§8] §cNo lobby instance available!"));
                    return;
                }
                if (lobbyTask.spawnInstance() == null) {
                    event.getPlayer().kick(Component.text("§8[§cmultistom§8] §cNo spawn instance available!"));
                }

                event.setSpawningInstance(lobbyTask.spawnInstance());
                return;
            }
            event.getPlayer().kick(Component.text("§8[§cmultistom§8] §cNo lobby space available!"));
        });

        MinecraftServer.getGlobalEventHandler().addListener(PlayerChatEvent.class, event -> {
            event.setCancelled(true);

            var space = MultiStom.instance().spaceFactory().spaces()
                    .stream()
                    .filter(it -> it.instances().stream().anyMatch(instance -> instance.getPlayers().contains(event.getPlayer())))
                    .findFirst()
                    .orElse(null);

            if (space == null) {
                event.getPlayer().sendMessage(Component.text("§8[§cmultistom§8] §cYou are not connected to any space!"));
                return;
            }

            space.instances().forEach(instance -> instance.getPlayers().forEach(player -> player.sendMessage(Component.text("§8[§7" + event.getPlayer().getUsername() + "§8] §7" + event.getMessage()))));
        });

        MinecraftServer.getGlobalEventHandler().addListener(PlayerSpawnEvent.class, event -> {
            var space = MultiStom.instance().spaceFactory().spaces()
                    .stream()
                    .filter(it -> it.instances().stream().anyMatch(instance -> instance.getPlayers().contains(event.getPlayer())))
                    .findFirst()
                    .orElse(null);

            if (space == null) {
                event.getPlayer().kick(Component.text("§8[§cmultistom§8] §cYou are not connected to any space!"));
                return;
            }

            var player = event.getPlayer();
            MinecraftServer.getConnectionManager()
                    .getOnlinePlayers()
                    .stream()
                    .forEach(players -> {
                        if(space.instances().stream().anyMatch(instance -> instance.getPlayers().contains(players))) {
                            players.sendPacket(new PlayerInfoUpdatePacket(EnumSet.allOf(PlayerInfoUpdatePacket.Action.class), List.of(new PlayerInfoUpdatePacket.Entry(player.getUuid(),
                                    player.getUsername(),
                                    List.of(new PlayerInfoUpdatePacket.Property("textures", player.getSkin().textures(), player.getSkin().signature())),
                                    true, 0, GameMode.SURVIVAL, player.getDisplayName(), null)
                            )));
                            players.addViewer(player);


                            log.info("ADD!");
                            return;
                        }
                        log.info("REMOVE!");

                        players.sendPacket(new PlayerInfoRemovePacket(player.getUuid()));
                        player.sendPacket(new PlayerInfoRemovePacket(players.getUuid()));
                    });
        });
    }

    public InstanceContainer create(MultiStomSpace space) {
        var container = MinecraftServer.getInstanceManager().createInstanceContainer();
        MinecraftServer.getInstanceManager().registerInstance(container);

        container.setChunkSupplier(LightingChunk::new);

        space.instances().add(container);
        return container;
    }
}
