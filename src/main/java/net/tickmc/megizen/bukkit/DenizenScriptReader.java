package net.tickmc.megizen.bukkit;

import com.denizenscript.denizencore.objects.core.ScriptTag;
import com.denizenscript.denizencore.utilities.ScriptUtilities;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.animation.script.ScriptReader;

public class DenizenScriptReader implements ScriptReader {
    @Override
    public void read(IAnimationProperty iAnimationProperty, String taskScript) {
        ScriptTag task = ScriptTag.valueOf(taskScript, null);
        if (task ==  null) return;
        ScriptUtilities.createAndStartQueue(task.getContainer(), null, null);
    }
}
