package net.blay09.mods.trashslot.client;

import com.google.common.collect.Lists;
import net.blay09.mods.trashslot.CommonProxy;
import net.blay09.mods.trashslot.TrashSlot;
import net.blay09.mods.trashslot.api.IGuiContainerLayout;
import net.blay09.mods.trashslot.api.TrashSlotAPI;
import net.blay09.mods.trashslot.client.deletion.DefaultDeletionProvider;
import net.blay09.mods.trashslot.client.deletion.DeletionProvider;
import net.blay09.mods.trashslot.client.gui.layout.ChestContainerLayout;
import net.blay09.mods.trashslot.client.gui.GuiHelper;
import net.blay09.mods.trashslot.client.gui.GuiTrashSlot;
import net.blay09.mods.trashslot.client.gui.layout.SimpleGuiContainerLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

	public static TextureAtlasSprite trashSlotIcon;

	private final KeyBinding keyBindToggleSlot = new KeyBinding("key.trashslot.toggle", KeyConflictContext.GUI, KeyModifier.NONE, Keyboard.KEY_T, "key.categories.trashslot");
	private final KeyBinding keyBindDelete = new KeyBinding("key.trashslot.delete", KeyConflictContext.GUI, KeyModifier.NONE, Keyboard.KEY_DELETE, "key.categories.trashslot");
	private final KeyBinding keyBindDeleteAll = new KeyBinding("key.trashslot.deleteAll", KeyConflictContext.GUI, KeyModifier.SHIFT, Keyboard.KEY_DELETE, "key.categories.trashslot");

	private final SlotTrash trashSlot = new SlotTrash();
	private boolean sentMissingMessage;
	private long missingMessageTime;

	private GuiTrashSlot guiTrashSlot;
	private TrashContainerSettings currentSettings = TrashContainerSettings.NONE;
	private DeletionProvider deletionProvider;
	private boolean ignoreMouseUp;

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		ClientRegistry.registerKeyBinding(keyBindToggleSlot);
		ClientRegistry.registerKeyBinding(keyBindDelete);
		ClientRegistry.registerKeyBinding(keyBindDeleteAll);
		TrashSlotAPI.registerLayout(GuiInventory.class, SimpleGuiContainerLayout.DEFAULT_ENABLED);
		TrashSlotAPI.registerLayout(GuiCrafting.class, SimpleGuiContainerLayout.DEFAULT_ENABLED);
		TrashSlotAPI.registerLayout(GuiChest.class, new ChestContainerLayout());

		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onTextureStitch(TextureStitchEvent.Pre event) {
		trashSlotIcon = event.getMap().registerSprite(new ResourceLocation("trashslot", "items/trashcan"));
	}

	@SubscribeEvent
	public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
		if(event.getGui() instanceof GuiContainerCreative) {
			currentSettings = TrashContainerSettings.NONE;
			guiTrashSlot = null;
			return;
		}
		if (event.getGui() instanceof GuiContainer) {
			GuiContainer gui = (GuiContainer) event.getGui();
			if (deletionProvider == null) {
				if (TrashSlot.isServerSideInstalled) {
					deletionProvider = new DefaultDeletionProvider();
				} else {
					if (!sentMissingMessage) {
						missingMessageTime = System.currentTimeMillis();
						sentMissingMessage = true;
					}
					return;
				}
			}
			if(gui instanceof GuiInventory && Minecraft.getMinecraft().player.capabilities.isCreativeMode) { // For some reason this event gets fired with GuiInventory right after opening the creative menu, AFTER it got fired for GuiContainerCreative
				return;
			}
			IGuiContainerLayout layout = TrashClient.getLayout(gui);
			currentSettings = TrashClient.getSettings(gui, layout);
			if(currentSettings != TrashContainerSettings.NONE) {
				guiTrashSlot = new GuiTrashSlot(gui, layout, currentSettings, trashSlot);
			} else {
				guiTrashSlot = null;
			}
		} else {
			currentSettings = TrashContainerSettings.NONE;
			guiTrashSlot = null;
		}
	}

	@SubscribeEvent
	public void onGuiMouse(GuiScreenEvent.MouseInputEvent.Pre event) {
		if(!currentSettings.isEnabled()) {
			return;
		}
		int mouseButton = Mouse.getEventButton();
		boolean buttonState = Mouse.getEventButtonState();
		if (ignoreMouseUp && !buttonState && mouseButton != -1) {
			event.setCanceled(true);
			ignoreMouseUp = false;
			return;
		}
		if (event.getGui() instanceof GuiContainer && buttonState) {
			GuiContainer gui = (GuiContainer) event.getGui();
			// I love how BackgroundDrawnEvent has a mouse position but MOUSE EVENT does not.
			final ScaledResolution resolution = new ScaledResolution(gui.mc);
			final int scaledWidth = resolution.getScaledWidth();
			final int scaledHeight = resolution.getScaledHeight();
			int mouseX = Mouse.getX() * scaledWidth / gui.mc.displayWidth;
			int mouseY = scaledHeight - Mouse.getY() * scaledHeight / gui.mc.displayHeight - 1;
			if (gui.isMouseOverSlot(trashSlot, mouseX, mouseY)) {
				EntityPlayer player = Minecraft.getMinecraft().player;
				if (player != null) {
					ItemStack mouseItem = player.inventory.getItemStack();
					boolean isRightClick = mouseButton == 1;
					if (mouseItem.isEmpty()) {
						deletionProvider.undeleteLast(player, trashSlot, isRightClick);
					} else {
						deletionProvider.deleteMouseItem(player, mouseItem, trashSlot, isRightClick);
					}
					event.setCanceled(true);
					ignoreMouseUp = true;
				}
			} else if(guiTrashSlot.isInside(mouseX, mouseY)) {
				event.setCanceled(true);
				ignoreMouseUp = true;
			}
		}
	}

	@SubscribeEvent
	public void onGuiKeyboard(GuiScreenEvent.KeyboardInputEvent.Post event) {
		if (Keyboard.getEventKeyState()) {
			int keyCode = Keyboard.getEventKey();
			if(currentSettings.isEnabled() && deletionProvider != null) {
				boolean isDelete = keyBindDelete.isActiveAndMatches(keyCode);
				boolean isDeleteAll = keyBindDeleteAll.isActiveAndMatches(keyCode);
				if (isDelete || isDeleteAll) {
					EntityPlayer entityPlayer = Minecraft.getMinecraft().player;
					if (entityPlayer != null && event.getGui() instanceof GuiContainer) {
						GuiContainer gui = ((GuiContainer) event.getGui());
						Slot mouseSlot = gui.getSlotUnderMouse(); // needs @Nullable
						if (mouseSlot != null && mouseSlot.getHasStack()) {
							deletionProvider.deleteContainerItem(gui.inventorySlots, mouseSlot.slotNumber, isDeleteAll);
						} else {
							final ScaledResolution resolution = new ScaledResolution(gui.mc);
							final int scaledWidth = resolution.getScaledWidth();
							final int scaledHeight = resolution.getScaledHeight();
							int mouseX = Mouse.getX() * scaledWidth / gui.mc.displayWidth;
							int mouseY = scaledHeight - Mouse.getY() * scaledHeight / gui.mc.displayHeight - 1;
							if(gui.isMouseOverSlot(trashSlot, mouseX, mouseY)) {
								deletionProvider.emptyTrashSlot(trashSlot);
							}
						}
					}
				}
			}
			if(event.getGui() instanceof GuiContainer && currentSettings != TrashContainerSettings.NONE) {
				if(keyBindToggleSlot.isActiveAndMatches(keyCode)) {
					currentSettings.setEnabled(!currentSettings.isEnabled());
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Pre event) {
		if (!currentSettings.isEnabled()) {
			return;
		}
		if (event.getGui() instanceof GuiContainer) {
			GuiContainer gui = (GuiContainer) event.getGui();
			if (guiTrashSlot != null) {
				guiTrashSlot.update(event.getMouseX(), event.getMouseY());
				guiTrashSlot.drawBackground(event.getMouseX(), event.getMouseY());
				if (gui.isMouseOverSlot(trashSlot, event.getMouseX(), event.getMouseY())) {
					GlStateManager.disableLighting();
					GlStateManager.disableDepth();
					int j1 = gui.getGuiLeft() + trashSlot.xPos;
					int k1 = gui.getGuiTop() + trashSlot.yPos;
					GlStateManager.colorMask(true, true, true, false);
					GuiHelper.drawGradientRect(j1, k1, j1 + 16, k1 + 16, -600, -2130706433, -2130706433);
					GlStateManager.colorMask(true, true, true, true);
					GlStateManager.enableDepth();
				}
			}
		}
	}

	@SubscribeEvent
	public void onBackgroundDrawn(GuiScreenEvent.BackgroundDrawnEvent event) {
		if (event.getGui() instanceof GuiContainer) {
			GuiContainer gui = (GuiContainer) event.getGui();
			if(missingMessageTime != 0 && System.currentTimeMillis() - missingMessageTime < 3000) {
				String noHabloEspanol = TextFormatting.RED + I18n.format("trashslot.serverNotInstalled");
				GuiUtils.drawHoveringText(Lists.newArrayList(noHabloEspanol), gui.getGuiLeft() + gui.getXSize() / 2 - gui.mc.fontRendererObj.getStringWidth(noHabloEspanol) / 2, 25, gui.width, gui.height, -1, gui.mc.fontRendererObj);
			}
			if (!currentSettings.isEnabled()) {
				return;
			}
			GlStateManager.pushMatrix();
			GlStateManager.translate(gui.getGuiLeft(), gui.getGuiTop(), 0);
			RenderHelper.enableGUIStandardItemLighting();
			gui.drawSlot(trashSlot);
			RenderHelper.disableStandardItemLighting();
			GlStateManager.popMatrix();
		}
	}

	@SubscribeEvent
	public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
		if (event.getGui() instanceof GuiContainer) {
			GuiContainer gui = (GuiContainer) event.getGui();
			if (!currentSettings.isEnabled()) {
				return;
			}
			boolean isMouseSlot = gui.isMouseOverSlot(trashSlot, event.getMouseX(), event.getMouseY());
			InventoryPlayer inventoryPlayer = Minecraft.getMinecraft().player.inventory;
			if (isMouseSlot && inventoryPlayer.getItemStack().isEmpty() && trashSlot.getHasStack()) {
				gui.renderToolTip(trashSlot.getStack(), event.getMouseX(), event.getMouseY());
			}
		}
	}

	@Override
	@Nullable
	public DeletionProvider getDeletionProvider() {
		return deletionProvider;
	}

	@Override
	public SlotTrash getTrashSlot() {
		return trashSlot;
	}

	@Nullable
	public GuiTrashSlot getGuiTrashSlot() {
		return guiTrashSlot;
	}
}
