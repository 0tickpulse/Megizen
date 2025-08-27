package net.tickmc.megizen.bukkit.properties;

import com.denizenscript.denizen.objects.EntityTag;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.mount.controller.MountController;
import net.tickmc.megizen.bukkit.objects.MegBoneTag;
import net.tickmc.megizen.bukkit.objects.MegModeledEntityTag;

public class MegizenEntityTagExtensions {

    public static MegModeledEntityTag getModeledEntity(EntityTag entity) {
        return new MegModeledEntityTag(entity.getBukkitEntity());
    }

    public static void register() {
        // <--[tag]
        // @attribute <EntityTag.modeled_entity>
        // @returns MegModeledEntityTag
        // @plugin Megizen
        // @description
        // Returns the modeled entity of the entity, if any.
        // -->
        EntityTag.tagProcessor.registerTag(MegModeledEntityTag.class, "modeled_entity", (attribute, entity) -> {
            return getModeledEntity(entity);
        });

        // <--[tag]
        // @attribute <EntityTag.mounted_bone>
        // @returns MegBoneTag
        // @plugin Megizen
        // @description
        // Returns the MegBoneTag that the entity is mounted on, if any.
        // -->
        EntityTag.tagProcessor.registerTag(MegBoneTag.class, "mounted_bone", (attribute, entity) -> {
            MountController controller = ModelEngineAPI.getMountPairManager().getController(entity.getUUID());
            if (controller == null || controller.getMount() == null) {
                return null;
            }
            return new MegBoneTag(((BoneBehavior) controller.getMount()).getBone());
        });
    }
}
