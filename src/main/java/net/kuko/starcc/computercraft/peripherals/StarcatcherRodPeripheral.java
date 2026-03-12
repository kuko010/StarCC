package net.kuko.starcc.computercraft.peripherals;

import com.wdiscute.starcatcher.registry.ModItems;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class StarcatcherRodPeripheral implements IPeripheral {
    private static ItemStack getItem() {
        return new ItemStack(ModItems.ROD.get());
    }

    /**
     * Should return a string that uniquely identifies this type of peripheral.
     * This can be queried from lua by calling {@code peripheral.getType()}
     *
     * @return A string identifying the type of peripheral.
     * @see PeripheralType#getPrimaryType()
     */
    @Override
    public String getType() {
        return "starcatcher_rod";
    }


    /**
     * Determine whether this peripheral is equivalent to another one.
     * <p>
     * The minimal example should at least check whether they are the same object. However, you may wish to check if
     * they point to the same block or block entity.
     *
     * @param other The peripheral to compare against. This may be {@code null}.
     * @return Whether these peripherals are equivalent.
     */
    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return false;
    }
}
