package net.tickmc.megizen.bukkit;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ScriptTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.denizenscript.denizencore.scripts.queues.ScriptQueue;
import com.denizenscript.denizencore.utilities.ScriptUtilities;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.api.mount.controller.impl.AbstractMountController;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;
import net.tickmc.megizen.bukkit.objects.MegActiveModelTag;
import org.bukkit.entity.Entity;

import java.util.function.Consumer;


public class DenizenMountController extends AbstractMountController {
    public final ScriptTag script;
    public final String path;
    public final ScriptEntryData entryData;

    public DenizenMountController(ScriptTag script, String path, ScriptEntryData entryData, Entity entity, Mount mount) {
        super(entity, mount);
        this.script = script;
        this.path = path;
        this.entryData = entryData;
    }

    private void sharedConfigurations(ScriptQueue queue, MoveController moveController, ActiveModel activeModel) {
        queue.addDefinition("entity", new EntityTag(getEntity()));
        queue.addDefinition("model", new MegActiveModelTag(activeModel));
        queue.addDefinition("is_on_ground", new ElementTag(moveController.isOnGround()));
        queue.addDefinition("is_in_water", new ElementTag(moveController.isInWater()));
        queue.addDefinition("velocity", new LocationTag(moveController.getVelocity()));
        queue.addDefinition("input_front", new ElementTag(getInput().getFront()));
        queue.addDefinition("input_side", new ElementTag(getInput().getSide()));
        queue.addDefinition("is_jump", new ElementTag(getInput().isJump()));
        queue.addDefinition("is_sneak", new ElementTag(getInput().isSneak()));
        queue.addDefinition("is_sprint", new ElementTag(getInput().isSprint()));
    }

    @Override
    public void updateDriverMovement(MoveController moveController, ActiveModel activeModel) {
        Consumer<ScriptQueue> configure = queue -> {
            sharedConfigurations(queue, moveController, activeModel);
            queue.addDefinition("mount_type", new ElementTag("driver"));
        };
        ScriptUtilities.createAndStartQueue(script.getContainer(), path, entryData, null, configure, null, null, null, null);
    }

    @Override
    public void updatePassengerMovement(MoveController moveController, ActiveModel activeModel) {
        Consumer<ScriptQueue> configure = queue -> {
            sharedConfigurations(queue, moveController, activeModel);
            queue.addDefinition("mount_type", new ElementTag("passenger"));
        };
        ScriptUtilities.createAndStartQueue(script.getContainer(), path, entryData, null, configure, null, null, null, null);
    }
}
