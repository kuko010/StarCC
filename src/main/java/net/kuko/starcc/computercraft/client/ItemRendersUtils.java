package net.kuko.starcc.computercraft.client;

import com.mojang.math.Transformation;
import dan200.computercraft.api.client.TransformedModel;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ItemRendersUtils {


    public static TransformedModel itemRender(ITurtleUpgrade upgrade, ITurtleAccess turtle, TurtleSide side, DataComponentPatch data) {
        // 1. Get the baked model for the upgrade's item
        ItemStack stack = upgrade.getCraftingItem();
        BakedModel model = Minecraft.getInstance()
                .getItemRenderer()
                .getModel(stack, null, null, 0);

        // 2. Translation: move model to the correct side (X axis)
        //    Typical offset: left side = -0.4f, right side = +0.4f
        float xOffset = 0f;
        xOffset = side == TurtleSide.LEFT ? -0.4f : 0.4f;
        Vector3f translation = new Vector3f(xOffset, 0.0f,  1f); // also nudge forward a bit (Z)

        // 3. Rotation: make the item lie flat against the turtle side and face outward
        //    Most upgrades need: rotateX 90° (lay flat) then rotateY 180° (face outward)
        Quaternionf rotation = new Quaternionf()
                .rotateY((float) Math.toRadians(90));   // or -90 for the opposite direction


        // 4. Build the transformation (rightRotation = null is fine)
        Transformation transform = new Transformation(
                translation,
                rotation,
                null,   // scale (null = 1)
                null    // right rotation (unused)
        );

        // 5. Return the transformed model
        return new TransformedModel(model, transform);
    }
}
