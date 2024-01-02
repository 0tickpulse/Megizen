package net.tickmc.megizen.bukkit;

import com.denizenscript.denizen.Denizen;
import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.objects.ObjectFetcher;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import net.tickmc.megizen.bukkit.commands.MegModelCommand;
import net.tickmc.megizen.bukkit.commands.MegStateCommand;
import net.tickmc.megizen.bukkit.objects.MegModeledEntityTag;
import net.tickmc.megizen.bukkit.properties.MegEntityProperties;
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
        PropertyParser.registerProperty(MegEntityProperties.class, EntityTag.class);
        DenizenCore.commandRegistry.registerCommand(MegModelCommand.class);
        DenizenCore.commandRegistry.registerCommand(MegStateCommand.class);

        Debug.log("Megizen loaded!");
    }

    @Override
    public void onDisable() {
        // To prevent issue with onDisable order, tell Denizen to shutdown NOW (it won't repeat itself).
        Denizen.getInstance().onDisable();
    }
}
