package dev.flxwdns.multistom;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

@UtilityClass
public final class MultiStomData {

    public Component text(String text) {
        return MiniMessage.miniMessage().deserialize("<dark_gray>[</dark_gray><gradient:#9863E7:#636CE7>ᴍᴜʟᴛɪꜱᴛᴏᴍ</gradient><dark_gray>]</dark_gray>").append(Component.text("§7 " + text));
    }
}
