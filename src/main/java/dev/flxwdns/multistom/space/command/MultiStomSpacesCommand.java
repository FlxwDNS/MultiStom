package dev.flxwdns.multistom.space.command;

import dev.flxwdns.multistom.MultiStom;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

public final class MultiStomSpacesCommand extends Command {

    public MultiStomSpacesCommand() {
        super("spaces");

        setDefaultExecutor((sender, context) -> {
            if(sender instanceof Player player) {
                player.sendMessage(Component.text("§8[§cmultistom§8] §7Spaces:"));
                MultiStom.instance().spaceFactory().spaces().forEach(space -> {
                    var isOnSpace = space.instances().stream().anyMatch(it -> it.getPlayers().contains(player));
                    int[] online = {0};
                    space.instances().forEach(it -> online[0] += it.getPlayers().size());

                    player.sendMessage(Component.text("§8[§cmultistom§8]  §8- §7" + space.name() + " §8[§7" + space.type().name() + "§8] §8[" + (isOnSpace ? "§7YOU" : "§c-") + "§8] §8[§7" + online[0] + "§8]"));
                });
            }
        });
        MinecraftServer.getCommandManager().register(this);
    }
}
