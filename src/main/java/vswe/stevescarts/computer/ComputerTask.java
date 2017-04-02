package vswe.stevescarts.computer;

import vswe.stevescarts.modules.workers.ModuleComputer;

import java.util.Random;

public class ComputerTask {
	private static Random rand;
	private ModuleComputer module;
	private ComputerProg prog;
	private int info;

	public ComputerTask(final ModuleComputer module, final ComputerProg prog) {
		this.module = module;
		this.prog = prog;
	}

	public int getTime() {
		return 5;
	}

	public int run(final ComputerProg prog, final int id) {
		if (this.isFlowGoto()) {
			final int labelId = this.getFlowLabelId();
			for (int i = 0; i < prog.getTasks().size(); ++i) {
				final ComputerTask task = prog.getTasks().get(i);
				if (task.isFlowLabel() && task.getFlowLabelId() == labelId) {
					return i;
				}
			}
		} else if (this.isFlowCondition()) {
			final boolean condition = this.evalFlowCondition();
			int nested = 0;
			if (!condition) {
				if (this.isFlowIf() || this.isFlowElseif()) {
					for (int j = id + 1; j < prog.getTasks().size(); ++j) {
						final ComputerTask task2 = prog.getTasks().get(j);
						if (task2.isFlowIf()) {
							++nested;
						} else if (task2.isFlowElseif() || task2.isFlowElse() || task2.isFlowEndif()) {
							if (nested == 0) {
								return j;
							}
							if (task2.isFlowEndif()) {
								--nested;
							}
						}
					}
				} else if (this.isFlowWhile()) {
					for (int j = id + 1; j < prog.getTasks().size(); ++j) {
						final ComputerTask task2 = prog.getTasks().get(j);
						if (task2.isFlowWhile()) {
							++nested;
						} else if (task2.isFlowEndwhile()) {
							if (nested == 0) {
								return j;
							}
							--nested;
						}
					}
				}
			}
		} else if (this.isFlowFor()) {
			final boolean condition = this.evalFlowFor();
			if (!condition) {
				int nested = 0;
				for (int j = id + 1; j < prog.getTasks().size(); ++j) {
					final ComputerTask task2 = prog.getTasks().get(j);
					if (task2.isFlowFor()) {
						++nested;
					} else if (task2.isFlowEndfor()) {
						if (nested == 0) {
							return j;
						}
						--nested;
					}
				}
			}
		} else if (this.isFlowContinue() || this.isFlowBreak()) {
			int nested2 = 0;
			for (int i = id + 1; i < prog.getTasks().size(); ++i) {
				final ComputerTask task = prog.getTasks().get(i);
				if (task.isFlowWhile() || task.isFlowFor()) {
					++nested2;
				} else if (task.isFlowEndwhile() || task.isFlowEndfor()) {
					if (nested2 == 0) {
						if (this.isFlowContinue()) {
							return task.preload(prog, i);
						}
						return i;
					} else {
						--nested2;
					}
				}
			}
		} else if (isVar(this.getType()) && !this.isVarEmpty()) {
			final ComputerVar var = this.getVarVar();
			if (var != null) {
				int value1;
				if (this.getVarUseFirstVar()) {
					final ComputerVar var2 = this.getVarFirstVar();
					if (var2 == null) {
						return -1;
					}
					value1 = var2.getByteValue();
				} else {
					value1 = this.getVarFirstInteger();
				}
				int value2;
				if (this.hasTwoValues()) {
					if (this.getVarUseSecondVar()) {
						final ComputerVar var3 = this.getVarSecondVar();
						if (var3 == null) {
							return -1;
						}
						value2 = var3.getByteValue();
					} else {
						value2 = this.getVarSecondInteger();
					}
				} else {
					value2 = 0;
				}
				var.setByteValue(this.calcVarValue(value1, value2));
			}
		} else if (isControl(this.getType()) && !this.isControlEmpty()) {
			final ComputerControl control = ComputerControl.getMap().get((byte) this.getControlType());
			if (control != null && control.isControlValid(this.module.getCart())) {
				int value3;
				if (this.getControlUseVar()) {
					final ComputerVar var4 = this.getControlVar();
					if (var4 == null) {
						return -1;
					}
					value3 = var4.getByteValue();
				} else {
					value3 = this.getControlInteger();
				}
				control.runHandler(this.module.getCart(), (byte) value3);
			}
		} else if (isInfo(this.getType()) && !this.isInfoEmpty()) {
			final ComputerInfo info = ComputerInfo.getMap().get((byte) this.getControlType());
			if (info != null && info.isInfoValid(this.module.getCart())) {
				final ComputerVar var5 = this.getInfoVar();
				if (var5 != null) {
					info.getHandler(this.module.getCart(), var5);
				}
			}
		} else if (isVar(this.getType())) {
			for (final ComputerVar var5 : prog.getVars()) {
				System.out.println(var5.getFullInfo());
			}
		}
		return -1;
	}

	public int preload(final ComputerProg prog, final int id) {
		if (this.isFlowElseif() || this.isFlowElse()) {
			int nested = 0;
			for (int i = id + 1; i < prog.getTasks().size(); ++i) {
				final ComputerTask task = prog.getTasks().get(i);
				if (task.isFlowIf()) {
					++nested;
				} else if (task.isFlowEndif()) {
					if (nested == 0) {
						return i;
					}
					--nested;
				}
			}
		} else if (this.isFlowEndwhile()) {
			int nested = 0;
			for (int i = id - 1; i >= 0; --i) {
				final ComputerTask task = prog.getTasks().get(i);
				if (task.isFlowEndwhile()) {
					++nested;
				} else if (task.isFlowWhile()) {
					if (nested == 0) {
						return i;
					}
					--nested;
				}
			}
		} else if (this.isFlowFor()) {
			final ComputerVar var = this.getFlowForVar();
			if (var != null) {
				if (this.getFlowForUseStartVar()) {
					final ComputerVar var2 = this.getFlowForStartVar();
					if (var2 != null) {
						var.setByteValue(var2.getByteValue());
					}
				} else {
					var.setByteValue(this.getFlowForStartInteger());
				}
			}
		} else if (this.isFlowEndfor()) {
			System.out.println("End for");
			int nested = 0;
			for (int i = id - 1; i >= 0; --i) {
				final ComputerTask task = prog.getTasks().get(i);
				if (task.isFlowEndfor()) {
					++nested;
				} else if (task.isFlowFor()) {
					if (nested == 0) {
						final ComputerVar var3 = task.getFlowForVar();
						if (var3 != null) {
							final int dif = task.getFlowForDecrease() ? -1 : 1;
							var3.setByteValue(var3.getByteValue() + dif);
						}
						return i;
					}
					--nested;
				}
			}
		}
		return id;
	}

	@Override
	public ComputerTask clone() {
		final ComputerTask clone = new ComputerTask(this.module, this.prog);
		clone.info = this.info;
		return clone;
	}

	public ComputerProg getProgram() {
		return this.prog;
	}

	public void setInfo(final int id, final short val) {
		int iVal = val;
		if (iVal < 0) {
			iVal += 65536;
		}
		final boolean oldVal = this.getIsActivated();
		this.info &= ~(65535 << id * 16);
		this.info |= iVal << id * 16;
		if (oldVal != this.getIsActivated()) {
			this.module.activationChanged();
		}
	}

	public short getInfo(final int id) {
		return (short) ((this.info & 65535 << id * 16) >> id * 16);
	}

	public void setIsActivated(final boolean val) {
		final boolean oldVal = this.getIsActivated();
		this.info &= 0xFFFFFFFE;
		this.info |= (val ? 1 : 0);
		if (oldVal != val) {
			this.module.activationChanged();
		}
	}

	public boolean getIsActivated() {
		return (this.info & 0x1) != 0x0;
	}

	public void setType(final int type) {
		final int oldType = this.getType();
		final boolean flag = isBuild(oldType);
		this.info &= 0xFFFFFFF1;
		this.info |= type << 1;
		if (oldType != type && (!flag || !isBuild(type))) {
			this.info &= 0xF;
		}
	}

	public int getType() {
		return (this.info & 0xE) >> 1;
	}

	public static boolean isEmpty(final int type) {
		return type == 0;
	}

	public static boolean isFlow(final int type) {
		return type == 1;
	}

	public static boolean isVar(final int type) {
		return type == 2;
	}

	public static boolean isControl(final int type) {
		return type == 3;
	}

	public static boolean isInfo(final int type) {
		return type == 4;
	}

	public static boolean isBuild(final int type) {
		return type == 5 || isAddon(type);
	}

	public static boolean isAddon(final int type) {
		return type == 6;
	}

	public int getImage() {
		if (isEmpty(this.getType())) {
			return -1;
		}
		if (isFlow(this.getType())) {
			return this.getFlowImageForTask();
		}
		if (isVar(this.getType())) {
			return getVarImage(this.getVarType());
		}
		if (isControl(this.getType())) {
			return getControlImage(this.getControlType());
		}
		if (isInfo(this.getType())) {
			return getInfoImage(this.getInfoType());
		}
		return -1;
	}

	public static String getTypeName(final int type) {
		switch (type) {
			default: {
				return "Empty";
			}
			case 1: {
				return "Flow Control";
			}
			case 2: {
				return "Variable Control";
			}
			case 3: {
				return "Module Control";
			}
			case 4: {
				return "Module Info";
			}
			case 5: {
				return "Builder";
			}
			case 6: {
				return "Addon";
			}
		}
	}

	@Override
	public String toString() {
		if (isEmpty(this.getType())) {
			return "Empty";
		}
		if (isFlow(this.getType())) {
			return getFlowTypeName(this.getFlowType()) + " " + this.getFlowText();
		}
		if (isVar(this.getType())) {
			return getVarTypeName(this.getVarType()) + ": " + this.getVarText();
		}
		if (isControl(this.getType())) {
			return "Set " + getControlTypeName(this.getControlType()) + " to " + this.getControlText();
		}
		if (isInfo(this.getType())) {
			return "Set " + getVarName(this.getInfoVar()) + " to " + getInfoTypeName(this.getInfoType());
		}
		return "Unknown";
	}

	public int getFlowType() {
		return (this.info & 0xF0) >> 4;
	}

	public void setFlowType(final int type) {
		final int oldType = this.getFlowType();
		if (oldType == type) {
			return;
		}
		final boolean conditionFlag = this.isFlowCondition();
		this.info &= 0xFFFFFF0F;
		this.info |= type << 4;
		if (!conditionFlag || !this.isFlowCondition()) {
			this.info &= 0xFF;
		}
	}

	public boolean isFlowEmpty() {
		return isFlow(this.getType()) && this.getFlowType() == 0;
	}

	public boolean isFlowLabel() {
		return isFlow(this.getType()) && this.getFlowType() == 1;
	}

	public boolean isFlowGoto() {
		return isFlow(this.getType()) && this.getFlowType() == 2;
	}

	public boolean isFlowIf() {
		return isFlow(this.getType()) && this.getFlowType() == 3;
	}

	public boolean isFlowElseif() {
		return isFlow(this.getType()) && this.getFlowType() == 4;
	}

	public boolean isFlowElse() {
		return isFlow(this.getType()) && this.getFlowType() == 5;
	}

	public boolean isFlowWhile() {
		return isFlow(this.getType()) && this.getFlowType() == 6;
	}

	public boolean isFlowFor() {
		return isFlow(this.getType()) && this.getFlowType() == 7;
	}

	public boolean isFlowEnd() {
		return isFlow(this.getType()) && this.getFlowType() == 8;
	}

	public boolean isFlowBreak() {
		return isFlow(this.getType()) && this.getFlowType() == 9;
	}

	public boolean isFlowContinue() {
		return isFlow(this.getType()) && this.getFlowType() == 10;
	}

	public boolean isFlowCondition() {
		return this.isFlowIf() || this.isFlowElseif() || this.isFlowWhile();
	}

	public static int getFlowImage(final int type) {
		return 12 + type;
	}

	public int getFlowImageForTask() {
		if (this.isFlowEnd()) {
			return getEndImage(this.getFlowEndType());
		}
		return getFlowImage(this.getFlowType());
	}

	public static String getFlowTypeName(final int type) {
		switch (type) {
			default: {
				return "Empty";
			}
			case 1: {
				return "Label";
			}
			case 2: {
				return "GoTo";
			}
			case 3: {
				return "If";
			}
			case 4: {
				return "Else if";
			}
			case 5: {
				return "Else";
			}
			case 6: {
				return "While";
			}
			case 7: {
				return "For";
			}
			case 8: {
				return "End";
			}
			case 9: {
				return "Break";
			}
			case 10: {
				return "Continue";
			}
		}
	}

	public String getFlowText() {
		if (this.isFlowLabel() || this.isFlowGoto()) {
			return "[" + this.getFlowLabelId() + "]";
		}
		if (this.isFlowCondition()) {
			final ComputerVar var = this.getFlowConditionVar();
			String str = getVarName(var);
			str += " ";
			str += getFlowOperatorName(this.getFlowConditionOperator(), false);
			str += " ";
			if (this.getFlowConditionUseSecondVar()) {
				final ComputerVar var2 = this.getFlowConditionSecondVar();
				str += getVarName(var2);
			} else {
				str += this.getFlowConditionInteger();
			}
			return str;
		}
		if (this.isFlowFor()) {
			String str2 = getVarName(this.getFlowForVar());
			str2 += " = ";
			if (this.getFlowForUseStartVar()) {
				str2 += getVarName(this.getFlowForStartVar());
			} else {
				str2 += this.getFlowForStartInteger();
			}
			str2 += " to ";
			if (this.getFlowForUseEndVar()) {
				str2 += getVarName(this.getFlowForEndVar());
			} else {
				str2 += this.getFlowForEndInteger();
			}
			str2 = str2 + "  step " + (this.getFlowForDecrease() ? "-" : "+") + "1";
			return str2;
		}
		if (this.isFlowEnd()) {
			return getEndTypeName(this.getFlowEndType());
		}
		return "(Not set)";
	}

	public int getFlowLabelId() {
		return (this.info & 0x1F00) >> 8;
	}

	public void setFlowLabelId(int id) {
		if (id < 0) {
			id = 0;
		} else if (id > 31) {
			id = 31;
		}
		this.info &= 0xFFFFE0FF;
		this.info |= id << 8;
	}

	public int getFlowConditionVarIndex() {
		return this.getVarIndex(8);
	}

	public ComputerVar getFlowConditionVar() {
		return this.getVar(8);
	}

	public void setFlowConditionVar(final int val) {
		this.setVar(8, val);
	}

	public int getFlowConditionOperator() {
		return (this.info & 0xE000) >> 13;
	}

	public void setFlowConditionOperator(final int val) {
		this.info &= 0xFFFF1FFF;
		this.info |= val << 13;
	}

	public boolean isFlowConditionOperatorEquals() {
		return this.getFlowConditionOperator() == 0;
	}

	public boolean isFlowConditionOperatorNotequals() {
		return this.getFlowConditionOperator() == 1;
	}

	public boolean isFlowConditionOperatorGreaterequals() {
		return this.getFlowConditionOperator() == 2;
	}

	public boolean isFlowConditionOperatorGreater() {
		return this.getFlowConditionOperator() == 3;
	}

	public boolean isFlowConditionOperatorLesserequals() {
		return this.getFlowConditionOperator() == 4;
	}

	public boolean isFlowConditionOperatorLesser() {
		return this.getFlowConditionOperator() == 5;
	}

	public boolean getFlowConditionUseSecondVar() {
		return this.getUseOptionalVar(16);
	}

	public void setFlowConditionUseSecondVar(final boolean val) {
		this.setUseOptionalVar(16, val);
	}

	public int getFlowConditionInteger() {
		return this.getInteger(17);
	}

	public void setFlowConditionInteger(final int val) {
		this.setInteger(17, val);
	}

	public int getFlowConditionSecondVarIndex() {
		return this.getVarIndex(17);
	}

	public ComputerVar getFlowConditionSecondVar() {
		return this.getVar(17);
	}

	public void setFlowConditionSecondVar(final int val) {
		this.setVar(17, val);
	}

	public boolean evalFlowCondition() {
		if (!this.isFlowCondition()) {
			return false;
		}
		final ComputerVar var = this.getFlowConditionVar();
		if (var == null) {
			return false;
		}
		final int varValue = var.getByteValue();
		int compareWith;
		if (this.getFlowConditionUseSecondVar()) {
			final ComputerVar var2 = this.getFlowConditionVar();
			if (var2 == null) {
				return false;
			}
			compareWith = var2.getByteValue();
		} else {
			compareWith = this.getFlowConditionInteger();
		}
		if (this.isFlowConditionOperatorEquals()) {
			return varValue == compareWith;
		}
		if (this.isFlowConditionOperatorNotequals()) {
			return varValue != compareWith;
		}
		if (this.isFlowConditionOperatorGreaterequals()) {
			return varValue >= compareWith;
		}
		if (this.isFlowConditionOperatorGreater()) {
			return varValue > compareWith;
		}
		if (this.isFlowConditionOperatorLesserequals()) {
			return varValue <= compareWith;
		}
		return this.isFlowConditionOperatorLesser() && varValue < compareWith;
	}

	public static String getFlowOperatorName(final int type, final boolean isLong) {
		switch (type) {
			default: {
				return isLong ? "Unknown" : "?";
			}
			case 0: {
				return isLong ? "Equals to" : "=";
			}
			case 1: {
				return isLong ? "Not equals to" : "!=";
			}
			case 2: {
				return isLong ? "Greater than or equals to" : ">=";
			}
			case 3: {
				return isLong ? "Greater than" : ">";
			}
			case 4: {
				return isLong ? "Smaller than or equals to" : "<=";
			}
			case 5: {
				return isLong ? "Smaller than" : "<";
			}
		}
	}

	public int getFlowForVarIndex() {
		return this.getVarIndex(8);
	}

	public ComputerVar getFlowForVar() {
		return this.getVar(8);
	}

	public void setFlowForVar(final int val) {
		this.setVar(8, val);
	}

	public boolean getFlowForUseStartVar() {
		return this.getUseOptionalVar(13);
	}

	public void setFlowForUseStartVar(final boolean val) {
		this.setUseOptionalVar(13, val);
	}

	public int getFlowForStartInteger() {
		return this.getInteger(14);
	}

	public void setFlowForStartInteger(final int val) {
		this.setInteger(14, val);
	}

	public int getFlowForStartVarIndex() {
		return this.getVarIndex(14);
	}

	public ComputerVar getFlowForStartVar() {
		return this.getVar(14);
	}

	public void setFlowForStartVar(final int val) {
		this.setVar(14, val);
	}

	public boolean getFlowForUseEndVar() {
		return this.getUseOptionalVar(22);
	}

	public void setFlowForUseEndVar(final boolean val) {
		this.setUseOptionalVar(22, val);
	}

	public int getFlowForEndInteger() {
		return this.getInteger(23);
	}

	public void setFlowForEndInteger(final int val) {
		this.setInteger(23, val);
	}

	public int getFlowForEndVarIndex() {
		return this.getVarIndex(23);
	}

	public ComputerVar getFlowForEndVar() {
		return this.getVar(23);
	}

	public void setFlowForEndVar(final int val) {
		this.setVar(23, val);
	}

	public boolean getFlowForDecrease() {
		return (this.info & Integer.MIN_VALUE) != 0x0;
	}

	public void setFlowForDecrease(final boolean val) {
		this.info &= Integer.MAX_VALUE;
		this.info |= (val ? 1 : 0) << 31;
	}

	public boolean evalFlowFor() {
		if (!this.isFlowFor()) {
			return false;
		}
		final ComputerVar var = this.getFlowForVar();
		if (var == null) {
			return false;
		}
		final int varValue = var.getByteValue();
		int compareWith;
		if (this.getFlowForUseEndVar()) {
			final ComputerVar var2 = this.getFlowForEndVar();
			if (var2 == null) {
				return false;
			}
			compareWith = var2.getByteValue();
		} else {
			compareWith = this.getFlowForEndInteger();
		}
		return varValue != compareWith;
	}

	public int getFlowEndType() {
		return (this.info & 0x300) >> 8;
	}

	public void setFlowEndType(int val) {
		if (val < 0) {
			val = 0;
		} else if (val > 3) {
			val = 3;
		}
		this.info &= 0xFFFFFCFF;
		this.info |= val << 8;
	}

	public boolean isFlowEndif() {
		return this.isFlowEnd() && this.getFlowEndType() == 1;
	}

	public boolean isFlowEndwhile() {
		return this.isFlowEnd() && this.getFlowEndType() == 2;
	}

	public boolean isFlowEndfor() {
		return this.isFlowEnd() && this.getFlowEndType() == 3;
	}

	public static String getEndTypeName(final int type) {
		switch (type) {
			default: {
				return "(not set)";
			}
			case 1: {
				return "If";
			}
			case 2: {
				return "While";
			}
			case 3: {
				return "For";
			}
		}
	}

	public static int getEndImage(final int type) {
		if (type == 0) {
			return 20;
		}
		return 45 + type;
	}

	public int getVarType() {
		return (this.info & 0x1F0) >> 4;
	}

	public void setVarType(final int val) {
		this.info &= 0xFFFFFE0F;
		this.info |= val << 4;
	}

	public boolean isVarEmpty() {
		return isVar(this.getType()) && this.getVarType() == 0;
	}

	public boolean isVarSet() {
		return isVar(this.getType()) && this.getVarType() == 1;
	}

	public boolean isVarAdd() {
		return isVar(this.getType()) && this.getVarType() == 2;
	}

	public boolean isVarSub() {
		return isVar(this.getType()) && this.getVarType() == 3;
	}

	public boolean isVarMult() {
		return isVar(this.getType()) && this.getVarType() == 4;
	}

	public boolean isVarDiv() {
		return isVar(this.getType()) && this.getVarType() == 5;
	}

	public boolean isVarMod() {
		return isVar(this.getType()) && this.getVarType() == 6;
	}

	public boolean isVarAnd() {
		return isVar(this.getType()) && this.getVarType() == 7;
	}

	public boolean isVarOr() {
		return isVar(this.getType()) && this.getVarType() == 8;
	}

	public boolean isVarXor() {
		return isVar(this.getType()) && this.getVarType() == 9;
	}

	public boolean isVarNot() {
		return isVar(this.getType()) && this.getVarType() == 10;
	}

	public boolean isVarShiftR() {
		return isVar(this.getType()) && this.getVarType() == 11;
	}

	public boolean isVarShiftL() {
		return isVar(this.getType()) && this.getVarType() == 12;
	}

	public boolean isVarMax() {
		return isVar(this.getType()) && this.getVarType() == 13;
	}

	public boolean isVarMin() {
		return isVar(this.getType()) && this.getVarType() == 14;
	}

	public boolean isVarAbs() {
		return isVar(this.getType()) && this.getVarType() == 15;
	}

	public boolean isVarClamp() {
		return isVar(this.getType()) && this.getVarType() == 16;
	}

	public boolean isVarRand() {
		return isVar(this.getType()) && this.getVarType() == 17;
	}

	public boolean hasOneValue() {
		return this.isVarSet() || this.isVarNot() || this.isVarAbs();
	}

	public boolean hasTwoValues() {
		return !this.isVarEmpty() && !this.hasOneValue();
	}

	public int getVarVarIndex() {
		return this.getVarIndex(9);
	}

	public ComputerVar getVarVar() {
		return this.getVar(9);
	}

	public void setVarVar(final int val) {
		this.setVar(9, val);
	}

	public boolean getVarUseFirstVar() {
		return this.getUseOptionalVar(14);
	}

	public void setVarUseFirstVar(final boolean val) {
		this.setUseOptionalVar(14, val);
	}

	public int getVarFirstInteger() {
		return this.getInteger(15);
	}

	public void setVarFirstInteger(final int val) {
		this.setInteger(15, val);
	}

	public int getVarFirstVarIndex() {
		return this.getVarIndex(15);
	}

	public ComputerVar getVarFirstVar() {
		return this.getVar(15);
	}

	public void setVarFirstVar(final int val) {
		this.setVar(15, val);
	}

	public boolean getVarUseSecondVar() {
		return this.getUseOptionalVar(23);
	}

	public void setVarUseSecondVar(final boolean val) {
		this.setUseOptionalVar(23, val);
	}

	public int getVarSecondInteger() {
		return this.getInteger(24);
	}

	public void setVarSecondInteger(final int val) {
		this.setInteger(24, val);
	}

	public int getVarSecondVarIndex() {
		return this.getVarIndex(24);
	}

	public ComputerVar getVarSecondVar() {
		return this.getVar(24);
	}

	public void setVarSecondVar(final int val) {
		this.setVar(24, val);
	}

	public static String getVarTypeName(final int type) {
		switch (type) {
			default: {
				return "Empty";
			}
			case 1: {
				return "Set";
			}
			case 2: {
				return "Addition";
			}
			case 3: {
				return "Subtraction";
			}
			case 4: {
				return "Multiplication";
			}
			case 5: {
				return "Integer division";
			}
			case 6: {
				return "Modulus";
			}
			case 7: {
				return "Bitwise And";
			}
			case 8: {
				return "Bitwise Or";
			}
			case 9: {
				return "Bitwise Xor";
			}
			case 10: {
				return "Bitwise Not";
			}
			case 11: {
				return "Right Bitshift";
			}
			case 12: {
				return "Left Bitshift";
			}
			case 13: {
				return "Maximum Value";
			}
			case 14: {
				return "Minimum Value";
			}
			case 15: {
				return "Absolute Value";
			}
			case 16: {
				return "Clamp Value";
			}
			case 17: {
				return "Random Value";
			}
		}
	}

	public String getVarPrefix() {
		if (this.isVarMax()) {
			return "max(";
		}
		if (this.isVarMin()) {
			return "min(";
		}
		if (this.isVarClamp()) {
			return "clamp(" + getVarName(this.getVarVar()) + ", ";
		}
		if (this.isVarAbs()) {
			return "abs(";
		}
		if (this.isVarNot()) {
			return "~";
		}
		if (this.isVarRand()) {
			return "rand(";
		}
		return "";
	}

	public String getVarMidfix() {
		if (this.isVarMax() || this.isVarMin() || this.isVarClamp() || this.isVarRand()) {
			return ", ";
		}
		if (this.isVarAdd()) {
			return " + ";
		}
		if (this.isVarSub()) {
			return " - ";
		}
		if (this.isVarMult()) {
			return " * ";
		}
		if (this.isVarDiv()) {
			return " / ";
		}
		if (this.isVarMod()) {
			return " % ";
		}
		if (this.isVarAnd()) {
			return " & ";
		}
		if (this.isVarOr()) {
			return " | ";
		}
		if (this.isVarXor()) {
			return " ^ ";
		}
		if (this.isVarShiftR()) {
			return " >> ";
		}
		if (this.isVarShiftL()) {
			return " << ";
		}
		return "";
	}

	public String getVarPostfix() {
		if (this.isVarMax() || this.isVarMin() || this.isVarClamp() || this.isVarAbs() || this.isVarRand()) {
			return ")";
		}
		return "";
	}

	public String getVarText() {
		if (this.isVarEmpty()) {
			return "(Not set)";
		}
		String str = "";
		str += getVarName(this.getVarVar());
		str += " = ";
		str += this.getVarPrefix();
		if (this.getVarUseFirstVar()) {
			str += getVarName(this.getVarFirstVar());
		} else {
			str += this.getVarFirstInteger();
		}
		if (this.hasTwoValues()) {
			str += this.getVarMidfix();
			if (this.getVarUseSecondVar()) {
				str += getVarName(this.getVarSecondVar());
			} else {
				str += this.getVarSecondInteger();
			}
		}
		str += this.getVarPostfix();
		return str;
	}

	public static int getVarImage(final int type) {
		if (type == 17) {
			return 98;
		}
		return 49 + type;
	}

	public int calcVarValue(final int val1, int val2) {
		if (this.isVarSet()) {
			return val1;
		}
		if (this.isVarAdd()) {
			return val1 + val2;
		}
		if (this.isVarSub()) {
			return val1 - val2;
		}
		if (this.isVarMult()) {
			return val1 * val2;
		}
		if (this.isVarDiv()) {
			return val1 / val2;
		}
		if (this.isVarMod()) {
			return val1 % val2;
		}
		if (this.isVarAnd()) {
			return val1 & val2;
		}
		if (this.isVarOr()) {
			return val1 | val2;
		}
		if (this.isVarXor()) {
			return val1 ^ val2;
		}
		if (this.isVarNot()) {
			byte b = (byte) val1;
			b ^= -1;
			return b;
		}
		if (this.isVarShiftR()) {
			val2 = Math.max(val2, 8);
			val2 = Math.min(val2, 0);
			return val1 >> val2;
		}
		if (this.isVarShiftL()) {
			val2 = Math.max(val2, 8);
			val2 = Math.min(val2, 0);
			return val1 << val2;
		}
		if (this.isVarMax()) {
			return Math.max(val1, val2);
		}
		if (this.isVarMin()) {
			return Math.min(val1, val2);
		}
		if (this.isVarAbs()) {
			return Math.abs(val1);
		}
		if (this.isVarClamp()) {
			int temp = this.getVarVar().getByteValue();
			temp = Math.max(temp, val1);
			temp = Math.min(temp, val2);
			return temp;
		}
		if (!this.isVarRand()) {
			return 0;
		}
		if (++val2 <= val1) {
			return 0;
		}
		return ComputerTask.rand.nextInt(val2 - val1) + val1;
	}

	public int getControlType() {
		return (this.info & 0xFF0) >> 4;
	}

	public void setControlType(final int val) {
		this.info &= 0xFFFFF00F;
		this.info |= val << 4;
		if (!this.getControlUseVar()) {
			final int min = this.getControlMinInteger();
			final int max = this.getControlMaxInteger();
			if (this.getControlInteger() < min) {
				this.setControlInteger(min);
			} else if (this.getControlInteger() > max) {
				this.setControlInteger(max);
			}
		}
	}

	public boolean isControlEmpty() {
		return this.getControlType() == 0;
	}

	public static String getControlTypeName(final int type) {
		if (type == 0) {
			return "Empty";
		}
		final ComputerControl control = ComputerControl.getMap().get((byte) type);
		if (control == null) {
			return "(not set)";
		}
		return control.getName();
	}

	public static int getControlImage(final int type) {
		if (type == 0) {
			return 68;
		}
		final ComputerControl control = ComputerControl.getMap().get((byte) type);
		if (control == null) {
			return -1;
		}
		return control.getTexture();
	}

	public String getControlText() {
		if (this.isControlEmpty()) {
			return "(not set)";
		}
		if (this.isControlActivator()) {
			return "Activate";
		}
		if (this.getControlUseVar()) {
			final ComputerVar var = this.getControlVar();
			return getVarName(var);
		}
		return String.valueOf(this.getControlInteger());
	}

	public boolean getControlUseVar() {
		return this.getUseOptionalVar(12);
	}

	public void setControlUseVar(final boolean val) {
		this.setUseOptionalVar(12, val);
	}

	public int getControlInteger() {
		return this.getInteger(13);
	}

	public void setControlInteger(final int val) {
		this.setInteger(13, val);
	}

	public int getControlVarIndex() {
		return this.getVarIndex(13);
	}

	public ComputerVar getControlVar() {
		return this.getVar(13);
	}

	public void setControlVar(final int val) {
		this.setVar(13, val);
	}

	public int getControlMinInteger() {
		final ComputerControl control = ComputerControl.getMap().get((byte) this.getControlType());
		if (control == null) {
			return -128;
		}
		return control.getIntegerMin();
	}

	public int getControlMaxInteger() {
		final ComputerControl control = ComputerControl.getMap().get((byte) this.getControlType());
		if (control == null) {
			return 127;
		}
		return control.getIntegerMax();
	}

	public boolean getControlUseBigInteger(final int size) {
		final ComputerControl control = ComputerControl.getMap().get((byte) this.getControlType());
		return control != null && control.useIntegerOfSize(size);
	}

	public boolean isControlActivator() {
		final ComputerControl control = ComputerControl.getMap().get((byte) this.getControlType());
		return control != null && control.isActivator();
	}

	public int getInfoType() {
		return (this.info & 0xFF0) >> 4;
	}

	public void setInfoType(final int val) {
		this.info &= 0xFFFFF00F;
		this.info |= val << 4;
	}

	public boolean isInfoEmpty() {
		return this.getInfoType() == 0;
	}

	public static String getInfoTypeName(final int type) {
		if (type == 0) {
			return "Empty";
		}
		final ComputerInfo info = ComputerInfo.getMap().get((byte) type);
		if (info == null) {
			return "(not set)";
		}
		return info.getName();
	}

	public static int getInfoImage(final int type) {
		if (type == 0) {
			return 83;
		}
		final ComputerInfo info = ComputerInfo.getMap().get((byte) type);
		if (info == null) {
			return -1;
		}
		return info.getTexture();
	}

	public int getInfoVarIndex() {
		return this.getVarIndex(12);
	}

	public ComputerVar getInfoVar() {
		return this.getVar(12);
	}

	public void setInfoVar(final int val) {
		this.setVar(12, val);
	}

	private static String getVarName(final ComputerVar var) {
		if (var == null) {
			return "(not set)";
		}
		return var.getText();
	}

	private int getInteger(final int startBit) {
		final int val = (this.info & 255 << startBit) >> startBit;
		if (val > 127) {
			return val - 255;
		}
		return val;
	}

	private void setInteger(final int startBit, int val) {
		if (val < -128) {
			val = -128;
		} else if (val > 127) {
			val = 127;
		}
		if (val < 0) {
			val += 256;
		}
		this.info &= ~(255 << startBit);
		this.info |= val << startBit;
	}

	private boolean getUseOptionalVar(final int startBit) {
		return (this.info & 1 << startBit) != 0x0;
	}

	private void setUseOptionalVar(final int startBit, final boolean val) {
		if (val == this.getUseOptionalVar(startBit)) {
			return;
		}
		this.info &= ~(1 << startBit);
		this.info |= (val ? 1 : 0) << startBit;
		this.setInteger(startBit + 1, 0);
	}

	private int getVarIndex(final int startBit) {
		return ((this.info & 31 << startBit) >> startBit) - 1;
	}

	public ComputerVar getVar(final int startBit) {
		final int ind = this.getVarIndex(startBit);
		if (ind < 0 || ind >= this.prog.getVars().size()) {
			return null;
		}
		return this.prog.getVars().get(ind);
	}

	public void setVar(final int startBit, int val) {
		if (val < -1) {
			val = -1;
		} else if (val >= this.prog.getVars().size()) {
			val = this.prog.getVars().size() - 2;
		}
		++val;
		this.info &= ~(31 << startBit);
		this.info |= val << startBit;
	}

	static {
		ComputerTask.rand = new Random();
	}
}
