package net.kuko.starcc.computercraft.peripherals.computer;

import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.blocks.display.DisplayBlockEntity;
import com.wdiscute.starcatcher.io.FishCaughtCounter;
import com.wdiscute.starcatcher.io.SCDataComponents;
import com.wdiscute.starcatcher.registry.SignedGuide;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.*;

import static net.kuko.starcc.computercraft.peripherals.computer.DisplayPeripheralUtils.*;

public class DisplayPeripheral implements IPeripheral {
    private final DisplayBlockEntity displayBlock;
    private IComputerAccess computer;  // store the computer reference

    public DisplayPeripheral(DisplayBlockEntity displayBlock) {
        this.displayBlock = displayBlock;
    }

    @Override
    public void attach(IComputerAccess computer) {
        this.computer = computer;
    }

    @Override
    public void detach(IComputerAccess computer) {
        if (this.computer == computer) {
            this.computer = null;
        }
    }

    @LuaFunction(mainThread = true)
    public Map<String, Object> getBookInfo() {
        SignedGuide guide = getGuide(displayBlock);

        // Return a simple error map if no guide exists
        if (guide == null) {
            return Map.of("success", false, "error", "No signed guide");
        }

        Map<String, Object> info = new HashMap<>();
        info.put("success", true);
        info.put("signature", guide.signature());
        info.put("date", guide.date());

        // Handle the Profile/Owner logic
        var profileCache = displayBlock.getLevel().getServer().getProfileCache();
        var profile = profileCache.get(guide.owner()).orElse(null);

        if (profile != null) {
            // Lua will see this as a list/table: { uuid, name }
            info.put("owner", List.of(guide.owner().toString(), profile.getName()));
        } else {
            info.put("owner", List.of(guide.owner().toString()));
        }

        return info;
    }

    //region QoL slop
    @LuaFunction(mainThread = true)
    public Object getOwner() {
        Map<String, Object> info = getBookInfo();
        // Simply return the owner part of the info map
        return info.getOrDefault("owner", null);
    }

    @LuaFunction(mainThread = true)
    public Object getDate() {
        Map<String, Object> info = getBookInfo();
        // Simply return the owner part of the info map
        return info.getOrDefault("date", null);
    }

    @LuaFunction(mainThread = true)
    public Object getSignature() {
        Map<String, Object> info = getBookInfo();
        // Simply return the owner part of the info map
        return info.getOrDefault("signature", null);
    }
    //endregion

    //region Fish Related
    @LuaFunction(mainThread = true)
    public Object[] getFishList() {
        SignedGuide guide = getGuide(displayBlock);
        if (guide == null) return new Object[]{false, "No signed guide"};
        List<String> fishNames = guide.fishesCaught().keySet().stream()
                .map(ResourceLocation::toString)
                .toList();
        return new Object[]{true, fishNames};
    }

    @LuaFunction(mainThread = true)
    public Object[] hasFish(String fish) {
        SignedGuide guide = getGuide(displayBlock);
        if (guide == null) return new Object[]{false, "No signed guide"};
        ResourceLocation fishLoc;
        if (fish.contains(":")) {
            fishLoc = ResourceLocation.tryParse(fish);
        } else {
            fishLoc = ResourceLocation.fromNamespaceAndPath(Starcatcher.MOD_ID, fish);
        }
        if (fishLoc == null) return new Object[]{false, "Invalid fish ID"};
        return new Object[]{true, guide.fishesCaught().containsKey(fishLoc)};
    }

    @LuaFunction(mainThread = true)
    public Object[] getFishCount() {
        SignedGuide guide = getGuide(displayBlock);
        if (guide == null) return new Object[]{false, "No signed guide"};
        return new Object[]{true, guide.fishesCaught().size()};
    }

    @LuaFunction(mainThread = true)
    public Object[] getRawData(Optional<Boolean> withAdvanced) {
        boolean advanced = withAdvanced.orElse(false);
        ItemStack stack = displayBlock.getItem();
        if (stack.isEmpty()) {
            return new Object[]{false, "Display has no item"};
        }

        SignedGuide guide = SCDataComponents.get(stack, SCDataComponents.SIGNED_GUIDE);
        if (guide == null) {
            return new Object[]{false, "Item in display is not a signed guide"};
        }

        return new Object[]{true, getData(advanced, displayBlock)};
    }

    @LuaFunction(mainThread = true)
    public Object[] getFish(String fish, Optional<Boolean> withAdvanced) {
        boolean advanced = withAdvanced.orElse(false);
        SignedGuide guide = getGuide(displayBlock);
        if (guide == null) {
            return new Object[]{false, "No signed guide in display"};
        }

        ResourceLocation fishLoc = null;
        if (fish.contains(":")) {
            fishLoc = ResourceLocation.tryParse(fish);
        } else {
            fishLoc = ResourceLocation.fromNamespaceAndPath(Starcatcher.MOD_ID, fish);
        }
        if (fishLoc == null) {
            return new Object[]{false, "Invalid fish ID: " + fishLoc};
        }
        FishCaughtCounter data = guide.fishesCaught().get(fishLoc);
        if (data == null) {
            return new Object[]{false, "Fish not found: " + fishLoc};
        }

        // Success: return true and the fish data map
        Map<String, Object> fishMap = new LinkedHashMap<>();
        fishMap.put("fish", fishLoc.toString());  // was: fishLoc
        if (advanced) {
            fishMap.put("fishData", putStatsAdvanced(data, fishLoc, displayBlock, getGuide(displayBlock))); // was: putAdvancedStats
        } else {
            fishMap.put("fishData", putStats(data, fishLoc));
        }
        return new Object[]{true, fishMap};
    }

    //endregion


    @Override
    public String getType() {
        return "sc_display";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other instanceof DisplayPeripheral that && this.displayBlock == that.displayBlock;
    }
}
