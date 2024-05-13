package net.tickmc.megizen.bukkit.events;

import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.ticxo.modelengine.api.events.BaseEntityInteractEvent;
import net.tickmc.megizen.bukkit.objects.MegActiveModelTag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BaseEntityInteractScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // meg player interacts with <'model'>
    //
    // @Group Player
    //
    // @Location true
    //
    // @Warning This event may in some cases double-fire, requiring usage of the 'ratelimit' command (like 'ratelimit <player> 1t') to prevent doubling firing.
    //
    // @Triggers when a player interacts with ModelEngine model.
    //
    // @Context
    // <context.action> returns a ElementTag of the interaction cause. Can be: ATTACK, INTERACT or INTERACT_ON.
    // <context.active_model> returns a MegActiveModelTag of the model.
    // <context.click_position> returns a LocationTag of the click position (as a world-less vector, relative to the model's center), if any.
    // <context.item> returns the ItemTag of the item used to interact with the model.
    // <context.is_secondary> returns a ElementTag(Boolean) of whether the player was crouching when interacting with the model.
    //
    // @Player Always.
    //
    // -->

    public BaseEntityInteractScriptEvent() {
        registerCouldMatcher("meg player interacts with <'model'>");
    }

    BaseEntityInteractEvent event;
    MegActiveModelTag activeModel;
    LocationTag clickedPosition;
    ItemTag item;

    @Override
    public boolean matches(ScriptPath path) {
        if (!runInCheck(path, event.getPlayer().getLocation())) {
            return false;
        }
        if (!path.eventArgLowerAt(4).equals("model") && !runGenericCheck(path.eventArgLowerAt(4), event.getModel().getBlueprint().getName())) {
            return false;
        }
        return super.matches(path);
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(event.getPlayer());
    }

    @Override
    public ObjectTag getContext(String name) {
        return switch (name) {
            case "action" -> new ElementTag(event.getAction().name());
            case "active_model" -> activeModel;
            case "click_position" -> clickedPosition;
            case "item" -> item;
            case "is_secondary" -> new ElementTag(event.isSecondary());
            case "slot" -> new ElementTag(event.getSlot().name());
            default -> super.getContext(name);
        };
    }

    @EventHandler
    public void onBaseEntityInteractEvent(BaseEntityInteractEvent event) {
        this.event = event;
        activeModel = new MegActiveModelTag(event.getModel());
        clickedPosition = event.getClickedPosition() != null ? new LocationTag(event.getClickedPosition()) : null;
        item = new ItemTag(event.getItem());
        fire(event);
    }
}
