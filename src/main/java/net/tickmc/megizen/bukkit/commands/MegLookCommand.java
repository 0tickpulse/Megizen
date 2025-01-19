package net.tickmc.megizen.bukkit.commands;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.scripts.commands.generator.ArgDefaultNull;
import com.denizenscript.denizencore.scripts.commands.generator.ArgName;
import com.denizenscript.denizencore.scripts.commands.generator.ArgPrefixed;
import com.denizenscript.denizencore.objects.LocationTag;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.nms.entity.LookController;
import net.tickmc.megizen.bukkit.objects.MegActiveModelTag;

public class MegLookCommand extends AbstractCommand {

    public MegLookCommand() {
        setName("meglook");
        setSyntax("meglook [model:<active_model>] [<location>/pitch:<pitch>/headYaw:<yaw>/bodyYaw:<yaw>]");
        autoCompile();
    }

    // <--[command]
    // @Name MegLook
    // @Syntax meglook [entity:<entity>] [model:<active_model>] [bone:<bone>] (driver) (passenger) (dismount) (interactable) (damageable)
    // @Required 3
    // @Short Mounts the given entity on the given modeled entity, either as a passenger or the driver.
    // @Group Megizen
    //
    // @Description
    // Mounts the given entity on the given modeled entity, either as a passenger or the driver.
    // The "bone_name" argument is required if the entity is being mounted as a passenger. It specifies which bone the entity will be mounted on.
    // You can find out mountable bone names by opening the model in Blockbench.
    // The "dismount" argument is optional, but will dismount the entity if specified.
    // The "interactable" and "damageable" arguments are optional, but make the mount interactable and damageable, respectively.
    // -->

    public static void autoExecute(ScriptEntry scriptEntry,
                                   @ArgName("model") @ArgPrefixed MegActiveModelTag model,
                                   @ArgName("location") @ArgDefaultNull @ArgLinear ObjectTag locationObj,
                                   @ArgName("pitch") @ArgDefaultNull @ArgPrefixed ElementTag pitch,
                                   @ArgName("headYaw") @ArgDefaultNull @ArgPrefixed ElementTag headYaw,
                                   @ArgName("bodyYaw") @ArgDefaultNull @ArgPrefixed ElementTag bodyYaw) {

        if (model == null) {
            Debug.echoError("The 'model' argument is required.");
            return;
        }
        ActiveModel activeModel = model.getActiveModel();

        if (locationObj != null && !(locationObj instanceof LocationTag) && locationObj.asElement().asLowerString().equals("cancel")) {
            return;
        }

        LocationTag loc = locationObj == null ? null : locationObj.asType(LocationTag.class, scriptEntry.context);

        if (loc == null && yaw == null && pitch == null) {
            Debug.echoError("Missing or invalid Location input!");;
        }

        final float pitchRaw = pitch == null ? 0 : pitch.asFloat();


    }
}
