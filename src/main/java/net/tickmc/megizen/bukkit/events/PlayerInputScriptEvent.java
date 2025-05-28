package net.tickmc.megizen.bukkit.events;

import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.core.MapTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import org.bukkit.Input;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInputEvent;

public class PlayerInputScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // player inputs
    //
    // @Group Block
    //
    // @Switch input:<input> to only process the event if the input type matches. Valid inputs: jump, forward, backward, sprint, sneak, left, right.
    //
    // @Triggers when a player sends an updated input to the server.
    //
    // @Context
    // <context.jump> returns an ElementTag(Boolean) of whether the player is jumping.
    // <context.forward> returns an ElementTag(Boolean) of whether the player moves forward.
    // <context.backward> returns an ElementTag(Boolean) of whether the player moves backward.
    // <context.sprint> returns an ElementTag(Boolean) of whether the player sprints.
    // <context.sneak> returns an ElementTag(Boolean) of whether the player sneaks.
    // <context.left> returns an ElementTag(Boolean) of whether the player moves left.
    // <context.right> returns an ElementTag(Boolean) of whether the player moves right.
    // <context.input> returns a ListTag(MapTag) of booleans containing only the input inputs.
    // <context.inputs> returns a ListTag(MapTag) of booleans containing all inputs.
    //
    // -->

    public PlayerInputScriptEvent() {
        registerCouldMatcher("player inputs");
        registerSwitches("input");
    }

    public PlayerInputEvent event;

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(event.getPlayer());
    }

    @Override
    public boolean matches(ScriptPath path) {
        if (!runGenericSwitchCheck(path, "input", getInputType())) {
            return false;
        }
        return super.matches(path);
    }

    @Override
    public ObjectTag getContext(String name) {
        Input input = event.getInput();
        switch (name) {
            case "jump": return new ElementTag(input.isJump());
            case "forward": return new ElementTag(input.isForward());
            case "backward": return new ElementTag(input.isBackward());
            case "sprint": return new ElementTag(input.isSprint());
            case "sneak": return new ElementTag(input.isSneak());
            case "left": return new ElementTag(input.isLeft());
            case "right": return new ElementTag(input.isRight());
            case "input": {
                ListTag inputs = new ListTag();
                MapTag map = new MapTag();
                if (input.isJump()) map.putObject("jump", new ElementTag(true));
                if (input.isForward()) map.putObject("forward", new ElementTag(true));
                if (input.isBackward()) map.putObject("backward", new ElementTag(true));
                if (input.isSprint()) map.putObject("sprint", new ElementTag(true));
                if (input.isSneak()) map.putObject("sneak", new ElementTag(true));
                if (input.isLeft()) map.putObject("left", new ElementTag(true));
                if (input.isRight()) map.putObject("right", new ElementTag(true));
                if (!map.isEmpty()) {
                    inputs.addObject(map);
                }
                return inputs;
            }
            case "inputs": {
                ListTag inputs = new ListTag();
                MapTag map = new MapTag();
                map.putObject("jump", new ElementTag(input.isJump()));
                map.putObject("forward", new ElementTag(input.isForward()));
                map.putObject("backward", new ElementTag(input.isBackward()));
                map.putObject("sprint", new ElementTag(input.isSprint()));
                map.putObject("sneak", new ElementTag(input.isSneak()));
                map.putObject("left", new ElementTag(input.isLeft()));
                map.putObject("right", new ElementTag(input.isRight()));
                inputs.addObject(map);
                return inputs;
            }
        }
        return super.getContext(name);
    }

    private String getInputType() {
        Input input = event.getInput();
        if (input.isJump()) return "jump";
        if (input.isForward()) return "forward";
        if (input.isBackward()) return "backward";
        if (input.isSprint()) return "sprint";
        if (input.isSneak()) return "sneak";
        if (input.isLeft()) return "left";
        if (input.isRight()) return "right";
        return null;
    }

    @EventHandler
    public void onPlayerInputEvent(PlayerInputEvent event) {
        this.event = event;
        fire(event);
    }
}
