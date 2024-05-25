package net.tickmc.megizen.bukkit.properties;

import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.ticxo.modelengine.api.ModelEngineAPI;

public class MegizenPlayerTagExtensions {
    public static void register() {

        // <--[tag]
        // @object PlayerTag
        // @attribute <PlayerTag.meg_forced_invisible>
        // @returns ElementTag(Boolean)
        // @plugin Megizen
        // @description
        // Returns whether the player has been forcefully turned invisible by either the megmodel command or by ModelEngine.
        // -->
        PlayerTag.tagProcessor.registerTag(ElementTag.class, "meg_forced_invisible", (attribute, entity) -> {
            return new ElementTag(ModelEngineAPI.getEntityHandler().isForcedInvisible(entity.getPlayerEntity()));
        });

        // <--[mechanism]
        // @object PlayerTag
        // @name meg_make_visible
        // @input ElementTag(Boolean)
        // @plugin Megizen
        // @description
        // Forces the player to be visible or invisible. This is a workaround for the invisible player bug when using the megmodel command.
        // See also: <@link tag MegModeledEntityTag.meg_make_visible>
        // -->
        PlayerTag.tagProcessor.registerMechanism("meg_make_visible", false, (prop, mechanism) -> {
            ModelEngineAPI.getEntityHandler().setForcedInvisible(prop.getPlayerEntity(), false);
            ModelEngineAPI.getEntityHandler().forceSpawn(prop.getPlayerEntity());
        });
    }
}
