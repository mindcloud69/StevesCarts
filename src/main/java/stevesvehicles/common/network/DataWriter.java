package stevesvehicles.common.network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class DataWriter extends DataOutputStream {
	public DataWriter(ByteBufOutputStream out) {
		super(out);
	}

	public ByteBufOutputStream getOut() {
		return (ByteBufOutputStream) out;
	}

	public void writeItemStack(ItemStack itemstack) throws IOException {
		if (itemstack == null) {
			writeUTF("");
		} else {
			writeUTF(ForgeRegistries.ITEMS.getKey(itemstack.getItem()).toString());
			writeVarInt(itemstack.getCount());
			writeVarInt(itemstack.getItemDamage());
			if (itemstack.getItem().isDamageable() || itemstack.getItem().getShareTag()) {
				writeNBTTagCompound(itemstack.getTagCompound());
			}
		}
	}

	public void writeItemStacks(ItemStack... itemStacks) throws IOException {
		writeVarInt(itemStacks.length);
		for (ItemStack itemstack : itemStacks) {
			writeItemStack(itemstack);
		}
	}

	public void writeItemStacks(Collection<ItemStack> itemStacks) throws IOException {
		writeVarInt(itemStacks.size());
		for (ItemStack itemstack : itemStacks) {
			writeItemStack(itemstack);
		}
	}

	public void writeInventory(IInventory inventory) throws IOException {
		int size = inventory.getSizeInventory();
		writeVarInt(size);
		for (int i = 0; i < size; i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			writeItemStack(stack);
		}
	}

	public void writeVarInt(int varInt) throws IOException {
		while ((varInt & -128) != 0) {
			writeByte(varInt & 127 | 128);
			varInt >>>= 7;
		}
		writeByte(varInt);
	}

	public <T extends Enum<T>> void writeEnum(T enumValue, T[] enumValues) throws IOException {
		if (enumValues.length <= 256) {
			writeByte(enumValue.ordinal());
		} else {
			writeVarInt(enumValue.ordinal());
		}
	}

	public void writeNBTTagCompound(NBTTagCompound nbttagcompound) throws IOException {
		if (nbttagcompound == null) {
			writeVarInt(-1);
		} else {
			writeVarInt(0);
			CompressedStreamTools.write(nbttagcompound, this);
		}
	}

	public void writeFluidStack(FluidStack fluidStack) throws IOException {
		if (fluidStack == null) {
			writeVarInt(-1);
		} else {
			writeVarInt(fluidStack.amount);
			writeUTF(fluidStack.getFluid().getName());
		}
	}
}
