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
            MultiStom.instance().templateFactory().templates().forEach(template -> {
                sender.sendMessage(Component.text("§8[§cmultistom§8] §7Template: §9" + template.configuration().name()));
            });
        });

        var template = ArgumentType.String("template");
        addSyntax((sender, context) -> {
            var taskName = context.get(template);
            var templateOptional = MultiStom.instance().templateFactory().templates()
                    .stream()
                    .filter(it -> it.configuration().name().equalsIgnoreCase(taskName))
                    .findFirst();
            if (templateOptional.isEmpty()) {
                sender.sendMessage(Component.text("§8[§cmultistom§8] §cTemplate not found!"));
                return;
            }

            var space = MultiStom.instance().spaceFactory().execute(templateOptional.get());
            sender.sendMessage(Component.text("§8[§cmultistom§8] §7Running template: §e" + space.name()));
        }, template);

        MinecraftServer.getCommandManager().register(this);
    }
}
