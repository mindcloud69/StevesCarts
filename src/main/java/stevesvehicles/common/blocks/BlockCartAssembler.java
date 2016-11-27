package stevesvehicles.common.blocks;

import java.util.ArrayList;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.server.permission.context.BlockPosContext;
import stevesvehicles.client.gui.GuiHandler;
import stevesvehicles.client.rendering.models.items.ModeledObject;
import stevesvehicles.common.blocks.PropertyUpgrades.Upgrades;
import stevesvehicles.common.blocks.tileentitys.TileEntityCartAssembler;
import stevesvehicles.common.blocks.tileentitys.assembler.UpgradeContainer;
import stevesvehicles.common.core.StevesVehicles;
import stevesvehicles.common.core.tabs.CreativeTabLoader;
import stevesvehicles.common.items.ModItems;
import stevesvehicles.common.network.PacketHandler;
import stevesvehicles.common.upgrades.Upgrade;
import stevesvehicles.common.upgrades.registries.UpgradeRegistry;

public class BlockCartAssembler extends BlockContainerBase {
	public static final PropertyUpgrades UPGRADES = new PropertyUpgrades();

	public BlockCartAssembler() {
		super(Material.ROCK);
		setCreativeTab(CreativeTabLoader.blocks);
	}

	public void updateMultiBlock(TileEntityCartAssembler assembler) {
		World world = assembler.getWorld();
		BlockPos pos = assembler.getPos();
		if (!world.isRemote) {
			byte[] data = new byte[]{-1, -1, -1, -1, -1, -1};
			for(UpgradeContainer container : assembler.getUpgrades()){
				data[container.getFacing().ordinal()] = (byte) UpgradeRegistry.getIdFromUpgrade(container.getUpgrade());
			}
			PacketHandler.sendBlockInfoToClients(world, data, pos);
		}else{
			world.markBlockRangeForRenderUpdate(pos, pos);
		}
		if (assembler != null) {
			assembler.onUpgradeUpdate();
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int var2) {
		return new TileEntityCartAssembler();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(hitY > 0.1875 && hitY < 0.8125){
			if(side == EnumFacing.NORTH || side == EnumFacing.SOUTH){
				if(hitX > 0.1875 && hitX < 0.8125){
					onUpgradeActivated(world, pos, state, player, hand, side);
					return true;
				}
			}else if(side == EnumFacing.EAST || side == EnumFacing.WEST){
				if(hitZ > 0.1875 && hitZ < 0.8125){
					onUpgradeActivated(world, pos, state, player, hand, side);
					return true;
				}
			}
		}else if(hitY == 1 || hitY == 0){
			if(hitZ > 0.1875 && hitZ < 0.8125 && hitX > 0.1875 && hitX < 0.8125){
				onUpgradeActivated(world, pos, state, player, hand, side);
				return true;
			}
		}
		if (!world.isRemote) {
			player.openGui(StevesVehicles.instance, 1, world, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}

	private void onUpgradeActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side){
		TileEntityCartAssembler assembler = (TileEntityCartAssembler) world.getTileEntity(pos);
		UpgradeContainer container = assembler.getUpgrade(side);
		if(container == null){
			ItemStack itemStack = player.getHeldItem(hand);
			if(!itemStack.isEmpty()){
				if(itemStack.getItem() == ModItems.upgrades){
					assembler.addUpgrade(side, UpgradeRegistry.getUpgradeFromId(itemStack.getItemDamage()));
					updateMultiBlock(assembler);
					if(!player.capabilities.isCreativeMode){
						itemStack.shrink(1);
					}
					return;
				}
			}
		}else{
			if(player.isSneaking()) {
				Upgrade upgrade = assembler.removeUpgrade(side);
				updateMultiBlock(assembler);
				if(upgrade != null){
					ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(ModItems.upgrades, 1, UpgradeRegistry.getIdFromUpgrade(upgrade)));
				}
				return;
			}
		}
		if (!world.isRemote) {
			if(container != null && !container.useStandardInterface()){
				player.openGui(StevesVehicles.instance, 3 + side.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
			}else{
				player.openGui(StevesVehicles.instance, 1, world, pos.getX(), pos.getY(), pos.getZ());
			}
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityCartAssembler) {
			TileEntityCartAssembler assembler = (TileEntityCartAssembler) te;
			assembler.isDead = true;
			for(UpgradeContainer upgradeContainer : assembler.getUpgrades()){
				if(upgradeContainer != null){
					ItemStack upgradeItem = new ItemStack(ModItems.upgrades, 1, UpgradeRegistry.getIdFromUpgrade(upgradeContainer.getUpgrade()));
					EntityItem entityItem = new EntityItem(world, (double) pos.getX() + 0.2F, (double) pos.getY() + 0.2F, pos.getZ() + 0.2F, upgradeItem);
					entityItem.motionX = world.rand.nextGaussian() * 0.05F;
					entityItem.motionY = world.rand.nextGaussian() * 0.25F;
					entityItem.motionZ = world.rand.nextGaussian() * 0.05F;
					world.spawnEntity(entityItem);
				}
			}
			ItemStack outputItem = assembler.getOutputOnInterrupt();
			if (!outputItem.isEmpty()) {
				EntityItem entityItem = new EntityItem(world, (double) pos.getX() + 0.2F, (double) pos.getY() + 0.2F, pos.getZ() + 0.2F, outputItem.copy());
				entityItem.motionX = world.rand.nextGaussian() * 0.05F;
				entityItem.motionY = world.rand.nextGaussian() * 0.25F;
				entityItem.motionZ = world.rand.nextGaussian() * 0.05F;
				world.spawnEntity(entityItem);
			}
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[]{UPGRADES});
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		Upgrades upgrades = Upgrades.EMPTY;
		if(tile instanceof TileEntityCartAssembler){
			TileEntityCartAssembler assembler = (TileEntityCartAssembler) tile;
			upgrades = new Upgrades();
			for(UpgradeContainer container : assembler.getUpgrades()){
				upgrades.upgrades.put(container.getFacing(), UpgradeRegistry.getIdFromUpgrade(container.getUpgrade()));
			}
		}
		return ((IExtendedBlockState)state).withProperty(UPGRADES, upgrades);
	}
}
