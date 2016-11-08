package vswe.stevescarts.Helpers;

import vswe.stevescarts.TileEntities.TileEntityDistributor;
import vswe.stevescarts.TileEntities.TileEntityManager;

import java.util.ArrayList;

public class DistributorSetting {
	public static ArrayList<DistributorSetting> settings;
	private int id;
	private int imageId;
	private boolean top;
	private Localization.GUI.DISTRIBUTOR name;

	public DistributorSetting(final int id, final boolean top, final Localization.GUI.DISTRIBUTOR name) {
		this.id = id;
		this.top = top;
		this.name = name;
		this.imageId = id / 2;
	}

	public boolean isValid(final TileEntityManager manager, final int chunkId, final boolean top) {
		return top == this.top;
	}

	public int getId() {
		return this.id;
	}

	public int getImageId() {
		return this.imageId;
	}

	public String getName(final TileEntityManager[] manager) {
		if (manager != null && manager.length > 1) {
			return this.name.translate() + " (" + (this.getIsTop() ? Localization.GUI.DISTRIBUTOR.MANAGER_TOP.translate()
			                                                       : Localization.GUI.DISTRIBUTOR.MANAGER_BOT.translate()) + ")";
		}
		return this.name.translate();
	}

	public boolean getIsTop() {
		return this.top;
	}

	public boolean isEnabled(final TileEntityDistributor distributor) {
		if (distributor.getInventories().length == 0) {
			return false;
		}
		if (this.top) {
			return distributor.hasTop;
		}
		return distributor.hasBot;
	}

	static {
		(DistributorSetting.settings = new ArrayList<DistributorSetting>()).add(new DistributorSetting(0, true, Localization.GUI.DISTRIBUTOR.SETTING_ALL));
		DistributorSetting.settings.add(new DistributorSetting(1, false, Localization.GUI.DISTRIBUTOR.SETTING_ALL));
		DistributorSetting.settings.add(new distributorSettingColor(2, true, Localization.GUI.DISTRIBUTOR.SETTING_RED, 1));
		DistributorSetting.settings.add(new distributorSettingColor(3, false, Localization.GUI.DISTRIBUTOR.SETTING_RED, 1));
		DistributorSetting.settings.add(new distributorSettingColor(4, true, Localization.GUI.DISTRIBUTOR.SETTING_BLUE, 2));
		DistributorSetting.settings.add(new distributorSettingColor(5, false, Localization.GUI.DISTRIBUTOR.SETTING_BLUE, 2));
		DistributorSetting.settings.add(new distributorSettingColor(6, true, Localization.GUI.DISTRIBUTOR.SETTING_YELLOW, 3));
		DistributorSetting.settings.add(new distributorSettingColor(7, false, Localization.GUI.DISTRIBUTOR.SETTING_YELLOW, 3));
		DistributorSetting.settings.add(new distributorSettingColor(8, true, Localization.GUI.DISTRIBUTOR.SETTING_GREEN, 4));
		DistributorSetting.settings.add(new distributorSettingColor(9, false, Localization.GUI.DISTRIBUTOR.SETTING_GREEN, 4));
		DistributorSetting.settings.add(new distributorSettingChunk(10, true, Localization.GUI.DISTRIBUTOR.SETTING_TOP_LEFT, 0));
		DistributorSetting.settings.add(new distributorSettingChunk(11, false, Localization.GUI.DISTRIBUTOR.SETTING_TOP_LEFT, 0));
		DistributorSetting.settings.add(new distributorSettingChunk(12, true, Localization.GUI.DISTRIBUTOR.SETTING_TOP_RIGHT, 1));
		DistributorSetting.settings.add(new distributorSettingChunk(13, false, Localization.GUI.DISTRIBUTOR.SETTING_TOP_RIGHT, 1));
		DistributorSetting.settings.add(new distributorSettingChunk(14, true, Localization.GUI.DISTRIBUTOR.SETTING_BOTTOM_LEFT, 2));
		DistributorSetting.settings.add(new distributorSettingChunk(15, false, Localization.GUI.DISTRIBUTOR.SETTING_BOTTOM_LEFT, 2));
		DistributorSetting.settings.add(new distributorSettingChunk(16, true, Localization.GUI.DISTRIBUTOR.SETTING_BOTTOM_RIGHT, 3));
		DistributorSetting.settings.add(new distributorSettingChunk(17, false, Localization.GUI.DISTRIBUTOR.SETTING_BOTTOM_RIGHT, 3));
		DistributorSetting.settings.add(new distributorSettingDirection(18, true, Localization.GUI.DISTRIBUTOR.SETTING_TO_CART, true));
		DistributorSetting.settings.add(new distributorSettingDirection(19, false, Localization.GUI.DISTRIBUTOR.SETTING_TO_CART, true));
		DistributorSetting.settings.add(new distributorSettingDirection(20, true, Localization.GUI.DISTRIBUTOR.SETTING_FROM_CART, false));
		DistributorSetting.settings.add(new distributorSettingDirection(21, false, Localization.GUI.DISTRIBUTOR.SETTING_FROM_CART, false));
	}

	private static class distributorSettingColor extends DistributorSetting {
		private int color;

		public distributorSettingColor(final int id, final boolean top, final Localization.GUI.DISTRIBUTOR name, final int color) {
			super(id, top, name);
			this.color = color;
		}

		@Override
		public boolean isValid(final TileEntityManager manager, final int chunkId, final boolean top) {
			if (manager.layoutType == 0) {
				return super.isValid(manager, chunkId, top);
			}
			return super.isValid(manager, chunkId, top) && manager.color[chunkId] == this.color;
		}
	}

	private static class distributorSettingChunk extends DistributorSetting {
		private int chunk;

		public distributorSettingChunk(final int id, final boolean top, final Localization.GUI.DISTRIBUTOR name, final int chunk) {
			super(id, top, name);
			this.chunk = chunk;
		}

		@Override
		public boolean isValid(final TileEntityManager manager, final int chunkId, final boolean top) {
			if (manager.layoutType == 0) {
				return super.isValid(manager, chunkId, top);
			}
			return super.isValid(manager, chunkId, top) && this.chunk == chunkId;
		}
	}

	private static class distributorSettingDirection extends DistributorSetting {
		private boolean toCart;

		public distributorSettingDirection(final int id, final boolean top, final Localization.GUI.DISTRIBUTOR name, final boolean toCart) {
			super(id, top, name);
			this.toCart = toCart;
		}

		@Override
		public boolean isValid(final TileEntityManager manager, final int chunkId, final boolean top) {
			if (manager.layoutType == 0) {
				return super.isValid(manager, chunkId, top);
			}
			return super.isValid(manager, chunkId, top) && manager.toCart[chunkId] == this.toCart;
		}
	}
}
