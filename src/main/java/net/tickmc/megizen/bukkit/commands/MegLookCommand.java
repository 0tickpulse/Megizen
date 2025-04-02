package net.tickmc.megizen.bukkit.commands;

import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.scripts.commands.generator.ArgDefaultNull;
import com.denizenscript.denizencore.scripts.commands.generator.ArgLinear;
import com.denizenscript.denizencore.scripts.commands.generator.ArgName;
import com.denizenscript.denizencore.scripts.commands.generator.ArgPrefixed;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.ticxo.modelengine.api.nms.entity.wrapper.LookController;
import net.tickmc.megizen.bukkit.objects.MegModeledEntityTag;

public class MegLookCommand extends AbstractCommand {

    public MegLookCommand() {
        setName("meglook");
        setSyntax("meglook [entity:<entity>] [<location>/pitch:<pitch>/head_yaw:<yaw>/body_yaw:<yaw>]");
        autoCompile();
    }

    // <--[command]
    // @Name MegLook
    // @Syntax meglook [entity:<entity>] [<location>/pitch:<pitch>/head_yaw:<yaw>/body_yaw:<yaw>]
    // @Required 2
    // @Short Causes the model look at a location.
    // @Group Megizen
    //
    // @Description
    // Makes the model look towards the location.
    // Similar to the Denizen look command: <@link url https://meta.denizenscript.com/Docs/Commands/look>
    // -->

    public static void autoExecute(ScriptEntry scriptEntry,
                                   @ArgName("entity") @ArgPrefixed MegModeledEntityTag entity,
                                   @ArgName("location") @ArgDefaultNull @ArgLinear LocationTag location,
                                   @ArgName("pitch") @ArgDefaultNull @ArgPrefixed ElementTag pitch,
                                   @ArgName("head_yaw") @ArgDefaultNull @ArgPrefixed ElementTag headYaw,
                                   @ArgName("body_yaw") @ArgDefaultNull @ArgPrefixed ElementTag bodyYaw) {

        if (location == null && headYaw == null && bodyYaw == null && pitch == null) {
            Debug.echoError("Missing or invalid Location input!");
            return;
        }

        LookController lookController = entity.getModeledEntity().getBase().getLookController();

        if (location != null) {
            lookController.lookAt(location.getX(), location.getY(), location.getZ());
        }

        if (pitch != null) {
            lookController.setPitch(pitch.asFloat());
        }

        if (headYaw != null) {
            lookController.setHeadYaw(headYaw.asFloat());
        }

        if (bodyYaw != null) {
            lookController.setBodyYaw(bodyYaw.asFloat());
        }
    }
}
