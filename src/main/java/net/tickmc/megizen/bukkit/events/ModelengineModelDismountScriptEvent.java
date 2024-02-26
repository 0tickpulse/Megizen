package net.tickmc.megizen.bukkit.events;

import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.ticxo.modelengine.api.events.ModelDismountEvent;
import net.tickmc.megizen.bukkit.objects.MegActiveModelTag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ModelengineModelDismountScriptEvent extends BukkitScriptEvent implements Listener {

    public ModelengineModelDismountScriptEvent() {
        registerCouldMatcher("modelengine model dismount");
    }

    public ModelDismountEvent event;

    @Override
    public ObjectTag getContext(String name) {
        return switch (name) {
            case "vehicle" -> new MegActiveModelTag(event.getVehicle());
            case "is_driver" -> new ElementTag(event.isDriver());
            case "passenger" -> new EntityTag(event.getPassenger());
            default -> super.getContext(name);
        };
    }

    @EventHandler
    public void onModelDismountEvent(ModelDismountEvent event) {
        this.event = event;
        fire(event);
    }

}
