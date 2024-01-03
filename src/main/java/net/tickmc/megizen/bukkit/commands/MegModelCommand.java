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
import org.bukkit.entity.Entity;

public class MegModelCommand extends AbstractCommand {

    public MegModelCommand() {
        setName("megmodel");
        setSyntax("megmodel [entity:<entity>] [model:<model>] (remove)");
        autoCompile();
    }

    public static void autoExecute(ScriptEntry scriptEntry,
                                   @ArgName("entity") @ArgPrefixed EntityTag entity,
                                   @ArgName("model") @ArgPrefixed ElementTag model,
                                   @ArgName("remove") @ArgPrefixed @ArgDefaultText("false") boolean remove) {
        ModelBlueprint blueprint = ModelEngineAPI.getBlueprint(model.asString());
        if (blueprint == null) {
            Debug.echoError("Invalid model: " + model.asString());
            return;
        }
        Entity bukkitEntity = entity.getBukkitEntity();
        if (remove) {
            ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(bukkitEntity);
            if (modeledEntity == null) {
                Debug.echoError("Entity is not modeled: " + entity.identify());
                return;
            }
            if (!modeledEntity.getModels().containsKey(model.asString())) {
                Debug.echoError("Entity does not have model: " + model.asString());
                return;
            }
            modeledEntity.removeModel(model.asString());
        } else {
            ModeledEntity modeledEntity = ModelEngineAPI.createModeledEntity(bukkitEntity);
            ActiveModel activeModel = ModelEngineAPI.createActiveModel(blueprint);
            modeledEntity.addModel(activeModel, true);
        }
    }
}
