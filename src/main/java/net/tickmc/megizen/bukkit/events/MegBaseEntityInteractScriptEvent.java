package net.tickmc.megizen.bukkit.events;

import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.ticxo.modelengine.api.events.BaseEntityInteractEvent;
import net.tickmc.megizen.bukkit.objects.MegActiveModelTag;
import net.tickmc.megizen.bukkit.objects.MegModeledEntityTag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MegBaseEntityInteractScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // meg player interacts with model
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
    // <context.active_model> returns the MegActiveModelTag of the entity.
    // <context.click_position> returns a LocationTag of the click position (as a world-less vector, relative to the entity's center), if any.
    // <context.action> returns interaction cause. Can be: ATTACK, INTERACT or INTERACT_ON.
    //
    // @Player Always.
    //
    // -->

    public MegBaseEntityInteractScriptEvent() {
        registerCouldMatcher("meg player interacts with model");
    }

    public BaseEntityInteractEvent event;

    @Override
    public boolean matches(ScriptPath path) {
        if (!runInCheck(path, event.getPlayer().getLocation())) {
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
            case "active_model" -> new MegActiveModelTag(event.getModel());
            case "slot" -> new ElementTag(event.getSlot().name());
            case "item" -> new ItemTag(event.getItem());
            case "is_secondary" -> new ElementTag(event.isSecondary());
            case "click_position" -> event.getClickedPosition() != null ? new LocationTag(event.getClickedPosition()) : null;
            case "action" -> new ElementTag(event.getAction().name(), true);
            default -> super.getContext(name);
        };
    }

    @EventHandler
    public void onBaseEntityInteractEvent(BaseEntityInteractEvent event) {
        this.event = event;
        fire(event);
    }
}
