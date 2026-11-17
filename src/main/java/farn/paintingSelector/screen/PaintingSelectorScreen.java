package farn.paintingSelector.screen;

import farn.paintingSelector.listener.PacketListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.entity.decoration.painting.PaintingVariants;
import net.modificationstation.stationapi.api.network.packet.MessagePacket;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.Arrays;
import java.util.Comparator;

public class PaintingSelectorScreen extends Screen {

    private static final Comparator<PaintingVariants> artComparator = (a, b) -> {
        if (a.height > b.height)
            return -1;
        if (a.height < b.height)
            return 1;
        return Integer.compare(b.width, a.width);
    };

    private int bottom;
    private int offset;
    private int minYOffset;

    private final int pX;
    private final int pY;
    private final int pZ;
    private final byte pSide;

    private final String TITLE = TranslationStorage.getInstance().get("painting.selector.title");

    public PaintingSelectorScreen(int x, int y, int z, byte side) {
        this.pX = x;
        this.pY = y;
        this.pZ = z;
        this.pSide = side;
    }

    protected void buttonClicked(ButtonWidget button) {
        if (button instanceof PaintingDisplayWidget p) {
            MessagePacket packet = new MessagePacket(PacketListener.NS.id("place"));
            packet.bytes = new byte[]{pSide};
            packet.ints = new int[]{pX,pY,pZ};
            packet.strings = new String[]{p.art.id};
            PacketHelper.send(packet);
        }

        this.minecraft.setScreen(null);
    }

    public void removed() {
        Keyboard.enableRepeatEvents(false);
        super.removed();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        Keyboard.enableRepeatEvents(true);
        this.bottom = height - 30;
        final int START_X = 10;
        final int END_X = width - 10;
        final int GAP = 5;

        PaintingVariants[] paintings = PaintingVariants.values();
        Arrays.sort(paintings, artComparator);

        int x = START_X;
        int y = 30, maxY = paintings[0].height;
        int id = 0;
        int rowStartIndex = 0;

        for (int index = 0; index < paintings.length; ++index) {
            PaintingVariants cur = paintings[index];

            if (x + cur.width > END_X || maxY > cur.height) {
                centerRow(rowStartIndex, index - 1);
                rowStartIndex = index;
                x = START_X;
                y += GAP + maxY;
                maxY = cur.height;
            }

            buttons.add(new PaintingDisplayWidget(++id, x, y, cur, this));
            x += cur.width + GAP;
        }

        centerRow(rowStartIndex, paintings.length - 1);

        minYOffset = this.bottom - (y + maxY);
        if (minYOffset > 0) minYOffset = 0;

        buttons.add(new ButtonWidget(300, width / 2 - 100, this.height - 25, TranslationStorage.getInstance().get("gui.cancel")));
        super.init();
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
        drawCenteredTextWithShadow(textRenderer, TITLE, width / 2, 10, 0xffffff);
    }

    private void offset(int i) {
        int dif = offset + i;
        if (dif > 0)
            dif = 0;
        if (dif < minYOffset)
            dif = minYOffset;
        for (Object o : buttons)
            if (o instanceof PaintingDisplayWidget p)
                p.y += dif-offset;
        offset = dif;
    }

    private void centerRow(int start, int end) {
        ButtonWidget startButton = (ButtonWidget) buttons.get(start);
        int left = startButton.x;

        ButtonWidget endButton = (ButtonWidget) buttons.get(end);
        int right = endButton.x + endButton.width;

        int correction = (width - 20 - (right - left)) / 2;
        for (int i = start; i <= end; ++i) {
            ((ButtonWidget)buttons.get(i)).x += correction;
        }
    }

    public boolean canRender(int yMin, int yMax) {
        return yMin >= 21 && yMax <= this.bottom;
    }

}
