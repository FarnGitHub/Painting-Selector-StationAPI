package farn.paintingSelector.screen;


import net.minecraft.client.Minecraft;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.Tessellator;
import net.minecraft.entity.decoration.painting.PaintingVariants;
import org.lwjgl.opengl.GL11;

public class PaintingWidget extends ButtonWidget {
	
	private String TEXTURE = "/art/kz.png";
    
	protected PaintingVariants art;
    private static final int EXT = 3;
    private static final int YELLOW = -256;
    
    public static int KZ_WIDTH = 256, KZ_HEIGHT = 256;
    private final int maxY;
    

    public PaintingWidget(int id, int x, int y, PaintingVariants art, int maxY) {
        super(id, x, y, art.width, art.height, art.id);
        this.art = art;
        this.maxY= maxY;
    }
    
    public void shiftY(int dy) {
    	y += dy;
    }
    
    public void shiftX(int dx) {
    	x += dx;
    }
    
    public int left() {
    	return x;
    }
    
    public int right() {
    	return x + width;
    }
    
    public boolean isMouseOver(Minecraft par1Minecraft, int par2, int par3) {
    	return super.isMouseOver(par1Minecraft, par2, par3) && shouldDraw();
    }
    
    @Override
    public void render(Minecraft mc, int mouseX, int mouseY) {
        if (shouldDraw()) {
            mc.textureManager.bindTexture(mc.textureManager.getTextureId(TEXTURE));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            boolean mouseOver = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
            drawTexturedModalRect(x, y, art.textureOffsetX, art.textureOffsetY, width, height);

            if (getYImage(mouseOver) == 2) { //draw the 4 outlining rectangles
                fill(x - EXT, y - EXT, x + width + EXT, y, YELLOW); //upper left to upper right
                fill(x - EXT, y + height, x + width + EXT, y + height + EXT, YELLOW); //lower left to lower right
                fill(x - EXT, y, x, y + height, YELLOW); //middle rectangle to the left
                fill(x + width, y, x + width + EXT, y + height, YELLOW); //middle rectangle to the right
            }
        }
    }
    public boolean shouldDraw() {
    	return  y >= PaintingSelectingScreen.TOP && y + height <= maxY;
    }
    
    public void drawTexturedModalRect(int theX, int theY, int xOffset, int yOffset, int width, int height) {
        float f = 1/ (float)KZ_WIDTH;
        float f1 = 1/ (float)KZ_HEIGHT;
        
        Tessellator tessellator = Tessellator.INSTANCE;
        tessellator.startQuads();
        tessellator.vertex(theX, theY + height, zOffset, (float)(xOffset) * f, (float)(yOffset + height) * f1);
        tessellator.vertex(theX + width, theY + height, zOffset, (float)(xOffset + width) * f, (float)(yOffset + height) * f1);
        tessellator.vertex(theX + width, theY, zOffset, (float)(xOffset + width) * f, (float)(yOffset) * f1);
        tessellator.vertex(theX, theY, zOffset, (float)(xOffset) * f, (float)(yOffset) * f1);
        tessellator.draw();
    }

}
