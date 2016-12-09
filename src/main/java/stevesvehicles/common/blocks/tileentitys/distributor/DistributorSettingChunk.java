package stevesvehicles.common.blocks.tileentitys.distributor;

import stevesvehicles.client.localization.ILocalizedText;
import stevesvehicles.common.blocks.tileentitys.TileEntityManager;

/**
 * Created by Vswe on 15/07/14.
 */
class DistributorSettingChunk extends DistributorSetting {
	private int chunk;

	public DistributorSettingChunk(int id, boolean top, ILocalizedText name, int chunk) {
		super(id, top, name);
		this.chunk = chunk;
	}

	@Override
	public boolean isValid(TileEntityManager manager, int chunkId, boolean top) {
		if (manager.layoutType == 0) {
			return super.isValid(manager, chunkId, top);
		} else {
			return super.isValid(manager, chunkId, top) && chunk == chunkId;
		}
	}
}
