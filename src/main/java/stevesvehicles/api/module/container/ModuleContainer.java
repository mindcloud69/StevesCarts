package stevesvehicles.api.module.container;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.item.ItemStack;
import stevesvehicles.api.module.data.ModuleData;

public class ModuleContainer implements IModuleContainer {

	protected final ItemStack parent;
	protected final Set<ModuleData> datas;

	public ModuleContainer(ItemStack fallbackParent, ModuleData... datas) {
		if(fallbackParent == null || fallbackParent.isEmpty()){
			fallbackParent = ItemStack.EMPTY;
		}
		this.parent = fallbackParent;
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
