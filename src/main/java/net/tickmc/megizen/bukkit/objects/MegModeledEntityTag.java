package net.tickmc.megizen.bukkit.objects;

import com.denizenscript.denizen.objects.EntityFormObject;
import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.objects.Adjustable;
import com.denizenscript.denizencore.objects.Fetchable;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.DurationTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.core.MapTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.denizenscript.denizencore.utilities.text.StringHolder;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.Dummy;
import com.ticxo.modelengine.api.entity.data.BukkitEntityData;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.nms.entity.wrapper.TrackedEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Map;

public class MegModeledEntityTag implements ObjectTag, Adjustable {

    // <--[ObjectType]
    // @name MegModeledEntityTag
    // @prefix megmodeledentity
    // @base ElementTag
    // @format
    // The identity format for modeled entities is <uuid>
    // Where <uuid> is the UUID of the base entity.
    // For example: 'megmodeledentity@dfc67056-b15d-45dd-b239-482d92e482e5'.
    //
    // @plugin Megizen
    // @description
    // Represents an entity that has a model on it.
    //
    // -->

    //////////////////
    //    Object Fetcher
    ////////////////

    public static MegModeledEntityTag valueOf(String string) {
        return valueOf(string, null);
    }

    @Fetchable("megmodeledentity")
    public static MegModeledEntityTag valueOf(String string, TagContext context) {
        if (string == null) {
            return null;
        }
        try {
            string = CoreUtilities.toLowerCase(string).replace("megmodeledentity@", "");
            ModeledEntity me = ModelEngineAPI.getOrCreateModeledEntity(EntityTag.valueOf(string, context).getBukkitEntity());
            if (me == null) {
                return null;
            }
            return new MegModeledEntityTag(me);
        } catch (Exception ignored) {
        }
        return null;
    }

    public static boolean matches(String arg) {
        return valueOf(arg) != null;
    }

    /////////////////////
    //   Constructors
    //////////////////

    public MegModeledEntityTag(ModeledEntity me) {
        this.modeledEntity = me;
    }

    public MegModeledEntityTag(Entity entity) {
        this.modeledEntity = ModelEngineAPI.getOrCreateModeledEntity(entity);
    }

    /////////////////////
    //   Instance Fields/Methods
    /////////////////

    private ModeledEntity modeledEntity;

    public ModeledEntity getModeledEntity() {
        return modeledEntity;
    }

    /////////////////////
    //  ObjectTag Methods
    ///////////////////

    private String prefix = "MegModeledEntity";

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public boolean isUnique() {
        return true;
    }

    @Override
    public String identify() {
        return "megmodeledentity@"
                + modeledEntity.getBase().getUUID().toString();
    }

    @Override
    public String identifySimple() {
        return identify();
    }

    @Override
    public Object getJavaObject() {
        return modeledEntity;
    }

    @Override
    public ObjectTag setPrefix(String s) {
        prefix = s;
        return this;
    }

    @Override
    public String toString() {
        return identify();
    }

    /////////////////////
    //  Tags
    ///////////////////

    public static ObjectTagProcessor<MegModeledEntityTag> tagProcessor = new ObjectTagProcessor<>();

    public static void registerTags() {
        tagProcessor.registerTag(EntityFormObject.class, "entity", (attribute, object) -> {
            return EntityTag.valueOf(object.modeledEntity.getBase().getUUID().toString(), attribute.context).getDenizenObject();
        });

        // <--[tag]
        // @attribute <MegModeledEntityTag.body_clamp_uneven>
        // @returns ElementTag(Boolean)
        // @plugin Megizen
        // @description
        // Returns whether the body clamp is uneven on the modeled entity.
        // Body clamp is used to clamp the body when the entity is standing still.
        // See also: <@link tag MegModeledEntityTag.max_body_angle>
        // See also: <@link tag MegModeledEntityTag.min_body_angle>
        // @mechanism MegModeledEntityTag.body_clamp_uneven
        // -->
        tagProcessor.registerTag(ElementTag.class, "body_clamp_uneven", (attribute, object) -> {
            return new ElementTag(object.getModeledEntity().getBase().getBodyRotationController().isBodyClampUneven());
        });

        // <--[tag]
        // @attribute <MegModeledEntityTag.entity_visible>
        // @returns ElementTag(Boolean)
        // @plugin Megizen
        // @description
        // Returns whether the base entity of the modeled entity is visible.
        // @mechanism MegModeledEntityTag.entity_visible
        // -->
        tagProcessor.registerTag(ElementTag.class, "entity_visible", (attribute, object) -> {
            return new ElementTag(object.modeledEntity.isBaseEntityVisible());
        });

        // <--[tag]
        // @attribute <MegModeledEntityTag.head_clamp_uneven>
        // @returns ElementTag(Boolean)
        // @plugin Megizen
        // @description
        // Returns whether the head clamp is uneven on the modeled entity.
        // Head clamp is used to clamp the head when the entity is moving.
        // See also: <@link tag MegModeledEntityTag.max_head_angle>
        // See also: <@link tag MegModeledEntityTag.max_head_angle>
        // @mechanism MegModeledEntityTag.head_clamp_uneven
        // -->
        tagProcessor.registerTag(ElementTag.class, "head_clamp_uneven", (attribute, object) -> {
            return new ElementTag(object.getModeledEntity().getBase().getBodyRotationController().isHeadClampUneven());
        });

        // <--[tag]
        // @attribute <MegModeledEntityTag.visible_to>
        // @returns ListTag(PlayerTag)
        // @plugin Megizen
        // @description
        // Returns a list of players that can see the modeled entity.
        // See also: <@link mechanism MegModeledEntityTag.hide_from>
        // See also: <@link mechanism MegModeledEntityTag.show_to>
        // -->
        tagProcessor.registerTag(ListTag.class, "visible_to", (attribute, object) -> {
            IEntityData entityData = object.modeledEntity.getBase().getData();
            if (entityData instanceof BukkitEntityData bukkitData) {
                return new ListTag(bukkitData.getTracked().getTrackedPlayer());
            }
            return null;
        });

        // <--[tag]
        // @attribute <MegModeledEntityTag.max_body_angle>
        // @returns ElementTag(Number)
        // @plugin Megizen
        // @description
        // Returns a ElementTag(Number) of the current maximum body angle of this entity.
        // See also: <@link tag MegModeledEntityTag.min_body_angle>
        // @mechanism MegModeledEntityTag.max_body_angle
        // -->
        tagProcessor.registerTag(ElementTag.class, "max_body_angle", (attribute, object) -> {
            return new ElementTag(object.getModeledEntity().getBase().getBodyRotationController().getMaxBodyAngle());
        });

        // <--[tag]
        // @attribute <MegModeledEntityTag.max_head_angle>
        // @returns ElementTag(Number)
        // @plugin Megizen
        // @description
        // Returns a ElementTag(Number) of the current maximum head angle of this entity.
        // See also: <@link tag MegModeledEntityTag.min_head_angle>
        // @mechanism MegModeledEntityTag.max_head_angle
        // -->
        tagProcessor.registerTag(ElementTag.class, "max_head_angle", (attribute, object) -> {
            return new ElementTag(object.getModeledEntity().getBase().getBodyRotationController().getMaxBodyAngle());
        });

        // <--[tag]
        // @attribute <MegModeledEntityTag.min_body_angle>
        // @returns ElementTag(Number)
        // @plugin Megizen
        // @description
        // Returns whether the base entity of the modeled entity is visible.
        // See also: <@link tag MegModeledEntityTag.max_body_angle>
        // @mechanism MegModeledEntityTag.min_body_angle
        // -->
        tagProcessor.registerTag(ElementTag.class, "min_body_angle", (attribute, object) -> {
            return new ElementTag(object.getModeledEntity().getBase().getBodyRotationController().getMinBodyAngle());
        });

        // <--[tag]
        // @attribute <MegModeledEntityTag.min_head_angle>
        // @returns ElementTag(Number)
        // @plugin Megizen
        // @description
        // Returns the minimum head angle of the modeled entity.
        // See also: <@link tag MegModeledEntityTag.max_head_angle>
        // @mechanism MegModeledEntityTag.min_head_angle
        // -->
        tagProcessor.registerTag(ElementTag.class, "min_head_angle", (attribute, object) -> {
            return new ElementTag(object.getModeledEntity().getBase().getBodyRotationController().getMinBodyAngle());
        });

        // <--[tag]
        // @attribute <MegModeledEntityTag.model[(model)]>
        // @returns MegActiveModelTag
        // @plugin Megizen
        // @description
        // Returns the active model with the specified name on the modeled entity.
        // If no name is specified, returns the first model on the modeled entity.
        // -->
        tagProcessor.registerTag(MegActiveModelTag.class, "model", (attribute, object) -> {
            String model;
            if (attribute.hasParam()) {
                model = attribute.getParam();
            } else {
                Iterator<String> iterator = object.modeledEntity.getModels().keySet().iterator();
                if (!iterator.hasNext()) {
                    return null;
                }
                model = iterator.next();
            }
            ActiveModel activeModel = object.modeledEntity.getModel(model).orElse(null);
            if (activeModel == null) {
                return null;
            }
            return new MegActiveModelTag(activeModel);
        });

        // <--[tag]
        // @attribute <MegModeledEntityTag.models>
        // @returns MapTag(MegActiveModelTag)
        // @plugin Megizen
        // @description
        // Returns a map of all models on the modeled entity.
        // The keys are the names of the models, and the values are the active models.
        // -->
        tagProcessor.registerTag(MapTag.class, "models", (attribute, object) -> {
            MapTag map = new MapTag();
            for (String model : object.modeledEntity.getModels().keySet()) {
                ActiveModel activeModel = object.modeledEntity.getModel(model).orElse(null);
                if (activeModel == null) {
                    continue;
                }
                map.putObject(model, new MegActiveModelTag(activeModel));
            }
            return map;
        });

        // <--[tag]
        // @attribute <MegModeledEntityTag.player_mode>
        // @returns ElementTag(Boolean)
        // @plugin Megizen
        // @description
        // Returns a ElementTag(Boolean) of whether the body will rotate like a player (i.e. will not try to match the head angle).
        // @mechanism MegModeledEntityTag.player_mode
        // -->
        tagProcessor.registerTag(ElementTag.class, "player_mode", (attribute, object) -> {
            return new ElementTag(object.getModeledEntity().getBase().getBodyRotationController().isPlayerMode());
        });

        // <--[tag]
        // @attribute <MegModeledEntityTag.rotation_delay>
        // @returns DurationTag
        // @plugin Megizen
        // @description
        // Returns a DurationTag of the delay before the body auto rotates to match the head's rotation.
        // See also: <@link tag MegModeledEntityTag.rotation_duration>
        // @mechanism MegModeledEntityTag.rotation_delay
        // -->
        tagProcessor.registerTag(DurationTag.class, "rotation_delay", (attribute, object) -> {
            return new DurationTag((long) object.getModeledEntity().getBase().getBodyRotationController().getRotationDelay());
        });

        // <--[tag]
        // @attribute <MegModeledEntityTag.rotation_duration>
        // @returns DurationTag
        // @plugin Megizen
        // @description
        // Returns a DurationTag the body takes to auto rotate back to the head's rotation.
        // See also: <@link tag MegModeledEntityTag.rotation_delay>
        // @mechanism MegModeledEntityTag.rotation_duration
        // -->
        tagProcessor.registerTag(DurationTag.class, "rotation_duration", (attribute, object) -> {
            return new DurationTag((long) object.getModeledEntity().getBase().getBodyRotationController().getRotationDuration());
        });

        // <--[tag]
        // @attribute <MedModeledEntityTag.should_save>
        // @returns ElementTag(Boolean)
        // @plugin Megizen
        // @description
        // Returns whether the modeled entity should be saved on unload.
        // @mechanism MegModeledEntityTag.should_save
        // -->
        tagProcessor.registerTag(ElementTag.class, "should_save", (attribute, object) -> {
            return new ElementTag(object.modeledEntity.shouldBeSaved());
        });

        // <--[tag]
        // @attribute <MegModeledEntityTag.stable_angle>
        // @returns ElementTag(Number)
        // @plugin Megizen
        // @description
        // Returns the angle in degrees where the body rotation can exceed the clamp before snapping towards the clamped values
        // @mechanism MegModeledEntityTag.stable_angle
        // -->
        tagProcessor.registerTag(ElementTag.class, "stable_angle", (attribute, object) -> {
            return new ElementTag(object.getModeledEntity().getBase().getBodyRotationController().getStableAngle());
        });

        // <--[mechanism]
        // @object MegModeledEntityTag
        // @name add_force_view
        // @input PlayerTag
        // @plugin Megizen
        // @description
        // Forces the modeled entity to only viewable by the input player.
        // If the model was created without the 'dummy' flag, this will do nothing.
        // Note: A forced viewer will be able to see the model forever, even when out of render radius.
        // See also: <MegModeledEntityTag.hidden_from>
        // -->
        tagProcessor.registerMechanism("force_view", false, PlayerTag.class, (object, mechanism, value) -> {
            BaseEntity<?> baseEntity = object.modeledEntity.getBase();
            if (baseEntity instanceof Dummy<?> dummy) {
                dummy.setForceViewing(value.getPlayerEntity(), true);
            }
            else {
                Debug.echoError("Cannot force view with a non-dummy.");
            }
        });

        // <--[mechanism]
        // @object MegModeledEntityTag
        // @name body_clamp_uneven
        // @input ElementTag(Boolean)
        // @plugin Megizen
        // @description
        // Sets whether the body clamp is uneven on the modeled entity.
        // Body clamp is used to clamp the body when the entity is standing still.
        // @tags
        // <MegModeledEntityTag.body_clamp_uneven>
        // -->
        tagProcessor.registerMechanism("body_clamp_uneven", false, ElementTag.class, (object, mechanism, value) -> {
            boolean uneven = value.asBoolean();
            object.modeledEntity.getBase().getBodyRotationController().setBodyClampUneven(uneven);
        });

        // <--[mechanism]
        // @object MegModeledEntityTag
        // @name entity_visible
        // @input ElementTag(Boolean)
        // @plugin Megizen
        // @description
        // Sets whether the base entity of the modeled entity is visible.
        // @tags
        // <MegModeledEntityTag.entity_visible>
        // -->
        tagProcessor.registerMechanism("entity_visible", false, ElementTag.class, (object, mechanism, value) -> {
            boolean visible = value.asBoolean();
            object.modeledEntity.setBaseEntityVisible(visible);
        });

        // <--[mechanism]
        // @object MegModeledEntityTag
        // @name force_view
        // @input MapTag
        // @plugin Megizen
        // @description
        // Forces the modeled entity to either be viewable or not by the input player.
        // If the model was created without the 'dummy' flag, this will do nothing.
        // Note: A forced viewer will be able to see the model forever, even when out of render radius.
        // See also: <MegModeledEntityTag.hidden_from>
        // -->
        tagProcessor.registerMechanism("force_view", false, MapTag.class, (object, mechanism, input) -> {
            BaseEntity<?> baseEntity = object.modeledEntity.getBase();
            if (baseEntity instanceof Dummy<?> dummy) {
                for (Map.Entry<StringHolder, ObjectTag> entry : input.entrySet()) {
                    PlayerTag player = PlayerTag.valueOf(entry.getKey().str, mechanism.context);
                    if (player == null) {
                        Debug.echoError("Invalid player provided: " + entry.getKey().str);
                        continue;
                    }
                    boolean view = new ElementTag(entry.getValue().toString()).asBoolean();
                    dummy.setForceViewing(player.getPlayerEntity(), view);
                }
            }
            else {
                Debug.echoError("Cannot force view with a non-dummy.");
            }
        });

        // <--[mechanism]
        // @object MegModeledEntityTag
        // @name head_clamp_uneven
        // @input ElementTag(Boolean)
        // @plugin Megizen
        // @description
        // Sets whether the head clamp is uneven on the modeled entity.
        // Head clamp is used to clamp the head when the entity is moving.
        // @tags
        // <MegModeledEntityTag.head_clamp_uneven>
        // -->
        tagProcessor.registerMechanism("head_clamp_uneven", false, ElementTag.class, (object, mechanism, value) -> {
            boolean uneven = value.asBoolean();
            object.modeledEntity.getBase().getBodyRotationController().setHeadClampUneven(uneven);
        });

        // <--[mechanism]
        // @object MegModeledEntityTag
        // @name hide_from
        // @input ListTag(PlayerTag)
        // @plugin Megizen
        // @description
        // Forces the modeled entity to be hidden from the input list of players.
        // See also: <MegModeledEntityTag.show_to>
        // @tags
        // <MegModeledEntityTag.visible_to>
        // -->
        tagProcessor.registerMechanism("hide_from", false, ListTag.class, (object, mechanism, value) -> {
            IEntityData entityData = object.modeledEntity.getBase().getData();
            if (entityData instanceof BukkitEntityData bukkitData) {
                TrackedEntity entity = bukkitData.getTracked();
                for (PlayerTag player : value.filter(PlayerTag.class, mechanism.context)) {
                    if (entity.getTrackedPlayer().contains(player.getUUID())) {
                        entity.addForcedHidden(player.getUUID());
                    }
                }
            }
        });

        // <--[mechanism]
        // @object MegModeledEntityTag
        // @name max_body_angle
        // @input ElementTag(Number)
        // @plugin Megizen
        // @description
        // Sets the maximum body angle of the modeled entity.
        // If <@link tag MegModeledEntityTag.body_clamp_uneven> is false, the minimum body angle will be set as the negative of this value.
        // @tags
        // <MegModeledEntityTag.max_body_angle>
        // -->
        tagProcessor.registerMechanism("max_body_angle", false, ElementTag.class, (object, mechanism, value) -> {
            float max = value.asFloat();
            object.modeledEntity.getBase().getBodyRotationController().setMaxBodyAngle(max);
        });

        // <--[mechanism]
        // @object MegModeledEntityTag
        // @name max_head_angle
        // @input ElementTag(Number)
        // @plugin Megizen
        // @description
        // Sets the maximum head angle of the modeled entity.
        // If <@link tag MegModeledEntityTag.head_clamp_uneven> is false, the minimum head angle will be set as the negative of this value.
        // @tags
        // <MegModeledEntityTag.max_head_angle>
        // -->
        tagProcessor.registerMechanism("max_head_angle", false, ElementTag.class, (object, mechanism, value) -> {
            float max = value.asFloat();
            object.modeledEntity.getBase().getBodyRotationController().setMaxHeadAngle(max);
        });

        // <--[mechanism]
        // @object MegModeledEntityTag
        // @name min_body_angle
        // @input ElementTag(Number)
        // @plugin Megizen
        // @description
        // Sets the minimum body angle of the modeled entity.
        // Will have no effect if <@link tag MegModeledEntityTag.body_clamp_uneven> is false.
        // @tags
        // <MegModeledEntityTag.min_body_angle>
        // -->
        tagProcessor.registerMechanism("min_body_angle", false, ElementTag.class, (object, mechanism, value) -> {
            float min = value.asFloat();
            object.modeledEntity.getBase().getBodyRotationController().setMinBodyAngle(min);
        });

        // <--[mechanism]
        // @object MegModeledEntityTag
        // @name min_head_angle
        // @input ElementTag(Number)
        // @plugin Megizen
        // @description
        // Sets the minimum head angle of the modeled entity.
        // Will have no effect if <@link tag MegModeledEntityTag.head_clamp_uneven> is false.
        // @tags
        // <MegModeledEntityTag.min_head_angle>
        // -->
        tagProcessor.registerMechanism("min_head_angle", false, ElementTag.class, (object, mechanism, value) -> {
            float min = value.asFloat();
            object.modeledEntity.getBase().getBodyRotationController().setMinHeadAngle(min);
        });

        // <--[mechanism]
        // @object MegModeledEntityTag
        // @name remove_force_view
        // @input PlayerTag
        // @plugin Megizen
        // @description
        // Removes the input player from the forced viewable list of the modeled entity.
        // If the model was created without the 'dummy' flag, this will do nothing.
        // Note: A forced viewer will be able to see the model forever, even when out of render radius.
        // See also: <MegModeledEntityTag.hidden_from>
        // -->
        tagProcessor.registerMechanism("remove_force_view", false, PlayerTag.class, (object, mechanism, value) -> {
            BaseEntity<?> baseEntity = object.modeledEntity.getBase();
            if (baseEntity instanceof Dummy<?> dummy) {
                dummy.setForceViewing(value.getPlayerEntity(), false);
            }
            else {
                Debug.echoError("Cannot force view a non-dummy entity.");
            }
        });

        // <--[mechanism]
        // @object MegModeledEntityTag
        // @name rotation_delay
        // @input DurationTag
        // @plugin Megizen
        // @description
        // Sets the delay before the body auto rotates to match the head's rotation.
        // See also: <@link tag MegModeledEntityTag.rotation_duration>
        // @tags
        // <MegModeledEntityTag.rotation_delay>
        // -->
        tagProcessor.registerMechanism("rotation_delay", false, DurationTag.class, (object, mechanism, value) -> {
            int delay = value.getTicksAsInt();
            object.modeledEntity.getBase().getBodyRotationController().setRotationDelay(delay);
        });

        // <--[mechanism]
        // @object MegModeledEntityTag
        // @name rotation_duration
        // @input DurationTag
        // @plugin Megizen
        // @description
        // Sets the time it takes for the body to auto rotate to match the head's rotation.
        // See also: <@link tag MegModeledEntityTag.rotation_delay>
        // @tags
        // <MegModeledEntityTag.rotation_duration>
        // -->
        tagProcessor.registerMechanism("rotation_duration", false, DurationTag.class, (object, mechanism, value) -> {
            int duration = value.getTicksAsInt();
            object.modeledEntity.getBase().getBodyRotationController().setRotationDuration(duration);
        });

        // <--[mechanism]
        // @object MegModeledEntityTag
        // @name player_mode
        // @input ElementTag(Boolean)
        // @plugin Megizen
        // @description
        // Sets whether the body will rotate like a player (i.e. will not try to match the head angle).
        // @tags
        // <MegModeledEntityTag.player_mode>
        // -->
        tagProcessor.registerMechanism("player_mode", false, ElementTag.class, (object, mechanism, value) -> {
            boolean player_mode = value.asBoolean();
            object.modeledEntity.getBase().getBodyRotationController().setPlayerMode(player_mode);
        });

        // <--[mechanism]
        // @object MegModeledEntityTag
        // @name should_save
        // @input ElementTag(Boolean)
        // @plugin Megizen
        // @description
        // Sets whether the modeled entity should be saved on unload.
        // @tags
        // <MegModeledEntityTag.should_save>
        // -->
        tagProcessor.registerMechanism("should_save", false, ElementTag.class, (object, mechanism, value) -> {
            boolean shouldSave = value.asBoolean();
            object.modeledEntity.setSaved(shouldSave);
        });

        // <--[mechanism]
        // @object MegModeledEntityTag
        // @name stable_angle
        // @input ElementTag(Number)
        // @plugin Megizen
        // @description
        // Sets the stable angle before correcting back to the clamped angle.
        // For example, if the clamp is 50, and the stable angle is 15, the body can be 65 degrees away from the head before snapping back to 50 degrees away.
        // This causes the "sudden jerk" effect on all entity body rotation.
        // @tags
        // <MegModeledEntityTag.stable_angle>
        // -->
        tagProcessor.registerMechanism("stable_angle", false, ElementTag.class, (object, mechanism, value) -> {
            float angle = value.asFloat();
            object.modeledEntity.getBase().getBodyRotationController().setStableAngle(angle);
        });

        // <--[mechanism]
        // @object MegModeledEntityTag
        // @name show_to
        // @input PlayerTag
        // @plugin Megizen
        // @description
        // Forces the modeled entity to be unhidden from the input player.
        // See also: <MegModeledEntityTag.hide_from>
        // @tags
        // <MegModeledEntityTag.visible_to>
        // -->
        tagProcessor.registerMechanism("show_to", false, PlayerTag.class, (object, mechanism, value) -> {
            IEntityData entityData = object.modeledEntity.getBase().getData();
            if (entityData instanceof BukkitEntityData bukkitData) {
                TrackedEntity entity = bukkitData.getTracked();
                if (entity.getTrackedPlayer().contains(value.getPlayerEntity().getUniqueId())) {
                    entity.removeForcedHidden(value.getPlayerEntity().getUniqueId());
                }
            }
        });
    }

    @Override
    public ObjectTag getObjectAttribute(Attribute attribute) {
        return tagProcessor.getObjectAttribute(this, attribute);
    }

    @Override
    public void adjust(Mechanism mechanism) {
        tagProcessor.processMechanism(this, mechanism);
    }

    @Override
    public void applyProperty(Mechanism mechanism) {
        Debug.echoError("Cannot apply properties to a MegModeledEntityTag!");
    }
}
