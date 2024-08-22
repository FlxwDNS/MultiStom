package dev.flxwdns.multistom.space.command;

import dev.flxwdns.multistom.MultiStom;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

public final class MultiStomSpaceConnectCommand extends Command {

    public MultiStomSpaceConnectCommand() {
        super("connect");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage(Component.text("§8[§cmultistom§8] §cProvide a valid space§8!"));
        });

        var space = ArgumentType.String("space");
        addSyntax((sender, context) -> {
            var spaceName = context.get(space);
            var spaceOptional = MultiStom.instance().spaceFactory().spaces().stream().filter(it -> it.name().equalsIgnoreCase(spaceName)).findFirst();
            if (spaceOptional.isEmpty()) {
                sender.sendMessage(Component.text("§8[§cmultistom§8] §cSpace not found!"));
                return;
            }

            var spaceInstance = spaceOptional.get();
            sender.sendMessage(Component.text("§8[§cmultistom§8] §7Connecting to space: §e" + spaceInstance.name()));

            if(sender instanceof Player player) {
                if(spaceInstance.spawnInstance() == null) {
                    player.sendMessage(Component.text("§8[§cmultistom§8] §cNo spawn instance available!"));
                    return;
                }
                if(spaceInstance.spawnInstance() == player.getInstance()) {
                    player.sendMessage(Component.text("§8[§cmultistom§8] §cAlready connected to this space!"));
                    return;
                }
                player.setInstance(spaceInstance.spawnInstance());
            }
        }, space);

        MinecraftServer.getCommandManager().register(this);
    }
}
