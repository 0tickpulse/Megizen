package net.tickmc.megizen.bukkit.commands;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.scripts.commands.generator.ArgDefaultNull;
import com.denizenscript.denizencore.scripts.commands.generator.ArgLinear;
import com.denizenscript.denizencore.scripts.commands.generator.ArgName;
import com.denizenscript.denizencore.scripts.commands.generator.ArgPrefixed;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.nms.entity.wrapper.LookController;
import net.tickmc.megizen.bukkit.objects.MegActiveModelTag;

public class MegLookCommand extends AbstractCommand {

    public MegLookCommand() {
        setName("meglook");
        setSyntax("meglook [entity:<entity>] [<location>/pitch:<pitch>/headYaw:<yaw>/bodyYaw:<yaw>]");
        autoCompile();
    }

    // <--[command]
    // @Name MegLook
    // @Syntax meglook [entity:<entity>] [<location>/pitch:<pitch>/headYaw:<yaw>/bodyYaw:<yaw>]
    // @Required 2
    // @Short Causes the model look at a location.
    // @Group Megizen
    //
    // @Description
    // Makes the model look towards the location.
    // Similar to the Denizen look command: <@link url https://meta.denizenscript.com/Docs/Commands/look>
    // -->

    public static void autoExecute(ScriptEntry scriptEntry,
                                   @ArgName("entity") @ArgPrefixed EntityTag entity,
                                   @ArgName("location") @ArgDefaultNull @ArgLinear ObjectTag locationObj,
                                   @ArgName("pitch") @ArgDefaultNull @ArgPrefixed ElementTag pitch,
                                   @ArgName("headYaw") @ArgDefaultNull @ArgPrefixed ElementTag headYaw,
                                   @ArgName("bodyYaw") @ArgDefaultNull @ArgPrefixed ElementTag bodyYaw) {

        if (entity == null) {
            Debug.echoError("The 'entity' argument is required.");
            return;
        }
//        ActiveModel activeModel = model.getActiveModel();

        ModeledEntity modeledEntity = modeledEntityTag.getModeledEntity();
        if (locationObj != null && !(locationObj instanceof LocationTag) && locationObj.asElement().asLowerString().equals("cancel")) {
            return;
        }

        LocationTag loc = locationObj == null ? null : locationObj.asType(LocationTag.class, scriptEntry.context);

        if (loc == null && headYaw == null && bodyYaw == null && pitch == null) {
            Debug.echoError("Missing or invalid Location input!");;
        }

        final float pitchRaw = pitch == null ? 0 : pitch.asFloat();
        final float headYawRaw = headYaw == null ? 0 : headYaw.asFloat();
        final float bodyYawRaw = bodyYaw == null ? 0 : bodyYaw.asFloat();
        LookController lookController = activeModel.getBase().getLookController();

        if (loc != null){

            lookController.lookAt(
                    loc.getX(),
                    loc.getY(),
                    loc.getZ()
            );

        }


    }
}
