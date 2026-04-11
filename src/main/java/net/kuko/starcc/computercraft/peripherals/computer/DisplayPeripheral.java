package net.kuko.starcc.computercraft.peripherals.computer;

import com.mojang.authlib.GameProfile;
import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.blocks.display.DisplayBlockEntity;
import com.wdiscute.starcatcher.io.FishCaughtCounter;
import com.wdiscute.starcatcher.io.SCDataComponents;
import com.wdiscute.starcatcher.registry.FishProperties;
import com.wdiscute.starcatcher.registry.SignedGuide;
import com.wdiscute.starcatcher.registry.fishrestrictions.AbstractFishRestriction;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

import static com.wdiscute.starcatcher.registry.fishrestrictions.AbstractFishRestriction.Context.GUIDE_ENTRY;

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
    public Object[] getOwner() {
        SignedGuide guide = getGuide();
        if (guide == null) return new Object[]{false, "No signed guide"};
        assert displayBlock.getLevel() != null;
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

        return new Object[]{true, getData(advanced)};
    }



    @LuaFunction(mainThread = true)
    public Object[] getFish(String fish, Optional<Boolean> withAdvanced) {
        boolean advanced = withAdvanced.orElse(false);
        SignedGuide guide = getGuide();
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
            fishMap.put("fishData", putStatsAdvanced(data, fishLoc)); // was: putAdvancedStats
        } else {
            fishMap.put("fishData", putStats(data, fishLoc));
        }
        return new Object[]{true, fishMap};
    }

    private Map<String, Object> getData(boolean advanced) {
        SignedGuide guide = getGuide();

        if (guide == null) return Map.of();

        Map<String, Object> out = new LinkedHashMap<>();

        out.put("owner", guide.owner().toString());
        out.put("signature", guide.signature());
        out.put("date", guide.date());

        Map<String, Object> fishes = new LinkedHashMap<>();
        for (var entry : guide.fishesCaught().entrySet()) {
            Map<String, Object> fMap;
            if (advanced) {
                fMap = putStatsAdvanced(entry.getValue(), entry.getKey());
            } else {
                fMap = putStats(entry.getValue(), entry.getKey());
            }

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

    private @NonNull Map<String, Object> putStats(FishCaughtCounter entry, ResourceLocation fish) {
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

    private @NonNull Map<String, Object> putStatsAdvanced(FishCaughtCounter entry, ResourceLocation fish) {
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

        fMap.put("advancedStats", putAdvancedStats(entry, fish));

        return fMap;
    }

    private @NonNull Map<String, Object> putAdvancedStats(FishCaughtCounter entry, ResourceLocation fish) {
        Map<String, Object> fMap = new LinkedHashMap<>();
        Level level = displayBlock.getLevel();
        if (level == null) return fMap;

        SignedGuide guide = getGuide();
        if (guide == null) return fMap;

        FishProperties fishProperties = FishProperties.getFP(level, fish);
        if (fishProperties == null) return fMap;

        // Player may be offline – handle gracefully
        Player player = level.getServer().getPlayerList().getPlayer(guide.owner());

        // Star data (always included for now)
        FishProperties.Star star = fishProperties.star();
        if (star != FishProperties.Star.DEFAULT) {
            Map<String, Object> starMap = new LinkedHashMap<>();
            starMap.put("name", star.name());
            starMap.put("x", star.x());
            starMap.put("y", star.y());
            starMap.put("connections", star.connections());
            starMap.put("debugColor", star.debugColor());
            fMap.put("star", starMap);
        }

        // Rarity data
        FishProperties.Rarity rarity = fishProperties.rarity();
        Map<String, Object> rarityMap = new LinkedHashMap<>();
        rarityMap.put("name", rarity.getSerializedName());
        rarityMap.put("xp", rarity.getXp());
        fMap.put("rarity", rarityMap);

        // Restrictions – build a list of maps
        List<Map<String, Object>> restrictionsList = new ArrayList<>();
        for (AbstractFishRestriction restriction : fishProperties.restrictions()) {
            if (!restriction.isEnabled()) continue;

            Map<String, Object> restrictionMap = new LinkedHashMap<>();
            // Description
            Component desc = restriction.getDescription(level, fishProperties, player, GUIDE_ENTRY);
            restrictionMap.put("description", desc.getString());

            // Hover texts
            List<Component> hoverComponents = restriction.getHover(level, fishProperties, player, GUIDE_ENTRY);
            List<String> hoverStrs = new ArrayList<>();
            for (Component c : hoverComponents) hoverStrs.add(c.getString());
            restrictionMap.put("hover", hoverStrs);

            // Blacklist texts
            List<Component> blacklistComponents = restriction.getBlacklist(level, fishProperties, player, GUIDE_ENTRY);
            List<String> blacklistStrs = new ArrayList<>();
            for (Component c : blacklistComponents) blacklistStrs.add(c.getString());
            restrictionMap.put("blacklist", blacklistStrs);

            restrictionsList.add(restrictionMap);
        }
        fMap.put("restrictions", restrictionsList);

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
