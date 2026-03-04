package net.kuko.starcc.registries;

import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.upgrades.UpgradeType;
import net.kuko.starcc.StarCC;
import net.kuko.starcc.computercraft.upgrades.ExampleTurtleUpgrade;
import net.kuko.starcc.computercraft.upgrades.StarcatcherGuideTurtleUpgrade;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.RegisterEvent;

public class TurtleUpgradesRegistry {
    // We use .singleton to bind this upgrade specifically to a Diamond
    public static final UpgradeType<ExampleTurtleUpgrade> EXAMPLE_TURTLE_UPGRADE = UpgradeType.simpleWithCustomItem(
            ExampleTurtleUpgrade::new
    );

    public static final UpgradeType<StarcatcherGuideTurtleUpgrade> STARCATCHER_GUIDE_TURTLE_UPGRADE_UPGRADE = UpgradeType.simpleWithCustomItem(
            StarcatcherGuideTurtleUpgrade::new
    );

    public static void register(RegisterEvent event) {
        event.register(
                ITurtleUpgrade.typeRegistry(),
                ResourceLocation.fromNamespaceAndPath(StarCC.MOD_ID, "example_turtle_upgrade"),
                () -> TurtleUpgradesRegistry.EXAMPLE_TURTLE_UPGRADE
        );
        event.register(
                ITurtleUpgrade.typeRegistry(),
                ResourceLocation.fromNamespaceAndPath(StarCC.MOD_ID, "starcatcher_guide"),
                () -> TurtleUpgradesRegistry.STARCATCHER_GUIDE_TURTLE_UPGRADE_UPGRADE
        );
    }
}
