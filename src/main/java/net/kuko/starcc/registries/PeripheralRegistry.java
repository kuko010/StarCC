package net.kuko.starcc.registries;

import com.wdiscute.starcatcher.registry.ModItems;
import com.wdiscute.starcatcher.rod.StarcatcherFishingRodItem;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import net.kuko.starcc.StarCC;
import net.kuko.starcc.computercraft.peripherals.StarcatcherRodPeripheral;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

public class PeripheralRegistry {

    public static void register(RegisterCapabilitiesEvent event) {
       // event.registerBlockEntity(PeripheralCapability.get(), BlockEntityType.BREWING_STAND, (b, d) -> new BrewingStandPeripheral(b));


    }

}
