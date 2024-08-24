package dev.flxwdns.multistom.space.command;

import dev.flxwdns.multistom.MultiStom;
import dev.flxwdns.multistom.MultiStomData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.Material;

public final class MultiStomSpacesCommand extends Command {

    public MultiStomSpacesCommand() {
        super("spaces");

        setDefaultExecutor((sender, context) -> {
            if(sender instanceof Player player) {
                MultiStom.instance().templateFactory().templates().forEach(template -> {
                    int[] templateOnline = {0};
                    var spaces = MultiStom.instance().spaceFactory().spaces().stream().filter(it -> it.template().equals(template)).toList();
                    spaces.forEach(space -> {
                        for (Instance instance : space.instances()) {
                            templateOnline[0] += instance.getPlayers().size();
                        }
                    });

                    player.sendMessage(MultiStomData.text(""));
                    player.sendMessage(MultiStomData.text("§9§l" + template.configuration().name() + " §8(§9§l" + templateOnline[0] + "§8)"));

                    if(spaces.isEmpty()) {
                        player.sendMessage(MultiStomData.text("§8• §cNo spaces available."));
                        return;
                    }

                    spaces.forEach(space -> {
                        var isOnSpace = space.instances().stream().anyMatch(it -> it.getPlayers().contains(player));
                        int[] online = {0};
                        space.instances().forEach(it -> online[0] += it.getPlayers().size());

                        player.sendMessage(MultiStomData.text("§8• §7" + space.name() + (isOnSpace ? " §8| §7<-" : ""))
                                .clickEvent(ClickEvent.runCommand("/connect " + space.name()))
                                .hoverEvent(HoverEvent.showText(Component.text("§7Click to connect to §9§l" + space.name()).append(
                                        Component.text("\n§8• §7" + online[0] + " players")
                                ))));
                    });
                });
                player.sendMessage(MultiStomData.text(""));
            }
        });
        MinecraftServer.getCommandManager().register(this);
    }
}
