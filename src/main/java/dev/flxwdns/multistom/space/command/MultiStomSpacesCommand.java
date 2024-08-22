package dev.flxwdns.multistom.space.command;

import dev.flxwdns.multistom.MultiStom;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;

public final class MultiStomSpacesCommand extends Command {

    public MultiStomSpacesCommand() {
        super("spaces");

        setDefaultExecutor((sender, context) -> {
            MultiStom.instance().spaceFactory().spaces().forEach(space -> {
                sender.sendMessage(Component.text("§8[§cmultistom§8] §7Space: §e" + space.name() + " §8- §e" + space.type().name()));
            });
        });
        MinecraftServer.getCommandManager().register(this);
    }
}
