package vswe.stevescarts.guis;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import vswe.stevescarts.blocks.tileentities.TileEntityDetector;
import vswe.stevescarts.containers.ContainerDetector;
import vswe.stevescarts.helpers.*;
import vswe.stevescarts.modules.data.ModuleData;

import java.util.ArrayList;
import java.util.Iterator;

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
		setXSize(255);
		setYSize(202);
		this.detector = detector;
		final Iterator<LogicObject> i$ = detector.mainObj.getChilds().iterator();
		if (i$.hasNext()) {
			final LogicObject child = i$.next();
			child.setParent(null);
		}
		detector.recalculateTree();
		(menus = new ArrayList<>()).add(modulesMenu = new DropDownMenuPages(0, 2));
		menus.add(statesMenu = new DropDownMenu(1));
		menus.add(flowMenu = new DropDownMenu(2));
	}

	@Override
	public void drawGuiForeground(final int x, final int y) {
		GL11.glDisable(2896);
		getFontRenderer().drawString(DetectorType.getTypeFromSate(detector.getWorld().getBlockState(detector.getPos())).getTranslatedName(), 8, 6, 4210752);
		if (modulesMenu.getScroll() != 0) {
			int modulePosId = 0;
			for (final ModuleData module : ModuleData.getModules()) {
				if (module.getIsValid()) {
					final int[] target = modulesMenu.getContentRect(modulePosId);
					if (drawMouseOver(module.getName(), x, y, target)) {
						break;
					}
					++modulePosId;
				}
			}
		} else if (statesMenu.getScroll() != 0) {
			int statesPosId = 0;
			for (final ModuleState state : ModuleState.getStateList()) {
				final int[] target = statesMenu.getContentRect(statesPosId);
				if (drawMouseOver(state.getName(), x, y, target)) {
					break;
				}
				++statesPosId;
			}
		} else if (flowMenu.getScroll() != 0) {
			int flowPosId = 0;
			for (final OperatorObject operator : OperatorObject.getOperatorList(detector.getType())) {
				if (operator.inTab()) {
					final int[] target = flowMenu.getContentRect(flowPosId);
					if (drawMouseOver(operator.getName(), x, y, target)) {
						break;
					}
					++flowPosId;
				}
			}
		} else {
			drawMouseOverFromObject(detector.mainObj, x, y);
		}
		GL11.glEnable(2896);
	}

	private boolean drawMouseOverFromObject(final LogicObject obj, final int x, final int y) {
		if (drawMouseOver(obj.getName(), x, y, obj.getRect())) {
			return true;
		}
		for (final LogicObject child : obj.getChilds()) {
			if (drawMouseOverFromObject(child, x, y)) {
				return true;
			}
		}
		return false;
	}

	private boolean drawMouseOver(final String str, final int x, final int y, final int[] rect) {
		if (rect != null && inRect(x - getGuiLeft(), y - getGuiTop(), rect)) {
			drawMouseOver(str, x - getGuiLeft(), y - getGuiTop());
			return true;
		}
		return false;
	}

	@Override
	public void drawGuiBackground(final float f, int x, int y) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		final int j = getGuiLeft();
		final int k = getGuiTop();
		ResourceHelper.bindResource(GuiDetector.texture);
		drawTexturedModalRect(j, k, 0, 0, xSize, ySize);
		x -= getGuiLeft();
		y -= getGuiTop();
		detector.mainObj.draw(this, x, y);
		DropDownMenu.update(this, x, y, menus);
		flowMenu.drawMain(this, x, y);
		ResourceHelper.bindResource(GuiDetector.texture);
		int flowPosId = 0;
		for (final OperatorObject operator : OperatorObject.getOperatorList(detector.getType())) {
			if (operator.inTab()) {
				final int[] src = getOperatorTexture(operator.getID());
				flowMenu.drawContent(this, flowPosId, src[0], src[1]);
				++flowPosId;
			}
		}
		statesMenu.drawMain(this, x, y);
		ResourceHelper.bindResource(GuiDetector.stateTexture);
		int statePosId = 0;
		for (final ModuleState state : ModuleState.getStateList()) {
			final int[] src2 = getModuleTexture(state.getID());
			statesMenu.drawContent(this, statePosId, src2[0], src2[1]);
			++statePosId;
		}
		modulesMenu.drawMain(this, x, y);
		int modulePosId = 0;
		for (final ModuleData module : ModuleData.getModules()) {
			if (module.getIsValid()) {
				modulesMenu.drawContent(this, modulePosId, module);
				++modulePosId;
			}
		}
		flowMenu.drawHeader(this);
		statesMenu.drawHeader(this);
		modulesMenu.drawHeader(this);
		if (currentObject != null) {
			currentObject.draw(this, -500, -500, x, y);
		}
	}

	public int[] getOperatorTexture(final byte operatorId) {
		final int x = operatorId % 11;
		final int y = operatorId / 11;
		return new int[] { 36 + x * 20, ySize + y * 11 };
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
		x -= getGuiLeft();
		y -= getGuiTop();
		if (button == 0) {
			if (isShiftKeyDown()) {
				if (currentObject == null) {
					pickupObject(x, y, detector.mainObj);
				}
			} else {
				int modulePosId = 0;
				for (final ModuleData module : ModuleData.getModules()) {
					if (module.getIsValid()) {
						final int[] target = modulesMenu.getContentRect(modulePosId);
						if (inRect(x, y, target)) {
							currentObject = new LogicObject((byte) 0, module.getID());
							return;
						}
						++modulePosId;
					}
				}
				int statePosId = 0;
				for (final ModuleState state : ModuleState.getStateList()) {
					final int[] target2 = statesMenu.getContentRect(statePosId);
					if (inRect(x, y, target2)) {
						currentObject = new LogicObject((byte) 2, state.getID());
						return;
					}
					++statePosId;
				}
				int flowPosId = 0;
				for (final OperatorObject operator : OperatorObject.getOperatorList(detector.getType())) {
					if (operator.inTab()) {
						final int[] target3 = flowMenu.getContentRect(flowPosId);
						if (inRect(x, y, target3)) {
							currentObject = new LogicObject((byte) 1, operator.getID());
							return;
						}
						++flowPosId;
					}
				}
				for (final DropDownMenu menu : menus) {
					menu.onClick(this, x, y);
				}
			}
		} else if (button == 1 && currentObject == null) {
			removeObject(x, y, detector.mainObj);
		}
	}

	@Override
	public void mouseMoved(int x, int y, final int button) {
		super.mouseMoved(x, y, button);
		x -= getGuiLeft();
		y -= getGuiTop();
		if (button != -1 && currentObject != null) {
			dropOnObject(x, y, detector.mainObj, currentObject);
			currentObject = null;
		}
	}

	private boolean removeObject(final int x, final int y, final LogicObject object) {
		if (inRect(x, y, object.getRect()) && object.canBeRemoved()) {
			object.setParent(detector, null);
			return true;
		}
		for (final LogicObject child : object.getChilds()) {
			if (removeObject(x, y, child)) {
				return true;
			}
		}
		return false;
	}

	private boolean pickupObject(final int x, final int y, final LogicObject object) {
		if (inRect(x, y, object.getRect()) && object.canBeRemoved()) {
			(currentObject = object).setParent(detector, null);
			return true;
		}
		for (final LogicObject child : object.getChilds()) {
			if (pickupObject(x, y, child)) {
				return true;
			}
		}
		return false;
	}

	private boolean dropOnObject(final int x, final int y, final LogicObject object, final LogicObject drop) {
		if (inRect(x, y, object.getRect())) {
			if (object.hasRoomForChild() && object.isChildValid(drop)) {
				drop.setParent(detector, object);
			}
			return true;
		}
		for (final LogicObject child : object.getChilds()) {
			if (dropOnObject(x, y, child, drop)) {
				return true;
			}
		}
		return false;
	}

	static {
		GuiDetector.texture = ResourceHelper.getResource("/gui/detector.png");
		GuiDetector.stateTexture = ResourceHelper.getResource("/gui/states.png");
		GuiDetector.dropdownTexture = ResourceHelper.getResource("/gui/detector2.png");
	}
}
