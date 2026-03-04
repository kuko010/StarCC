package net.kuko.starcc.computercraft.upgrades;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.api.upgrades.UpgradeType;
import net.kuko.starcc.StarCC;
import net.kuko.starcc.computercraft.peripherals.ExamplePeripheral;
import net.kuko.starcc.registries.TurtleUpgradesRegistry;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class ExampleTurtleUpgrade extends AbstractTurtleUpgrade {
    public ExampleTurtleUpgrade(ItemStack stack) {
        super(TurtleUpgradeType.PERIPHERAL, "example", stack);
    }

    @Override
    public UpgradeType<ExampleTurtleUpgrade> getType() {
        return TurtleUpgradesRegistry.EXAMPLE_TURTLE_UPGRADE;
    }

    @Override
    public @Nullable IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
        return new ExamplePeripheral(turtle);
    }
}

