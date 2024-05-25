package net.tickmc.megizen.bukkit.objects;

import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizencore.objects.Adjustable;
import com.denizenscript.denizencore.objects.Fetchable;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ColorTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.core.MapTag;
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
import net.tickmc.megizen.bukkit.Megizen;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

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
        // @name skin_texture
        // @input ElementTag
        // @plugin Megizen
        // @description
        // Sets the skin texture of the bone.
        // This should only be used on bones that have the PLAYER_LIMB behavior.
        // This uses the format UUID|Texture|Name.
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
