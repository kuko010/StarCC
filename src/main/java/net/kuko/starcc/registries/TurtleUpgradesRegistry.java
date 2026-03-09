package net.kuko.starcc.registries;


import net.minecraftforge.registries.RegisterEvent;

public class TurtleUpgradesRegistry {
/*    // We use .singleton to bind this upgrade specifically to a Diamond
    public static final UpgradeType<StarcatcherGuideTurtleUpgrade> STARCATCHER_GUIDE_TURTLE_UPGRADE_UPGRADE = UpgradeType.simpleWithCustomItem(
            StarcatcherGuideTurtleUpgrade::new
    );*/

    public static void register(RegisterEvent event) {
     /*   event.register(
                ITurtleUpgrade.typeRegistry(),
                ResourceLocation.fromNamespaceAndPath(StarCC.MOD_ID, "starcatcher_guide"),
                () -> TurtleUpgradesRegistry.STARCATCHER_GUIDE_TURTLE_UPGRADE_UPGRADE
        );*/
    }
}
