package net.tickmc.megizen.bukkit.commands;

import com.denizenscript.denizencore.objects.core.DurationTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.scripts.commands.generator.ArgDefaultNull;
import com.denizenscript.denizencore.scripts.commands.generator.ArgDefaultText;
import com.denizenscript.denizencore.scripts.commands.generator.ArgName;
import com.denizenscript.denizencore.scripts.commands.generator.ArgPrefixed;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.animation.handler.IStateMachineHandler;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.model.ActiveModel;
import net.tickmc.megizen.bukkit.objects.MegActiveModelTag;

public class MegStateCommand extends AbstractCommand {
    public MegStateCommand() {
        setName("megstate");
        setSyntax("megstate [model:<active_model>] [state:<state>] (priority:<#>/{1}) ((speed:<#.#>/{1}) (lerp_in:<duration>/{0}) (lerp_out:<duration>/{0}) (loop:once/loop/hold) (override:true/false) (force)/remove (ignore_lerp))");
        autoCompile();
    }

    // <--[command]
    // @Name MegState
    // @Syntax megstate [model:<active_model>] [state:<state>] (priority:<#>/{1}) ((speed:<#.#>/{1}) (lerp_in:<duration>/{0}) (lerp_out:<duration>/{0}) (loop:once/loop/hold) (override:true/false) (force)/remove (ignore_lerp))
    // @Required 3
    // @Short Plays a state on a modeled entity.
    // @Group Megizen
    //
    // @Description
    // Plays a state on a modeled entity.
    // This command is similar to the "state" Mythic Mechanic.
    // The documentation can be found at <@link url https://git.lumine.io/mythiccraft/model-engine-4/-/wikis/MythicMobs/mechanics/model/State>.
    //
    // If the 'remove' argument is specified, the state will be removed. If the 'state' argument is not specified, all states will be removed.
    // -->

    public static void autoExecute(ScriptEntry scriptEntry,
                                   @ArgName("model") @ArgPrefixed MegActiveModelTag model,
                                   @ArgName("state") @ArgPrefixed @ArgDefaultNull String state,
                                   @ArgName("speed") @ArgPrefixed @ArgDefaultText("1") float speed,
                                   @ArgName("lerp_in") @ArgPrefixed @ArgDefaultText("0") DurationTag lerpIn,
                                   @ArgName("lerp_out") @ArgPrefixed @ArgDefaultText("0") DurationTag lerpOut,
                                   @ArgName("loop") @ArgPrefixed @ArgDefaultText("once") BlueprintAnimation.LoopMode loop,
                                   @ArgName("override") @ArgPrefixed @ArgDefaultNull ElementTag override,
                                   @ArgName("force") @ArgDefaultText("true") boolean force,
                                   @ArgName("remove") @ArgDefaultText("false") boolean remove,
                                   @ArgName("ignore_lerp") @ArgDefaultText("false") boolean ignoreLerp,
                                   @ArgName("priority") @ArgPrefixed @ArgDefaultText("1") int priority) {
        ActiveModel activeModel = model.getActiveModel();
        AnimationHandler handler = activeModel.getAnimationHandler();
        if (remove) {
            if (state == null) {
                handler.forceStopAllAnimations();
                return;
            }
            if (handler instanceof IStateMachineHandler smh) {
                if (ignoreLerp) {
                    smh.forceStopAnimation(priority, state);
                } else {
                    smh.stopAnimation(priority, state);
                }
            } else if (ignoreLerp) {
                handler.forceStopAnimation(state);
            } else {
                handler.stopAnimation(state);
            }
            return;
        }
        if (state == null) {
            Debug.echoError("The 'state' argument is required to play an animation.");
            return;
        }
        IAnimationProperty property = handler instanceof IStateMachineHandler smh
            ? smh.playAnimation(priority, state, lerpIn.getTicks(), lerpOut.getTicks(), speed, force)
            : handler.playAnimation(state, lerpIn.getTicks(), lerpOut.getTicks(), speed, force);
        if (property != null) {
            property.setForceLoopMode(loop);
            if (override != null && override.isBoolean()) {
                property.setForceOverride(override.asBoolean());
            }
        }
    }
}
