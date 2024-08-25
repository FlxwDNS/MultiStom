package dev.flxwdns.multistom.space.command;

import ch.qos.logback.core.util.SystemInfo;
import com.sun.management.OperatingSystemMXBean;
import dev.flxwdns.multistom.MultiStom;
import dev.flxwdns.multistom.MultiStomData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.utils.time.Tick;

import java.lang.management.ManagementFactory;
import java.util.Arrays;

public final class MultiStomCommand extends Command {

    public MultiStomCommand() {
        super("multistom");

        var typeArgument = ArgumentType.Enum("type", MultiStomCommandType.class);
        addSyntax((sender, context) -> {
            if (sender instanceof Player player) {
                var type = context.get(typeArgument);
                if (type.equals(MultiStomCommandType.START)) {
                    sender.sendMessage(MultiStomData.text("§cUse: /multistom START §8<§ctemplate§8>"));
                } else if (type.equals(MultiStomCommandType.STOP)) {
                    sender.sendMessage(MultiStomData.text("§cUse: /multistom STOP §8<§cspace§8>"));
                } else if (type.equals(MultiStomCommandType.CONNECT)) {
                    sender.sendMessage(MultiStomData.text("§cUse: /multistom CONNECT §8<§cspace§8>"));
                } else if (type.equals(MultiStomCommandType.SPACES)) {
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

                        if (spaces.isEmpty()) {
                            player.sendMessage(MultiStomData.text("§8• §cNo spaces available."));
                            return;
                        }

                        spaces.forEach(space -> {
                            var isOnSpace = space.instances().stream().anyMatch(it -> it.getPlayers().contains(player));
                            int[] online = {0};
                            space.instances().forEach(it -> online[0] += it.getPlayers().size());

                            player.sendMessage(MultiStomData.text("§8• §7" + space.name() + (isOnSpace ? " §8| §7<-" : ""))
                                    .clickEvent(ClickEvent.runCommand("/multistom CONNECT " + space.name()))
                                    .hoverEvent(HoverEvent.showText(Component.text("§7Click to connect to §9§l" + space.name()).append(
                                            Component.text("\n§8• §7" + online[0] + " players")
                                    ))));
                        });
                    });
                    var osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

                    player.sendMessage(MultiStomData.text(""));
                    player.sendMessage(MultiStomData.text("§c" + (int) (MinecraftServer.getBenchmarkManager().getUsedMemory() / 1024.0 / 1024.0) + "mb §7& §c" +
                                                          (osBean.getProcessCpuLoad() * 100)
                                                          + "% usage §7with §c" + Tick.SERVER_TICKS.getTicksPerSecond() + "ticks/s"));
                    player.sendMessage(MultiStomData.text(""));
                }
            }
        }, typeArgument);


        var valueArgument = ArgumentType.String("value").setSuggestionCallback((sender, context, suggestion) -> {
            if(context.get(typeArgument).equals(MultiStomCommandType.CONNECT) || context.get(typeArgument).equals(MultiStomCommandType.STOP)) {
                MultiStom.instance().spaceFactory().spaces().forEach(space -> {
                    suggestion.addEntry(new SuggestionEntry(space.name()));
                });
            } else if(context.get(typeArgument).equals(MultiStomCommandType.START)) {
                MultiStom.instance().templateFactory().templates().forEach(template -> {
                    suggestion.addEntry(new SuggestionEntry(template.configuration().name()));
                });
            }
        });
        addSyntax((sender, context) -> {
            if (sender instanceof Player player) {
                var type = context.get(typeArgument);
                var value = context.get(valueArgument);

                if (type.equals(MultiStomCommandType.START)) {
                    var templateOptional = MultiStom.instance().templateFactory().templates()
                            .stream()
                            .filter(it -> it.configuration().name().equalsIgnoreCase(value))
                            .findFirst();
                    if (templateOptional.isEmpty()) {
                        sender.sendMessage(MultiStomData.text("§cTemplate not found!"));
                        return;
                    }

                    var space = MultiStom.instance().spaceFactory().execute(templateOptional.get());
                    sender.sendMessage(MultiStomData.text("§7Running space with following name: §9" + space.name()));
                } else if (type.equals(MultiStomCommandType.STOP)) {
                    var spaceOptional = MultiStom.instance().spaceFactory().spaces().stream().filter(it -> it.name().equalsIgnoreCase(value)).findFirst();
                    if (spaceOptional.isEmpty()) {
                        sender.sendMessage(MultiStomData.text("§cSpace not found!"));
                        return;
                    }

                    var space = spaceOptional.get();
                    MultiStom.instance().spaceFactory().shutdown(space);
                    sender.sendMessage(MultiStomData.text("§7Stopping space with following name: §9" + space.name()));
                } else if (type.equals(MultiStomCommandType.CONNECT)) {
                    var spaceOptional = MultiStom.instance().spaceFactory().spaces().stream().filter(it -> it.name().equalsIgnoreCase(value)).findFirst();
                    if (spaceOptional.isEmpty()) {
                        sender.sendMessage(MultiStomData.text("§cSpace not found!"));
                        return;
                    }

                    var spaceInstance = spaceOptional.get();
                    sender.sendMessage(MultiStomData.text("§7Connecting to space: §9§l" + spaceInstance.name()));
                    if (spaceInstance.spawnInstance() == null) {
                        player.sendMessage(MultiStomData.text("§cNo spawn instance available!"));
                        return;
                    }
                    if (spaceInstance.spawnInstance() == player.getInstance()) {
                        player.sendMessage(MultiStomData.text("§cAlready connected to this space!"));
                        return;
                    }
                    player.setInstance(spaceInstance.spawnInstance());
                } else if (type.equals(MultiStomCommandType.SPACES)) {
                    sender.sendMessage(MultiStomData.text("§cUse: /multistom SPACES"));
                }
            }
        }, typeArgument, valueArgument);

        MinecraftServer.getCommandManager().register(this);
    }
}
