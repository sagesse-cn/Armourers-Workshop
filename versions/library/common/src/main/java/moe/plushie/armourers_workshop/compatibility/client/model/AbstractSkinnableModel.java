package moe.plushie.armourers_workshop.compatibility.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.GhastModel;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.ZombieVillagerModel;

@SuppressWarnings("rawtypes")
@Environment(EnvType.CLIENT)
public class AbstractSkinnableModel extends AbstractSkinnableModelImpl {

    public static final Class<IllagerModel> ILLAGER = IllagerModel.class;
    public static final Class<ZombieVillagerModel> ZOMBIE_VILLAGER = ZombieVillagerModel.class;

    public static final Class<VillagerModel> VILLAGER = VillagerModel.class;
    public static final Class<IronGolemModel> IRON_GOLEM = IronGolemModel.class;
    public static final Class<EndermanModel> ENDERMAN = EndermanModel.class;
    public static final Class<PlayerModel> PLAYER = PlayerModel.class;

    public static final Class<HumanoidModel> HUMANOID = HumanoidModel.class;
    public static final Class<SlimeModel> SLIME = SlimeModel.class;

    public static final Class<GhastModel> GHAST = GhastModel.class;

    public static final Class<ChickenModel> CHICKEN = ChickenModel.class;
    public static final Class<CreeperModel> CREEPER = CreeperModel.class;
    public static final Class<HorseModel> HORSE = HorseModel.class;

    public static final Class<BoatModel> BOAT = BoatModel.class;
}
