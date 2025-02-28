package moe.plushie.armourers_workshop;

import moe.plushie.armourers_workshop.core.armature.Armatures;
import moe.plushie.armourers_workshop.core.crafting.recipe.SkinningRecipes;
import moe.plushie.armourers_workshop.core.data.EntityActions;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.init.ModArgumentTypes;
import moe.plushie.armourers_workshop.init.ModBlockEntityTypes;
import moe.plushie.armourers_workshop.init.ModBlocks;
import moe.plushie.armourers_workshop.init.ModCapabilities;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import moe.plushie.armourers_workshop.init.ModEntityProfiles;
import moe.plushie.armourers_workshop.init.ModEntitySerializers;
import moe.plushie.armourers_workshop.init.ModEntityTypes;
import moe.plushie.armourers_workshop.init.ModHolidays;
import moe.plushie.armourers_workshop.init.ModItemGroups;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.init.ModLootFunctions;
import moe.plushie.armourers_workshop.init.ModMenuTypes;
import moe.plushie.armourers_workshop.init.ModMolangBindings;
import moe.plushie.armourers_workshop.init.ModPackets;
import moe.plushie.armourers_workshop.init.ModPermissions;
import moe.plushie.armourers_workshop.init.ModSounds;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.init.platform.RendererManager;
import moe.plushie.armourers_workshop.init.proxy.ClientProxy;
import moe.plushie.armourers_workshop.init.proxy.CommonProxy;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import moe.plushie.armourers_workshop.utils.RenderSystem;

public class ArmourersWorkshop {

    public static void init() {
        ModDataComponents.init();
        ModItemGroups.init();
        ModItems.init();
        ModBlocks.init();
        ModBlockEntityTypes.init();
        ModEntityTypes.init();
        ModEntitySerializers.init();
        ModEntityProfiles.init();
        ModCapabilities.init();
        ModMenuTypes.init();
        ModLootFunctions.init();
        ModSounds.init();
        ModConfig.init();
        ModArgumentTypes.init();
        // setup common objects.
        EnvironmentExecutor.willInit(EnvironmentType.COMMON, () -> CommonProxy::init);
        EnvironmentExecutor.didInit(EnvironmentType.COMMON, () -> () -> {
            // setup network manager.
            ModPackets.init();
            NetworkManager.init("play", ModConstants.MOD_NET_ID);

            ModHolidays.init();
            ModPermissions.init();
            ModMolangBindings.init();
            SkinningRecipes.init();
            Armatures.init();
            EntityActions.init();

            EnvironmentExecutor.run(() -> SkinLibraryManager::startClient, () -> SkinLibraryManager::startServer);
        });
        // setup client object.
        EnvironmentExecutor.willInit(EnvironmentType.CLIENT, () -> ClientProxy::init);
        EnvironmentExecutor.didSetup(EnvironmentType.CLIENT, () -> () -> {
            // setup skin renderer manager.
            RenderSystem.init();
            RendererManager.init();
        });
    }

    public static OpenResourceLocation getItemIcon(SkinType skinType) {
        if (skinType == SkinTypes.UNKNOWN || skinType.getRegistryName() == null) {
            return null;
        }
        return ModConstants.key("textures/item/template/" + skinType.getRegistryName().getPath() + ".png");
    }
}
