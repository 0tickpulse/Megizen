package net.tickmc.megizen.bukkit.objects;

import com.denizenscript.denizen.Denizen;
import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizencore.objects.Adjustable;
import com.denizenscript.denizencore.objects.Fetchable;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.*;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.type.NameTag;
import net.tickmc.megizen.bukkit.Megizen;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MegBoneTag implements ObjectTag, Adjustable {

    // <--[ObjectType]
    // @name MegBoneTag
    // @prefix megbone
    // @base ElementTag
    // @format
    // The identity format for a MegBoneTag is <uuid> + <blueprint_name> + <bone_id>
    // Where the <uuid> is the UUID of the base entity, <blueprint_name> is the name of the model, and <bone_id> is the internal name/id of the bone.
    // For example: 'megbone@dfc67056-b15d-45dd-b239-482d92e482e5,capybara,left_leg'.
    //
    // @description
    // Represents a bone in an ActiveModel.
    //
    // -->

    //////////////////
    //    Object Fetcher
    ////////////////

    public static MegBoneTag valueOf(String string) {
        return valueOf(string, null);
    }

    @Fetchable("megbone")
    public static MegBoneTag valueOf(String string, TagContext context) {
        if (string == null) {
            return null;
        }
        try { // format: megbone@uuid,blueprint,boneid
            string = string.replace("megbone@", "");
            String[] split = string.split(",");
            ActiveModel am = MegActiveModelTag.valueOf(split[0] + "," + split[1]).getActiveModel();
            if (am == null) {
                return null;
            }
            ModelBone bone = am.getBone(split[2]).orElse(null);
            if (bone == null) {
                return null;
            }
            return new MegBoneTag(bone);
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

    public MegBoneTag(ModelBone bone) {
        this.bone = bone;
    }

    /////////////////////
    //   Instance Fields/Methods
    /////////////////

    private ModelBone bone;

    public ModelBone getBone() {
        return bone;
    }

    /////////////////////
    //  ObjectTag Methods
    ///////////////////

    private String prefix = "MegBone";

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
        return "megbone@"
            + bone.getActiveModel().getModeledEntity().getBase().getUUID().toString()
            + "," + bone.getActiveModel().getBlueprint().getName()
            + "," + bone.getBoneId();
    }

    @Override
    public String identifySimple() {
        return identify();
    }

    @Override
    public Object getJavaObject() {
        return bone;
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

    public static ObjectTagProcessor<MegBoneTag> tagProcessor = new ObjectTagProcessor<>();

    public static void registerTags() {

        // <--[tag]
        // @attribute <MegBoneTag.cached_left_rotation>
        // @returns LocationTag
        // @plugin Megizen
        // @description
        // Returns the cached left rotation of the bone as a vector.
        // -->
        tagProcessor.registerTag(VectorObject.class, "cached_left_rotation", (attribute, object) -> {
            return new LocationTag(Vector.fromJOML(object.getBone().getGlobalTransform().getLastLeftEuler()));
        });

        // <--[tag]
        // @attribute <MegBoneTag.cached_right_rotation>
        // @returns LocationTag
        // @plugin Megizen
        // @description
        // Returns the cached right rotation of the bone as a vector.
        // -->
        tagProcessor.registerTag(VectorObject.class, "cached_right_rotation", (attribute, object) -> {
            return new LocationTag(Vector.fromJOML(object.getBone().getGlobalTransform().getLastRightEuler()));
        });

        // <--[tag]
        // @attribute <MegBoneTag.children>
        // @returns MapTag(MegBoneTag)
        // @plugin Megizen
        // @description
        // Returns a map of all the children of the bone, with the bone id as the key and the bone as the value.
        // -->
        tagProcessor.registerTag(MapTag.class, "children", (attribute, object) -> {
            MapTag map = new MapTag();
            for (Map.Entry<String, ModelBone> entry : object.getBone().getChildren().entrySet()) {
                map.putObject(entry.getKey(), new MegBoneTag(entry.getValue()));
            }
            return map;
        });

        // <--[tag]
        // @attribute <MegBoneTag.damage_tint>
        // @returns ColorTag
        // @plugin Megizen
        // @mechanism MegBoneTag.damage_tint
        // @description
        // Returns the damage tint of the bone.
        // -->
        tagProcessor.registerTag(ColorTag.class, "damage_tint", (attribute, object) -> {
            Color tint = object.getBone().getDamageTint();
            return new ColorTag(tint.getRed(), tint.getGreen(), tint.getBlue());
        });

        // <--[tag]
        // @attribute <MegBoneTag.default_tint>
        // @returns ColorTag
        // @plugin Megizen
        // @mechanism MegBoneTag.default_tint
        // @description
        // Returns the default tint of the bone.
        // -->
        tagProcessor.registerTag(ColorTag.class, "default_tint", (attribute, object) -> {
            Color tint = object.getBone().getDefaultTint();
            return new ColorTag(tint.getRed(), tint.getGreen(), tint.getBlue());
        });

        // <--[tag]
        // @attribute <MegBoneTag.global_left_rotation>
        // @returns QuaternionTag
        // @plugin Megizen
        // @description
        // Returns the global left rotation of the bone as a quaternion.
        // -->
        tagProcessor.registerTag(QuaternionTag.class, "global_left_rotation", (attribute, object) -> {
            Quaternionf rotation = object.getBone().getGlobalTransform().getLeftQuaternion();
            return new QuaternionTag(rotation.x, rotation.y, rotation.z, rotation.w);
        });

        // <--[tag]
        // @attribute <MegBoneTag.global_right_rotation>
        // @returns QuaternionTag
        // @plugin Megizen
        // @description
        // Returns the global right rotation of the bone as a quaternion.
        // -->
        tagProcessor.registerTag(QuaternionTag.class, "global_right_rotation", (attribute, object) -> {
            Quaternionf rotation = object.getBone().getGlobalTransform().getRightQuaternion();
            return new QuaternionTag(rotation.x, rotation.y, rotation.z, rotation.w);
        });

        // <--[tag]
        // @attribute <MegBoneTag.global_position>
        // @returns LocationTag
        // @plugin Megizen
        // @mechanism MegBoneTag.global_position
        // @description
        // Returns the position of the bone as a vector.
        // -->
        tagProcessor.registerTag(VectorObject.class, "global_position", (attribute, object) -> {
            return new LocationTag(Vector.fromJOML(object.getBone().getGlobalTransform().getPosition()));
        });

        // <--[tag]
        // @attribute <MegBoneTag.glow_color>
        // @returns ColorTag
        // @plugin Megizen
        // @mechanism MegBoneTag.glow_color
        // @description
        // Returns the glow color of the bone.
        // -->
        tagProcessor.registerTag(ColorTag.class, "glow_color", (attribute, object) -> {
            Color color = Color.fromRGB(object.getBone().getGlowColor());
            return new ColorTag(color.getRed(), color.getGreen(), color.getBlue());
        });

        // <--[tag]
        // @attribute <MegBoneTag.id>
        // @returns ElementTag
        // @plugin Megizen
        // @description
        // Returns the id of the bone.
        // -->
        tagProcessor.registerTag(ElementTag.class, "id", (attribute, object) -> {
            return new ElementTag(object.getBone().getBoneId());
        });

        // <--[tag]
        // @attribute <MegBoneTag.item>
        // @returns ItemTag
        // @plugin Megizen
        // @mechanism MegBoneTag.item
        // @description
        // Returns the underlying item that the bone uses.
        // -->
        tagProcessor.registerTag(ItemTag.class, "item", (attribute, object) -> {
            return new ItemTag(object.getBone().getModel());
        });

        // <--[tag]
        // @attribute <MegBoneTag.is_glowing>
        // @returns ElementTag(Boolean)
        // @plugin Megizen
        // @mechanism MegBoneTag.is_glowing
        // @description
        // Returns whether the bone is glowing.
        // -->
        tagProcessor.registerTag(ElementTag.class, "is_glowing", (attribute, object) -> {
            return new ElementTag(object.getBone().isGlowing());
        });

        // <--[tag]
        // @attribute <MegBoneTag.location>
        // @returns LocationTag
        // @plugin Megizen
        // @description
        // Returns the location of the bone.
        // -->
        tagProcessor.registerTag(LocationTag.class, "location", (attribute, object) -> {
            return new LocationTag(object.getBone().getLocation());
        });

        // <--[tag]
        // @attribute <MegBoneTag.model>
        // @returns MegActiveModelTag
        // @plugin Megizen
        // @description
        // Returns the active model that the bone is a part of.
        // -->
        tagProcessor.registerTag(MegActiveModelTag.class, "model", (attribute, object) -> {
            return new MegActiveModelTag(object.getBone().getActiveModel());
        });

        // <--[tag]
        // @attribute <MegBoneTag.scale>
        // @returns LocationTag
        // @plugin Megizen
        // @mechanism MegBoneTag.scale
        // @description
        // Returns the scale of the bone as a vector.
        // -->
        tagProcessor.registerTag(VectorObject.class, "scale", (attribute, object) -> {
            return new LocationTag(Vector.fromJOML(object.getBone().getGlobalTransform().getScale()));
        });

        // <--[tag]
        // @attribute <MegBoneTag.skin_texture>
        // @returns ElementTag
        // @plugin Megizen
        // @mechanism MegBoneTag.skin_texture
        // @description
        // Returns the skin texture of the bone.
        // Returns null if the bone doesn't have a skin texture.
        // The format is UUID|Texture|Name.
        // -->
        tagProcessor.registerTag(ElementTag.class, "skin_texture", (attribute, object) -> {
            ModelBone bone = object.getBone();
            PlayerProfile profile = bone.getBoneBehavior(BoneBehaviorTypes.PLAYER_LIMB).map(behavior -> {
                ItemStack stack = behavior.getBone().getModel();
                SkullMeta meta = (SkullMeta) stack.getItemMeta();
                return meta.getPlayerProfile();
            }).orElse(null);
            if (profile == null) {
                return null;
            }
            UUID id = profile.getId();
            String texture = profile.getProperties().stream().filter(prop -> prop.getName().equals("textures")).map(ProfileProperty::getValue).findFirst().orElse(null);
            String name = profile.getName();
            List<String> list = new ArrayList<>();
            if (id != null) {
                list.add(id.toString());
            }
            if (texture != null) {
                list.add(texture);
            }
            if (name != null) {
                list.add(name);
            }
            return new ElementTag(String.join("|", list));
        });

        // <--[tag]
        // @attribute <MegBoneTag.yaw>
        // @returns ElementTag(Decimal)
        // @plugin Megizen
        // @description
        // Returns the yaw rotation of the bone.
        // -->
        tagProcessor.registerTag(ElementTag.class, "yaw", (attribute, object) -> {
            return new ElementTag(object.getBone().getYaw());
        });

        // <--[tag]
        // @attribute <MegBoneTag.nametag_visible>
        // @returns ElementTag(Boolean)
        // @plugin Megizen
        // @description
        // Returns whether the bone's nametag is visible.
        // -->
        tagProcessor.registerTag(ElementTag.class, "nametag_visible", (attribute, object) ->
                object.getBone()
                        .getBoneBehavior(BoneBehaviorTypes.NAMETAG)
                        .map(behavior -> new ElementTag(((NameTag) behavior).isVisible()))
                        .orElse(null)
        );


        // <--[mechanism]
        // @object MegBoneTag
        // @name damage_tint
        // @input ColorTag
        // @plugin Megizen
        // @description
        // Sets the damage tint of the bone.
        // @tags
        // <MegBoneTag.damage_tint>
        // -->
        tagProcessor.registerMechanism("damage_tint", false, ColorTag.class, (object, mechanism, value) -> {
            ColorTag color = mechanism.valueAsType(ColorTag.class);
            object.getBone().setDamageTint(Color.fromRGB(color.red, color.green, color.blue));
        });

        // <--[mechanism]
        // @object MegBoneTag
        // @name default_tint
        // @input ColorTag
        // @plugin Megizen
        // @description
        // Sets the default tint of the bone.
        // @tags
        // <MegBoneTag.default_tint>
        // -->
        tagProcessor.registerMechanism("default_tint", false, ColorTag.class, (object, mechanism, value) -> {
            ColorTag color = mechanism.valueAsType(ColorTag.class);
            object.getBone().setDefaultTint(Color.fromRGB(color.red, color.green, color.blue));
        });

        // <--[mechanism]
        // @object MegBoneTag
        // @name glow_color
        // @input ColorTag
        // @plugin Megizen
        // @description
        // Sets the glow color of the bone.
        // @tags
        // <MegBoneTag.glow_color>
        // -->
        tagProcessor.registerMechanism("glow_color", false, ColorTag.class, (object, mechanism, value) -> {
            ColorTag color = mechanism.valueAsType(ColorTag.class);
            object.getBone().setGlowColor(color.asRGB());
        });

        // <--[mechanism]
        // @object MegBoneTag
        // @name item
        // @input ItemTag
        // @plugin Megizen
        // @description
        // Sets the underlying item that the bone uses.
        // Shouldn't be used unless you know what you're doing.
        // @tags
        // <MegBoneTag.item>
        // -->
        tagProcessor.registerMechanism("item", false, ItemTag.class, (object, mechanism, value) -> {
            ItemTag item = mechanism.valueAsType(ItemTag.class);
            object.getBone().setModel(item.getItemStack());
        });

        // <--[mechanism]
        // @object MegBoneTag
        // @name is_glowing
        // @input ElementTag(Boolean)
        // @plugin Megizen
        // @description
        // Sets whether the bone is glowing.
        // @tags
        // <MegBoneTag.is_glowing>
        // -->
        tagProcessor.registerMechanism("is_glowing", false, ElementTag.class, (object, mechanism, value) -> {
            object.getBone().setGlowing(value.asBoolean());
        });

        // <--[mechanism]
        // @object MegBoneTag
        // @name scale
        // @input ElementTag(Number)
        // @plugin Megizen
        // @description
        // Sets the scale of the bone.
        // @tags
        // <MegBoneTag.scale>
        // -->
        tagProcessor.registerMechanism("scale", false, ElementTag.class, (object, mechanism, value) -> {
            object.getBone().setModelScale(value.asInt());
        });

        // <--[mechanism]
        // @object MegBoneTag
        // @name nametag_text
        // @input ElementTag
        // @plugin Megizen
        // @description
        // Sets the text of the bone's nametag (NAMETAG behavior).
        // @tags
        // <MegBoneTag.nametag_text>
        // -->
        tagProcessor.registerMechanism("nametag_text", false, ElementTag.class, (object, mechanism, value) -> {
            object.getBone().getBoneBehavior(BoneBehaviorTypes.NAMETAG).ifPresent(behavior -> {
                ((NameTag) behavior).setJsonString(value.asString());
            });
        });

        // <--[mechanism]
        // @object MegBoneTag
        // @name nametag_visible
        // @input ElementTag(Boolean)
        // @plugin Megizen
        // @description
        // Sets whether the bone's nametag is visible (NAMETAG behavior).
        // @tags
        // <MegBoneTag.nametag_visible>
        // -->
        tagProcessor.registerMechanism("nametag_visible", false, ElementTag.class, (object, mechanism, value) -> {
            object.getBone().getBoneBehavior(BoneBehaviorTypes.NAMETAG).ifPresent(behavior -> {
                behavior.setVisible(value.asBoolean());
            });
        });


        if (Denizen.supportsPaper) {

            // <--[mechanism]
            // @object MegBoneTag
            // @name skin_texture
            // @input ElementTag
            // @plugin Megizen
            // @description
            // Sets the skin texture of the bone.
            // This should only be used on bones that have the PLAYER_LIMB behavior.
            // This uses the format UUID|Texture|Name.
            // Note: This mechanism is only available on Paper servers.
            // @tags
            // <MegBoneTag.skin_texture>
            // -->
            tagProcessor.registerMechanism("skin_texture", false, ElementTag.class, (object, mechanism, value) -> {
                ModelBone bone = object.getBone();
                ListTag list = mechanism.valueAsType(ListTag.class);
                String idString = list.get(0);
                String texture = null;
                if (list.size() == 1 && idString.length() > 64) {
                    texture = idString;
                    idString = null;
                }
                if (list.size() > 1) {
                    texture = list.get(1);
                }
                PlayerProfile profile;
                if (idString == null) {
                    profile = Megizen.instance.getServer().createProfile(new UUID(0, 0), "null");
                } else if (idString.length() < 3 && list.size() == 2) {
//                profile = new PlayerProfile(idString, new UUID(0, 0));
                    profile = Megizen.instance.getServer().createProfile(new UUID(0, 0), idString);
                } else {
                    if (CoreUtilities.contains(idString, '-')) {
                        UUID uuid = UUID.fromString(idString);
                        String name = null;
                        if (list.size() > 2) {
                            name = list.get(2);
                        }
//                    profile = new PlayerProfile(name, uuid, texture);
                        profile = Megizen.instance.getServer().createProfile(uuid, name);
                    } else {
//                    profile = new PlayerProfile(idString, Settings.nullifySkullSkinIds ? new UUID(0, 0) : null, texture);
                        profile = Megizen.instance.getServer().createProfile(new UUID(0, 0), idString);
                    }
                }
                if (texture == null || profile.getId() == null) { // Load if needed
                    profile.complete(); // TODO: perhaps async this?
                }
                if (texture != null) {
                    // Format is texture;signature
                    List<String> split = CoreUtilities.split(texture, ';');
                    String textureString = split.get(0);
                    String signatureString = split.size() > 1 ? split.get(1) : null;
                    profile.setProperty(new ProfileProperty("textures", textureString, signatureString));
                }
                if (profile.getTextures().isEmpty()) {
                    return; // Can't set a skull skin to nothing.
                }
                PlayerProfile finalProfile = profile;
                bone.getBoneBehavior(BoneBehaviorTypes.PLAYER_LIMB).ifPresent(behavior -> {
                    behavior.setTexture(finalProfile);
                });
            });
        }
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
        Debug.echoError("Cannot apply properties to a MegBoneTag!");
    }
}
