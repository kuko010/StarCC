package net.kuko.starcc.computercraft.turtle.upgrades;

import com.wdiscute.starcatcher.Starcatcher;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.*;
import dan200.computercraft.api.upgrades.UpgradeType;
import net.kuko.starcc.computercraft.peripherals.StarcatcherRodPeripheral;
import net.kuko.starcc.registries.UpgradesRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class StarcatcherRodTurtleUpgrade extends AbstractTurtleUpgrade {
    public StarcatcherRodTurtleUpgrade(ItemStack stack) {
        super(TurtleUpgradeType.PERIPHERAL, Component.translatable("upgrade.starcc.starcatcher_rod.adjective"), stack);
    }

    /**
     * Get the type of this upgrade.
     *
     * @return The type of this upgrade.
     */
    @Override
    public UpgradeType<? extends ITurtleUpgrade> getType() {
        return UpgradesRegistry.STARCATCHER_ROD_TURTLE_UPGRADE_UPGRADE;
    }
    @Override
    public @Nullable IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
        return new StarcatcherRodPeripheral();
    }
}
