package net.kuko.starcc;

import dan200.computercraft.api.client.turtle.RegisterTurtleModellersEvent;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = StarCC.MOD_ID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = StarCC.MOD_ID, value = Dist.CLIENT)
public class StarCCClient {
    public StarCCClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
    }

    @SubscribeEvent
    public static void registerTurtleModellers(RegisterTurtleModellersEvent event) {
        //event.register(TurtleUpgradesRegistry.STARCATCHER_GUIDE_TURTLE_UPGRADE_UPGRADE, (ItemRendersUtils::itemRender));
    }
    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        StarCC.LOGGER.info("HELLO FROM CLIENT SETUP");
        StarCC.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }
}
