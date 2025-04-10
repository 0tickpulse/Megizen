package net.tickmc.megizen.bukkit.commands;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ScriptTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.scripts.commands.BracedCommand;
import com.denizenscript.denizencore.scripts.commands.generator.*;
import com.denizenscript.denizencore.scripts.queues.ScriptQueue;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.api.mount.controller.MountControllerSupplier;
import com.ticxo.modelengine.api.mount.controller.MountControllerType;
import com.ticxo.modelengine.api.mount.controller.MountControllerTypes;
import net.tickmc.megizen.bukkit.DenizenMountController;
import net.tickmc.megizen.bukkit.objects.MegActiveModelTag;

import java.util.List;

public class MegMountCommand extends AbstractCommand {

    public MegMountCommand() {
        setName("megmount");
        setSyntax("megmount [entity:<entity>] [model:<active_model>] [bones:<bone>|...] [mode:driver/passenger/dismount] (force) (interactable) (damageable) (auto_dismount)");
        addRemappedPrefixes("bones", "bone");
        autoCompile();
    }

    public enum MountMode {
        DRIVER,
        PASSENGER,
    }

    public enum MountControllerTypeEnum {
        WALKING,
        FLYING,
        WALKING_FORCE,
        FLYING_FORCE,
    }

    // <--[command]
    // @Name MegMount
    // @Syntax megmount [entity:<entity>] [model:<active_model>] [type:driver/passenger] (bones:<bone>|...) (dismount) (force) (interactable) (damageable) (auto_dismount) (controller_type:{walking}/flying/walking_force/flying_force) (controller_script:<script>)
    // @Required 3
    // @Short Mounts the given entity on the given modeled entity, either as a passenger or the driver.
    // @Group Megizen
    //
    // @Description
    // Mounts the given entity on the given modeled entity, either as a passenger or the driver.
    // The "bones" argument is required if the entity is being mounted as a passenger. It specifies which bones the entity will be mounted on.
    // If "force" is true, it selects the least occupied bone to mount the entity on. Otherwise, it will select the first bone that is not occupied.
    // You can find out mountable bone names by opening the model in Blockbench.
    // The "dismount" argument is optional, but will dismount the entity if specified.
    // The "interactable" and "damageable" arguments are optional, but make the mount interactable and damageable, respectively.
    // If the command is used on an entity already mounted, it will do nothing if "auto_dismount" is not specified. Otherwise, it will dismount the entity first.
    // -->

    public static void autoExecute(ScriptEntry scriptEntry,
                                   @ArgName("entity") @ArgPrefixed EntityTag entity,
                                   @ArgName("model") @ArgPrefixed MegActiveModelTag model,
                                   @ArgName("type") @ArgDefaultText("driver") MountMode type,
                                   @ArgName("bones") @ArgSubType(ElementTag.class) @ArgPrefixed @ArgDefaultText("") List<ElementTag> bones,
                                   @ArgName("dismount") boolean dismount,
                                   @ArgName("force") boolean force,
                                   @ArgName("interactable") boolean interactable,
                                   @ArgName("damageable") boolean damageable,
                                   @ArgName("auto_dismount") boolean autoDismount,
                                   @ArgName("controller_type") @ArgPrefixed @ArgDefaultText("walking") MountControllerTypeEnum controllerType,
                                   @ArgName("controller_script") @ArgPrefixed @ArgDefaultNull ScriptTag controllerScript,

                                   // for backwards compatibility
                                   @ArgName("driver") boolean driver,
                                   @ArgName("passenger") boolean passenger) {
        if (entity == null) {
            Debug.echoError("The 'entity' argument is required to mount an entity.");
            return;
        }
        if (model == null) {
            Debug.echoError("The 'model' argument is required to mount an entity.");
            return;
        }

        // Backwards compatibility
        if (driver && passenger) {
            Debug.echoError("The 'driver' and 'passenger' arguments are mutually exclusive.");
            return;
        }
        if (driver) {
            type = MountMode.DRIVER;
        }
        if (passenger) {
            type = MountMode.PASSENGER;
        }

        ActiveModel activeModel = model.getActiveModel();
        List<String> boneNames = bones.stream().map(ElementTag::asString).toList();
        MountControllerSupplier mountControllerType;
        if (controllerScript == null) {
            mountControllerType = switch (controllerType) {
                case WALKING -> MountControllerTypes.WALKING;
                case FLYING -> MountControllerTypes.FLYING;
                case WALKING_FORCE -> MountControllerTypes.WALKING_FORCE;
                case FLYING_FORCE -> MountControllerTypes.FLYING_FORCE;
            };
        } else {
            mountControllerType = (entity1, mount) -> new DenizenMountController(controllerScript, null, scriptEntry.entryData, entity1, mount);
        }

        if (!dismount) {
            ActiveModel mountedPair = ModelEngineAPI.getMountPairManager().getMountedPair(entity.getUUID());
            if (mountedPair != null) {
                if (!autoDismount) {
                    return;
                }
                mountedPair.getMountManager().ifPresent(mountManager -> {
                    mountManager.dismountRider(entity.entity);
                });
            }
        }
        switch (type) {
            case DRIVER -> {
                if (dismount) {
                    activeModel.getMountManager().ifPresent(MountManager::dismountDriver);
                    return;
                }
                activeModel.getMountManager().ifPresent(mountManager -> {
                    if (mountManager.getDriver() != null) {
                        mountManager.dismountDriver();
                    }
                    mountManager.setCanDrive(true);
                    mountManager.setCanRide(true);
                    mountManager.mountDriver(entity.entity, mountControllerType, mountController -> {
                        mountController.setCanDamageMount(damageable);
                        mountController.setCanInteractMount(interactable);
                    });
                });
            }
            case PASSENGER -> {
                if (dismount) {
                    activeModel.getMountManager().ifPresent(mountManager -> {
                        if (boneNames.isEmpty()) {
                            mountManager.dismountRider(entity.entity);
                            return;
                        }
                        mountManager.getMount(entity.entity).ifPresent(mountController -> {
                            if (boneNames.contains(mountController.getBone().getUniqueBoneId())) {
                                mountManager.dismountRider(entity.entity);
                            }
                        });
                    });
                    return;
                }
                if (boneNames.isEmpty()) {
                    Debug.echoError("The 'bone' argument is required to mount an entity as a passenger.");
                    return;
                }

                activeModel.getMountManager().ifPresent(mountManager -> {
                    mountManager.setCanDrive(true);
                    mountManager.setCanRide(true);
                    if (force) {
                        mountManager.mountLeastOccupied(entity.entity, boneNames, mountControllerType, mountController -> {
                            mountController.setCanDamageMount(damageable);
                            mountController.setCanInteractMount(interactable);
                        });
                    } else {
                        mountManager.mountAvailable(entity.entity, boneNames, mountControllerType, mountController -> {
                            mountController.setCanDamageMount(damageable);
                            mountController.setCanInteractMount(interactable);
                        });
                    }
                });
            }
        }
    }
}
