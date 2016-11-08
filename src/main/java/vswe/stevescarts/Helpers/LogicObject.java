package vswe.stevescarts.Helpers;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.PacketHandler;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Interfaces.GuiDetector;
import vswe.stevescarts.ModuleData.ModuleData;
import vswe.stevescarts.Modules.ModuleBase;
import vswe.stevescarts.TileEntities.TileEntityDetector;

public class LogicObject {
	private byte id;
	private LogicObject parent;
	private byte type;
	private ArrayList<LogicObject> childs;
	private int x;
	private int y;
	private int level;
	private byte data;

	public LogicObject(final byte id, final byte type, final byte data) {
		this.id = id;
		this.type = type;
		this.data = data;
		this.childs = new ArrayList<LogicObject>();
	}

	public LogicObject(final byte type, final byte data) {
		this((byte) 0, type, data);
	}

	public void setParent(final TileEntityDetector detector, final LogicObject parent) {
		if (parent != null) {
			PacketHandler.sendPacket(0, new byte[] { parent.id, this.getExtra(), this.data });
			for (final LogicObject child : this.childs) {
				child.setParent(detector, this);
			}
		} else {
			PacketHandler.sendPacket(1, new byte[] { this.id });
		}
	}

	public void setParent(final LogicObject parent) {
		if (this.parent != null) {
			this.parent.childs.remove(this);
		}
		this.parent = parent;
		if (this.parent != null && this.parent.hasRoomForChild()) {
			this.parent.childs.add(this);
		}
	}

	public ArrayList<LogicObject> getChilds() {
		return this.childs;
	}

	public LogicObject getParent() {
		return this.parent;
	}

	public byte getId() {
		return this.id;
	}

	public byte getExtra() {
		return this.type;
	}

	public byte getData() {
		return this.data;
	}

	public void setX(final int val) {
		this.x = val;
	}

	public void setY(final int val) {
		this.y = val;
	}

	public void setXCenter(final int val) {
		this.setX(val + (this.isOperator() ? -10 : -8));
	}

	public void setYCenter(final int val) {
		this.setY(val + (this.isOperator() ? -5 : -8));
	}

	@SideOnly(Side.CLIENT)
	public void draw(final GuiDetector gui, final int mouseX, final int mouseY, final int x, final int y) {
		this.generatePosition(x - 50, y, 100, 0);
		this.draw(gui, mouseX, mouseY);
	}

	@SideOnly(Side.CLIENT)
	public void draw(final GuiDetector gui, final int mouseX, final int mouseY) {
		if (!this.isOperator()) {
			ResourceHelper.bindResource(GuiDetector.texture);
			int yIndex = 0;
			if (gui.inRect(mouseX, mouseY, this.getRect())) {
				yIndex = 1;
			}
			gui.drawTexturedModalRect(gui.getGuiLeft() + this.x, gui.getGuiTop() + this.y, 0, 202 + yIndex * 16, 16, 16);
			if (this.isModule()) {
				ResourceHelper.bindResource(GuiDetector.moduleTexture);
				final ModuleData module = ModuleData.getList().get(this.data);
				if (module != null) {
					gui.drawModuleIcon(module, gui.getGuiLeft() + this.x, gui.getGuiTop() + this.y, 1.0f, 1.0f, 0.0f, 0.0f);
				}
			} else {
				ResourceHelper.bindResource(GuiDetector.stateTexture);
				final int[] src = gui.getModuleTexture(this.data);
				gui.drawTexturedModalRect(gui.getGuiLeft() + this.x, gui.getGuiTop() + this.y, src[0], src[1], 16, 16);
			}
		} else {
			ResourceHelper.bindResource(GuiDetector.texture);
			final int[] src2 = gui.getOperatorTexture(this.data);
			gui.drawTexturedModalRect(gui.getGuiLeft() + this.x, gui.getGuiTop() + this.y, src2[0], src2[1], 20, 11);
			if (gui.inRect(mouseX, mouseY, this.getRect())) {
				int yIndex2;
				if (gui.currentObject == null) {
					yIndex2 = 2;
				} else if (this.hasRoomForChild() && this.isChildValid(gui.currentObject)) {
					yIndex2 = 0;
				} else {
					yIndex2 = 1;
				}
				gui.drawTexturedModalRect(gui.getGuiLeft() + this.x, gui.getGuiTop() + this.y, 16, 202 + yIndex2 * 11, 20, 11);
			}
		}
		if (this.parent != null && this.parent.maxChilds() > 1) {
			int px1 = gui.getGuiLeft() + this.x;
			final int py1 = gui.getGuiTop() + this.y;
			int px2 = gui.getGuiLeft() + this.parent.x;
			int py2 = gui.getGuiTop() + this.parent.y;
			py2 += 5;
			px1 += (this.isOperator() ? 10 : 8);
			boolean tooClose = false;
			if (this.x > this.parent.x) {
				px2 += 20;
				if (px1 < px2) {
					tooClose = true;
				}
			} else if (px1 > px2) {
				tooClose = true;
			}
			if (!tooClose) {
				Gui.drawRect(px1, py2, px2, py2 + 1, -12566464);
				Gui.drawRect(px1, py1, px1 + 1, py2, -12566464);
				GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			}
		}
		for (final LogicObject child : this.childs) {
			child.draw(gui, mouseX, mouseY);
		}
	}

	public void generatePosition(final int x, final int y, final int w, final int level) {
		this.setXCenter(x + w / 2);
		this.setYCenter(y);
		this.level = level;
		final int max = this.maxChilds();
		for (int i = 0; i < this.childs.size(); ++i) {
			this.childs.get(i).generatePosition(x + w / max * i, y + (this.childs.get(i).isOperator() ? 11 : 16), w / max, level + ((this.childs.get(i).maxChilds() > 1) ? 1 : 0));
		}
	}

	private boolean isModule() {
		return this.type == 0;
	}

	private boolean isOperator() {
		return this.type == 1;
	}

	private boolean isState() {
		return this.type == 2;
	}

	private OperatorObject getOperator() {
		if (this.isOperator()) {
			return OperatorObject.getAllOperators().get(this.data);
		}
		return null;
	}

	public boolean evaluateLogicTree(final TileEntityDetector detector, final MinecartModular cart, final int depth) {
		if (depth >= 1000) {
			return false;
		}
		if (this.isState()) {
			final ModuleState state = ModuleState.getStates().get(this.getData());
			return state != null && state.evaluate(cart);
		}
		if (this.isModule()) {
			for (final ModuleBase module : cart.getModules()) {
				if (this.getData() == module.getModuleId()) {
					return true;
				}
			}
			return false;
		}
		if (this.getChilds().size() != this.maxChilds()) {
			return false;
		}
		final OperatorObject operator = this.getOperator();
		if (operator == null) {
			return false;
		}
		if (operator.getChildCount() == 2) {
			return operator.evaluate(detector, cart, depth + 1, this.getChilds().get(0), this.getChilds().get(1));
		}
		if (operator.getChildCount() == 1) {
			return operator.evaluate(detector, cart, depth + 1, this.getChilds().get(0), null);
		}
		return operator.evaluate(detector, cart, depth + 1, null, null);
	}

	private int maxChilds() {
		final OperatorObject operator = this.getOperator();
		if (operator != null) {
			return operator.getChildCount();
		}
		return 0;
	}

	public boolean isChildValid(final LogicObject child) {
		if (this.level >= 4 && child.isOperator()) {
			return false;
		}
		if (this.level >= 5) {
			return false;
		}
		final OperatorObject operator = this.getOperator();
		final OperatorObject operatorchild = child.getOperator();
		return operator == null || operatorchild == null || operator.isChildValid(operatorchild);
	}

	public boolean canBeRemoved() {
		final OperatorObject operator = this.getOperator();
		return operator == null || operator.inTab();
	}

	public boolean hasRoomForChild() {
		return this.childs.size() < this.maxChilds();
	}

	public int[] getRect() {
		if (!this.isOperator()) {
			return new int[] { this.x, this.y, 16, 16 };
		}
		return new int[] { this.x, this.y, 20, 11 };
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof LogicObject) {
			final LogicObject logic = (LogicObject) obj;
			return logic.id == this.id && ((logic.parent == null && this.parent == null) || (logic.parent != null && this.parent != null && logic.parent.id == this.parent.id)) && logic.getExtra() == this.getExtra() && logic.getData() == this.getData();
		}
		return false;
	}

	public LogicObject copy(final LogicObject parent) {
		final LogicObject obj = new LogicObject(this.id, this.getExtra(), this.getData());
		obj.setParent(parent);
		return obj;
	}

	public String getName() {
		if (this.isState()) {
			final ModuleState state = ModuleState.getStates().get(this.getData());
			if (state == null) {
				return "Undefined";
			}
			return state.getName();
		} else {
			if (!this.isModule()) {
				String name = "Undefined";
				final OperatorObject operator = this.getOperator();
				if (operator != null) {
					name = operator.getName();
				}
				return name + "\nChild nodes: " + this.getChilds().size() + "/" + this.maxChilds();
			}
			final ModuleData module = ModuleData.getList().get(this.getData());
			if (module == null) {
				return "Undefined";
			}
			return module.getName();
		}
	}
}
