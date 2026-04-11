package net.kuko.starcc.computercraft.peripherals.computer;

import com.wdiscute.starcatcher.blocks.display.DisplayBlockEntity;
import com.wdiscute.starcatcher.io.FishCaughtCounter;
import com.wdiscute.starcatcher.io.SCDataComponents;
import com.wdiscute.starcatcher.registry.FishProperties;
import com.wdiscute.starcatcher.registry.SignedGuide;
import com.wdiscute.starcatcher.registry.fishrestrictions.AbstractFishRestriction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.wdiscute.starcatcher.registry.fishrestrictions.AbstractFishRestriction.Context.GUIDE_ENTRY;

public class DisplayPeripheralUtils {
    protected static @NonNull Map<String, Object> putStats(FishCaughtCounter entry, ResourceLocation fish) {
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

    protected static @NonNull Map<String, Object> putStatsAdvanced(FishCaughtCounter entry,
                                                                   ResourceLocation fish,
                                                                   DisplayBlockEntity displayBlock,
                                                                   SignedGuide guide) {
        Map<String, Object> fMap = putStats(entry, fish);
        fMap.put("advancedStats", putAdvancedStats(entry, fish, displayBlock, guide));
        return fMap;
    }

    protected static @NonNull Map<String, Object> putAdvancedStats(FishCaughtCounter entry,
                                                                   ResourceLocation fish,
                                                                   DisplayBlockEntity displayBlock,
                                                                   SignedGuide guide
    ) {
        Map<String, Object> fMap = new LinkedHashMap<>();
        Level level = displayBlock.getLevel();
        if (level == null) return fMap;
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

    public static Map<String, Object> getData(boolean advanced, DisplayBlockEntity displayBlock) {
        SignedGuide guide = getGuide(displayBlock);
        if (guide == null) return Map.of();

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("owner", guide.owner().toString());
        out.put("signature", guide.signature());
        out.put("date", guide.date());

        Map<String, Object> fishes = new LinkedHashMap<>();
        for (var entry : guide.fishesCaught().entrySet()) {
            Map<String, Object> fMap;
            if (advanced) {
                fMap = putStatsAdvanced(entry.getValue(), entry.getKey(), displayBlock, guide);
            } else {
                fMap = putStats(entry.getValue(), entry.getKey());
            }
            fishes.put(entry.getKey().toString(), fMap);
        }
        out.put("fishesCaught", fishes);
        return out;
    }

    public static SignedGuide getGuide(DisplayBlockEntity displayBlock) {
        ItemStack stack = displayBlock.getItem();
        if (stack.isEmpty()) return null;
        return SCDataComponents.get(stack, SCDataComponents.SIGNED_GUIDE);
    }
}
