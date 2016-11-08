package vswe.stevescarts.modules.realtimers;

import net.minecraft.block.BlockRailBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.modules.ModuleBase;

public class ModuleRocket extends ModuleBase {
	private boolean flying;
	private int landDirX;
	private int landDirZ;
	private double flyX;
	private double flyZ;
	private float yaw;
	private boolean isLanding;
	private double landY;
	private double groundY;
	//TODO: Find a name
	private static DataParameter<Integer> UNKNOWN = createDw(DataSerializers.VARINT);

	public ModuleRocket(final EntityMinecartModular cart) {
		super(cart);
	}

	@Override
	public void update() {
		if (this.isPlaceholder()) {
			return;
		}
		if (this.getCart().worldObj.isRemote) {
			if (!this.flying && this.getDw(UNKNOWN) != 0) {
				this.takeOff();
			} else if (!this.isLanding && this.getDw(UNKNOWN) > 1) {
				this.land();
			} else if (this.flying && this.isLanding && this.getDw(UNKNOWN) == 0) {
				this.done();
			}
		}
		if (this.flying) {
			this.getCart().motionX = (this.isLanding ? (this.landDirX * 0.05f) : 0.0);
			this.getCart().motionY = (this.isLanding ? 0.0 : 0.1);
			this.getCart().motionZ = (this.isLanding ? (this.landDirZ * 0.05f) : 0.0);
			if (!this.isLanding || this.landDirX == 0) {
				this.getCart().posX = this.flyX;
			} else {
				final EntityMinecartModular cart = this.getCart();
				cart.posX += this.getCart().motionX;
			}
			if (!this.isLanding || this.landDirZ == 0) {
				this.getCart().posZ = this.flyZ;
			} else {
				final EntityMinecartModular cart2 = this.getCart();
				cart2.posZ += this.getCart().motionZ;
			}
			this.getCart().rotationYaw = this.yaw;
			this.getCart().rotationPitch = 0.0f;
			BlockPos pos = getCart().getPosition();
			if (this.isLanding) {
				this.getCart().posY = this.landY;
				if (BlockRailBase.isRailBlock(getCart().worldObj, pos)) {
					this.done();
					this.updateDw(UNKNOWN, 0);
				}
			}
			if (!this.isLanding && this.getCart().posY - this.groundY > 2.0 && BlockRailBase.isRailBlock(this.getCart().worldObj, pos.add(landDirX, 0, landDirZ))) {
				this.land();
				this.updateDw(UNKNOWN, 2);
			}
		}
	}

	@Override
	public void activatedByRail(final int x, final int y, final int z, final boolean active) {
		if (active) {
			this.takeOff();
			this.updateDw(UNKNOWN, 1);
		}
	}

	private void takeOff() {
		this.flying = true;
		this.getCart().setCanUseRail(false);
		this.flyX = this.getCart().posX;
		this.flyZ = this.getCart().posZ;
		this.yaw = this.getCart().rotationYaw;
		this.groundY = this.getCart().posY;
		if (Math.abs(this.getCart().motionX) > Math.abs(this.getCart().motionZ)) {
			this.landDirX = ((this.getCart().motionX > 0.0) ? 1 : -1);
		} else {
			this.landDirZ = ((this.getCart().motionZ > 0.0) ? 1 : -1);
		}
	}

	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	@Override
	public void initDw() {
		registerDw(UNKNOWN, 0);
	}

	private void land() {
		this.isLanding = true;
		this.landY = this.getCart().posY;
		this.getCart().setCanUseRail(true);
	}

	private void done() {
		this.flying = false;
		this.isLanding = false;
		this.landDirX = 0;
		this.landDirZ = 0;
	}
}
