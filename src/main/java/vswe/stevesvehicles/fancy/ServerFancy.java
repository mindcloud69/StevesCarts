package vswe.stevesvehicles.fancy;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ServerFancy {
	private List<FancyPancy> fancies = new ArrayList<>();
	public void add(FancyPancy fancyPancy) {
		fancies.add(fancyPancy);
	}

	public List<FancyPancy> getFancies() {
		return fancies;
	}
}
