package com.projectreddog.machinemod.entity;

import com.projectreddog.machinemod.init.ModItems;
import com.projectreddog.machinemod.init.ModNetwork;
import com.projectreddog.machinemod.network.MachineModMessageEntityCurrentTargetPosToClient;
import com.projectreddog.machinemod.utility.BlockUtil;
import com.projectreddog.machinemod.utility.LogHelper;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class EntityExcavator extends EntityMachineModRideable {

	private static final AxisAlignedBB boundingBox = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
	public static float AmrLength = 3.1f;
	public static float armPiviotForward = 1.4f;
	public static float armPiviotUp = -0f;

	public BlockPos targetBlockPos;
	public double currPosX;
	public double currPosY;
	public double currPosZ;
	public double mainBodyRotation = 0;

	public double angleArm1 = 0;
	public double angleArm2 = 0;

	public double angleArm3 = 0;

	public double armSpeed = .1d;

	public EntityExcavator(World world) {
		super(world);

		setSize(4f, 2f);
		inventory = new ItemStack[9];

		this.mountedOffsetY = .5D;
		this.mountedOffsetX = -1.5D;
		this.mountedOffsetZ = 2D;
		this.maxAngle = 256;
		this.minAngle = 0;
		this.droppedItem = ModItems.excavator;
		currPosX = this.posX;
		currPosY = this.posY;
		currPosZ = this.posZ;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!worldObj.isRemote) {
			// if (this.Attribute1 == this.getMaxAngle()) {
			// bucket Down
			// break blocks first
			if (currentFuelLevel > 0) {
				// server move bucket towards target and send it's new pos to the client via network
				if (this.isPlayerPushingSegment1Down) {
					this.angleArm1--;
				}

				if (this.isPlayerPushingSegment1Up) {
					this.angleArm1++;
				}

				if (this.angleArm1 < -10) {
					angleArm1 = -10;
				}
				if (this.angleArm1 > 25) {
					angleArm1 = 25;
				}

				if (this.isPlayerPushingSegment2Down) {
					this.angleArm2--;
				}
				if (this.isPlayerPushingSegment2Up) {
					this.angleArm2++;
				}

				if (this.angleArm2 < 00) {
					angleArm2 = 0;
				}
				if (this.angleArm2 > 90) {
					angleArm2 = 90;
				}
				if (this.isPlayerPushingSegment3Down) {
					this.angleArm3--;
				}
				if (this.isPlayerPushingSegment3Up) {
					this.angleArm3++;
				}

				if (this.angleArm3 < -90) {
					angleArm3 = -90;
				}
				if (this.angleArm3 > 45) {
					angleArm3 = 45;
				}

				if (this.isPlayerPushingTurretLeft) {
					mainBodyRotation++;
				}
				if (this.isPlayerPushingTurretRight) {
					mainBodyRotation--;
				}

				// BlockPos bp = new BlockPos(posX + calcTwoOffsetX(10, 0, 0), posY + +3, posZ + calcTwoOffsetZ(10, 0, 0));

				ModNetwork.sendPacketToAllAround((new MachineModMessageEntityCurrentTargetPosToClient(this.getEntityId(), this.currPosX, this.currPosY, this.currPosZ, this.angleArm1, this.angleArm2, this.angleArm3, this.mainBodyRotation)), new TargetPoint(worldObj.provider.getDimension(), posX, posY, posZ, 80));
				// if (this.isPlayerPushingSprintButton) {
				// player wants to break the block
				// bp = new BlockPos(currPosX, currPosY, currPosZ);
				// public BlockPos calculateBlockPosGivenStartAngleDistance4(double startX, double startY, double startZ, float angleX1, float angleY1, float angleZ1, double distance1, float angleX2, float angleY2, float angleZ2, double distance2, float angleX3, float angleY3, float angleZ3, double distance3, float angleX4, float angleY4, float angleZ4, double distance4) {
				LogHelper.info(this.yaw);
				LogHelper.info(this.rotationYaw);
				BlockPos BP = this.calculateBlockPosGivenStartAngleDistance4(this.posX, this.posY, this.posZ, (360 - this.yaw) + 90, 0, -.5d, (360 - this.yaw), (float) (45f + this.angleArm1), 9.5d, (360 - this.yaw), (float) (this.angleArm2 + 270 + this.angleArm1), 7.5d, (360 - this.yaw), (float) (this.angleArm3 + 90), -2.75d);
				// BlockPos BP = this.calculateBlockPosGivenStartAngleDistance4(this.posX, this.posY, this.posZ, (360 - this.yaw) + 90, 0, -.5d, (360 - this.yaw), (float) (45f + this.angleArm1), 9.5d, (360 - this.yaw), (float) (this.angleArm2 + 270 + this.angleArm1), 7.5d, 0,0,0);

				// BlockPos BP = this.calculateBlockPosGivenStartAngleDistance4(this.posX, this.posY, this.posZ, (360 - this.yaw) + 90, 0, -.5d, (360 - this.yaw), (float) (45f + this.angleArm1), 9.5d, (360 - this.yaw), (float) (this.angleArm2 + 265), 7.5d, 0, 0, 0);

				// BlockPos BP = this.calculateBlockPosGivenStartAngleDistance4(this.posX, this.posY, this.posZ, (360 - this.yaw) + 90, 0, -1d, (360 - this.yaw), (float) this.angleArm1 - 40, 10d, (360 - this.yaw), (float) this.angleArm2 - 90, 10d, 0, 0, 0);

				BlockUtil.BreakBlock(worldObj, BP, this.getControllingPassenger());
				// LogHelper.info(BP);
				// worldObj.setBlockState(BP, Blocks.DIRT.getDefaultState());

				// }
			}

		}

	}

	@Override
	public double getMountedXOffset() {
		// should be overridden in extended class if not default;

		return calcTwoOffsetX(this.mountedOffsetZ, 90, this.mountedOffsetX);
		// return calcOffsetX(mountedOffsetX);
	}

	@Override
	public double getMountedZOffset() {
		// should be overridden in extended class if not default;

		return calcTwoOffsetZ(this.mountedOffsetZ, 90, this.mountedOffsetX);
		// return calcOffsetX(mountedOffsetX);
	}

	public AxisAlignedBB getBoundingBox() {
		return boundingBox;
	}

}
