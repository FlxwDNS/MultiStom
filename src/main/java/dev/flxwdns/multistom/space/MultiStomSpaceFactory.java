package dev.flxwdns.multistom.space;

import dev.flxwdns.multistom.MultiStom;
import dev.flxwdns.multistom.MultiStomData;
import dev.flxwdns.multistom.space.type.MultiStomSpaceState;
import dev.flxwdns.multistom.template.MultiStomTemplate;
import dev.flxwdns.multistom.template.type.MultiStomTemplateType;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Accessors(fluent = true)
public final class MultiStomSpaceFactory {
    private final List<MultiStomSpace> spaces;

    public MultiStomSpaceFactory() {
        this.spaces = new ArrayList<>();
    }

    public void shutdown(MultiStomSpace space) {
        log.info("Stopping space {}...", space.name());
        space.template().tasks().forEach(it -> it.spaceState(space, MultiStomSpaceState.DISCONNECTED));
        this.spaces.remove(space);

        space.instances().forEach(instance -> {
            instance.getPlayers().forEach(player -> {
                var lobbyInstance = this.findLobby(player);
                if(lobbyInstance != null) {
                    player.sendMessage(MultiStomData.text("The space is shutting down..."));
                    player.setInstance(lobbyInstance);
                }
            });
            MinecraftServer.getInstanceManager().unregisterInstance(instance);
        });

        var spaceCount = this.spaces.stream().filter(it -> it.template().equals(space.template())).count();
        MultiStom.instance().templateFactory().templates()
                .stream()
                .filter(it -> it.configuration().minOnline() > 0 && it.configuration().minOnline() > spaceCount)
                .forEach(template -> {
                    for (int i = 0; i < template.configuration().minOnline() - spaceCount; i++) {
                        this.execute(template);
                    }
                });
    }

    public MultiStomSpace execute(MultiStomTemplate template) {
        var space = new MultiStomSpace(template.configuration().name() + "-" + spaces.stream()
                .filter(it -> it.name().startsWith(template.configuration().name() + "-"))
                .toList()
                .size(), template);

        log.info("Starting space {}...", space.name());
        template.tasks().forEach(it -> it.spaceState(space, MultiStomSpaceState.CONNECTED));
        this.spaces.add(space);

        return space;
    }

    public Instance findLobby(Player player) {
        var lobbyTask = this.spaces()
                .stream()
                .filter(it -> it.template().configuration().type().equals(MultiStomTemplateType.LOBBY))
                .findFirst()
                .orElse(null);
        if (lobbyTask != null) {
            if (lobbyTask.instances().isEmpty()) {
                player.kick(Component.text("§8[§cmultistom§8] §cNo lobby instance available!"));
                return null;
            }
            if (lobbyTask.spawnInstance() == null) {
                player.kick(Component.text("§8[§cmultistom§8] §cNo spawn instance available!"));
            }

            return lobbyTask.spawnInstance();
        }
        player.kick(Component.text("§8[§cmultistom§8] §cNo lobby space available!"));
        return null;
    }
}
