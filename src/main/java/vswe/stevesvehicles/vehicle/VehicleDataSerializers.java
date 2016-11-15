package vswe.stevesvehicles.vehicle;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;

public class VehicleDataSerializers {

	public static void init() {
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
