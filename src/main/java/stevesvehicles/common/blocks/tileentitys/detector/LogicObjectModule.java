package stevesvehicles.common.blocks.tileentitys.detector;

import stevesvehicles.client.ResourceHelper;
import stevesvehicles.client.gui.screen.GuiDetector;
import stevesvehicles.common.blocks.tileentitys.TileEntityDetector;
import stevesvehicles.common.modules.ModuleBase;
import stevesvehicles.common.modules.datas.ModuleData;
import stevesvehicles.common.modules.datas.registries.ModuleRegistry;
import stevesvehicles.common.vehicles.VehicleBase;

public class LogicObjectModule extends LogicObject {
	private ModuleData module;

	public LogicObjectModule(byte id, ModuleData module) {
		super(id, (short) ModuleRegistry.getIdFromModule(module));
		this.module = module;
	}

	@Override
	public LogicObject copy(LogicObject parent) {
		LogicObject obj = new LogicObjectModule(getId(), module);
		obj.setParent(parent);
		return obj;
	}

	@Override
	public String getName() {
		return module.getName();
	}

	@Override
	public int getType() {
		return 0;
	}

	@Override
	public void draw(GuiDetector gui, int mouseX, int mouseY) {
		ResourceHelper.bindResource(GuiDetector.TEXTURE);
		int backgroundIndex = gui.inRect(mouseX, mouseY, getRect()) ? 1 : 0;
		gui.drawTexturedModalRect(gui.getGuiLeft() + x, gui.getGuiTop() + y, 1 + backgroundIndex * 17, 203, getWidth(), getHeight());
		ModuleData module = ModuleRegistry.getModuleFromId(data);
		if (module != null) {
			gui.drawItemStack(module.getItemStack(), gui.getGuiLeft() + x, gui.getGuiTop() + y);
		}
		super.draw(gui, mouseX, mouseY);
	}

	@Override
	public boolean evaluateLogicTree(TileEntityDetector detector, VehicleBase vehicle, int depth) {
		if (!super.evaluateLogicTree(detector, vehicle, depth)) {
			return false;
		} else {
			for (ModuleBase module : vehicle.getModules()) {
				if (ModuleRegistry.getIdFromModule(this.module) == module.getModuleId()) {
					return true;
				}
			}
			return false;
		}
	}
}
