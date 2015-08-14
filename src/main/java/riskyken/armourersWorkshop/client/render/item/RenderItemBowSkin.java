package riskyken.armourersWorkshop.client.render.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.skin.IEntityEquipment;
import riskyken.armourersWorkshop.client.model.ClientModelCache;
import riskyken.armourersWorkshop.client.model.armourer.ModelArrow;
import riskyken.armourersWorkshop.client.model.equipmet.ModelCustomEquipmetBow;
import riskyken.armourersWorkshop.client.render.EquipmentModelRenderer;
import riskyken.armourersWorkshop.client.render.EquipmentPartRenderer;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.common.addons.Addons;
import riskyken.armourersWorkshop.common.skin.cubes.CubeMarkerData;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;

public class RenderItemBowSkin implements IItemRenderer {
    
    private final RenderItem renderItem;
    private final Minecraft mc;
    
    public RenderItemBowSkin() {
        renderItem = (RenderItem) RenderManager.instance.entityRenderMap.get(EntityItem.class);
        mc = Minecraft.getMinecraft();
    }
    
    @Override
    public boolean handleRenderType(ItemStack stack, ItemRenderType type) {
        IItemRenderer render = Addons.getItemRenderer(stack, type);
        if (canRenderModel(stack)) {
            if (type == ItemRenderType.INVENTORY) {
                if (render != null) {
                    return render.handleRenderType(stack, type);
                } else {
                    return false;
                }
            } else {
                return true;
            }
        } else {
            if (render != null) {
                return render.handleRenderType(stack, type);
            } else {
                return false;
            }
        }
    }
    
    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack stack, ItemRendererHelper helper) {
        IItemRenderer render = Addons.getItemRenderer(stack, type);
        if (canRenderModel(stack)) {
            if (type == ItemRenderType.INVENTORY) {
                if (render != null) {
                    return render.shouldUseRenderHelper(type, stack, helper);
                } else {
                    return false;
                }
            } else {
                return type == ItemRenderType.ENTITY;
            }
        } else {
            if (render != null) {
                return render.shouldUseRenderHelper(type, stack, helper);
            } else {
                return false;
            }
        }
    }
    
    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        if (canRenderModel(stack) & type != ItemRenderType.INVENTORY) {
            if (type != ItemRenderType.ENTITY) {
                GL11.glPopMatrix();
                //GL11.glPopMatrix();
                //GL11.glRotatef(-135, 0, 1, 0);
                //GL11.glRotatef(-10, 0, 0, 1);
            }

            GL11.glPushMatrix();
            
            AbstractClientPlayer player = null;
            int useCount = 0;
            boolean hasArrow = false;
            boolean hasArrowSkin = false;
            int arrowSkinId = 0;
            
            if (data.length >= 2) {
                if (data[1] instanceof AbstractClientPlayer & data[0] instanceof RenderBlocks) {
                    RenderBlocks renderBlocks = (RenderBlocks) data[0];
                    player = (AbstractClientPlayer) data[1];
                    useCount = player.getItemInUseDuration();
                    hasArrow = player.inventory.hasItem(Items.arrow);
                    IEntityEquipment entityEquipment = EquipmentModelRenderer.INSTANCE.getPlayerCustomEquipmentData(player);
                    if (entityEquipment.haveEquipment(SkinTypeRegistry.skinArrow)) {
                        hasArrowSkin = true;
                        arrowSkinId = entityEquipment.getEquipmentId(SkinTypeRegistry.skinArrow);
                    }
                    if (!hasArrow) {
                        if (player.capabilities.isCreativeMode) {
                            hasArrow = true;
                        }
                    }
                }
            }
            
            float scale = 0.0625F;
            float angle = (float) (((double)System.currentTimeMillis() / 5) % 360F);
            
            switch (type) {
            case EQUIPPED:
                GL11.glScalef(1F, -1F, 1F);
                GL11.glScalef(1.6F, 1.6F, 1.6F);
                GL11.glRotatef(-135, 0, 1, 0);
                GL11.glRotatef(10, 0, 0, 1);
                GL11.glRotatef(-20, 1, 0, 0);
                
                GL11.glRotatef(90, 0, 1, 0);
                
                GL11.glTranslatef(0F * scale, -6F * scale, 1F * scale);
                break;
            case ENTITY:
                GL11.glRotatef(180, 0, 0, 1);
                GL11.glTranslatef(0F, -10F * scale, 0F);
                break;
            case EQUIPPED_FIRST_PERSON:
                GL11.glScalef(1.6F, 1.6F, 1.6F);
                GL11.glRotatef(-135, 0, 1, 0);
                GL11.glRotatef(180, 0, 0, 1);
                GL11.glRotatef(-90, 0, 1, 0);
                //Back tilt
                GL11.glRotatef(-17, 1, 0, 0);
                GL11.glRotatef(2, 0, 0, 1);
                GL11.glTranslatef(0F * scale, -2F * scale, 1F * scale);
                
                if (useCount > 0) {
                    GL11.glTranslatef(-5 * scale, 3 * scale, 1 * scale);
                    GL11.glRotatef(-6, 1, 0, 0);
                    GL11.glRotatef(-16, 0, 1, 0);
                    GL11.glRotatef(2, 0, 0, 1);
                }
                
                break;
            default:
                break;
            }
            
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GL11.glEnable(GL11.GL_CULL_FACE);
            ModRenderHelper.enableAlphaBlend();
            
            ModelCustomEquipmetBow model = EquipmentModelRenderer.INSTANCE.customBow;
            model.bowUse = useCount;
            int equipmentId = EquipmentNBTHelper.getSkinIdFromStack(stack);
            Skin skin = ClientModelCache.INSTANCE.getEquipmentItemData(equipmentId);
            model.render(player, skin, false);
            if (hasArrow) {
                GL11.glTranslatef(1 * scale, 1 * scale, -12 * scale);
                int tarPart = useCount / 10;
                if (tarPart > 2) {
                    tarPart = 2;
                }
                if (skin.getParts().get(tarPart).getMarkerBlocks().size() > 0) {
                    CubeMarkerData cmd = skin.getParts().get(tarPart).getMarkerBlocks().get(0);
                    ForgeDirection dir = ForgeDirection.getOrientation(cmd.meta - 1);
                    GL11.glTranslatef((-dir.offsetX + cmd.x) * scale, (-dir.offsetY + cmd.y) * scale, (dir.offsetZ + cmd.z) * scale);
                    //Shift the arrow a little to stop z fighting.
                    GL11.glTranslatef(-0.01F * scale, 0.01F * scale, -0.01F * scale);
                    if (hasArrowSkin && ClientModelCache.INSTANCE.isEquipmentInCache(arrowSkinId)) {
                        Skin arrowSkin = ClientModelCache.INSTANCE.getEquipmentItemData(arrowSkinId);
                        if (arrowSkin != null) {
                            arrowSkin.onUsed();
                            for (int i = 0; i < arrowSkin.getParts().size(); i++) {
                                SkinPart skinPart = arrowSkin.getParts().get(i);
                                EquipmentPartRenderer.INSTANCE.renderPart(skinPart, scale);
                            }
                        } else {
                            ModelArrow.MODEL.render(scale, false);
                        }
                    } else {
                        ClientModelCache.INSTANCE.requestEquipmentDataFromServer(arrowSkinId);
                        ModelArrow.MODEL.render(scale, false);
                    }
                }
            }
            GL11.glPopAttrib();
            
            GL11.glPopMatrix();
            
            if (type != ItemRenderType.ENTITY) {
                //GL11.glPushMatrix();
                GL11.glPushMatrix();
            }

        } else {
            IItemRenderer render = Addons.getItemRenderer(stack, type);
            if (render != null) {
                render.renderItem(type, stack, data);
            } else {
                renderNomalIcon(stack);
            }
        }
    }
    
    private boolean canRenderModel(ItemStack stack) {
        if (EquipmentNBTHelper.stackHasSkinData(stack)) {
            SkinPointer skinData = EquipmentNBTHelper.getSkinPointerFromStack(stack);
            if (ClientModelCache.INSTANCE.isEquipmentInCache(skinData.skinId)) {
                return true;
            } else {
                ClientModelCache.INSTANCE.requestEquipmentDataFromServer(skinData.skinId);
                return false;
            }
        } else {
            return false;
        }
    }
    
    private void renderNomalIcon(ItemStack stack) {
        IIcon icon = stack.getItem().getIcon(stack, 0);
        renderItem.renderIcon(0, 0, icon, icon.getIconWidth(), icon.getIconHeight());
        icon = stack.getItem().getIcon(stack, 1);
        renderItem.renderIcon(0, 0, icon, icon.getIconWidth(), icon.getIconHeight());
    }
}
