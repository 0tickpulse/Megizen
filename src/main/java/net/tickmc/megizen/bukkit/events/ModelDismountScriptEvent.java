package net.tickmc.megizen.bukkit.events;

import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.ticxo.modelengine.api.events.ModelDismountEvent;
import net.tickmc.megizen.bukkit.objects.MegActiveModelTag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ModelDismountScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // meg <entity> dismounts <'model'>
    //
    // @Group Megizen
    //
    // @Cancellable true
    //
    // @Triggers when an entity is dismounting a model.
    //
    // @Context
    // <context.model> returns the MegActiveModelTag of the model that the entity is dismounting.
    // <context.entity> returns the EntityTag of the entity that is dismounting the model.
    // <context.was_driver> returns a ElementTag(Boolean) of whether the dismounting entity was the driver.
    //
    // -->

    public ModelDismountScriptEvent() {
        registerCouldMatcher("meg <entity> dismounts <'model'>");
    }

    ModelDismountEvent event;
    MegActiveModelTag model;
    EntityTag entity;

    @Override
    public boolean matches(ScriptPath path) {
        if (!path.tryArgObject(1, entity)) {
            return false;
        }
        if (!path.eventArgLowerAt(3).equals("model") && !runGenericCheck(path.eventArgLowerAt(3), event.getVehicle().getBlueprint().getName())) {
            return false;
        }
        return super.matches(path);
    }

    @Override
    public ObjectTag getContext(String name) {
        return switch (name) {
            case "model" -> model;
            case "entity" -> entity;
            case "was_driver" -> new ElementTag(event.isDriver());
            default -> super.getContext(name);
        };
    }

    @EventHandler
    public void onModelAdded(ModelDismountEvent event) {
        this.event = event;
        model = new MegActiveModelTag(event.getVehicle());
        entity = new EntityTag(event.getPassenger());
        fire(event);
    }
}
