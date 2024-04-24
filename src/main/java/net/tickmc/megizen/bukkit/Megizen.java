package net.tickmc.megizen.bukkit;

import com.denizenscript.denizen.Denizen;
import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.ObjectFetcher;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import net.tickmc.megizen.bukkit.commands.MegModelCommand;
import net.tickmc.megizen.bukkit.commands.MegStateCommand;
import net.tickmc.megizen.bukkit.events.MegBaseEntityInteractScriptEvent;
import net.tickmc.megizen.bukkit.objects.MegActiveModelTag;
import net.tickmc.megizen.bukkit.objects.MegBoneTag;
import net.tickmc.megizen.bukkit.objects.MegModeledEntityTag;
import net.tickmc.megizen.bukkit.properties.MegizenEntityTagExtensions;
import net.tickmc.megizen.bukkit.properties.MegizenPlayerTagExtensions;
import org.bukkit.plugin.java.JavaPlugin;

public class Megizen extends JavaPlugin {

    public static Megizen instance;

    @Override
    public void onEnable() {
        Debug.log("Megizen loading...");
        saveDefaultConfig();
        instance = this;

        // register stuff
        ObjectFetcher.registerWithObjectFetcher(MegModeledEntityTag.class, MegModeledEntityTag.tagProcessor);
        ObjectFetcher.registerWithObjectFetcher(MegActiveModelTag.class, MegActiveModelTag.tagProcessor);
        ObjectFetcher.registerWithObjectFetcher(MegBoneTag.class, MegBoneTag.tagProcessor);
        MegizenEntityTagExtensions.register();
        MegizenPlayerTagExtensions.register();
        DenizenCore.commandRegistry.registerCommand(MegModelCommand.class);
        DenizenCore.commandRegistry.registerCommand(MegStateCommand.class);
        ScriptEvent.registerScriptEvent(MegBaseEntityInteractScriptEvent.class);

        Debug.log("Megizen loaded!");
    }

    @Override
    public void onDisable() {
        // To prevent issue with onDisable order, tell Denizen to shutdown NOW (it won't repeat itself).
        Denizen.getInstance().onDisable();
    }
}
