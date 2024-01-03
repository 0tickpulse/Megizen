package net.tickmc.megizen.bukkit.objects;

import com.denizenscript.denizencore.objects.Adjustable;
import com.denizenscript.denizencore.objects.Fetchable;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ColorTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import org.bukkit.Color;

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
