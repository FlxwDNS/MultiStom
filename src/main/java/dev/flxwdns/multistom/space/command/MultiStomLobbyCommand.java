package dev.flxwdns.multistom.space.command;

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

public final class MultiStomLobbyCommand extends Command {

    public MultiStomLobbyCommand() {
        super("lobby", "hub");

        setDefaultExecutor((sender, context) -> {
            if (!(sender instanceof Player player)) {
                return;
            }

            var lobbyInstance = MultiStom.instance().spaceFactory().findLobby(player);
            if (lobbyInstance == null) {
                return;
            }

            player.setInstance(lobbyInstance);
            player.sendMessage(MultiStomData.text("§7You have been send to the lobby!"));
        });

        MinecraftServer.getCommandManager().register(this);
    }
}
