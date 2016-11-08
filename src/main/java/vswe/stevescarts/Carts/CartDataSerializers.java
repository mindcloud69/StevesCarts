package vswe.stevescarts.Carts;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;

public class CartDataSerializers {

	public CartDataSerializers() {
		DataSerializers.registerSerializer(VARINT);
	}

	public static final DataSerializer<int[]> VARINT = new DataSerializer<int[]>(){
		@Override
		public void write(PacketBuffer buf, int[] value)
		{
			buf.writeVarIntArray(value);
		}
		@Override
		public int[] read(PacketBuffer buf)
		{
			return buf.readVarIntArray();
		}
		@Override
		public DataParameter<int[]> createKey(int id)
		{
			return new DataParameter(id, this);
		}
	};
}
