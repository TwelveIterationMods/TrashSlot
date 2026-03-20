package net.blay09.mods.trashslot.api.layout;

public interface TrashContainerLayoutBuilder {
    TrashContainerLayoutBuilder addCollisionArea(int x, int y, int width, int height);

    TrashContainerLayoutBuilder addVerticalSnap(int x);

    TrashContainerLayoutBuilder addHorizontalSnap(int y);

    TrashContainerLayoutBuilder addSnappingPoint(int x, int y);

    TrashContainerLayoutBuilder enableDefaultCollision();

    TrashContainerLayoutBuilder enableDefaultSnaps();

    TrashContainerLayoutBuilder setEnabledByDefault();
}
