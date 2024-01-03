package net.tickmc.megizen.bukkit.properties;

import com.denizenscript.denizen.objects.EntityTag;
import net.tickmc.megizen.bukkit.objects.MegModeledEntityTag;

public class MegizenEntityTagExtensions {

    public static MegModeledEntityTag getModeledEntity(EntityTag entity) {
        return MegModeledEntityTag.valueOf(entity.getUUID().toString(), null);
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
    }
}
