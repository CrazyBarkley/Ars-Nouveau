package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class GlyphRecipeTooltip implements ClientTooltipComponent {
    public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/gui/container/bundle.png");
    private static final int MARGIN_Y = 4;
    private static final int BORDER_WIDTH = 1;
    private static final int TEX_SIZE = 128;
    private static final int SLOT_SIZE_X = 18;
    private static final int SLOT_SIZE_Y = 20;
    private final List<Ingredient> items;


    public GlyphRecipeTooltip(List<Ingredient> items) {
        this.items = items;
    }

    public int getHeight() {
        return this.gridSizeY() * SLOT_SIZE_Y + 2 + MARGIN_Y;
    }

    public int getWidth(Font pFont) {
        return this.gridSizeX() * SLOT_SIZE_X + 2;
    }

    public void renderImage(Font pFont, int pMouseX, int pMouseY, PoseStack pPoseStack, ItemRenderer pItemRenderer, int pBlitOffset) {
        if(this.items.isEmpty())
            return;
        int i = this.gridSizeX();
        int j = this.gridSizeY();
        boolean overWEight = false;
        int k = 0;

        for(int l = 0; l < j; ++l) {
            for(int i1 = 0; i1 < i; ++i1) {
                int j1 = pMouseX + i1 * SLOT_SIZE_X + BORDER_WIDTH;
                int k1 = pMouseY + l * SLOT_SIZE_Y + BORDER_WIDTH;
                this.renderSlot(j1, k1, k++, overWEight, pFont, pPoseStack, pItemRenderer, pBlitOffset);
            }
        }

        this.drawBorder(pMouseX, pMouseY, i, j, pPoseStack, pBlitOffset);
    }

    private void renderSlot(int pX, int pY, int pItemIndex, boolean pIsBundleFull, Font pFont, PoseStack pPoseStack, ItemRenderer pItemRenderer, int pBlitOffset) {
        if (pItemIndex >= this.items.size()) {
            this.blit(pPoseStack, pX, pY, pBlitOffset, pIsBundleFull ? GlyphRecipeTooltip.Texture.BLOCKED_SLOT : GlyphRecipeTooltip.Texture.SLOT);
        } else {
            List<ItemStack> items = new ArrayList<>(List.of(this.items.get(pItemIndex).getItems()));
            ItemStack itemstack = items.get((ClientInfo.ticksInGame / 20) % items.size());
            this.blit(pPoseStack, pX, pY, pBlitOffset, GlyphRecipeTooltip.Texture.SLOT);
            pItemRenderer.renderAndDecorateItem(itemstack, pX + BORDER_WIDTH, pY + BORDER_WIDTH, pItemIndex);
            pItemRenderer.renderGuiItemDecorations(pFont, itemstack, pX + BORDER_WIDTH, pY + BORDER_WIDTH);
        }
    }

    private void drawBorder(int pX, int pY, int pSlotWidth, int pSlotHeight, PoseStack pPoseStack, int pBlitOffset) {
        this.blit(pPoseStack, pX, pY, pBlitOffset, GlyphRecipeTooltip.Texture.BORDER_CORNER_TOP);
        this.blit(pPoseStack, pX + pSlotWidth * SLOT_SIZE_X + BORDER_WIDTH, pY, pBlitOffset, GlyphRecipeTooltip.Texture.BORDER_CORNER_TOP);

        for(int i = 0; i < pSlotWidth; ++i) {
            this.blit(pPoseStack, pX + BORDER_WIDTH + i * SLOT_SIZE_X, pY, pBlitOffset, GlyphRecipeTooltip.Texture.BORDER_HORIZONTAL_TOP);
            this.blit(pPoseStack, pX + BORDER_WIDTH + i * SLOT_SIZE_X, pY + pSlotHeight * SLOT_SIZE_Y, pBlitOffset, GlyphRecipeTooltip.Texture.BORDER_HORIZONTAL_BOTTOM);
        }

        for(int j = 0; j < pSlotHeight; ++j) {
            this.blit(pPoseStack, pX, pY + j * SLOT_SIZE_Y + BORDER_WIDTH, pBlitOffset, GlyphRecipeTooltip.Texture.BORDER_VERTICAL);
            this.blit(pPoseStack, pX + pSlotWidth * SLOT_SIZE_X + BORDER_WIDTH, pY + j * SLOT_SIZE_Y + BORDER_WIDTH, pBlitOffset, GlyphRecipeTooltip.Texture.BORDER_VERTICAL);
        }

        this.blit(pPoseStack, pX, pY + pSlotHeight * SLOT_SIZE_Y, pBlitOffset, GlyphRecipeTooltip.Texture.BORDER_CORNER_BOTTOM);
        this.blit(pPoseStack, pX + pSlotWidth * SLOT_SIZE_X + BORDER_WIDTH, pY + pSlotHeight * SLOT_SIZE_Y, pBlitOffset, GlyphRecipeTooltip.Texture.BORDER_CORNER_BOTTOM);
    }

    private void blit(PoseStack pPoseStack, int pX, int pY, int pBlitOffset, GlyphRecipeTooltip.Texture pTexture) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
        GuiComponent.blit(pPoseStack, pX, pY, pBlitOffset, (float)pTexture.x, (float)pTexture.y, pTexture.w, pTexture.h, TEX_SIZE, TEX_SIZE);
    }

    private int gridSizeX() {
        return this.items.size() == 0 ? 0 : Math.min(3, this.items.size());
    }

    private int gridSizeY() {
        if(items.isEmpty())
            return 0;
        if(items.size() % 3 != 0){
            return items.size() / 3 + 1;
        }
        return items.size() / 3;
    }

    @OnlyIn(Dist.CLIENT)
    static enum Texture {
        SLOT(0, 0, 18, 20),
        BLOCKED_SLOT(0, 40, 18, 20),
        BORDER_VERTICAL(0, 18, BORDER_WIDTH, 20),
        BORDER_HORIZONTAL_TOP(0, 20, 18, BORDER_WIDTH),
        BORDER_HORIZONTAL_BOTTOM(0, 60, 18, BORDER_WIDTH),
        BORDER_CORNER_TOP(0, 20, BORDER_WIDTH, BORDER_WIDTH),
        BORDER_CORNER_BOTTOM(0, 60, BORDER_WIDTH, BORDER_WIDTH);

        public final int x;
        public final int y;
        public final int w;
        public final int h;

        private Texture(int p_169928_, int p_169929_, int p_169930_, int p_169931_) {
            this.x = p_169928_;
            this.y = p_169929_;
            this.w = p_169930_;
            this.h = p_169931_;
        }
    }
}
