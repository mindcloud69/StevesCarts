package vswe.stevescarts;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import vswe.stevescarts.blocks.tileentities.TileEntityBase;
import vswe.stevescarts.entitys.EntityMinecartModular;

public class CommonProxy implements IGuiHandler {
	public void init() {
	}

	@Override
	public Object getClientGuiElement(final int ID, final EntityPlayer player, final World world, final int x, final int y, final int z) {
		if (ID == 0) {
			final EntityMinecartModular cart = this.getCart(x, world);
			if (cart != null) {
				return cart.getGui(player);
			}
		} else {
			final TileEntity tileentity = world.getTileEntity(new BlockPos(x, y, z));
			if (tileentity != null && tileentity instanceof TileEntityBase) {
				return ((TileEntityBase) tileentity).getGui(player.inventory);
			}
		}
		return null;
	}

	@Override
	public Object getServerGuiElement(final int ID, final EntityPlayer player, final World world, final int x, final int y, final int z) {
		if (ID == 0) {
			final EntityMinecartModular cart = this.getCart(x, world);
			if (cart != null) {
				return cart.getCon(player.inventory);
			}
		} else {
			final TileEntity tileentity = world.getTileEntity(new BlockPos(x, y, z));
			if (tileentity != null && tileentity instanceof TileEntityBase) {
				return ((TileEntityBase) tileentity).getContainer(player.inventory);
			}
		}
		return null;
	}

	private EntityMinecartModular getCart(final int ID, final World world) {
		for (final Object e : world.loadedEntityList) {
			if (e instanceof Entity && ((Entity) e).getEntityId() == ID && e instanceof EntityMinecartModular) {
				return (EntityMinecartModular) e;
			}
		}
		return null;
	}

	public World getClientWorld() {
		return null;
	}

	public void preInit() {}

	public void initItemModels() {}

	public void loadComplete() {}
}
