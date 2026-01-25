package farn.paintingSelector.screen;

import farn.paintingSelector.PaintingSelectorStationAPI;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.decoration.painting.PaintingVariants;
import net.modificationstation.stationapi.api.network.packet.MessagePacket;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.List;

public class PaintingSelectingScreen extends Screen {

    private static final int CLOSE_ID = 300;
    public static final int TOP = 21;

    private int bottom;
    private int offset;
    private int minYOffset;
    private final String[] artsName;
    private final PaintingVariants[] arts;

    public PaintingSelectingScreen() {
        super();
        this.arts = PaintingVariants.values();
        this.artsName = new String[this.arts.length];
        for(PaintingVariants art : this.arts) {
            this.artsName[art.ordinal()] = art.id;
        }
        Keyboard.enableRepeatEvents(true);
    }

    protected void buttonClicked(ButtonWidget guibutton) {
        if (guibutton instanceof PaintingWidget paintButton) {
            MessagePacket packet = new MessagePacket(PaintingSelectorStationAPI.NAMESPACE.id("place_painting"));
            packet.strings = new String[]{paintButton.art.id};
            PacketHelper.send(packet);
        }
        minecraft.setScreen(null);

    }

    public void removed() {
        Keyboard.enableRepeatEvents(false);
    }

    public void init() {
        super.init();
        buttons.clear();
        bottom = height-30;

        final int START_X = 10;
        final int END_X = width - 10;
        final int GAP = 5;

        int x = START_X;
        int y = 30, maxY = this.arts[0].height;
        int id = 0;
        int rowStartIndex = 0;

        for (int index = 0; index < this.arts.length; ++index) {
            PaintingVariants cur = this.arts[index];

            if (x + cur.width > END_X) {
                centerRow(buttons, rowStartIndex, index - 1);
                rowStartIndex = index;
                x = START_X;
                y += GAP + maxY;
                maxY = cur.height;
            }
            else if (cur.height > maxY)
                maxY = cur.height;

            buttons.add(new PaintingWidget(++id, x, y, cur,bottom));
            x += cur.width + GAP;
        }

        //Center the last row
        centerRow(buttons, rowStartIndex, this.arts.length - 1);

        minYOffset = bottom - (y + maxY);
        if (minYOffset >0)
            minYOffset = 0;

        buttons.add(new ButtonWidget(CLOSE_ID, width / 2 - 100, this.height - 25, "Cancel"));
    }

    private void centerRow(List list, int start, int end) {
        int left = ((PaintingWidget)list.get(start)).left();
        int right = ((PaintingWidget)list.get(end)).right();

        //We're 10 pixels away from each edge
        int correction = (width - 20 - (right - left)) / 2;
        for (int i = start; i <= end; ++i) {
            PaintingWidget b = (PaintingWidget)list.get(i);
            b.shiftX(correction);
        }
    }

    public void tick() {
        if (minecraft.player == null || !minecraft.player.isAlive()) {
            minecraft.setScreen(null);
        }
    }

    public void onMouseEvent() {
        int l = Mouse.getEventDWheel();

        if (l != 0)
        {
            if (l > 0)
            {
                l = 3;
            }
            else if (l < 0)
            {
                l = -3;
            }
            offset(l);
        }
        super.onMouseEvent();
    }

    public void onKeyboardEvent() {
        super.onKeyboardEvent();
        if (Keyboard.getEventKeyState()){
            switch(Keyboard.getEventKey()){
                case Keyboard.KEY_UP:
                    offset(3);
                    break;
                case Keyboard.KEY_DOWN:
                    offset(-3);
                    break;
            }
        }
    }
    public void render(int mouseX, int mouseY, float par3) {
        renderBackground();
        drawCenteredTextWithShadow(textRenderer, "Select a Painting", width / 2, 10, 0xffffff);
        super.render(mouseX, mouseY, par3);
        for (Object o : buttons) {
            if (o instanceof PaintingWidget widget
                    && widget.isMouseOver(this.minecraft, mouseX, mouseY)) {
                int textWidth = this.minecraft.textRenderer.getWidth(widget.art.id);
                int tooltipX = mouseX + 8;
                int tooltipY = mouseY - 4;
                this.fillGradient(tooltipX - 3, tooltipY - 3, tooltipX + textWidth + 3, tooltipY + 8 + 3, -1073741824, -1073741824);
                this.minecraft.textRenderer.drawWithShadow(widget.art.id, tooltipX, tooltipY, -1);
            }

        }
    }

    private void offset(int i) {
        int dif = offset + i;
        if (dif > 0)
            dif = 0;
        if (dif < minYOffset)
            dif = minYOffset;
        for (Object o : buttons) {
            if (o instanceof PaintingWidget)
                ((PaintingWidget)o).shiftY(dif-offset);
        }
        offset = dif;
    }
}
