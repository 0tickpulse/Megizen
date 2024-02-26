package net.tickmc.megizen.bukkit;

import com.denizenscript.denizencore.events.ScriptEvent;
import net.tickmc.megizen.bukkit.events.AddModelScriptEvent;
import net.tickmc.megizen.bukkit.events.ModelRegistrationScriptEvent;
import net.tickmc.megizen.bukkit.events.RemoveModelScriptEvent;

public class ScriptEventRegistry {
    public static void registerEvents() {
        ScriptEvent.registerScriptEvent(AddModelScriptEvent.class);
        ScriptEvent.registerScriptEvent(ModelRegistrationScriptEvent.class);
        ScriptEvent.registerScriptEvent(RemoveModelScriptEvent.class);
    }
}
