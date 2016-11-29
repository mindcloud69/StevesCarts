package stevesvehicles.common.network.packets;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import stevesvehicles.api.network.DataReader;
import stevesvehicles.api.network.DataWriter;
import stevesvehicles.api.network.packets.IPacketClient;
import stevesvehicles.api.network.packets.IPacketProvider;
import stevesvehicles.common.blocks.BlockCartAssembler;
import stevesvehicles.common.blocks.ModBlocks;
import stevesvehicles.common.blocks.tileentitys.TileEntityCartAssembler;
import stevesvehicles.common.blocks.tileentitys.assembler.UpgradeContainer;
import stevesvehicles.common.network.PacketType;
import stevesvehicles.common.upgrades.registries.UpgradeRegistry;

public class PacketCartAssembler extends PacketPositioned implements IPacketClient {

	private Map<EnumFacing, Byte> upgrades;

	public PacketCartAssembler() {
		super();
		upgrades = new HashMap<>();
	}

	public PacketCartAssembler(TileEntityCartAssembler assembler) {
		super(assembler.getPos());
		upgrades = new HashMap<>();
		Collection<UpgradeContainer> containers = assembler.getUpgrades();
		if(!containers.isEmpty()){
			for (UpgradeContainer container : containers) {
				upgrades.put(container.getFacing(), (byte)UpgradeRegistry.getIdFromUpgrade(container.getUpgrade()));
			}
		}
		for(EnumFacing facing : EnumFacing.VALUES){
			UpgradeContainer container = assembler.getUpgrade(facing);
			if(container != null){
				upgrades.put(facing, (byte)UpgradeRegistry.getIdFromUpgrade(container.getUpgrade()));
			}else{
				upgrades.put(facing, (byte)-1);
			}
		}
	}

	@Override
	public void onPacketData(DataReader data, EntityPlayer player) throws IOException {
		World world = player.world;
		TileEntityCartAssembler assembler = (TileEntityCartAssembler) world.getTileEntity(getPos());
		for (EnumFacing facing : EnumFacing.VALUES) {
			byte upgradeType = upgrades.get(facing);
			if (upgradeType >= 0 && upgradeType < 255) {
				assembler.addUpgrade(facing, UpgradeRegistry.getUpgradeFromId(upgradeType));
			} else {
				assembler.removeUpgrade(facing);
			}
		}
		((BlockCartAssembler) ModBlocks.CART_ASSEMBLER.getBlock()).updateMultiBlock(assembler);
	}

	@Override
	public void readData(DataReader data) throws IOException {
		super.readData(data);
		for(EnumFacing facing : EnumFacing.VALUES){
			upgrades.put(facing, data.readByte());
		}
	}

	@Override
	protected void writeData(DataWriter data) throws IOException {
		super.writeData(data);
		for(EnumFacing facing : EnumFacing.VALUES){
			data.writeByte(upgrades.get(facing));
		}
	}

	@Override
	public IPacketProvider getProvider() {
		return PacketType.BLOCK;
	}
}
