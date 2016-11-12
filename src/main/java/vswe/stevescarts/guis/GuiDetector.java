package vswe.stevescarts.guis;

import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.blocks.tileentities.TileEntityDetector;
import vswe.stevescarts.containers.ContainerDetector;
import vswe.stevescarts.helpers.DetectorType;
import vswe.stevescarts.helpers.DropDownMenu;
import vswe.stevescarts.helpers.DropDownMenuPages;
import vswe.stevescarts.helpers.LogicObject;
import vswe.stevescarts.helpers.ModuleState;
import vswe.stevescarts.helpers.OperatorObject;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.data.ModuleData;

@SideOnly(Side.CLIENT)
public class GuiDetector extends GuiBase {
	private ArrayList<DropDownMenu> menus;
	private DropDownMenuPages modulesMenu;
	private DropDownMenu statesMenu;
	private DropDownMenu flowMenu;
	public static ResourceLocation texture;
	public static ResourceLocation moduleTexture;
	public static ResourceLocation stateTexture;
	public static ResourceLocation dropdownTexture;
	public LogicObject currentObject;
	TileEntityDetector detector;
	InventoryPlayer invPlayer;

	public GuiDetector(final InventoryPlayer invPlayer, final TileEntityDetector detector) {
		super(new ContainerDetector(invPlayer, detector));
		this.invPlayer = invPlayer;
		this.setXSize(255);
		this.setYSize(202);
		this.detector = detector;
		final Iterator<LogicObject> i$ = detector.mainObj.getChilds().iterator();
		if (i$.hasNext()) {
			final LogicObject child = i$.next();
			child.setParent(null);
		}
		detector.recalculateTree();
		(this.menus = new ArrayList<>()).add(this.modulesMenu = new DropDownMenuPages(0, 2));
		this.menus.add(this.statesMenu = new DropDownMenu(1));
		this.menus.add(this.flowMenu = new DropDownMenu(2));
	}

	@Override
	public void drawGuiForeground(final int x, final int y) {
		GL11.glDisable(2896);
		this.getFontRenderer().drawString(DetectorType.getTypeFromSate(this.detector.getWorld().getBlockState(this.detector.getPos())).getTranslatedName(), 8, 6, 4210752);
		if (this.modulesMenu.getScroll() != 0) {
			int modulePosId = 0;
			for (final ModuleData module : ModuleData.getModules()) {
				if (module.getIsValid()) {
					final int[] target = this.modulesMenu.getContentRect(modulePosId);
					if (this.drawMouseOver(module.getName(), x, y, target)) {
						break;
					}
					++modulePosId;
				}
			}
		} else if (this.statesMenu.getScroll() != 0) {
			int statesPosId = 0;
			for (final ModuleState state : ModuleState.getStateList()) {
				final int[] target = this.statesMenu.getContentRect(statesPosId);
				if (this.drawMouseOver(state.getName(), x, y, target)) {
					break;
				}
				++statesPosId;
			}
		} else if (this.flowMenu.getScroll() != 0) {
			int flowPosId = 0;
			for (final OperatorObject operator : OperatorObject.getOperatorList(this.detector.getBlockMetadata())) {
				if (operator.inTab()) {
					final int[] target = this.flowMenu.getContentRect(flowPosId);
					if (this.drawMouseOver(operator.getName(), x, y, target)) {
						break;
					}
					++flowPosId;
				}
			}
		} else {
			this.drawMouseOverFromObject(this.detector.mainObj, x, y);
		}
		GL11.glEnable(2896);
	}

	private boolean drawMouseOverFromObject(final LogicObject obj, final int x, final int y) {
		if (this.drawMouseOver(obj.getName(), x, y, obj.getRect())) {
			return true;
		}
		for (final LogicObject child : obj.getChilds()) {
			if (this.drawMouseOverFromObject(child, x, y)) {
				return true;
			}
		}
		return false;
	}

	private boolean drawMouseOver(final String str, final int x, final int y, final int[] rect) {
		if (rect != null && this.inRect(x - this.getGuiLeft(), y - this.getGuiTop(), rect)) {
			this.drawMouseOver(str, x - this.getGuiLeft(), y - this.getGuiTop());
			return true;
		}
		return false;
	}

	@Override
	public void drawGuiBackground(final float f, int x, int y) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		final int j = this.getGuiLeft();
		final int k = this.getGuiTop();
		ResourceHelper.bindResource(GuiDetector.texture);
		this.drawTexturedModalRect(j, k, 0, 0, this.xSize, this.ySize);
		x -= this.getGuiLeft();
		y -= this.getGuiTop();
		this.detector.mainObj.draw(this, x, y);
		DropDownMenu.update(this, x, y, this.menus);
		this.flowMenu.drawMain(this, x, y);
		ResourceHelper.bindResource(GuiDetector.texture);
		int flowPosId = 0;
		for (final OperatorObject operator : OperatorObject.getOperatorList(this.detector.getBlockMetadata())) {
			if (operator.inTab()) {
				final int[] src = this.getOperatorTexture(operator.getID());
				this.flowMenu.drawContent(this, flowPosId, src[0], src[1]);
				++flowPosId;
			}
		}
		this.statesMenu.drawMain(this, x, y);
		ResourceHelper.bindResource(GuiDetector.stateTexture);
		int statePosId = 0;
		for (final ModuleState state : ModuleState.getStateList()) {
			final int[] src2 = this.getModuleTexture(state.getID());
			this.statesMenu.drawContent(this, statePosId, src2[0], src2[1]);
			++statePosId;
		}
		this.modulesMenu.drawMain(this, x, y);
		ResourceHelper.bindResource(GuiDetector.moduleTexture);
		int modulePosId = 0;
		for (final ModuleData module : ModuleData.getModules()) {
			if (module.getIsValid()) {
				this.modulesMenu.drawContent(this, modulePosId, module);
				++modulePosId;
			}
		}
		this.flowMenu.drawHeader(this);
		this.statesMenu.drawHeader(this);
		this.modulesMenu.drawHeader(this);
		if (this.currentObject != null) {
			this.currentObject.draw(this, -500, -500, x, y);
		}
	}

	public int[] getOperatorTexture(final byte operatorId) {
		final int x = operatorId % 11;
		final int y = operatorId / 11;
		return new int[] { 36 + x * 20, this.ySize + y * 11 };
	}

	public int[] getModuleTexture(final byte moduleId) {
		final int srcX = moduleId % 16 * 16;
		final int srcY = moduleId / 16 * 16;
		return new int[] { srcX, srcY };
	}

	private int[] getOperatorRect(final int posId) {
		return new int[] { 20 + posId * 30, 20, 20, 11 };
	}

	@Override
	public void mouseClick(int x, int y, final int button) {
		super.mouseClick(x, y, button);
		x -= this.getGuiLeft();
		y -= this.getGuiTop();
		if (button == 0) {
			if (isShiftKeyDown()) {
				if (this.currentObject == null) {
					this.pickupObject(x, y, this.detector.mainObj);
				}
			} else {
				int modulePosId = 0;
				for (final ModuleData module : ModuleData.getModules()) {
					if (module.getIsValid()) {
						final int[] target = this.modulesMenu.getContentRect(modulePosId);
						if (this.inRect(x, y, target)) {
							this.currentObject = new LogicObject((byte) 0, module.getID());
							return;
						}
						++modulePosId;
					}
				}
				int statePosId = 0;
				for (final ModuleState state : ModuleState.getStateList()) {
					final int[] target2 = this.statesMenu.getContentRect(statePosId);
					if (this.inRect(x, y, target2)) {
						this.currentObject = new LogicObject((byte) 2, state.getID());
						return;
					}
					++statePosId;
				}
				int flowPosId = 0;
				for (final OperatorObject operator : OperatorObject.getOperatorList(this.detector.getBlockMetadata())) {
					if (operator.inTab()) {
						final int[] target3 = this.flowMenu.getContentRect(flowPosId);
						if (this.inRect(x, y, target3)) {
							this.currentObject = new LogicObject((byte) 1, operator.getID());
							return;
						}
						++flowPosId;
					}
				}
				for (final DropDownMenu menu : this.menus) {
					menu.onClick(this, x, y);
				}
			}
		} else if (button == 1 && this.currentObject == null) {
			this.removeObject(x, y, this.detector.mainObj);
		}
	}

	@Override
	public void mouseMoved(int x, int y, final int button) {
		super.mouseMoved(x, y, button);
		x -= this.getGuiLeft();
		y -= this.getGuiTop();
		if (button != -1 && this.currentObject != null) {
			this.dropOnObject(x, y, this.detector.mainObj, this.currentObject);
			this.currentObject = null;
		}
	}

	private boolean removeObject(final int x, final int y, final LogicObject object) {
		if (this.inRect(x, y, object.getRect()) && object.canBeRemoved()) {
			object.setParent(this.detector, null);
			return true;
		}
		for (final LogicObject child : object.getChilds()) {
			if (this.removeObject(x, y, child)) {
				return true;
			}
		}
		return false;
	}

	private boolean pickupObject(final int x, final int y, final LogicObject object) {
		if (this.inRect(x, y, object.getRect()) && object.canBeRemoved()) {
			(this.currentObject = object).setParent(this.detector, null);
			return true;
		}
		for (final LogicObject child : object.getChilds()) {
			if (this.pickupObject(x, y, child)) {
				return true;
			}
		}
		return false;
	}

	private boolean dropOnObject(final int x, final int y, final LogicObject object, final LogicObject drop) {
		if (this.inRect(x, y, object.getRect())) {
			if (object.hasRoomForChild() && object.isChildValid(drop)) {
				drop.setParent(this.detector, object);
			}
			return true;
		}
		for (final LogicObject child : object.getChilds()) {
			if (this.dropOnObject(x, y, child, drop)) {
				return true;
			}
		}
		return false;
	}

	static {
		GuiDetector.texture = ResourceHelper.getResource("/gui/detector.png");
		GuiDetector.moduleTexture = ResourceHelper.getResourceFromPath("/atlas/items.png");
		GuiDetector.stateTexture = ResourceHelper.getResource("/gui/states.png");
		GuiDetector.dropdownTexture = ResourceHelper.getResource("/gui/detector2.png");
	}
}
