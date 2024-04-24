package net.tickmc.megizen.bukkit.events;

import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.ticxo.modelengine.api.events.ModelRegistrationEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ModelRegistrationScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // meg model registration begins
    //
    // @Group Megizen
    //
    // @Triggers when the model generator enters and exits a phase, and when the generator has finished everything.
    //
    // @Context
    // <context.phase> returns an ElementTag of the current model registration phase.
    //
    // -->

    public ModelRegistrationScriptEvent() {
        registerCouldMatcher("meg model registration begins");
        registerSwitches("phase");
    }

    ModelRegistrationEvent event;

    @Override
    public boolean matches(ScriptPath path) {
        if (!runGenericSwitchCheck(path, "phase", event.getPhase().toString())) {
            return false;
        }
        return super.matches(path);
    }

    @Override
    public ObjectTag getContext(String name) {
        return switch (name) {
            case "phase" -> new ElementTag(event.getPhase());
            default -> super.getContext(name);
        };
    }

    @EventHandler
    public void onModelRegisters(ModelRegistrationEvent event) {
        this.event = event;
        fire(event);
    }
}
