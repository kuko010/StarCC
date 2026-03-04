package net.kuko.starcc.computercraft.upgrades;

import dan200.computercraft.api.turtle.AbstractTurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.api.upgrades.UpgradeType;
import net.kuko.starcc.StarCC;
import net.kuko.starcc.registries.TurtleUpgradesRegistry;
import net.minecraft.world.item.ItemStack;

public class ExampleTurtleUpgrade extends AbstractTurtleUpgrade {
    public ExampleTurtleUpgrade(ItemStack stack) {
        super(TurtleUpgradeType.PERIPHERAL, "example", stack);
    }

    @Override
    public UpgradeType<ExampleTurtleUpgrade> getType() {
        return TurtleUpgradesRegistry.EXAMPLE_TURTLE_UPGRADE;
    }
}

