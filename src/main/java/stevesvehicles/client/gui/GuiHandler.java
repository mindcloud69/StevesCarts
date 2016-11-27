package stevesvehicles.client.gui;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import stevesvehicles.client.gui.screen.GuiBuoy;
import stevesvehicles.common.blocks.tileentitys.TileEntityBase;
import stevesvehicles.common.blocks.tileentitys.TileEntityCartAssembler;
import stevesvehicles.common.blocks.tileentitys.assembler.UpgradeContainer;
import stevesvehicles.common.container.ContainerBuoy;
import stevesvehicles.common.entitys.buoy.EntityBuoy;
import stevesvehicles.common.vehicles.VehicleBase;
import stevesvehicles.common.vehicles.entitys.IVehicleEntity;

public class GuiHandler implements IGuiHandler {
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if (id == 0) {
			VehicleBase vehicle = getVehicle(x, world);
			if (vehicle != null) {
				return vehicle.getGui(player);
			}
		} else if (id == 1) {
			TileEntity tileentity = world.getTileEntity(new BlockPos(x, y, z));
			if (tileentity instanceof TileEntityBase) {
				return ((TileEntityBase) tileentity).getGui(player.inventory);
			}
		} else if (id == 2) {
			Entity entity = world.getEntityByID(x);
			if (entity instanceof EntityBuoy) {
				return new GuiBuoy((EntityBuoy) entity);
			}
		} else if (id >= 3) {
			TileEntity tileentity = world.getTileEntity(new BlockPos(x, y, z));
			if (tileentity instanceof TileEntityCartAssembler) {
				UpgradeContainer container = ((TileEntityCartAssembler)tileentity).getUpgrade(EnumFacing.VALUES[id-3]);
				if(container != null){
					return container.getGui(player.inventory);
				}
			}
		} 
		return null;
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if (id == 0) {
			VehicleBase vehicle = getVehicle(x, world);
			if (vehicle != null) {
				return vehicle.getCon(player.inventory);
			}
		} else if (id == 1) {
			TileEntity tileentity = world.getTileEntity(new BlockPos(x, y, z));
			if (tileentity instanceof TileEntityBase) {
				return ((TileEntityBase) tileentity).getContainer(player.inventory);
			}
		} else if (id == 2) {
			Entity entity = world.getEntityByID(x);
			if (entity instanceof EntityBuoy) {
				return new ContainerBuoy((EntityBuoy) entity);
			}
		} else if (id >= 3) {
			TileEntity tileentity = world.getTileEntity(new BlockPos(x, y, z));
			if (tileentity instanceof TileEntityCartAssembler) {
				UpgradeContainer container = ((TileEntityCartAssembler)tileentity).getUpgrade(EnumFacing.VALUES[id-3]);
				if(container != null){
					return container.getContainer(player.inventory);
				}
			}
		} 
		return null;
	}

	private VehicleBase getVehicle(int id, World world) {
		Entity entity = world.getEntityByID(id);
		if (entity instanceof IVehicleEntity) {
			return ((IVehicleEntity) entity).getVehicle();
		}
		return null;
	}
}
