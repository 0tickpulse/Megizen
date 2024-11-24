package net.tickmc.megizen.bukkit.commands;

import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.scripts.commands.generator.ArgDefaultNull;
import com.denizenscript.denizencore.scripts.commands.generator.ArgName;
import com.denizenscript.denizencore.scripts.commands.generator.ArgPrefixed;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.Dummy;
import com.ticxo.modelengine.api.entity.data.BukkitEntityData;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import net.tickmc.megizen.bukkit.objects.MegModeledEntityTag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MegModelCommand extends AbstractCommand {

    public MegModelCommand() {
        setName("megmodel");
        setSyntax("megmodel [model:<model>] (entity:<modeled_entity>) (dummy) (remove)");
        autoCompile();
    }

    // <--[command]
    // @Name MegModel
    // @Syntax megmodel [model:<model>] (entity:<modeled_entity>) (dummy) (remove)
    // @Required 2
    // @Short Adds or removes a model from an entity, or creates a dummy entity with a model.
    // @Group Megizen
    //
    // @Description
    // Adds or removes a model from an entity, or creates a dummy entity with a model.
    // If the player disconnects, the model does not persist and the player becomes invisible. Use the 'meg_make_visible' mechanism to fix this.
    // Models do not persist across worlds. They should be removed first, then added back.
    // Dummy entities require a modeled entity to not be specified, as it creates a new modeled entity.
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

    @Override
    public void addCustomTabCompletions(TabCompletionsBuilder tab) {
        tab.addWithPrefix("model:", ModelEngineAPI.getAPI().getModelRegistry().getKeys());
    }

    public static void autoExecute(ScriptEntry scriptEntry,
                                   @ArgName("entity") @ArgPrefixed @ArgDefaultNull MegModeledEntityTag modeledEntityTag,
                                   @ArgName("dummy") boolean dummy,
                                   @ArgName("model") @ArgPrefixed ElementTag model,
                                   @ArgName("remove") boolean remove) {
        ModelBlueprint blueprint = ModelEngineAPI.getBlueprint(model.asString());
        if (blueprint == null) {
            Debug.echoError("Invalid model provided: " + model.asString());
            return;
        }

        ModeledEntity modeledEntity = modeledEntityTag.getModeledEntity();
        if (modeledEntity == null && !dummy) {
            Debug.echoError("Invalid entity provided: " + modeledEntityTag.identify());
            return;
        }

        if (modeledEntity != null && dummy) {
            Debug.echoError("Cannot specify a modeled entity and add the dummy flag at the same time.");
            return;
        }

        if (modeledEntity == null && dummy) {
            Dummy<?> dummyEntity = new Dummy<>();
            dummyEntity.setDetectingPlayers(false);
            modeledEntity = ModelEngineAPI.createModeledEntity(dummyEntity);
        }

        Player player = Bukkit.getPlayer(modeledEntity.getBase().getUUID()) != null ? Bukkit.getPlayer(modeledEntity.getBase().getUUID()) : null;
        if (player == null) {
            if (remove) {
                if (!modeledEntity.getModels().containsKey(model.asString())) {
                    Debug.echoError("Entity does not have model: " + model.asString());
                    return;
                }
                modeledEntity.removeModel(model.asString());
            }
            else {
                ActiveModel activeModel = ModelEngineAPI.createActiveModel(blueprint);
                modeledEntity.addModel(activeModel, true);
            }
        }

        if (player != null) {
            if (remove) {
                modeledEntity.removeModel(model.asString()).ifPresent(ActiveModel::destroy);
                if (modeledEntity.getModels().isEmpty()) {
                    modeledEntity.markRemoved();
                    ModelEngineAPI.getEntityHandler().setForcedInvisible(player, false);
                    ModelEngineAPI.getEntityHandler().forceSpawn(player);
                }
            }
            else {
                modeledEntity.getBase().getBodyRotationController().setPlayerMode(true);
                modeledEntity.setBaseEntityVisible(false);
                IEntityData iEntityData = modeledEntity.getBase().getData();
                if (iEntityData instanceof BukkitEntityData data) {
                    data.getTracked().addForcedPairing(player);
                }
                ModelEngineAPI.getEntityHandler().setForcedInvisible(player, true);
                ActiveModel activeModel = ModelEngineAPI.createActiveModel(blueprint);
                modeledEntity.addModel(activeModel, true).ifPresent(ActiveModel::destroy);
            }
        }
    }
}
