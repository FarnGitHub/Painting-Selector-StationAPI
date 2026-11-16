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

    private int offset;
    private int minYOffset;

    private final int placeX;
    private final int placeY;
    private final int placeZ;
    private final int placeSide;

    public PaintingSelectingScreen(int x, int y, int z, int side) {
        Keyboard.enableRepeatEvents(true);
        this.placeX = x;
        this.placeY = y;
        this.placeZ = z;
        this.placeSide = side;
    }

    protected void buttonClicked(ButtonWidget guibutton) {
        if (guibutton instanceof PaintingWidget paintButton) {
            MessagePacket packet = new MessagePacket(PaintingSelectorStationAPI.NAMESPACE.id("place_painting"));
            packet.strings = new String[]{paintButton.art.id};
            packet.ints = new int[]{placeX, placeY, placeZ, placeSide};
            PacketHelper.send(packet);
        }

        this.minecraft.setScreen(null);
    }

    public void removed() {
        Keyboard.enableRepeatEvents(false);
        super.removed();
    }

    @SuppressWarnings("unchecked")
    public void init() {
        super.init();
        buttons.clear();
        int bottom = height - 30;

        final int START_X = 10;
        final int END_X = width - 10;
        final int GAP = 5;

        int x = START_X;
        int y = 30, maxY = PaintingVariants.values()[0].height;
        int id = 0;
        int rowStartIndex = 0;

        for (int index = 0; index < PaintingVariants.values().length; ++index) {
            PaintingVariants cur = PaintingVariants.values()[index];

            if (x + cur.width > END_X) {
                centerRow(buttons, rowStartIndex, index - 1);
                rowStartIndex = index;
                x = START_X;
                y += GAP + maxY;
                maxY = cur.height;
            }
            else if (cur.height > maxY)
                maxY = cur.height;

            buttons.add(new PaintingWidget(++id, x, y, cur, bottom));
            x += cur.width + GAP;
        }
        centerRow(buttons, rowStartIndex, PaintingVariants.values().length - 1);

        minYOffset = bottom - (y + maxY);
        if (minYOffset >0)
            minYOffset = 0;

        buttons.add(new ButtonWidget(CLOSE_ID, width / 2 - 100, this.height - 25, "Cancel"));
    }

    @Override
    public void onMouseEvent() {
        int l = Mouse.getEventDWheel();
        if (l != 0) offset(l > 0 ? 3 : -3);
        super.onMouseEvent();
    }

    @Override
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

    @Override
    public void render(int mouseX, int mouseY, float tick) {
        renderBackground();
        super.render(mouseX, mouseY, tick);
        drawCenteredTextWithShadow(textRenderer, "Select a Painting", width / 2, 10, 0xffffff);
    }

    private void offset(int i) {
        int dif = offset + i;
        if (dif > 0)
            dif = 0;
        if (dif < minYOffset)
            dif = minYOffset;
        for (Object o : buttons)
            if (o instanceof PaintingWidget p)
                p.shiftY(dif-offset);
        offset = dif;
    }

    private void centerRow(List<PaintingWidget> list, int start, int end) {
        int left = list.get(start).left();
        int right = list.get(end).right();
        int correction = (width - 20 - (right - left)) / 2;
        for (int i = start; i <= end; ++i) {
            PaintingWidget b = list.get(i);
            b.shiftX(correction);
        }
    }
}
