package net.tickmc.megizen.bukkit.objects;

import com.denizenscript.denizen.objects.EntityFormObject;
import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.Adjustable;
import com.denizenscript.denizencore.objects.Fetchable;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.core.MapTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;

public class MegModeledEntityTag implements ObjectTag, Adjustable {

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
        return "megmodeledentity@" + modeledEntity.getBase().getUUID().toString();
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
        // @attribute <MedModeledEntityTag.should_save>
        // @returns ElementTag(Boolean)
        // @description
        // Returns whether the modeled entity should be saved on unload.
        // @mechanism MegModeledEntityTag.should_save
        // -->
        tagProcessor.registerTag(ElementTag.class, "should_save", (attribute, object) -> {
            return new ElementTag(object.modeledEntity.shouldBeSaved());
        });

        // <--[tag]
        // @attribute <MegModeledEntityTag.models>
        // @returns ListTag
        // @description
        // Returns a list of all models on the modeled entity.
        // -->
        tagProcessor.registerTag(ListTag.class, "models", (attribute, object) -> {
            return new ListTag(object.modeledEntity.getModels().keySet());
        });

        // <--[tag]
        // @attribute <MegModeledEntityTag.model[(model)]>
        // @returns MegActiveModelTag
        // @description
        // Returns the active model with the specified name on the modeled entity.
        // If no name is specified, returns the first model on the modeled entity.
        // -->
        tagProcessor.registerTag(MegActiveModelTag.class, "model", (attribute, object) -> {
            String model = attribute.hasParam() ? attribute.getParam() : object.modeledEntity.getModels().keySet().iterator().next();
            ActiveModel activeModel = object.modeledEntity.getModel(model).orElse(null);
            if (activeModel == null) {
                return null;
            }
            return new MegActiveModelTag(activeModel);
        });

        // <--[tag]
        // @attribute <MegModeledEntityTag.entity_visible>
        // @returns ElementTag(Boolean)
        // @description
        // Returns whether the base entity of the modeled entity is visible.
        // @mechanism MegModeledEntityTag.entity_visible
        // -->
        tagProcessor.registerTag(ElementTag.class, "entity_visible", (attribute, object) -> {
            return new ElementTag(object.modeledEntity.isBaseEntityVisible());
        });

        // <--[tag]
        // @attribute <MegModeledEntityTag.models>
        // @returns MapTag(MegActiveModelTag)
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

        // <--[mechanism]
        // @object MegModeledEntityTag
        // @name should_save
        // @input ElementTag(Boolean)
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
        // @name entity_visible
        // @input ElementTag(Boolean)
        // @description
        // Sets whether the base entity of the modeled entity is visible.
        // @tags
        // <MegModeledEntityTag.entity_visible>
        // -->
        tagProcessor.registerMechanism("entity_visible", false, ElementTag.class, (object, mechanism, value) -> {
            boolean visible = value.asBoolean();
            object.modeledEntity.setBaseEntityVisible(visible);
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
