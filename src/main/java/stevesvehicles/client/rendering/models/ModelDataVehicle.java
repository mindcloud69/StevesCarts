package stevesvehicles.client.rendering.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.api.modules.data.IModelData;
import stevesvehicles.api.modules.data.IModuleData;
import stevesvehicles.api.modules.data.IModuleSide;
import stevesvehicles.common.modules.datas.ModuleSide;

@SideOnly(Side.CLIENT)
public class ModelDataVehicle implements IModelData<ModelVehicle> {

	private HashMap<String, ModelVehicle> models;
	private HashMap<String, ModelVehicle> modelsPlaceholder;
	private ArrayList<String> removedModels;
	private float modelMultiplier;

	@Override
	public float getModelMultiplier() {
		return modelMultiplier;
	}

	@Override
	public ModelDataVehicle setModelMultiplier(float val) {
		modelMultiplier = val;
		return this;
	}

	@Override
	public ModelDataVehicle addModel(String tag, ModelVehicle model) {
		addModel(tag, model, false);
		addModel(tag, model, true);
		return this;
	}

	@Override
	public ModelDataVehicle addModel(String tag, ModelVehicle model, boolean placeholder) {
		if (placeholder) {
			if (modelsPlaceholder == null) {
				modelsPlaceholder = new HashMap<>();
			}
			modelsPlaceholder.put(tag, model);
		} else {
			if (models == null) {
				models = new HashMap<>();
			}
			models.put(tag, model);
		}
		return this;
	}

	@Override
	public HashMap<String, ModelVehicle> getModels(boolean placeholder) {
		if (placeholder) {
			return modelsPlaceholder;
		} else {
			return models;
		}
	}

	@Override
	public boolean haveModels(boolean placeholder) {
		if (placeholder) {
			return modelsPlaceholder != null;
		} else {
			return models != null;
		}
	}

	@Override
	public ModelDataVehicle removeModel(String tag) {
		if (removedModels == null) {
			removedModels = new ArrayList<>();
		}
		if (!removedModels.contains(tag)) {
			removedModels.add(tag);
		}
		return this;
	}

	@Override
	public ArrayList<String> getRemovedModels() {
		return removedModels;
	}

	@Override
	public boolean haveRemovedModels() {
		return removedModels != null;
	}

	protected void loadModels(IModuleData data) {
	}

	@Override
	public void loadClientValues(IModuleData data) {
		List<IModuleSide> sides = data.getSides();
		modelMultiplier = 0.75F;
		loadModels(data);
		// TODO do this in a nicer way
		if (sides != null && sides.contains(ModuleSide.TOP)) {
			removeModel("Rails");
		}
	}
}
