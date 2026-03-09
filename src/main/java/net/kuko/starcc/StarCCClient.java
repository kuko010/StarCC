package net.kuko.starcc;

import dan200.computercraft.api.client.turtle.RegisterTurtleModellersEvent;
import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller;
import net.kuko.starcc.registries.TurtleUpgradesRegistry;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;


// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = StarCC.MOD_ID)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@Mod.EventBusSubscriber(modid = StarCC.MOD_ID, value = Dist.CLIENT)
public class StarCCClient {
    public StarCCClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
    }

    @SubscribeEvent
    public static void registerTurtleModellers(RegisterTurtleModellersEvent event) {
      //  event.register(TurtleUpgradesRegistry.STARCATCHER_GUIDE_TURTLE_UPGRADE_UPGRADE, TurtleUpgradeModeller.flatItem());
        event.register(TurtleUpgradesRegistry.EXAMPLE_TURTLE_UPGRADE, TurtleUpgradeModeller.flatItem());

    }
    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        StarCC.LOGGER.info("HELLO FROM CLIENT SETUP");
        StarCC.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }
}
