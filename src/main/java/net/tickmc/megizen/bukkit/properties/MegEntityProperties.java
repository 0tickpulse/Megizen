package net.tickmc.megizen.bukkit.properties;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import net.tickmc.megizen.bukkit.objects.MegModeledEntityTag;

public class MegEntityProperties implements Property {
    /**
     * @deprecated
     */
    @Override
    public String getPropertyString() {
        return null;
    }

    @Override
    public String getPropertyId() {
        return "MegEntity";
    }

    public static boolean describes(ObjectTag object) {
        return object instanceof EntityTag;
    }

    public MegModeledEntityTag getModeledEntity() {
        return MegModeledEntityTag.valueOf(entity.getUUID().toString(), null);
    }

    public MegEntityProperties(EntityTag entity) {
        this.entity = entity;
    }

    EntityTag entity;

    public static void register() {
        // <--[tag]
        // @attribute <EntityTag.modeled_entity>
        // @returns MegModeledEntityTag
        // @plugin Megizen
        // @description
        // Returns the modeled entity of the entity, if any.
        // -->
        PropertyParser.registerTag(MegEntityProperties.class, MegModeledEntityTag.class, "modeled_entity", (attribute, object) -> {
            return object.getModeledEntity();
        });
    }
}
