package net.tickmc.megizen.bukkit.events;

import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.ticxo.modelengine.api.events.BaseEntityInteractEvent;
import net.tickmc.megizen.bukkit.objects.MegActiveModelTag;
import net.tickmc.megizen.bukkit.objects.MegModeledEntityTag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ModelengineInteractEntityScriptEvent extends BukkitScriptEvent implements Listener {

    public ModelengineInteractEntityScriptEvent() {
        registerCouldMatcher("modelengine interact entity");
    }

    public BaseEntityInteractEvent event;

    @Override
    public boolean matches(ScriptPath path) {
        return super.matches(path);
    }

    @Override
    public ObjectTag getContext(String name) {
        return switch (name) {
            case "active_model" -> new MegActiveModelTag(event.getModel());
            case "modeled_entity" -> new MegModeledEntityTag(event.getModel().getModeledEntity());
            case "clicked_position" -> event.getClickedPosition() != null ? new LocationTag(event.getClickedPosition()) : null;
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
