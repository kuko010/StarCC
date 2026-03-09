package net.kuko.starcc.registries;


import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser;
import net.kuko.starcc.StarCC;
import net.kuko.starcc.computercraft.upgrades.StarcatchersRodTurtleUpgrade;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

public class TurtleUpgradesRegistry {
/*    // We use .singleton to bind this upgrade specifically to a Diamond
    public static final UpgradeType<StarcatcherGuideTurtleUpgrade> STARCATCHER_GUIDE_TURTLE_UPGRADE_UPGRADE = UpgradeType.simpleWithCustomItem(
            StarcatcherGuideTurtleUpgrade::new
    );*/

    public static final TurtleUpgradeSerialiser<StarcatchersRodTurtleUpgrade> EXAMPLE_TURTLE_UPGRADE = TurtleUpgradeSerialiser.simpleWithCustomItem(
            StarcatchersRodTurtleUpgrade::new
    );

    public static void register(IEventBus modBus) {
        modBus.addListener((RegisterEvent event) -> {
            event.register(TurtleUpgradeSerialiser.registryId(), new ResourceLocation(StarCC.MOD_ID, "example_turtle_upgrade"), () -> EXAMPLE_TURTLE_UPGRADE);
        });
    }
}
