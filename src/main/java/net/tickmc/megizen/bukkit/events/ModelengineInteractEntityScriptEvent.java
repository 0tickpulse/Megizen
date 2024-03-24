package net.tickmc.megizen.bukkit.events;

import com.denizenscript.denizen.events.BukkitScriptEvent;
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

public class ModelengineInteractEntityScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // modelengine player interact with model
    //
    // @Group Player
    //
    // @Location true
    //
    // @Warning this event may in some cases double-fire, requiring usage of the 'ratelimit' command (like 'ratelimit <player> 1t') to prevent doubling actions.
    //
    // @Triggers when a player interacts with modelengine entity.
    //
    // @Context
    // <context.active_model> returns the MegActiveModelTag of interacted entity.
    // <context.modeled_entity> returns the MegModeledEntityTag of interacted entity.
    // <context.click_position> returns a LocationTag of the click position (as a world-less vector, relative to the entity's center).
    // <context.action> returns a cause of the interaction. Can be: ATTACK, INTERACT or INTERACT_ON.
    //
    // @Player Always.
    //
    // -->

    public ModelengineInteractEntityScriptEvent() {
        registerCouldMatcher("modelengine player interact with model");
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
            case "modeled_entity" -> new MegModeledEntityTag(event.getModel().getModeledEntity());
            case "click_position" -> event.getClickedPosition() != null ? new LocationTag(event.getClickedPosition()) : null;
            case "action" -> new ElementTag(event.getAction());
            default -> super.getContext(name);
        };
    }

    @EventHandler
    public void onBaseEntityInteractEvent(BaseEntityInteractEvent event) {
        this.event = event;
        fire(event);
    }
}
