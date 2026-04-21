package farn.paintingSelector.data;

public interface PaintingPlacementPlayer {

    default PaintingPlacementData paintSelect_getPaintingData() {
        throw new AssertionError("Method not implemented.");
    }

    default void paintSelect_setPaintingData(PaintingPlacementData paintingSelectHandler) {
        throw new AssertionError("Method not implemented.");
    }
}
