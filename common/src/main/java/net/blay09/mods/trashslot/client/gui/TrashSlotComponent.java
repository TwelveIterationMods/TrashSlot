package net.blay09.mods.trashslot.client.gui;

import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.balm.mixin.SlotAccessor;
import net.blay09.mods.kuma.api.Kuma;
import net.blay09.mods.trashslot.TrashSlotSaveState;
import net.blay09.mods.trashslot.api.layout.SlotVisual;
import net.blay09.mods.trashslot.api.layout.Snap;
import net.blay09.mods.trashslot.api.layout.TrashContainerLayout;
import net.blay09.mods.trashslot.api.layout.TrashSlotContainerContext;
import net.blay09.mods.trashslot.client.ContainerSettings;
import net.blay09.mods.trashslot.client.TrashSlotGuiHandler;
import net.blay09.mods.trashslot.client.TrashSlotSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Vector2i;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TrashSlotComponent {

    private static final int SNAP_SIZE = 8;

    private final AbstractContainerScreen<?> screen;
    private final TrashContainerLayout layout;
    private final ContainerSettings settings;
    private final TrashSlotSlot trashSlot;
    private final TrashSlotContainerContext context;

    private SlotVisual visual = SlotVisual.DEFAULT;

    private boolean wasMouseDown;
    private boolean isDragging;
    private int dragStartX;
    private int dragStartY;

    public TrashSlotComponent(AbstractContainerScreen<?> screen, TrashContainerLayout layout, ContainerSettings settings, TrashSlotSlot trashSlot) {
        this.screen = screen;
        this.layout = layout;
        this.settings = settings;
        this.trashSlot = trashSlot;
        this.context = layout.createContext(screen);
    }

    public boolean isInside(int mouseX, int mouseY) {
        final var bounds = visual.getBounds(getAbsoluteSlotX(), getAbsoluteSlotY());
        return bounds.contains(mouseX, mouseY);
    }

    public void update(int mouseX, int mouseY) {
        final var bounds = visual.getBounds(getAbsoluteSlotX(), getAbsoluteSlotY());
        boolean isMouseOver = bounds.contains(mouseX, mouseY);
        if (TrashSlotGuiHandler.isLeftMouseDown()) {
            if (!isDragging && isMouseOver && !wasMouseDown && !settings.isLocked()) {
                if (Minecraft.getInstance().player.containerMenu.getCarried()
                        .isEmpty() && (!trashSlot.hasItem() || !((AbstractContainerScreenAccessor) screen).callIsHovering(trashSlot, mouseX, mouseY))) {
                    dragStartX = getAbsoluteSlotX() - mouseX;
                    dragStartY = getAbsoluteSlotY() - mouseY;
                    isDragging = true;
                }
            }
            wasMouseDown = true;
        } else {
            if (isDragging) {
                TrashSlotSaveState.save();
                isDragging = false;
            }
            wasMouseDown = false;
        }
        if (isDragging) {
            int targetX = mouseX + dragStartX;
            int targetY = mouseY + dragStartY;
            final var collisionAreas = layout.getAllBounds(context);
            final var resolvedTarget = resolveCollision(targetX, targetY, collisionAreas);
            targetX = resolvedTarget.x();
            targetY = resolvedTarget.y();

            Identifier snapId = null;
            if (!Kuma.hasShiftDown()) {
                int bestSnapDist = Integer.MAX_VALUE;
                Map.Entry<Identifier, Snap> bestSnapEntry = null;
                for (final var entry : layout.getSnaps(context).entrySet()) {
                    final var snap = entry.getValue();
                    final int currentX = targetX;
                    final int currentY = targetY;
                    final var optDistX = snap.x(context, currentX).map(it -> Math.abs(it - currentX));
                    final var optDistY = snap.y(context, currentY).map(it -> Math.abs(it - currentY));
                    if (optDistX.isEmpty() || optDistY.isEmpty()) {
                        continue;
                    }
                    final var distX = optDistX.get();
                    final var distY = optDistY.get();
                    final int dist = (int) Math.sqrt(distX * distX + distY * distY);
                    if (dist < SNAP_SIZE && dist < bestSnapDist) {
                        bestSnapEntry = entry;
                        bestSnapDist = dist;
                    }
                }
                if (bestSnapEntry != null) {
                    final var bestSnap = bestSnapEntry.getValue();
                    targetX = bestSnap.x(context, targetX).orElse(targetX);
                    targetY = bestSnap.y(context, targetY).orElse(targetY);
                    snapId = bestSnapEntry.getKey();
                }
            }
            targetX = Mth.clamp(targetX, -visual.getOffsetX(), screen.width - visual.getWidth() - visual.getOffsetX());
            targetY = Mth.clamp(targetY, -visual.getOffsetY(), screen.height - visual.getHeight() - visual.getOffsetY());
            settings.setSlotX(toRelativeX(targetX));
            settings.setSlotY(toRelativeY(targetY));
            settings.setSnap(snapId);
        }
    }

    public void drawBackground(GuiGraphicsExtractor guiGraphics) {
        int slotX = getAbsoluteSlotX();
        int slotY = getAbsoluteSlotY();
        visual = Optional.ofNullable(settings.getSnap())
                .flatMap(it -> layout.getSnap(context, it))
                .map(Snap::visual)
                .orElse(SlotVisual.DEFAULT);
        AbstractContainerScreenAccessor screenAccessor = (AbstractContainerScreenAccessor) screen;
        ((SlotAccessor) trashSlot).setX(slotX - screenAccessor.getLeftPos());
        ((SlotAccessor) trashSlot).setY(slotY - screenAccessor.getTopPos());

        var poseStack = guiGraphics.pose();
        poseStack.pushMatrix();

        final var bounds = visual.getBounds(slotX, slotY);
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, visual.sprite(), bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
        poseStack.popMatrix();
    }

    public boolean isVisible() {
        return settings.isEnabled();
    }

    public boolean isDragging() {
        return isDragging;
    }

    public Rect2i getRectangle() {
        return visual.getBounds(getAbsoluteSlotX(), getAbsoluteSlotY());
    }

    private int getAbsoluteSlotX() {
        return settings.getSlotX() + ((AbstractContainerScreenAccessor) screen).getLeftPos();
    }

    private int getAbsoluteSlotY() {
        return settings.getSlotY() + ((AbstractContainerScreenAccessor) screen).getTopPos();
    }

    private int toRelativeX(int absoluteX) {
        return absoluteX - ((AbstractContainerScreenAccessor) screen).getLeftPos();
    }

    private int toRelativeY(int absoluteY) {
        return absoluteY - ((AbstractContainerScreenAccessor) screen).getTopPos();
    }

    private Vector2i resolveCollision(int targetX, int targetY, List<Rect2i> collisionAreas) {
        int resolvedX = targetX;
        int resolvedY = targetY;
        final int maxIterations = Math.max(1, collisionAreas.size() * 2);
        for (int i = 0; i < maxIterations; i++) {
            boolean resolvedAnyCollision = false;
            final var targetBounds = visual.getBounds(resolvedX, resolvedY);
            for (final var collisionArea : collisionAreas) {
                if (!intersects(targetBounds, collisionArea)) {
                    continue;
                }

                final var resolvedPoint = pushOutOfCollision(resolvedX, resolvedY, targetBounds, collisionArea);
                if (resolvedPoint.x() == resolvedX && resolvedPoint.y() == resolvedY) {
                    continue;
                }

                resolvedX = resolvedPoint.x();
                resolvedY = resolvedPoint.y();
                resolvedAnyCollision = true;
                break;
            }

            if (!resolvedAnyCollision) {
                break;
            }
        }

        return new Vector2i(resolvedX, resolvedY);
    }

    private Vector2i pushOutOfCollision(int targetX, int targetY, Rect2i targetBounds, Rect2i collisionArea) {
        Vector2i bestCandidate = new Vector2i(targetX, targetY);
        long bestDistance = Long.MAX_VALUE;

        final var leftCandidate = new Vector2i(collisionArea.getX() - targetBounds.getWidth() - visual.getOffsetX(), targetY);
        bestCandidate = pickCloserCandidate(targetX, targetY, collisionArea, bestCandidate, bestDistance, leftCandidate);
        bestDistance = distanceSquared(targetX, targetY, bestCandidate);

        final var rightCandidate = new Vector2i(collisionArea.getX() + collisionArea.getWidth() - visual.getOffsetX(), targetY);
        bestCandidate = pickCloserCandidate(targetX, targetY, collisionArea, bestCandidate, bestDistance, rightCandidate);
        bestDistance = distanceSquared(targetX, targetY, bestCandidate);

        final var topCandidate = new Vector2i(targetX, collisionArea.getY() - targetBounds.getHeight() - visual.getOffsetY());
        bestCandidate = pickCloserCandidate(targetX, targetY, collisionArea, bestCandidate, bestDistance, topCandidate);
        bestDistance = distanceSquared(targetX, targetY, bestCandidate);

        final var bottomCandidate = new Vector2i(targetX, collisionArea.getY() + collisionArea.getHeight() - visual.getOffsetY());
        return pickCloserCandidate(targetX, targetY, collisionArea, bestCandidate, bestDistance, bottomCandidate);
    }

    private Vector2i pickCloserCandidate(int originX, int originY, Rect2i collisionArea, Vector2i currentBest, long currentBestDistance, Vector2i candidate) {
        if (intersects(visual.getBounds(candidate.x(), candidate.y()), collisionArea)) {
            return currentBest;
        }

        final long candidateDistance = distanceSquared(originX, originY, candidate);
        if (candidateDistance < currentBestDistance) {
            return candidate;
        }

        return currentBest;
    }

    private long distanceSquared(int originX, int originY, Vector2i candidate) {
        final long deltaX = candidate.x() - originX;
        final long deltaY = candidate.y() - originY;
        return deltaX * deltaX + deltaY * deltaY;
    }

    private boolean intersects(Rect2i a, Rect2i b) {
        return a.getX() < b.getX() + b.getWidth()
                && a.getX() + a.getWidth() > b.getX()
                && a.getY() < b.getY() + b.getHeight()
                && a.getY() + a.getHeight() > b.getY();
    }

}
