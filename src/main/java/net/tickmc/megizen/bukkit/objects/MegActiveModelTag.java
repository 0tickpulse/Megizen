package net.tickmc.megizen.bukkit.objects;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.Adjustable;
import com.denizenscript.denizencore.objects.Fetchable;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ColorTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import org.bukkit.Color;

public class MegActiveModelTag implements ObjectTag, Adjustable {

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
        return "megactivemodel@" + activeModel.getModeledEntity().getBase().getUUID().toString() + "," + activeModel.getBlueprint().getName();
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
        // @attribute <MegActiveModelTag.damage_tint>
        // @returns ColorTag
        // @description
        // Returns the damage tint of the active model.
        // -->
        tagProcessor.registerTag(ColorTag.class, "damage_tint", (attribute, object) -> {
            Color tint = object.getActiveModel().getDamageTint();
            return new ColorTag(tint.getRed(), tint.getGreen(), tint.getBlue());
        });

        // <--[tag]
        // @attribute <MegActiveModelTag.modeled_entity>
        // @returns MegModeledEntityTag
        // @description
        // Returns the modeled entity of the active model.
        // -->
        tagProcessor.registerTag(MegModeledEntityTag.class, "modeled_entity", (attribute, object) -> {
            return new MegModeledEntityTag(object.getActiveModel().getModeledEntity());
        });

        // <--[mechanism]
        // @object MegActiveModelTag
        // @name damage_tint
        // @input ColorTag
        // @description
        // Sets the damage tint of the active model.
        // @tags
        // <MegActiveModelTag.damage_tint>
        // -->
        tagProcessor.registerMechanism("damage_tint", false, ColorTag.class, (object, mechanism, value) -> {
            object.getActiveModel().setDamageTint(Color.fromRGB(value.red, value.green, value.blue));
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
