package dev.flxwdns.multistom.space;

import dev.flxwdns.multistom.space.type.MultiStomSpaceState;
import dev.flxwdns.multistom.task.MultiStomTask;
import dev.flxwdns.multistom.template.MultiStomTemplate;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.network.packet.server.play.TeamsPacket;

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

    public MultiStomSpace execute(MultiStomTemplate template) {
        var space = new MultiStomSpace(template.configuration().name() + "-" + spaces.stream()
                .filter(it -> it.name().startsWith(template.configuration().name() + "-"))
                .toList()
                .size(), template);

        log.info("Starting space {}...", space.name());
        template.tasks().forEach(it -> it.spaceState(space, MultiStomSpaceState.CONNECTED));
        this.spaces.add(space);

        var team = MinecraftServer.getTeamManager().createTeam(space.name());
        team.setNameTagVisibility(TeamsPacket.NameTagVisibility.NEVER);
        team.setCollisionRule(TeamsPacket.CollisionRule.NEVER);
        team.setPrefix(Component.text("§8[§7" + space.name() + "§8] §7"));

        return space;
    }
}
