package vswe.stevescarts.models;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelWireTop extends ModelWire {
	public ModelWireTop() {
		this.CreateEnd(1, 0);
		this.CreateEnd(4, 0);
		this.CreateEnd(8, 1);
		this.CreateEnd(6, 2);
		this.CreateEnd(5, 4);
		this.CreateEnd(1, 6);
		this.CreateEnd(6, 6);
		this.CreateEnd(3, 7);
		this.CreateEnd(4, 8);
		this.CreateEnd(1, 9);
		this.CreateEnd(0, 11);
		this.CreateEnd(4, 11);
		this.CreateEnd(7, 11);
		this.CreateEnd(5, 12);
		this.CreateEnd(9, 12);
		this.CreateEnd(2, 13);
		this.CreateWire(1, 1, 1, 4);
		this.CreateWire(2, 2, 5, 2);
		this.CreateWire(4, 1, 4, 1);
		this.CreateWire(2, 4, 4, 4);
		this.CreateWire(3, 5, 3, 6);
		this.CreateWire(8, 2, 8, 8);
		this.CreateWire(7, 6, 7, 6);
		this.CreateWire(5, 8, 7, 8);
		this.CreateWire(7, 9, 7, 10);
		this.CreateWire(1, 7, 1, 8);
		this.CreateWire(1, 11, 3, 11);
		this.CreateWire(2, 12, 2, 12);
		this.CreateWire(6, 12, 8, 12);
	}
}
