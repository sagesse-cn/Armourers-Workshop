package moe.plushie.armourers_workshop.core.skin.molang.thirdparty;

import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.ContextBinding;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.ContextSelector;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.EntitySelector;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.LevelSelector;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.LivingEntitySelector;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.PlayerSelector;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.ProjectileEntitySelector;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.variable.LivingEntityVariableBinding;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.query.EffectLevel;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.query.ItemEnchantmentLevel;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.query.RelativeBlockName;
import moe.plushie.armourers_workshop.core.skin.molang.thirdparty.function.BoneAccessFunction;
import moe.plushie.armourers_workshop.core.skin.molang.thirdparty.function.DumpFunction;
import moe.plushie.armourers_workshop.core.skin.molang.thirdparty.function.ModVersionFunction;

public class YSMBinding extends ContextBinding {

    public YSMBinding() {
        variable("rendering_in_inventory", ContextSelector::isRenderingInInventory);
        variable("first_person_mod_hide", ContextSelector::isRenderingInFirstPersonMod);

        variable("head_yaw", EntitySelector::getHeadYaw);
        variable("head_pitch", EntitySelector::getHeadPitch);

        variable("weather", LevelSelector::getWeather);
        variable("dimension_name", LevelSelector::getDimensionId);
        variable("fps", ContextSelector::getFPS);

        variable("is_passenger", EntitySelector::isPassenger);
        variable("is_sleep", EntitySelector::isSleeping);
        variable("is_sneak", EntitySelector::isSneaking);
        variable("is_open_air", EntitySelector::canSeeSky);
        variable("is_close_eyes", EntitySelector::isCloseEyes);
        variable("eye_in_water", EntitySelector::isUnderWater);
        variable("frozen_ticks", EntitySelector::getTicksFrozen);
        variable("air_supply", EntitySelector::getAirSupply);

        variable("has_helmet", hasEquipmentSlot("head"));
        variable("has_chest_plate", hasEquipmentSlot("chest"));
        variable("has_leggings", hasEquipmentSlot("legs"));
        variable("has_boots", hasEquipmentSlot("feet"));
        variable("has_mainhand", hasEquipmentSlot("mainhand"));
        variable("has_offhand", hasEquipmentSlot("offhand"));
        variable("has_elytra", hasEquipmentSlot("elytra"));

        variable("is_riptide", LivingEntitySelector::isAutoSpinAttack);
        variable("armor_value", LivingEntitySelector::getArmorValue);
        variable("hurt_time", LivingEntitySelector::getHurtTime);
        variable("on_ladder", LivingEntitySelector::isOnClimbable);
        variable("ladder_facing", LivingEntitySelector::getLastClimbableFacing);
        variable("arrow_count", LivingEntitySelector::getArrowCount);
        variable("stinger_count", LivingEntitySelector::getStingerCount);

        variable("food_level", PlayerSelector::getFoodLevel);
        variable("elytra_rot_x", PlayerSelector::getElytraYaw);
        variable("elytra_rot_y", PlayerSelector::getElytraPitch);
        variable("elytra_rot_z", PlayerSelector::getElytraRoll);

        variable("has_left_shoulder_parrot", PlayerSelector::hasLeftShoulderParrot);
        variable("has_right_shoulder_parrot", PlayerSelector::hasRightShoulderParrot);
        variable("left_shoulder_parrot_variant", PlayerSelector::getLeftShoulderParrotVariant);
        variable("right_shoulder_parrot_variant", PlayerSelector::getRightShoulderParrotVariant);

        variable("attack_damage", getAttributeValue("generic.attack_damage"));
        variable("attack_speed", getAttributeValue("generic.attack_speed"));
        variable("attack_knockback", getAttributeValue("generic.attack_knockback"));
        variable("movement_speed", getAttributeValue("generic.movement_speed"));
        variable("knockback_resistance", getAttributeValue("generic.knockback_resistance"));
        variable("luck", getAttributeValue("generic.luck"));

        variable("block_reach", getAttributeValue("forge:block_reach"));
        variable("entity_reach", getAttributeValue("forge:entity_reach"));
        variable("swim_speed", getAttributeValue("forge:swim_speed"));
        variable("entity_gravity", getAttributeValue("forge:entity_gravity"));
        variable("step_height_addition", getAttributeValue("forge:step_height_addition"));
        variable("nametag_distance", getAttributeValue("forge:nametag_distance"));

        variable("in_ground", ProjectileEntitySelector::isOnGround);
        variable("on_ground_time", ProjectileEntitySelector::getOnGroundTime);
        variable("is_spectral_arrow", ProjectileEntitySelector::isSpectral);
        variable("projectile_owner", ProjectileEntitySelector::getOwner);
        variable("delta_movement_length", ProjectileEntitySelector::distanceFromMove);

        function("mod_version", ModVersionFunction::new);
        function("equipped_enchantment_level", ItemEnchantmentLevel::new);
        function("effect_level", EffectLevel::new);
        function("relative_block_name", RelativeBlockName::new);

        function("bone_rot", BoneAccessFunction::rotation);
        function("bone_pos", BoneAccessFunction::position);
        function("bone_scale", BoneAccessFunction::scale);
        function("bone_pivot_abs", BoneAccessFunction::pivot);

        function("dump_equipped_item", DumpFunction::items);
        function("dump_relative_block", DumpFunction::blocks);
        function("dump_effects", DumpFunction::effects);
        function("dump_biome", DumpFunction::biomes);
        function("dump_mods", DumpFunction::mods);

        constant("biome_category", Result.NULL);
        constant("texture_name", Result.NULL);
    }

    private static LivingEntityVariableBinding hasEquipmentSlot(String name) {
        // the elytra have a space handle.
        if (!name.equals("elytra")) {
            return entity -> entity.getEquippedItem(name) != null;
        }
        return entity -> {
            var item = entity.getEquippedItem("chest");
            return item != null && item.getId().equals("minecraft:elytra");
        };
    }

    private static LivingEntityVariableBinding getAttributeValue(String name) {
        return entity -> entity.getAttributeValue(name);
    }
}
