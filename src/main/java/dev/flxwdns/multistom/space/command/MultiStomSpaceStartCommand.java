package dev.flxwdns.multistom.space.command;

import dev.flxwdns.multistom.MultiStom;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;

public final class MultiStomSpaceStartCommand extends Command {

    public MultiStomSpaceStartCommand() {
        super("start");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage(Component.text("§8[§cmultistom§8] §cProvide a valid task§8!"));
            MultiStom.instance().taskFactory().tasks().forEach(task -> {
                sender.sendMessage(Component.text("§8[§cmultistom§8] §7Task: §e" + task.environment().prefix()));
            });
        });

        var task = ArgumentType.String("task");
        addSyntax((sender, context) -> {
            var taskName = context.get(task);
            var taskOptional = MultiStom.instance().taskFactory().tasks().stream().filter(it -> it.environment().prefix().equalsIgnoreCase(taskName)).findFirst();
            if (taskOptional.isEmpty()) {
                sender.sendMessage(Component.text("§8[§cmultistom§8] §cTask not found!"));
                return;
            }

            var space = MultiStom.instance().spaceFactory().runSpace(taskOptional.get());
            sender.sendMessage(Component.text("§8[§cmultistom§8] §7Running task: §e" + space.name()));
        }, task);

        MinecraftServer.getCommandManager().register(this);
    }
}
