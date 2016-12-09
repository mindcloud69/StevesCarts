package stevesvehicles.common.blocks.tileentitys.detector;

import java.util.Collection;
import java.util.HashMap;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import stevesvehicles.client.localization.ILocalizedText;
import stevesvehicles.client.localization.entry.block.LocalizationDetector;
import stevesvehicles.common.blocks.tileentitys.TileEntityDetector;
import stevesvehicles.common.vehicles.VehicleBase;

public class OperatorObject {
	private static HashMap<Byte, OperatorObject> allOperators;
	public static final OperatorObject MAIN;
	static {
		allOperators = new HashMap<>();
		HashMap<Byte, OperatorObject> operators = new HashMap<>();
		MAIN = new OperatorObject(operators, 0, LocalizationDetector.OUTPUT, 1) {
			@Override
			public boolean inTab() {
				return false;
			}

			@Override
			public boolean evaluate(TileEntityDetector detector, VehicleBase vehicle, int depth, LogicObject A, LogicObject B) {
				return A.evaluateLogicTree(detector, vehicle, depth);
			}
		};
		new OperatorObject(operators, 1, LocalizationDetector.AND, 2) {
			@Override
			public boolean evaluate(TileEntityDetector detector, VehicleBase vehicle, int depth, LogicObject A, LogicObject B) {
				return A.evaluateLogicTree(detector, vehicle, depth) && B.evaluateLogicTree(detector, vehicle, depth);
			}
		};
		new OperatorObject(operators, 2, LocalizationDetector.OR, 2) {
			@Override
			public boolean evaluate(TileEntityDetector detector, VehicleBase vehicle, int depth, LogicObject A, LogicObject B) {
				return A.evaluateLogicTree(detector, vehicle, depth) || B.evaluateLogicTree(detector, vehicle, depth);
			}
		};
		new OperatorObject(operators, 3, LocalizationDetector.NOT, 1) {
			@Override
			public boolean isChildValid(OperatorObject child) {
				return getId() != child.id;
			}

			@Override
			public boolean evaluate(TileEntityDetector detector, VehicleBase vehicle, int depth, LogicObject A, LogicObject B) {
				return !A.evaluateLogicTree(detector, vehicle, depth);
			}
		};
		new OperatorObject(operators, 4, LocalizationDetector.XOR, 2) {
			@Override
			public boolean evaluate(TileEntityDetector detector, VehicleBase vehicle, int depth, LogicObject A, LogicObject B) {
				return A.evaluateLogicTree(detector, vehicle, depth) != B.evaluateLogicTree(detector, vehicle, depth);
			}
		};
		new OperatorObjectRedirection(operators, 5, LocalizationDetector.TOP_UNIT, 0, 1, 0);
		new OperatorObjectRedirection(operators, 6, LocalizationDetector.BOTTOM_UNIT, 0, -1, 0);
		new OperatorObjectRedirection(operators, 7, LocalizationDetector.NORTH_UNIT, 0, 0, -1);
		new OperatorObjectRedirection(operators, 8, LocalizationDetector.WEST_UNIT, -1, 0, 0);
		new OperatorObjectRedirection(operators, 9, LocalizationDetector.SOUTH_UNIT, 0, 0, 1);
		new OperatorObjectRedirection(operators, 10, LocalizationDetector.EAST_UNIT, 1, 0, 0);
		// Note that IDs are also used by the specific types, the next ID here
		// shouldn't be 11, that one is already in use.
		for (DetectorType type : DetectorType.values()) {
			type.initOperators(new HashMap<>(operators));
		}
	}

	public static Collection<OperatorObject> getOperatorList(int meta) {
		return getOperatorList(DetectorType.getTypeFromInt(meta));
	}

	public static Collection<OperatorObject> getOperatorList(DetectorType type) {
		return type.getOperators().values();
	}

	public static HashMap<Byte, OperatorObject> getAllOperators() {
		return allOperators;
	}

	public static class OperatorObjectRedirection extends OperatorObject {
		private BlockPos offset;

		public OperatorObjectRedirection(HashMap<Byte, OperatorObject> operators, int ID, ILocalizedText name, int x, int y, int z) {
			super(operators, ID, name, 0);
			offset = new BlockPos(x, y, z);
		}

		@Override
		public boolean evaluate(TileEntityDetector detector, VehicleBase vehicle, int depth, LogicObject A, LogicObject B) {
			TileEntity tileentity = detector.getWorld().getTileEntity(offset.add(detector.getPos()));
			return tileentity != null && tileentity instanceof TileEntityDetector && ((TileEntityDetector) tileentity).evaluate(vehicle, depth);
		}
	}

	public static class OperatorObjectRedstone extends OperatorObject {
		private BlockPos offset;

		public OperatorObjectRedstone(HashMap<Byte, OperatorObject> operators, int ID, ILocalizedText name, int x, int y, int z) {
			super(operators, ID, name, 0);
			offset = new BlockPos(x, y, z);
		}

		@Override
		public boolean evaluate(TileEntityDetector detector, VehicleBase vehicle, int depth, LogicObject A, LogicObject B) {
			BlockPos pos = offset.add(detector.getPos());
			if (offset.equals(BlockPos.ORIGIN)) {
				return detector.getWorld().isBlockIndirectlyGettingPowered(pos) > 0;
			} else {
				int facing;
				if (pos.getY() > 0) {
					facing = 0;
				} else if (pos.getY() < 0) {
					facing = 1;
				} else if (pos.getX() > 0) {
					facing = 4;
				} else if (pos.getX() < 0) {
					facing = 5;
				} else if (pos.getZ() > 0) {
					facing = 2;
				} else {
					facing = 3;
				}
				return detector.getWorld().getRedstonePower(pos, EnumFacing.VALUES[facing]) > 0;
			}
		}
	}

	private byte id;
	private ILocalizedText name;
	private int children;

	public OperatorObject(HashMap<Byte, OperatorObject> operators, int id, ILocalizedText name, int children) {
		this.id = (byte) id;
		this.name = name;
		this.children = children;
		operators.put(this.id, this);
		allOperators.put(this.id, this);
	}

	public byte getId() {
		return id;
	}

	public String getName() {
		return name.translate();
	}

	public int getChildCount() {
		return children;
	}

	public boolean inTab() {
		return true;
	}

	public boolean isChildValid(OperatorObject child) {
		return true;
	}

	public boolean evaluate(TileEntityDetector detector, VehicleBase vehicle, int depth, LogicObject A, LogicObject B) {
		return false;
	}
}
