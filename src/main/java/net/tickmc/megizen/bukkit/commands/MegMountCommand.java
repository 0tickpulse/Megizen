package net.tickmc.megizen.bukkit.commands;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.scripts.commands.generator.ArgDefaultNull;
import com.denizenscript.denizencore.scripts.commands.generator.ArgName;
import com.denizenscript.denizencore.scripts.commands.generator.ArgPrefixed;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.mount.controller.MountControllerTypes;
import net.tickmc.megizen.bukkit.objects.MegActiveModelTag;

public class MegMountCommand extends AbstractCommand {

    public MegMountCommand() {
        setName("megmount");
        setSyntax("megmount [entity:<entity>] [model:<active_model>] [bone:<bone>] (driver) (passenger) (dismount) (interactable) (damageable)");
        autoCompile();
    }

    // <--[command]
    // @Name MegState
    // @Syntax megmount [entity:<entity>] [model:<active_model>] [bone:<bone>] (driver) (passenger) (dismount) (interactable) (damageable)
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
                                   @ArgName("entity") @ArgPrefixed EntityTag entity,
                                   @ArgName("model") @ArgPrefixed MegActiveModelTag model,
                                   @ArgName("driver") @ArgDefaultNull boolean driver,
                                   @ArgName("passenger") @ArgDefaultNull boolean passenger,
                                   @ArgName("bone") @ArgDefaultNull String boneName,
                                   @ArgName("dismount") boolean dismount,
                                   @ArgName("damageable") boolean damageable,
                                   @ArgName("interactable") boolean interactable) {
        if (entity == null) {
            Debug.echoError("The 'entity' argument is required to mount an entity.");
            return;
        }
        if (model == null) {
            Debug.echoError("The 'model' argument is required to mount an entity.");
            return;
        }
        if (!driver && !passenger) {
            Debug.echoError("A type of 'driver' or 'passenger' must be specified.");
            return;
        }
        if (driver && passenger) {
            Debug.echoError("Only one of 'driver' or 'passenger' can be specified.");
            return;
        }
        ActiveModel activeModel = model.getActiveModel();
        if (driver) {
            activeModel.getMountManager().ifPresent(mountManager -> {
                if (mountManager.getDriver() != null) {
                    mountManager.dismountDriver();
                }
                mountManager.setCanDrive(true);
                mountManager.setCanRide(true);
                mountManager.mountDriver(entity.entity, MountControllerTypes.WALKING, mountController -> {
                    mountController.setCanDamageMount(damageable);
                    mountController.setCanInteractMount(interactable);
                });
            });
        }
        if (passenger && boneName.isEmpty()) {
            Debug.echoError("The 'bone' argument is required to mount an entity as a passenger.");
            return;
        }
        if (passenger) {
            activeModel.getMountManager().ifPresent(mountManager -> {
                mountManager.setCanDrive(true);
                mountManager.setCanRide(true);
                mountManager.mountPassenger(boneName, entity.entity, MountControllerTypes.WALKING, mountController -> {
                    mountController.setCanDamageMount(damageable);
                    mountController.setCanInteractMount(interactable);
                });
            });
        }
        if (dismount) {
            activeModel.getMountManager().ifPresent(mountManager -> {
                mountManager.dismountRider(entity.entity);
            });
        }
    }
}
