package stevesvehicles.common.blocks.tileentitys;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.api.network.DataReader;
import stevesvehicles.api.network.DataWriter;
import stevesvehicles.api.network.IStreamable;
import stevesvehicles.client.gui.screen.GuiBase;
import stevesvehicles.client.gui.screen.GuiDetector;
import stevesvehicles.common.blocks.tileentitys.detector.DetectorType;
import stevesvehicles.common.blocks.tileentitys.detector.LogicObject;
import stevesvehicles.common.blocks.tileentitys.detector.LogicObjectOperator;
import stevesvehicles.common.blocks.tileentitys.detector.OperatorObject;
import stevesvehicles.common.container.ContainerBase;
import stevesvehicles.common.container.ContainerDetector;
import stevesvehicles.common.network.PacketHandler;
import stevesvehicles.common.network.PacketType;
import stevesvehicles.common.vehicles.VehicleBase;

public class TileEntityDetector extends TileEntityBase implements ITickable, IStreamable {
	@SideOnly(Side.CLIENT)
	@Override
	public GuiBase getGui(InventoryPlayer inv) {
		return new GuiDetector(inv, this);
	}

	@Override
	public ContainerBase getContainer(InventoryPlayer inv) {
		return new ContainerDetector(this);
	}

	public LogicObject mainObj;

	public TileEntityDetector() {
		mainObj = new LogicObjectOperator((byte) 0, OperatorObject.MAIN);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		byte count = nbttagcompound.getByte("LogicObjectCount");
		for (int i = 0; i < count; i++) {
			loadLogicObjectFromInteger(nbttagcompound.getInteger("LogicObject" + i));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		int count = saveLogicObject(nbttagcompound, mainObj, 0, false);
		nbttagcompound.setByte("LogicObjectCount", (byte) count);
		return nbttagcompound;
	}

	private int saveLogicObject(NBTTagCompound nbttagcompound, LogicObject obj, int id, boolean saveMe) {
		if (saveMe) {
			nbttagcompound.setInteger("LogicObject" + id++, saveLogicObjectToInteger(obj));
		}
		for (LogicObject child : obj.getChildren()) {
			id = saveLogicObject(nbttagcompound, child, id, true);
		}
		return id;
	}

	private int saveLogicObjectToInteger(LogicObject obj) {
		return (obj.getInfoShort() << 16) | obj.getData();
	}

	private void loadLogicObjectFromInteger(int val) {
		short info = (short) ((val >> 16) & 65535);
		short data = (short) (val & 65535);
		LogicObject.createObject(this, info, data);
	}

	private int activeTimer = 20;

	@Override
	public void update() {
		if (activeTimer > 0) {
			if (--activeTimer == 0) {
				IBlockState state = getWorld().getBlockState(pos);
				Block block = state.getBlock();
				DetectorType.getTypeFromSate(getWorld().getBlockState(pos)).deactivate(this);
				world.setBlockState(pos, state.withProperty(DetectorType.ACTIVE, true), 3);
			}
		}
	}

	public DetectorType getType() {
		return world.getBlockState(pos).getValue(DetectorType.SATE);
	}
	
	@Override
	public void writeData(DataWriter data) throws IOException {
		LogicObject parent = mainObj.getParent();
		if (parent != null) {
			List<LogicObject> objects = new ArrayList<>();
			mainObj.fillTree(objects, parent);
			data.writeBoolean(true);
			data.writeByte(objects.size());
			for (LogicObject object : objects) {
				data.writeByte(object.getParent().getId());
				data.writeByte(object.getType());
				data.writeShort(object.getData());
			}
			PacketHandler.sendCustomToServer(data);
		} else {
			data.writeBoolean(false);
			data.writeByte(mainObj.getId());
			PacketHandler.sendCustomToServer(data);
		}
	}
	
	@Override
	public void readData(DataReader data, EntityPlayer player) throws IOException {
		// add object
		if (data.readBoolean()) {
			int count = data.readByte();
			for (int i = 0; i < count; i++) {
				createObject(data);
			}
			// remove object
		} else {
			removeObject(mainObj, data.readByte());
		}
	}

	private void createObject(DataReader dr) throws IOException {
		byte lowestId = (byte) -1;
		for (int i = 0; i < 128; i++) {
			if (!isIdOccupied(mainObj, i)) {
				lowestId = (byte) i;
				break;
			}
		}
		if (lowestId == -1) {
			return;
		}
		LogicObject.createObject(this, lowestId, dr);
	}

	public LogicObject getObjectFromId(LogicObject object, int id) {
		if (object.getId() == id) {
			return object;
		}
		for (LogicObject child : object.getChildren()) {
			LogicObject result = getObjectFromId(child, id);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	private boolean removeObject(LogicObject object, int idToRemove) {
		if (object.getId() == idToRemove) {
			object.setParent(null);
			return true;
		}
		for (LogicObject child : object.getChildren()) {
			if (removeObject(child, idToRemove)) {
				return true;
			}
		}
		return false;
	}

	private boolean isIdOccupied(LogicObject object, int id) {
		if (object.getId() == id) {
			return true;
		}
		for (LogicObject child : object.getChildren()) {
			if (isIdOccupied(child, id)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void initGuiData(Container con, IContainerListener crafting) {
		// sendAllLogicObjects(con, crafting, mainObj);
	}

	@Override
	public void checkGuiData(Container con, IContainerListener crafting) {
		sendUpdatedLogicObjects(con, crafting, mainObj, ((ContainerDetector) con).mainObj);
	}

	private void sendUpdatedLogicObjects(Container con, IContainerListener crafting, LogicObject real, LogicObject cache) {
		if (!real.equals(cache)) {
			LogicObject parent = cache.getParent();
			cache.setParent(null);
			LogicObject clone = real.copy(parent);
			removeLogicObject(con, crafting, cache);
			sendLogicObject(con, crafting, clone);
			cache = clone;
		}
		while (real.getChildren().size() > cache.getChildren().size()) {
			int i = cache.getChildren().size();
			LogicObject clone = real.getChildren().get(i).copy(cache);
			sendLogicObject(con, crafting, clone);
		}
		while (real.getChildren().size() < cache.getChildren().size()) {
			int i = real.getChildren().size();
			LogicObject toBeRemoved = cache.getChildren().get(i);
			toBeRemoved.setParent(null);
			removeLogicObject(con, crafting, toBeRemoved);
		}
		for (int i = 0; i < real.getChildren().size(); i++) {
			sendUpdatedLogicObjects(con, crafting, real.getChildren().get(i), cache.getChildren().get(i));
		}
	}

	private void sendLogicObject(Container con, IContainerListener crafting, LogicObject obj) {
		if (obj.getParent() == null) {
			return;
		}
		updateGuiData(con, crafting, 0, obj.getInfoShort());
		updateGuiData(con, crafting, 1, obj.getData());
	}

	private void removeLogicObject(Container con, IContainerListener crafting, LogicObject obj) {
		updateGuiData(con, crafting, 2, obj.getId());
	}

	private short oldData;
	private boolean hasOldData;

	@Override
	public void receiveGuiData(int id, short data) {
		if (id == 0) {
			oldData = data;
			hasOldData = true;
		} else if (id == 1) {
			if (!hasOldData) {
				System.out.println("Doesn't have the other part of the data");
				return;
			}
			LogicObject.createObject(this, oldData, data);
			recalculateTree();
			hasOldData = false;
		} else if (id == 2) {
			removeObject(mainObj, data);
			recalculateTree();
		}
	}

	public void recalculateTree() {
		mainObj.generatePosition(5, 60, 245, 0);
	}

	public boolean evaluate(VehicleBase vehicle, int depth) {
		return mainObj.evaluateLogicTree(this, vehicle, depth);
	}

	public void handleCart(VehicleBase vehicle) {
		boolean truthValue = evaluate(vehicle, 0);
		IBlockState state = world.getBlockState(pos);
		boolean isOn = state.getValue(DetectorType.ACTIVE);
		if (truthValue != isOn) {
			if (truthValue) {
				DetectorType.getTypeFromSate(state).activate(this, vehicle);
				state = state.withProperty(DetectorType.ACTIVE, true);
			} else {
				DetectorType.getTypeFromSate(state).deactivate(this);
				state = state.withProperty(DetectorType.ACTIVE, false);
			}
			world.setBlockState(pos, state, 3);
		}
		if (truthValue) {
			activeTimer = 20;
		}
	}
}
