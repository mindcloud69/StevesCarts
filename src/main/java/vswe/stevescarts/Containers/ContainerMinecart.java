package vswe.stevescarts.Containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Modules.ModuleBase;
import vswe.stevescarts.Slots.SlotBase;
import vswe.stevescarts.TileEntities.TileEntityBase;

import java.util.ArrayList;
import java.util.HashMap;

public class ContainerMinecart extends ContainerBase {
	private IInventory player;
	public HashMap<Short, Short> cache;
	public MinecartModular cart;

	public ContainerMinecart(final IInventory player, final MinecartModular cart) {
		this.cartInv(cart);
		this.playerInv(player);
	}

	@Override
	public IInventory getMyInventory() {
		return this.cart;
	}

	@Override
	public TileEntityBase getTileEntity() {
		return null;
	}

	protected void cartInv(final MinecartModular cart) {
		this.cart = cart;
		if (cart.getModules() != null) {
			for (final ModuleBase module : cart.getModules()) {
				if (module.hasSlots()) {
					final ArrayList<SlotBase> slotsList = module.getSlots();
					for (final SlotBase slot : slotsList) {
						slot.xDisplayPosition = slot.getX() + module.getX() + 1;
						slot.yDisplayPosition = slot.getY() + module.getY() + 1;
						this.addSlotToContainer(slot);
					}
				}
			}
		} else {
			for (int i = 0; i < 100; ++i) {
				this.addSlotToContainer(new Slot(cart, i, -1000, -1000));
			}
		}
	}

	protected void playerInv(final IInventory player) {
		this.player = player;
		for (int i = 0; i < 3; ++i) {
			for (int k = 0; k < 9; ++k) {
				this.addSlotToContainer(new Slot(player, k + i * 9 + 9, this.offsetX() + k * 18, i * 18 + this.offsetY()));
			}
		}
		for (int j = 0; j < 9; ++j) {
			this.addSlotToContainer(new Slot(player, j, this.offsetX() + j * 18, 58 + this.offsetY()));
		}
	}

	protected int offsetX() {
		return 159;
	}

	protected int offsetY() {
		return 174;
	}

	@Override
	public boolean canInteractWith(final EntityPlayer entityplayer) {
		return this.cart.isUseableByPlayer(entityplayer);
	}

	public void onContainerClosed(final EntityPlayer par1EntityPlayer) {
		super.onContainerClosed(par1EntityPlayer);
		this.cart.closeInventory(par1EntityPlayer);
	}

	@Override
	public void addListener(final IContainerListener par1ICrafting) {
		super.addListener(par1ICrafting);
		if (this.cart.getModules() != null) {
			for (ModuleBase module : this.cart.getModules()) {}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(final int par1, int par2) {
		par2 &= 0xFFFF;
		if (this.cart.getModules() != null) {
			for (final ModuleBase module : this.cart.getModules()) {
				if (par1 >= module.getGuiDataStart() && par1 < module.getGuiDataStart() + module.numberOfGuiData()) {
					module.receiveGuiData(par1 - module.getGuiDataStart(), (short) par2);
					break;
				}
			}
		}
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (this.cart.getModules() != null && this.listeners.size() > 0) {
			for (final ModuleBase module : this.cart.getModules()) {
				module.checkGuiData(this, this.listeners, false);
			}
		}
	}
}
