package net.nerdorg.vortexmod.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class LogUtil {
    private static Component prefix = Component.literal("NerdOrg").withStyle(ChatFormatting.GREEN).append(Component.literal("(VortexMod) ").withStyle(ChatFormatting.LIGHT_PURPLE)).append(Component.literal("-> ").withStyle(ChatFormatting.DARK_GRAY));

    public static void doSuccessMessage(Player player, String message) {
        player.displayClientMessage(prefix.copy().append(Component.literal(message).withStyle(ChatFormatting.GOLD)), false);
    }

    public static void doFailureMessage(Player player, String message) {
        player.displayClientMessage(prefix.copy().append(Component.literal(message).withStyle(ChatFormatting.RED)), false);
    }
}
