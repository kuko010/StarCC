package net.kuko.starcc.event;

import net.kuko.starcc.StarCC;
import net.minecraft.locale.Language;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


public class ServerEvents {
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (player.isCrouching()) {
            if (!player.level().isClientSide) {
                if (player.tickCount % 20 != 0) return;
                //region Getting the libtooltips translation values from stack on server

                //endregion
            }
        }
    }


    public static StringBuilder getTranslationsAsString(ItemStack stack) {
        boolean logging = false;
        if (stack.isEmpty()) return null;

        String id = stack.getItem().getDescriptionId();
        Language lang = Language.getInstance();
        String tooltipBase = id.replace("item.", "tooltip.");

        StringBuilder description = new StringBuilder();
        int i = 0;
        while (lang.has(tooltipBase + "." + i)) {
            description.append(lang.getOrDefault(tooltipBase + "." + i).replaceAll("<[^>]+>", "")).append("\n");
            i++;
        }

        if (!description.isEmpty()) return description;
        return null;
    }
}
