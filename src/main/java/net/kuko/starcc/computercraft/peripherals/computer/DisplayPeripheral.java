package net.kuko.starcc.computercraft.peripherals.computer;

import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.blocks.display.DisplayBlockEntity;
import com.wdiscute.starcatcher.io.FishCaughtCounter;
import com.wdiscute.starcatcher.io.SCDataComponents;
import com.wdiscute.starcatcher.registry.SignedGuide;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class DisplayPeripheral implements IPeripheral {
    private final DisplayBlockEntity displayBlock;

    public DisplayPeripheral(DisplayBlockEntity displayBlock) {
        this.displayBlock = displayBlock;
    }

    @LuaFunction(mainThread = true)
    public Object[] getOwner() {
        SignedGuide guide = getGuide();
        if (guide == null) return new Object[]{false, "No signed guide"};
        GameProfile profile = displayBlock.getLevel().getServer().getProfileCache().get(guide.owner()).orElse(null);
        if (profile != null) {
            return new Object[]{true, guide.owner().toString(), profile.getName()};
        } else {
            return new Object[]{true, guide.owner().toString()};
        }
    }

    @LuaFunction(mainThread = true)
    public Object[] getFishList() {
        SignedGuide guide = getGuide();
        if (guide == null) return new Object[]{false, "No signed guide"};
        List<String> fishNames = guide.fishesCaught().keySet().stream()
                .map(ResourceLocation::toString)
                .toList();
        return new Object[]{true, fishNames};
    }

    @LuaFunction(mainThread = true)
    public Object[] hasFish(String fish) {
        SignedGuide guide = getGuide();
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
        SignedGuide guide = getGuide();
        if (guide == null) return new Object[]{false, "No signed guide"};
        return new Object[]{true, guide.fishesCaught().size()};
    }

    @LuaFunction(mainThread = true)
    public Object[] getRawData() {
        ItemStack stack = displayBlock.getItem();
        if (stack.isEmpty()) {
            return new Object[]{false, "Display has no item"};
        }

        SignedGuide guide = SCDataComponents.get(stack, SCDataComponents.SIGNED_GUIDE);
        if (guide == null) {
            return new Object[]{false, "Item in display is not a signed guide"};
        }

        return new Object[]{true, getData()};
    }

    @LuaFunction(mainThread = true)
    public Object[] getFish(String fish) {
        ItemStack stack = displayBlock.getItem();
        SignedGuide guide = SCDataComponents.get(stack, SCDataComponents.SIGNED_GUIDE);
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
        fishMap.put("fish", fishLoc);
        fishMap.put("fishData", putStats(data)); // Or a more detailed map from data
        return new Object[]{true, fishMap};
    }

    private Map<String, Object> getData() {
        ItemStack stack = displayBlock.getItem();
        SignedGuide guide = SCDataComponents.get(stack, SCDataComponents.SIGNED_GUIDE);

        if (guide == null) return Map.of();

        Map<String, Object> out = new LinkedHashMap<>();

        out.put("owner", guide.owner().toString());
        out.put("signature", guide.signature());
        out.put("date", guide.date());

        Map<String, Object> fishes = new LinkedHashMap<>();
        for (var entry : guide.fishesCaught().entrySet()) {
            Map<String, Object> fMap = putStats(entry.getValue());

            fishes.put(entry.getKey().toString(), fMap);
        }
        out.put("fishesCaught", fishes);
        return out;
    }

    private SignedGuide getGuide() {
        ItemStack stack = displayBlock.getItem();
        if (stack.isEmpty()) return null;
        return SCDataComponents.get(stack, SCDataComponents.SIGNED_GUIDE);
    }

    private static @NonNull Map<String, Object> putStats(FishCaughtCounter entry) {
        Map<String, Object> fMap = new LinkedHashMap<>();

        fMap.put("count", entry.count());
        fMap.put("fastestTicks", entry.fastestTicks());
        fMap.put("averageTicks", entry.averageTicks());
        fMap.put("size", entry.size());
        fMap.put("weight", entry.weight());
        fMap.put("percentile", entry.percentile());
        fMap.put("firstCatch", entry.firstCatch());
        fMap.put("caughtGolden", entry.caughtGolden());
        fMap.put("perfectCatch", entry.perfectCatch());
        fMap.put("hasGuideNotification", entry.hasGuideNotification());
        return fMap;
    }

    @Override
    public String getType() {
        return "sc_display";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other instanceof DisplayPeripheral that && this.displayBlock == that.displayBlock;
    }
}
