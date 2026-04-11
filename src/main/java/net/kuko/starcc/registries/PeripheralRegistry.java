package net.kuko.starcc.registries;

import com.wdiscute.starcatcher.blocks.SCBlockEntities;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import net.kuko.starcc.computercraft.peripherals.computer.DisplayPeripheral;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class PeripheralRegistry {

    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(PeripheralCapability.get(), SCBlockEntities.DISPLAY.get(),
                (b, d) -> new DisplayPeripheral(b));
    }

}
