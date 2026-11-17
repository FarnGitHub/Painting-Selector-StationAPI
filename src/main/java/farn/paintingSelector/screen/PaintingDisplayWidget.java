package farn.paintingSelector.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.decoration.painting.PaintingVariants;
import org.lwjgl.opengl.GL11;

public class PaintingDisplayWidget extends ButtonWidget {
    protected PaintingVariants art;
    protected PaintingSelectorScreen screen;

    public PaintingDisplayWidget(int id, int x, int y, PaintingVariants art, PaintingSelectorScreen screen) {
        super(id, x, y, art.width, art.height, art.id);
        this.art = art;
        this.screen = screen;
    }

    @Override
    public void render(Minecraft mc, int mouseX, int mouseY) {
        if (this.screen.canRender(y, y + height)) {
            mc.textureManager.bindTexture(mc.textureManager.getTextureId("/art/kz.png"));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            boolean mouseOver = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
            this.drawTexture(x, y, art.textureOffsetX, art.textureOffsetY, width, height);

            if (getYImage(mouseOver) == 2) {
                fill(x - 3, y - 3, x + width + 3, y, -256);
                fill(x - 3, y + height, x + width + 3, y + height + 3, -256);
                fill(x - 3, y, x, y + height, -256);
                fill(x + width, y, x + width + 3, y + height, -256);
            }

            if (mouseOver) {
                int textWidth = mc.textRenderer.getWidth(this.art.id);
                int tooltipX = mouseX + 8;
                int tooltipY = mouseY - 4;
                this.fillGradient(tooltipX - 3, tooltipY - 3, tooltipX + textWidth + 3, tooltipY + 8 + 3, -1073741824, -1073741824);
                mc.textRenderer.drawWithShadow(this.art.id, tooltipX, tooltipY, -1);
            }
        }
    }

}
