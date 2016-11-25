package stevesvehicles.api.modules.container;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.item.ItemStack;
import stevesvehicles.api.modules.data.ModuleData;

public class ModuleContainer implements IModuleContainer {

	protected final ItemStack parent;
	protected final Set<ModuleData> datas;

	public ModuleContainer(ItemStack parent, ModuleData... datas) {
		if(parent == null || parent.isEmpty()){
			parent = ItemStack.EMPTY;
		}
		this.parent = parent;
		this.datas = Sets.newHashSet(datas);
	}

	@Override
	public ItemStack getParent() {
		return parent;
	}

	@Override
	public Collection<ModuleData> getDatas() {
		return datas;
	}

	@Override
	public boolean matches(ItemStack stack) {
		if(stack.isEmpty()){
			return false;
		}
		return stack.getItem() == parent.getItem();
	}
}
