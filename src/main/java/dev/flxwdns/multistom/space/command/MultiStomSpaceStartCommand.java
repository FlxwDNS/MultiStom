package dev.flxwdns.multistom.space.command;

import dev.flxwdns.multistom.MultiStom;
import dev.flxwdns.multistom.MultiStomData;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;

public final class MultiStomSpaceStartCommand extends Command {

    public MultiStomSpaceStartCommand() {
        super("start");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage(MultiStomData.text(""));
            sender.sendMessage(MultiStomData.text("§cProvide a valid template§8!"));
            sender.sendMessage(MultiStomData.text("§8•§8§m                               §r§8•"));
            MultiStom.instance().templateFactory().templates().forEach(template -> {
                sender.sendMessage(MultiStomData.text("§8• §9§l" + template.configuration().name()));
            });
            sender.sendMessage(MultiStomData.text(""));
        });

        var template = ArgumentType.String("template");
        addSyntax((sender, context) -> {
            var taskName = context.get(template);
            var templateOptional = MultiStom.instance().templateFactory().templates()
                    .stream()
                    .filter(it -> it.configuration().name().equalsIgnoreCase(taskName))
                    .findFirst();
            if (templateOptional.isEmpty()) {
                sender.sendMessage(MultiStomData.text("§cTemplate not found!"));
                return;
            }

            var space = MultiStom.instance().spaceFactory().execute(templateOptional.get());
            sender.sendMessage(MultiStomData.text("§7Running space with following name: §9" + space.name()));
        }, template);

        MinecraftServer.getCommandManager().register(this);
    }
}
