package net.tickmc.megizen.bukkit.events;

import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.ticxo.modelengine.api.events.RemoveModelEvent;
import net.tickmc.megizen.bukkit.objects.MegActiveModelTag;
import net.tickmc.megizen.bukkit.objects.MegModeledEntityTag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RemoveModelScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // meg removes <'model'> (from <'entity'>)
    //
    // @Group Megizen
    //
    // @Cancellable true
    //
    // @Triggers when a model is about to be removed from a <@link objecttype MegModeledEntityTag>.
    //
    // @Context
    // <context.active_model> returns a MegActiveModelTag of the model that is about to be removed.
    // <context.modeled_entity> returns a MegModeledEntityTag of the entity that the model is about to be removed from.
    //
    // -->

    public RemoveModelScriptEvent() {
        registerCouldMatcher("meg removes <'model'> (from <'entity'>)");
    }

    RemoveModelEvent event;
    MegActiveModelTag activeModel;
    MegModeledEntityTag modeledEntity;

    @Override
    public boolean matches(ScriptPath path) {
        if (!path.eventArgLowerAt(2).equals("model") && !runGenericCheck(path.eventArgLowerAt(2), event.getModel().toString())) {
            return false;
        }
        if (path.eventArgLowerAt(3).equals("from") && !runGenericCheck(path.eventArgLowerAt(4), event.getTarget().toString())) {
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
    public void onModelRemoved(RemoveModelEvent event) {
        this.event = event;
        activeModel = new MegActiveModelTag(event.getModel());
        modeledEntity = new MegModeledEntityTag(event.getTarget());
        fire(event);
    }
}