package vswe.stevescarts.modules.realtimers;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.ModuleBase;

import java.util.List;

public class ModuleExperience extends ModuleBase {
	private static final int MAX_EXPERIENCE_AMOUNT = 1500;
	private int experienceAmount;

	public ModuleExperience(final EntityMinecartModular cart) {
		super(cart);
	}

	@Override
	public void update() {
		if (!this.getCart().world.isRemote) {
			final List list = this.getCart().world.getEntitiesWithinAABBExcludingEntity(this.getCart(), this.getCart().getEntityBoundingBox().expand(3.0, 1.0, 3.0));
			for (int e = 0; e < list.size(); ++e) {
				if (list.get(e) instanceof EntityXPOrb) {
					this.experienceAmount += ((EntityXPOrb) list.get(e)).getXpValue();
					if (this.experienceAmount > 1500) {
						this.experienceAmount = 1500;
					} else {
						((EntityXPOrb) list.get(e)).setDead();
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawMouseOver(final GuiMinecart gui, final int x, final int y) {
		this.drawStringOnMouseOver(gui, Localization.MODULES.ATTACHMENTS.EXPERIENCE_LEVEL.translate(String.valueOf(this.experienceAmount), String.valueOf(1500)) + "\n" + Localization.MODULES.ATTACHMENTS.EXPERIENCE_EXTRACT.translate() + "\n" + Localization.MODULES.ATTACHMENTS.EXPERIENCE_PLAYER_LEVEL.translate(String.valueOf(this.getClientPlayer().experienceLevel)), x, y, this.getContainerRect());
	}

	@Override
	public int numberOfGuiData() {
		return 1;
	}

	@Override
	protected void checkGuiData(final Object[] info) {
		this.updateGuiData(info, 0, (short) this.experienceAmount);
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		if (id == 0) {
			this.experienceAmount = data;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, Localization.MODULES.ATTACHMENTS.EXPERIENCE.translate(), 8, 6, 4210752);
	}

	private int[] getContainerRect() {
		return new int[] { 10, 15, 26, 65 };
	}

	private int[] getContentRect(final float part) {
		final int[] cont = this.getContainerRect();
		final int normalHeight = cont[3] - 4;
		final int currentHeight = (int) (normalHeight * part);
		return new int[] { cont[0] + 2, cont[1] + 2 + normalHeight - currentHeight, cont[2] - 4, currentHeight, normalHeight };
	}

	private void drawContent(final GuiMinecart gui, final int x, final int y, final int id) {
		final int lowerLevel = id * 1500 / 3;
		final int currentLevel = this.experienceAmount - lowerLevel;
		float part = 3.0f * currentLevel / 1500.0f;
		if (part > 1.0f) {
			part = 1.0f;
		}
		final int[] content = this.getContentRect(part);
		this.drawImage(gui, content, 4 + content[2] * (id + 1), content[4] - content[3]);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		ResourceHelper.bindResource("/gui/experience.png");
		for (int i = 0; i < 3; ++i) {
			this.drawContent(gui, x, y, i);
		}
		this.drawImage(gui, this.getContainerRect(), 0, this.inRect(x, y, this.getContainerRect()) ? 65 : 0);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int button) {
		if (this.inRect(x, y, this.getContainerRect())) {
			this.sendPacket(0);
		}
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public boolean hasSlots() {
		return false;
	}

	@Override
	public int guiWidth() {
		return 70;
	}

	@Override
	public int guiHeight() {
		return 84;
	}

	@Override
	protected int numberOfPackets() {
		return 1;
	}

	@Override
	protected void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0) {
			player.addExperience(Math.min(this.experienceAmount, 50));
			this.experienceAmount -= Math.min(this.experienceAmount, 50);
		}
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		this.experienceAmount = tagCompound.getShort(this.generateNBTName("Experience", id));
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		tagCompound.setShort(this.generateNBTName("Experience", id), (short) this.experienceAmount);
	}
}
