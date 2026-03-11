package net.kuko.starcc.registries;

import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.upgrades.UpgradeType;
import net.kuko.starcc.StarCC;
import net.kuko.starcc.computercraft.turtle.upgrades.StarcatcherRodTurtleUpgrade;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegisterEvent;

public class UpgradesRegistry {
    public static final UpgradeType<StarcatcherRodTurtleUpgrade> STARCATCHER_ROD_TURTLE_UPGRADE_UPGRADE = UpgradeType.simpleWithCustomItem(
            StarcatcherRodTurtleUpgrade::new
    );

    public static void register(RegisterEvent event) {
        event.register(
                ITurtleUpgrade.typeRegistry(),
                ResourceLocation.fromNamespaceAndPath(StarCC.MOD_ID, "starcatcher_rod_turtle_upgrade"),
                () -> UpgradesRegistry.STARCATCHER_ROD_TURTLE_UPGRADE_UPGRADE);


    }

}
