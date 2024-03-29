package net.tickmc.megizen.bukkit.commands;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.scripts.commands.generator.ArgDefaultText;
import com.denizenscript.denizencore.scripts.commands.generator.ArgName;
import com.denizenscript.denizencore.scripts.commands.generator.ArgPrefixed;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import net.tickmc.megizen.bukkit.objects.MegModeledEntityTag;
import org.bukkit.entity.Entity;

public class MegModelCommand extends AbstractCommand {

    public MegModelCommand() {
        setName("megmodel");
        setSyntax("megmodel [entity:<modeled_entity>] [model:<model>] (remove)");
        autoCompile();
    }

    // <--[command]
    // @Name MegModel
    // @Syntax megmodel [entity:<modeled_entity>] [model:<model>] (remove)
    // @Required 2
    // @Short Adds or removes a model from an entity.
    // @Group Megizen
    //
    // @Description
    // Adds or removes a model from an entity.
    //
    // The model must be a name of a loaded model in ModelEngine.
    //
    // If you have come over from Mythic, this is equivalent to the `model` mechanic.
    // To configure other options such as hitbox/invisible/damagetint/etc, adjust the MegModeledEntityTag object instead.
    //
    // @Tags
    // <EntityTag.modeled_entity>
    //
    // @Usage
    // Use to add a model to an entity.
    // - megmodel entity:<context.entity> model:my_model
    // -->

    public static void autoExecute(ScriptEntry scriptEntry,
                                   @ArgName("entity") @ArgPrefixed MegModeledEntityTag modeledEntityTag,
                                   @ArgName("model") @ArgPrefixed ElementTag model,
                                   @ArgName("remove") @ArgPrefixed @ArgDefaultText("false") boolean remove) {
        ModelBlueprint blueprint = ModelEngineAPI.getBlueprint(model.asString());
        if (blueprint == null) {
            Debug.echoError("Invalid model: " + model.asString());
            return;
        }
        ModeledEntity modeledEntity = modeledEntityTag.getModeledEntity();
        if (remove) {
            if (modeledEntity == null) {
                Debug.echoError("Entity is not modeled: " + modeledEntityTag.identify());
                return;
            }
            if (!modeledEntity.getModels().containsKey(model.asString())) {
                Debug.echoError("Entity does not have model: " + model.asString());
                return;
            }
            modeledEntity.removeModel(model.asString());
        } else {
            ActiveModel activeModel = ModelEngineAPI.createActiveModel(blueprint);
            modeledEntity.addModel(activeModel, true);
        }
    }
}
