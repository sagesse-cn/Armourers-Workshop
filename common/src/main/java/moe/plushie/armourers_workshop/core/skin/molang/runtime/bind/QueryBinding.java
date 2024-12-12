package moe.plushie.armourers_workshop.core.skin.molang.runtime.bind;

import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.ContextSelector;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.EntitySelector;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.LevelSelector;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.LivingEntitySelector;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.PlayerSelector;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.builtin.Print;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.query.BiomeHasAllTags;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.query.BiomeHasAnyTag;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.query.ItemHasAllTags;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.query.ItemHasAnyName;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.query.ItemHasAnyTag;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.query.ItemMaxDurability;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.query.ItemRemainingDurability;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.query.Position;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.query.PositionDelta;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.query.RelativeBlockHasAllTags;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.query.RelativeBlockHasAnyTag;

public class QueryBinding extends ContextBinding {

    public QueryBinding() {
        variable("anim_time", ContextSelector::getAnimTime);
        variable("life_time", ContextSelector::getLifeTime);

        variable("actor_count", ContextSelector::getEntityCount);
        variable("is_first_person", ContextSelector::isFirstPerson);

        variable("time_of_day", LevelSelector::getDays);
        variable("time_stamp", LevelSelector::getTimestamp);
        variable("moon_phase", LevelSelector::getMoonPhase);

        variable("eye_target_x_rotation", EntitySelector::getEyeYaw);
        variable("eye_target_y_rotation", EntitySelector::getEyePitch);
        variable("head_x_rotation", EntitySelector::getHeadYaw);
        variable("head_y_rotation", EntitySelector::getHeadPitch);

        variable("yaw_speed", EntitySelector::getYawSpeed);
        variable("ground_speed", EntitySelector::getGroundSpeed);
        variable("vertical_speed", EntitySelector::getVerticalSpeed);

        variable("cardinal_facing_2d", EntitySelector::getCardinalFacing);
        variable("distance_from_camera", EntitySelector::distanceFromCamera);
        variable("modified_distance_moved", EntitySelector::distanceFromMove);
        variable("walk_distance", EntitySelector::distanceFromWalk);

        variable("has_rider", EntitySelector::isVehicle);

        variable("is_in_water", EntitySelector::isInWater);
        variable("is_in_water_or_rain", EntitySelector::isInWaterRainOrBubble);
        variable("is_on_fire", EntitySelector::isOnFire);
        variable("is_on_ground", EntitySelector::isOnGround);
        variable("is_riding", EntitySelector::isPassenger);
        variable("is_sneaking", EntitySelector::isSneaking);
        variable("is_jumping", EntitySelector::isJumping);
        variable("is_spectator", EntitySelector::isSpectator);
        variable("is_sprinting", EntitySelector::isSprinting);
        variable("is_swimming", EntitySelector::isSwimming);

        variable("body_x_rotation", LivingEntitySelector::getBodyYaw);
        variable("body_y_rotation", LivingEntitySelector::getBodyPitch);
        variable("health", LivingEntitySelector::getHealth);
        variable("max_health", LivingEntitySelector::getMaxHealth);
        variable("hurt_time", LivingEntitySelector::getHurtTime);
        variable("is_playing_dead", LivingEntitySelector::isDeadOrDying);
        variable("is_eating", LivingEntitySelector::isEating);
        variable("is_sleeping", LivingEntitySelector::isSleeping);
        variable("is_using_item", LivingEntitySelector::isUsingItem);
        variable("item_in_use_duration", LivingEntitySelector::getUsingItemDuration);
        variable("item_max_use_duration", LivingEntitySelector::getUsingItemMaxDuration);
        variable("item_remaining_use_duration", LivingEntitySelector::getUsingItemRemainingDuration);
        variable("equipment_count", LivingEntitySelector::getEquipmentCount);

        variable("has_cape", PlayerSelector::hasCape);
        variable("cape_flap_amount", PlayerSelector::getCapeFlapAmount);
        variable("player_level", PlayerSelector::getExperience);

        function("biome_has_all_tags", BiomeHasAllTags::new);
        function("biome_has_any_tag", BiomeHasAnyTag::new);
        function("relative_block_has_all_tags", RelativeBlockHasAllTags::new);
        function("relative_block_has_any_tag", RelativeBlockHasAnyTag::new);
        function("is_item_name_any", ItemHasAnyName::new);
        function("equipped_item_all_tags", ItemHasAllTags::new);
        function("equipped_item_any_tag", ItemHasAnyTag::new);
        function("position", Position::new);
        function("position_delta", PositionDelta::new);
        function("max_durability", ItemMaxDurability::new);
        function("remaining_durability", ItemRemainingDurability::new);

        function("debug_output", Print::new);
    }
}
