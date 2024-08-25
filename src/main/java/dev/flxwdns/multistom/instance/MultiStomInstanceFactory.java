package dev.flxwdns.multistom.instance;

import dev.flxwdns.multistom.MultiStom;
import dev.flxwdns.multistom.space.MultiStomSpace;
import dev.flxwdns.multistom.template.type.MultiStomTemplateType;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.instance.AddEntityToInstanceEvent;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerChatEvent;
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

        MinecraftServer.getGlobalEventHandler().addListener(AddEntityToInstanceEvent.class, event -> {
            if (event.getEntity() instanceof Player player) {
                MultiStom.instance().spaceFactory().spaces().forEach(space -> {
                    if(space.instances().stream().anyMatch(it -> it.equals(event.getInstance()))) {
                        space.instances().forEach(instance -> instance.getPlayers().forEach(it -> {
                            if(it == player) {
                                return;
                            }
                            it.sendPacket(playerPacket(player));
                            player.sendPacket(playerPacket(it));
                        }));
                    } else {
                        space.instances().forEach(instance -> instance.getPlayers().forEach(it -> {
                            if(it == player) {
                                return;
                            }
                            it.sendPacket(new PlayerInfoRemovePacket(player.getUuid()));
                            player.sendPacket(new PlayerInfoRemovePacket(it.getUuid()));
                        }));
                    }
                });
            }
        });
    }

    private PlayerInfoUpdatePacket playerPacket(Player player) {
        var infoEntry = new PlayerInfoUpdatePacket.Entry(player.getUuid(), player.getUsername(), List.of(
                new PlayerInfoUpdatePacket.Property("textures", player.getSkin().textures(), player.getSkin().signature())
        ),
                true, 1, player.getGameMode(), null, null);
        return new PlayerInfoUpdatePacket(EnumSet.of(PlayerInfoUpdatePacket.Action.ADD_PLAYER, PlayerInfoUpdatePacket.Action.UPDATE_LISTED), List.of(infoEntry));
    }

    public InstanceContainer create(MultiStomSpace space) {
        var container = MinecraftServer.getInstanceManager().createInstanceContainer();
        MinecraftServer.getInstanceManager().registerInstance(container);

        container.setChunkSupplier(LightingChunk::new);

        space.instances().add(container);
        return container;
    }
}
