package vswe.stevescarts.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import reborncore.common.network.ExtendedPacketBuffer;
import reborncore.common.network.INetworkPacket;
import vswe.stevescarts.blocks.tileentities.TileEntityLiquid;

import java.io.IOException;

public class PacketFluidSync implements INetworkPacket<PacketFluidSync> {

	FluidStack fluidStack;
	BlockPos pos;
	int worldID;
	int tankID;

	public PacketFluidSync(FluidStack fluidStack, BlockPos pos, int worldID, int tankID) {
		this.fluidStack = fluidStack;
		this.pos = pos;
		this.worldID = worldID;
		this.tankID = tankID;
	}

	public PacketFluidSync() {
	}

	@Override
	public void writeData(ExtendedPacketBuffer buffer) throws IOException {
		NBTTagCompound compound = new NBTTagCompound();
		fluidStack.writeToNBT(compound);
		buffer.writeCompoundTag(compound);
		buffer.writeBlockPos(pos);
		buffer.writeInt(worldID);
		buffer.writeInt(tankID);
	}

	@Override
	public void readData(ExtendedPacketBuffer buffer) throws IOException {
		fluidStack = FluidStack.loadFluidStackFromNBT(buffer.readCompoundTag());
		pos = buffer.readBlockPos();
		worldID = buffer.readInt();
		tankID = buffer.readInt();
	}

	@Override
	public void processData(PacketFluidSync message, MessageContext context) {
		if(context.side == Side.CLIENT){
			TileEntityLiquid liquid = (TileEntityLiquid) Minecraft.getMinecraft().world.getTileEntity(message.pos);
			liquid.tanks[message.tankID].setFluid(message.fluidStack);
		}
	}
}
