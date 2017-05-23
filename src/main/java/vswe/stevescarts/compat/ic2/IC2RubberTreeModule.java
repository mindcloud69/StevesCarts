package vswe.stevescarts.compat.ic2;

import ic2.core.block.BlockRubWood;
import ic2.core.item.type.MiscResourceType;
import ic2.core.ref.ItemName;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import vswe.stevescarts.api.farms.EnumHarvestResult;
import vswe.stevescarts.api.farms.ITreeProduceModule;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.modules.workers.tools.ModuleTreeTap;
import vswe.stevescarts.modules.workers.tools.ModuleWoodcutter;

import java.util.List;

/**
 * Created by modmuss50 on 08/05/2017.
 */
public class IC2RubberTreeModule implements ITreeProduceModule {

	public static final ResourceLocation IC2_SAPLING_NAME = new ResourceLocation("ic2", "sapling");
	public static final ResourceLocation IC2_LEAF_NAME = new ResourceLocation("ic2", "leaves");
	public static final ResourceLocation IC2_LOG_NAME = new ResourceLocation("ic2", "rubber_wood");

	@Override
	public EnumHarvestResult isLeaves(IBlockState blockState, BlockPos pos, EntityMinecartModular cart) {
		if(blockState.getBlock().getRegistryName().equals(IC2_LEAF_NAME)){
			if(cart.hasModule(ModuleTreeTap.class)){
				return EnumHarvestResult.DISALLOW;
			}
			return EnumHarvestResult.ALLOW;
		}
		return EnumHarvestResult.SKIP;
	}

	@Override
	public EnumHarvestResult isWood(IBlockState blockState, BlockPos pos, EntityMinecartModular cart) {
		if(blockState.getBlock().getRegistryName().equals(IC2_LOG_NAME)){
			return EnumHarvestResult.ALLOW;
		}
		return EnumHarvestResult.SKIP;
	}

	@Override
	public boolean isSapling(ItemStack itemStack) {
		return itemStack.getItem().getRegistryName().equals(IC2_SAPLING_NAME);
	}

	@Override
	public boolean plantSapling(World world, BlockPos pos, ItemStack stack, FakePlayer fakePlayer) {
		Block block = Block.getBlockFromItem(stack.getItem());
		if(block.canPlaceBlockAt(world, pos.up())){
			world.setBlockState(pos.up(), block.getDefaultState());
			return true;
		}
		return false;
	}

	@Override
	public boolean harvest(IBlockState blockState, BlockPos pos, EntityMinecartModular cart, List<ItemStack> drops, boolean simulate, ModuleWoodcutter woodcutter) {
		if(!cart.hasModule(ModuleTreeTap.class)){
			return false;
		}
		BlockPos workPos = pos;
		IBlockState workSate = cart.world.getBlockState(workPos);
		boolean foundBlock = false;
		while (isWood(workSate, workPos, cart) == EnumHarvestResult.ALLOW){
			if(workSate.getBlock() instanceof BlockRubWood){
				foundBlock = true;
				BlockRubWood.RubberWoodState rubberWoodState = workSate.getValue(BlockRubWood.stateProperty);
				if(!rubberWoodState.isPlain() && rubberWoodState.wet){
					drops.add(ItemName.misc_resource.getItemStack(MiscResourceType.resin).copy());
					if(!simulate){
						cart.world.setBlockState(workPos, workSate.withProperty(BlockRubWood.stateProperty, rubberWoodState.getDry()));
						woodcutter.damageTool(1);
						woodcutter.startWorking(20);
					}
				}
				workPos = workPos.up();
				workSate = cart.world.getBlockState(workPos);
			}
		}
		return foundBlock;
	}
}
