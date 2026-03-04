package net.kuko.starcc.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import net.kuko.starcc.event.ServerEvents;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ExamplePeripheral implements IPeripheral {
    private final ITurtleAccess turtle;

    public ExamplePeripheral(ITurtleAccess turtle) {
        this.turtle = turtle;
    }

    @Override
    public String getType() {
        return "example"; // This is what peripheral.getType() returns in Lua
    }

    // Add your Lua-callable methods here with @LuaFunction
    @LuaFunction
    public final String hello() {
        return "Hello from StarCC!";
    }

    @LuaFunction(mainThread = true) // needs main thread to access world/inventory
    public final String getStackDescription(int slot) {
        // Turtle slots are 1-16 in Lua, but 0-15 in Java
        ItemStack stack = turtle.getInventory().getItem(slot-1);
        if (stack.isEmpty()) return "Empty slot";
        return Objects.requireNonNull(
                ServerEvents.getTranslationsAsString(stack)
        ).toString();
    }


    @LuaFunction
    public final int add(int a, int b) {
        return a + b;
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return this == other;
    }
}