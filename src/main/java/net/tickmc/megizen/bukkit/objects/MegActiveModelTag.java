package net.tickmc.megizen.bukkit.objects;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizencore.objects.Adjustable;
import com.denizenscript.denizencore.objects.Fetchable;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.*;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import org.bukkit.Color;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.Map;

public class MegActiveModelTag implements ObjectTag, Adjustable {

    // <--[ObjectType]
    // @name MegActiveModelTag
    // @prefix megactivemodel
    // @base ElementTag
    // @format
    // The identity format for active models is <uuid> + <blueprint_name>
    // Where <uuid> is the UUID of the base entity, and <blueprint_name> is the name of the model.
    // For example: 'megactivemodel@7902f97a-07dd-406e-8a07-b6bee072a303,capybara'.
    //
    // @plugin Megizen
    // @description
    // Represents a model that is attached to an entity.
    //
    // -->

    //////////////////
    //    Object Fetcher
    ////////////////

    public static MegActiveModelTag valueOf(String string) {
        return valueOf(string, null);
    }

    @Fetchable("megactivemodel")
    public static MegActiveModelTag valueOf(String string, TagContext context) {
        if (string == null) {
            return null;
        }
        try {
            string = string.replace("megactivemodel@", "");
            String[] split = string.split(",");
            ModeledEntity me = ModelEngineAPI.getModeledEntity(EntityTag.valueOf(split[0], context).getUUID());
            if (me == null) {
                return null;
            }
            if (split.length == 1) {
                return new MegActiveModelTag(me.getModels().values().iterator().next());
            }
            ActiveModel am = me.getModel(split[1]).orElse(me.getModels().values().iterator().next());
            return new MegActiveModelTag(am);
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

    public MegActiveModelTag(ActiveModel am) {
        this.activeModel = am;
    }

    /////////////////////
    //   Instance Fields/Methods
    /////////////////

    private ActiveModel activeModel;

    public ActiveModel getActiveModel() {
        return activeModel;
    }

    /////////////////////
    //  ObjectTag Methods
    ///////////////////

    private String prefix = "MegActiveModel";

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
        return "megactivemodel@"
                + activeModel.getModeledEntity().getBase().getUUID().toString()
                + "," + activeModel.getBlueprint().getName();
    }

    @Override
    public String identifySimple() {
        return identify();
    }

    @Override
    public Object getJavaObject() {
        return activeModel;
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

    public static ObjectTagProcessor<MegActiveModelTag> tagProcessor = new ObjectTagProcessor<>();

    public static void registerTags() {

        // <--[tag]
        // @attribute <MegActiveModelTag.bone[<id>]>
        // @returns MegBoneTag
        // @plugin Megizen
        // @description
        // Returns the bone with the specified id of the model.
        // -->
        tagProcessor.registerTag(MegBoneTag.class, "bone", (attribute, object) -> {
            String id = attribute.getParam();
            ModelBone bone = object.getActiveModel().getBone(id).orElse(null);
            if (bone == null) {
                return null;
            }
            return new MegBoneTag(bone);
        });

        // <--[tag]
        // @attribute <MegActiveModelTag.bones>
        // @returns MapTag(MegBoneTag)
        // @plugin Megizen
        // @description
        // Returns a map of all the bones of the model, with the bone id as the key and the bone as the value.
        // -->
        tagProcessor.registerTag(MapTag.class, "bones", (attribute, object) -> {
            MapTag map = new MapTag();
            for (Map.Entry<String, ModelBone> entry : object.getActiveModel().getBones().entrySet()) {
                map.putObject(entry.getKey(), new MegBoneTag(entry.getValue()));
            }
            return map;
        });

        // <--[tag]
        // @attribute <MegActiveModelTag.can_drive>
        // @returns ElementTag(Boolean)
        // @description
        // Returns whether the active model can be driven.
        // -->
        tagProcessor.registerTag(ElementTag.class, "can_drive", (attribute, object) -> {
            if (object.getActiveModel().getMountManager().isEmpty()) {
                return null;
            }
            return new ElementTag(object.getActiveModel().getMountManager().get().canDrive());
        });

        // <--[tag]
        // @attribute <MegActiveModelTag.can_ride>
        // @returns ElementTag(Boolean)
        // @plugin Megizen
        // @description
        // Returns whether the active model can be ridden.
        // -->
        tagProcessor.registerTag(ElementTag.class, "can_ride", (attribute, object) -> {
            if (object.getActiveModel().getMountManager().isEmpty()) {
                return null;
            }
            return new ElementTag(object.getActiveModel().getMountManager().get().canRide());
        });

        // <--[tag]
        // @attribute <MegActiveModelTag.damage_tint>
        // @returns ColorTag
        // @plugin Megizen
        // @mechanism MegActiveModelTag.damage_tint
        // @description
        // Returns the damage tint of the active model.
        // -->
        tagProcessor.registerTag(ColorTag.class, "damage_tint", (attribute, object) -> {
            Color tint = object.getActiveModel().getDamageTint();
            return new ColorTag(tint.getRed(), tint.getGreen(), tint.getBlue());
        });

        // <--[tag]
        // @attribute <MegActiveModelTag.default_tint>
        // @returns ColorTag
        // @plugin Megizen
        // @mechanism MegActiveModelTag.default_tint
        // @description
        // Returns the default tint of the active model.
        // -->
        tagProcessor.registerTag(ColorTag.class, "default_tint", (attribute, object) -> {
            Color tint = object.getActiveModel().getDefaultTint();
            return new ColorTag(tint.getRed(), tint.getGreen(), tint.getBlue());
        });

        // <--[tag]
        // @attribute <MegActiveModelTag.driver>
        // @returns EntityTag
        // @plugin Megizen
        // @description
        // Returns a EntityTag of the driver of the active model.
        // -->
        tagProcessor.registerTag(EntityTag.class, "driver", (attribute, object) -> {
            if (object.getActiveModel().getMountManager().isEmpty()) {
                return null;
            }
            return new EntityTag(object.getActiveModel().getMountManager().get().getDriver());
        });

        // <--[tag]
        // @attribute <MegActiveModelTag.driver_bone>
        // @returns MegBoneTag
        // @plugin Megizen
        // @description
        // Returns a MegBoneTag of the driver bone of the active model.
        // -->
        tagProcessor.registerTag(MegBoneTag.class, "driver_bone", (attribute, object) -> {
            if (object.getActiveModel().getMountManager().isEmpty()) {
                return null;
            }
            return new MegBoneTag(object.getActiveModel().getMountManager().get().getDriverBone());
        });

        // <--[tag]
        // @attribute <MegActiveModelTag.has_passengers>
        // @returns ElementTag(Boolean)
        // @plugin Megizen
        // @description
        // Returns a ElementTag(Boolean) of whether the active model has passengers.
        // -->
        tagProcessor.registerTag(ElementTag.class, "has_passengers", (attribute, object) -> {
            if (object.getActiveModel().getMountManager().isEmpty()) {
                return null;
            }
            return new ElementTag(object.getActiveModel().getMountManager().get().hasPassengers());
        });

        // <--[tag]
        // @attribute <MegActiveModelTag.has_riders>
        // @returns ElementTag(Boolean)
        // @plugin Megizen
        // @description
        // Returns a ElementTag(Boolean) of whether the active model has riders.
        // -->
        tagProcessor.registerTag(ElementTag.class, "has_riders", (attribute, object) -> {
            if (object.getActiveModel().getMountManager().isEmpty()) {
                return null;
            }
            return new ElementTag(object.getActiveModel().getMountManager().get().hasRiders());
        });

        // <--[tag]
        // @attribute <MegActiveModelTag.hitbox_scale>
        // @returns LocationTag
        // @plugin Megizen
        // @mechanism MegActiveModelTag.hitbox_scale
        // @description
        // Returns the scale of the active model's hitbox as a vector.
        // -->
        tagProcessor.registerTag(VectorObject.class, "hitbox_scale", (attribute, object) -> {
            return new LocationTag(Vector.fromJOML(object.getActiveModel().getHitboxScale()));
        });

        // <--[tag]
        // @attribute <MegActiveModelTag.hitbox_visible>
        // @returns ElementTag(Boolean)
        // @plugin Megizen
        // @mechanism MegActiveModelTag.hitbox_visible
        // @description
        // Returns whether the hitbox of the active model is visible.
        // -->
        tagProcessor.registerTag(ElementTag.class, "hitbox_visible", (attribute, object) -> {
            return new ElementTag(object.getActiveModel().isHitboxVisible());
        });

        // <--[tag]
        // @attribute <MegActiveModelTag.lock_yaw>
        // @returns ElementTag(Boolean)
        // @plugin Megizen
        // @mechanism MegActiveModelTag.lock_yaw
        // @description
        // Returns whether the yaw of the active model is locked.
        // -->
        tagProcessor.registerTag(ElementTag.class, "lock_yaw", (attribute, object) -> {
            return new ElementTag(object.getActiveModel().isLockYaw());
        });

        // <--[tag]
        // @attribute <MegActiveModelTag.lock_pitch>
        // @returns ElementTag(Boolean)
        // @plugin Megizen
        // @mechanism MegActiveModelTag.lock_pitch
        // @description
        // Returns whether the pitch of the active model is locked.
        // -->
        tagProcessor.registerTag(ElementTag.class, "lock_pitch", (attribute, object) -> {
            return new ElementTag(object.getActiveModel().isLockPitch());
        });

        // <--[tag]
        // @attribute <MegActiveModelTag.modeled_entity>
        // @returns MegModeledEntityTag
        // @plugin Megizen
        // @description
        // Returns the modeled entity of the active model.
        // -->
        tagProcessor.registerTag(MegModeledEntityTag.class, "modeled_entity", (attribute, object) -> {
            return new MegModeledEntityTag(object.getActiveModel().getModeledEntity());
        });

        // <--[tag]
        // @attribute <MegActiveModelTag.name>
        // @returns ElementTag
        // @plugin Megizen
        // @description
        // Returns the name of the active model.
        // -->
        tagProcessor.registerTag(ElementTag.class, "name", (attribute, object) -> {
            return new ElementTag(object.getActiveModel().getBlueprint().getName());
        });

        // <--[tag]
        // @attribute <MegActiveModelTag.passengers>
        // @returns ListTag(EntityTag)
        // @plugin Megizen
        // @description
        // Returns a ListTag(EntityTag) of the passengers currently on the active model.
        // -->
        tagProcessor.registerTag(ListTag.class, "passengers", (attribute, object) -> {
            if (object.getActiveModel().getMountManager().isEmpty()) {
                return null;
            }
            return new ListTag(object.getActiveModel().getMountManager().get().getPassengerSeatMap().keySet());
        });

        // <--[tag]
        // @attribute <MegActiveModelTag.scale>
        // @returns LocationTag
        // @plugin Megizen
        // @mechanism MegActiveModelTag.scale
        // @description
        // Returns the scale of the active model as a vector.
        // -->
        tagProcessor.registerTag(VectorObject.class, "scale", (attribute, object) -> {
            return new LocationTag(Vector.fromJOML(object.getActiveModel().getScale()));
        });

        // <--[tag]
        // @attribute <MegActiveModelTag.seats>
        // @returns ListTag(ElementTag)
        // @plugin Megizen
        // @description
        // Returns a ListTag of seats on the active model.
        // -->
        tagProcessor.registerTag(ListTag.class, "seats", (attribute, object) -> {
            if (object.getActiveModel().getMountManager().isEmpty()) {
                return null;
            }
            return new ListTag(object.getActiveModel().getMountManager().get().getSeats().keySet());
        });

        // <--[tag]
        // @attribute <MegActiveModelTag.seat_map>
        // @returns MapTag
        // @plugin Megizen
        // @description
        // Returns a MapTag of entities on the active model, if any.
        // Keys will be the bones entities are mounted on, while the values of those keys will be the entities themselves.
        // Only returns the bones of entities who are mounted on the active model.
        // -->
        tagProcessor.registerTag(MapTag.class, "seat_map", (attribute, object) -> {
            if (object.getActiveModel().getMountManager().isEmpty()) {
                return null;
            }
            MapTag map = new MapTag();
            for (Map.Entry<Entity, Mount> entry : object.getActiveModel().getMountManager().get().getPassengerSeatMap().entrySet()) {
                String bone = ((BoneBehavior) entry.getValue()).getBone().getBoneId();
                map.putObject(bone, new EntityTag(entry.getKey()));
            }
            return map;
        });
        
        // <--[mechanism]
        // @object MegActiveModelTag
        // @name can_drive
        // @input ElementTag(Boolean)
        // @plugin Megizen
        // @description
        // Sets whether the active model can be driven.
        // @tags
        // <MegActiveModelTag.can_drive>
        // -->
        tagProcessor.registerMechanism("can_drive", false, ElementTag.class, (object, mechanism, value) -> {
            if (object.getActiveModel().getMountManager().isEmpty()) return;
            object.getActiveModel().getMountManager().get().setCanDrive(value.asBoolean());
        });

        // <--[mechanism]
        // @object MegActiveModelTag
        // @name can_ride
        // @input ElementTag(Boolean)
        // @plugin Megizen
        // @description
        // Sets whether the active model can be ridden.
        // @tags
        // <MegActiveModelTag.can_ride>
        // -->
        tagProcessor.registerMechanism("can_ride", false, ElementTag.class, (object, mechanism, value) -> {
            if (object.getActiveModel().getMountManager().isEmpty()) return;
            object.getActiveModel().getMountManager().get().setCanRide(value.asBoolean());
        });

        // <--[mechanism]
        // @object MegActiveModelTag
        // @name damage_tint
        // @input ColorTag
        // @plugin Megizen
        // @description
        // Sets the damage tint of the active model.
        // @tags
        // <MegActiveModelTag.damage_tint>
        // -->
        tagProcessor.registerMechanism("damage_tint", false, ColorTag.class, (object, mechanism, value) -> {
            object.getActiveModel().setDamageTint(Color.fromRGB(value.red, value.green, value.blue));
        });
        // <--[mechanism]
        // @object MegActiveModelTag
        // @name dismount_all
        // @plugin Megizen
        // @description
        // Dismounts everyone mounted on the active model.
        // -->
        tagProcessor.registerMechanism("dismount_all", false, (object, mechanism) -> {
            if (object.getActiveModel().getMountManager().isEmpty()) return;
            object.getActiveModel().getMountManager().get().dismountAll();
        });

        // <--[mechanism]
        // @object MegActiveModelTag
        // @name dismount_driver
        // @plugin Megizen
        // @description
        // Dismounts the driver of the active model.
        // -->
        tagProcessor.registerMechanism("dismount_driver", false, (object, mechanism) -> {
            if (object.getActiveModel().getMountManager().isEmpty()) return;
            object.getActiveModel().getMountManager().get().dismountDriver();
        });

        // <--[mechanism]
        // @object MegActiveModelTag
        // @name default_tint
        // @input ColorTag
        // @plugin Megizen
        // @description
        // Sets the default tint of the active model.
        // @tags
        // <MegActiveModelTag.default_tint>
        // -->
        tagProcessor.registerMechanism("default_tint", false, ColorTag.class, (object, mechanism, value) -> {
            object.getActiveModel().setDefaultTint(Color.fromRGB(value.red, value.green, value.blue));
        });

        // <--[mechanism]
        // @object MegActiveModelTag
        // @name hitbox_scale
        // @input ElementTag(decimal)
        // @plugin Megizen
        // @description
        // Sets the scale of the active model's hitbox.
        // @tags
        // <MegActiveModelTag.hitbox_scale>
        // -->
        tagProcessor.registerMechanism("hitbox_scale", false, ElementTag.class, (object, mechanism, value) -> {
            object.getActiveModel().setHitboxScale(value.asDouble());
        });


        // <--[mechanism]
        // @object MegActiveModelTag
        // @name hitbox_visible
        // @input ElementTag(Boolean)
        // @plugin Megizen
        // @description
        // Sets whether the hitbox of the active model is visible.
        // @tags
        // <MegActiveModelTag.hitbox_visible>
        // -->
        tagProcessor.registerMechanism("hitbox_visible", false, ElementTag.class, (object, mechanism, value) -> {
            object.getActiveModel().setHitboxVisible(value.asBoolean());
        });

        // <--[mechanism]
        // @object MegActiveModelTag
        // @name lock_pitch
        // @input ElementTag(Boolean)
        // @plugin Megizen
        // @description
        // Sets whether the pitch of the active model is locked.
        // @tags
        // <MegActiveModelTag.lock_pitch>
        // -->
        tagProcessor.registerMechanism("lock_pitch", false, ElementTag.class, (object, mechanism, value) -> {
            object.getActiveModel().setLockPitch(value.asBoolean());
        });

        // <--[mechanism]
        // @object MegActiveModelTag
        // @name lock_yaw
        // @input ElementTag(Boolean)
        // @plugin Megizen
        // @description
        // Sets whether the yaw of the active model is locked.
        // @tags
        // <MegActiveModelTag.lock_yaw>
        // -->
        tagProcessor.registerMechanism("lock_yaw", false, ElementTag.class, (object, mechanism, value) -> {
            object.getActiveModel().setLockYaw(value.asBoolean());
        });

        // <--[mechanism]
        // @object MegActiveModelTag
        // @name scale
        // @input ElementTag(decimal)
        // @plugin Megizen
        // @description
        // Sets the scale of the active model.
        // @tags
        // <MegActiveModelTag.scale>
        // -->
        tagProcessor.registerMechanism("scale", false, ElementTag.class, (object, mechanism, value) -> {
            object.getActiveModel().setScale(value.asDouble());
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
        Debug.echoError("Cannot apply properties to a MegActiveModelTag!");
    }
}
