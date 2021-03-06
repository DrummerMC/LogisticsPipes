package logisticspipes.gui.popup;

import logisticspipes.blocks.LogisticsSecurityTileEntity;
import logisticspipes.network.PacketHandler;
import logisticspipes.network.packets.block.SaveSecurityPlayerPacket;
import logisticspipes.proxy.MainProxy;
import logisticspipes.security.SecuritySettings;
import logisticspipes.utils.gui.GuiCheckBox;
import logisticspipes.utils.gui.GuiGraphics;
import logisticspipes.utils.gui.SmallGuiButton;
import logisticspipes.utils.gui.SubGuiScreen;
import logisticspipes.utils.string.StringUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;

public class GuiSecurityStationPopup extends SubGuiScreen {
	private static final String PREFIX = "gui.securitystation.popup.player.";
	
	private final LogisticsSecurityTileEntity _tile;
	private final SecuritySettings activeSetting;
	
	public GuiSecurityStationPopup(SecuritySettings setting, LogisticsSecurityTileEntity tile) {
		super(140, 120, 0, 0);
		activeSetting = setting;
		_tile = tile;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();
		this.buttonList.clear();
		this.buttonList.add(new GuiCheckBox(0, guiLeft + 110, guiTop + 26, 16, 16, false));
		this.buttonList.add(new GuiCheckBox(1, guiLeft + 110, guiTop + 41, 16, 16, false));
		this.buttonList.add(new GuiCheckBox(2, guiLeft + 110, guiTop + 56, 16, 16, false));
		this.buttonList.add(new GuiCheckBox(3, guiLeft + 110, guiTop + 71, 16, 16, false));
		this.buttonList.add(new GuiCheckBox(4, guiLeft + 110, guiTop + 86, 16, 16, false));
		this.buttonList.add(new SmallGuiButton(5, guiLeft + 94, guiTop + 103, 30, 10, StringUtils.translate(PREFIX + "Close")));
		refreshCheckBoxes();
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		GuiGraphics.drawGuiBackGround(mc, guiLeft, guiTop, right, bottom, zLevel, true);
		mc.fontRenderer.drawString(StringUtils.translate(PREFIX + "Player") + ": " + activeSetting.name, guiLeft + 10, guiTop + 10, 0x404040);
		mc.fontRenderer.drawString(StringUtils.translate(PREFIX + "ConfigureSettings") + ": ", guiLeft + 10, guiTop + 30, 0x404040);
		mc.fontRenderer.drawString(StringUtils.translate(PREFIX + "ActiveRequesting") + ": ", guiLeft + 10, guiTop + 45, 0x404040);
		mc.fontRenderer.drawString(StringUtils.translate(PREFIX + "UpgradePipes") + ": ", guiLeft + 10, guiTop + 60, 0x404040);
		mc.fontRenderer.drawString(StringUtils.translate(PREFIX + "CheckNetwork") + ": ", guiLeft + 10, guiTop + 75, 0x404040);
		mc.fontRenderer.drawString(StringUtils.translate(PREFIX + "RemovePipes") + ": ", guiLeft + 10, guiTop + 90, 0x404040);
		super.drawScreen(par1, par2, par3);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if(button.id == 0) {
			activeSetting.openGui = !activeSetting.openGui;
			refreshCheckBoxes();
			NBTTagCompound nbt = new NBTTagCompound();
			activeSetting.writeToNBT(nbt);
			MainProxy.sendPacketToServer(PacketHandler.getPacket(SaveSecurityPlayerPacket.class).setTag(nbt).setPosX(_tile.xCoord).setPosY(_tile.yCoord).setPosZ(_tile.zCoord));
		} else if(button.id == 1) {
			activeSetting.openRequest = !activeSetting.openRequest;
			refreshCheckBoxes();
			NBTTagCompound nbt = new NBTTagCompound();
			activeSetting.writeToNBT(nbt);
			MainProxy.sendPacketToServer(PacketHandler.getPacket(SaveSecurityPlayerPacket.class).setTag(nbt).setPosX(_tile.xCoord).setPosY(_tile.yCoord).setPosZ(_tile.zCoord));
		} else if(button.id == 2) {
			activeSetting.openUpgrades = !activeSetting.openUpgrades;
			refreshCheckBoxes();
			NBTTagCompound nbt = new NBTTagCompound();
			activeSetting.writeToNBT(nbt);
			MainProxy.sendPacketToServer(PacketHandler.getPacket(SaveSecurityPlayerPacket.class).setTag(nbt).setPosX(_tile.xCoord).setPosY(_tile.yCoord).setPosZ(_tile.zCoord));
		} else if(button.id == 3) {
			activeSetting.openNetworkMonitor = !activeSetting.openNetworkMonitor;
			refreshCheckBoxes();
			NBTTagCompound nbt = new NBTTagCompound();
			activeSetting.writeToNBT(nbt);
			MainProxy.sendPacketToServer(PacketHandler.getPacket(SaveSecurityPlayerPacket.class).setTag(nbt).setPosX(_tile.xCoord).setPosY(_tile.yCoord).setPosZ(_tile.zCoord));
		} else if(button.id == 4) {
			activeSetting.removePipes = !activeSetting.removePipes;
			refreshCheckBoxes();
			NBTTagCompound nbt = new NBTTagCompound();
			activeSetting.writeToNBT(nbt);
			MainProxy.sendPacketToServer(PacketHandler.getPacket(SaveSecurityPlayerPacket.class).setTag(nbt).setPosX(_tile.xCoord).setPosY(_tile.yCoord).setPosZ(_tile.zCoord));
		} else if(button.id == 5) {
			this.exitGui();
		} else {
			super.actionPerformed(button);
		}
	}

	public void refreshCheckBoxes() {
		((GuiCheckBox)this.buttonList.get(0)).setState(activeSetting.openGui);
		((GuiCheckBox)this.buttonList.get(1)).setState(activeSetting.openRequest);
		((GuiCheckBox)this.buttonList.get(2)).setState(activeSetting.openUpgrades);
		((GuiCheckBox)this.buttonList.get(3)).setState(activeSetting.openNetworkMonitor);
		((GuiCheckBox)this.buttonList.get(4)).setState(activeSetting.removePipes);
	}
}
