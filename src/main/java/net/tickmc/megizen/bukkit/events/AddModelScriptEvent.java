package net.tickmc.megizen.bukkit.events;

import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.ticxo.modelengine.api.events.AddModelEvent;
import net.tickmc.megizen.bukkit.objects.MegActiveModelTag;
import net.tickmc.megizen.bukkit.objects.MegModeledEntityTag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AddModelScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // meg adds <'model'>
    //
    // @Group Megizen
    //
    // @Cancellable true
    //
    // @Triggers when a model is about to be added to a <@link objecttype MegModeledEntityTag>.
    //
    // @Context
    // <context.active_model> returns the MegActiveModelTag of the model that is about to be added.
    // <context.modeled_entity> returns the MegModeledEntityTag of the entity that the model is about to be added to.
    //
    // -->

    public AddModelScriptEvent() {
        registerCouldMatcher("meg adds <'model'>");
    }

    AddModelEvent event;
    MegActiveModelTag activeModel;
    MegModeledEntityTag modeledEntity;

    @Override
    public boolean matches(ScriptPath path) {
        if (!path.eventArgLowerAt(2).equals("model") && !runGenericCheck(path.eventArgLowerAt(2), event.getModel().getBlueprint().getName())) {
            return false;
        }
        return super.matches(path);
    }

    @Override
    public ObjectTag getContext(String name) {
        return switch (name) {
            case "active_model" -> activeModel;
            case "modeled_entity" -> modeledEntity;
            default -> super.getContext(name);
        };
    }

    @EventHandler
    public void onModelAdded(AddModelEvent event) {
        this.event = event;
        activeModel = new MegActiveModelTag(event.getModel());
        modeledEntity = new MegModeledEntityTag(event.getTarget());
        fire(event);
    }
}
