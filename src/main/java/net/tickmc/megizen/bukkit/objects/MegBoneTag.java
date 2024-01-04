package net.tickmc.megizen.bukkit.objects;

import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizencore.objects.Adjustable;
import com.denizenscript.denizencore.objects.Fetchable;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ColorTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.MapTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import org.bukkit.Color;

import java.util.Map;

public class MegBoneTag implements ObjectTag, Adjustable {

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
        // @description
        // Returns the id of the bone.
        // -->
        tagProcessor.registerTag(ElementTag.class, "id", (attribute, object) -> {
            return new ElementTag(object.getBone().getBoneId());
        });

        // <--[tag]
        // @attribute <MegBoneTag.item>
        // @returns ItemTag
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
        // @description
        // Returns the location of the bone.
        // -->
        tagProcessor.registerTag(LocationTag.class, "location", (attribute, object) -> {
            return new LocationTag(object.getBone().getLocation());
        });

        // <--[mechanism]
        // @object MegBoneTag
        // @name damage_tint
        // @input ColorTag
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
