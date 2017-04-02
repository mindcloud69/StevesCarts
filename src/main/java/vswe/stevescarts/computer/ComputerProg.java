package vswe.stevescarts.computer;

import vswe.stevescarts.modules.workers.ModuleComputer;

import java.util.ArrayList;

public class ComputerProg {
	private ModuleComputer module;
	private int activeTaskId;
	private ArrayList<ComputerTask> tasks;
	private ArrayList<ComputerVar> vars;
	private short info;
	private String myName;

	public ComputerProg(final ModuleComputer module) {
		this.module = module;
		this.tasks = new ArrayList<>();
		this.vars = new ArrayList<>();
		this.info = 1;
	}

	public void start() {
		this.module.setActiveProgram(this);
		if (this.activeTaskId < 0 || this.activeTaskId >= this.tasks.size()) {
			this.activeTaskId = 0;
			return;
		}
		this.activeTaskId = this.tasks.get(this.activeTaskId).preload(this, this.activeTaskId);
	}

	public int getActiveId() {
		return this.activeTaskId;
	}

	public void setActiveId(final int val) {
		this.activeTaskId = val;
	}

	public int getRunTime() {
		if (this.activeTaskId < 0 || this.activeTaskId >= this.tasks.size()) {
			return this.activeTaskId = 0;
		}
		return this.tasks.get(this.activeTaskId).getTime();
	}

	public boolean run() {
		if (this.activeTaskId < 0 || this.activeTaskId >= this.tasks.size()) {
			this.activeTaskId = 0;
			return false;
		}
		final int result = this.tasks.get(this.activeTaskId).run(this, this.activeTaskId);
		if (result == -1) {
			++this.activeTaskId;
		} else {
			this.activeTaskId = result;
		}
		if (this.activeTaskId < 0 || this.activeTaskId >= this.tasks.size()) {
			this.activeTaskId = 0;
			return false;
		}
		if (result == -1) {
			this.activeTaskId = this.tasks.get(this.activeTaskId).preload(this, this.activeTaskId);
		}
		return true;
	}

	public ArrayList<ComputerTask> getTasks() {
		return this.tasks;
	}

	public ArrayList<ComputerVar> getVars() {
		return this.vars;
	}

	public void setTaskCount(final int count) {
		while (this.tasks.size() > count) {
			this.tasks.remove(this.tasks.size() - 1);
		}
		while (this.tasks.size() < count) {
			this.tasks.add(new ComputerTask(this.module, this));
		}
	}

	public void setVarCount(final int count) {
		while (this.vars.size() > count) {
			this.vars.remove(this.vars.size() - 1);
		}
		while (this.vars.size() < count) {
			this.vars.add(new ComputerVar(this.module));
		}
	}

	public short getInfo() {
		return this.info;
	}

	public void setInfo(final short val) {
		this.info = val;
	}

	public void setName(final String name) {
		this.myName = name;
	}

	public String getName() {
		return this.myName;
	}

	@Override
	public String toString() {
		return this.getName();
	}
}
