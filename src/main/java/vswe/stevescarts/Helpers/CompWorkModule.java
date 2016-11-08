package vswe.stevescarts.Helpers;

import java.util.Comparator;

import vswe.stevescarts.Modules.Workers.ModuleWorker;

public class CompWorkModule implements Comparator {
	@Override
	public int compare(final Object obj1, final Object obj2) {
		final ModuleWorker work1 = (ModuleWorker) obj1;
		final ModuleWorker work2 = (ModuleWorker) obj2;
		return (work1.getWorkPriority() < work2.getWorkPriority()) ? -1 : ((work1.getWorkPriority() > work2.getWorkPriority()) ? 1 : 0);
	}
}
