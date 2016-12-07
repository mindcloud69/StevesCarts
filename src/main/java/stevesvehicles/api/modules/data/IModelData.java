package stevesvehicles.api.modules.data;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IModelData<M> {

	float getModelMultiplier();

	IModelData<M> setModelMultiplier(float val);

	IModelData<M> addModel(String tag, M model);

	IModelData<M> addModel(String tag, M model, boolean placeholder);

	HashMap<String, M> getModels(boolean placeholder);

	boolean haveModels(boolean placeholder);

	IModelData<M> removeModel(String tag);

	ArrayList<String> getRemovedModels();

	boolean haveRemovedModels();

	void loadClientValues(IModuleData data);
}
