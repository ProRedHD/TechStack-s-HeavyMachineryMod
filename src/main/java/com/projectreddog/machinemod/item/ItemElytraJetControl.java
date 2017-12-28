package com.projectreddog.machinemod.item;

import java.util.List;

import com.projectreddog.machinemod.entity.EntityElytraJet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class ItemElytraJetControl extends ItemMachineMod {
	public String registryName = "elytrajetcontroller";

	public ItemElytraJetControl() {
		super();
		this.setUnlocalizedName(registryName);
		this.setRegistryName(registryName);

		this.maxStackSize = 64;

	}

	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		boolean foundPriorEntity = false;

		if (playerIn.isElytraFlying()) {
			ItemStack itemstack = playerIn.getHeldItem(handIn);

			if (!worldIn.isRemote) {
				// offset
				double offset = .5d;
				AxisAlignedBB aabb = new AxisAlignedBB(playerIn.posX - offset, playerIn.posY - offset, playerIn.posZ - offset, playerIn.posX + offset, playerIn.posY + offset, playerIn.posZ + offset);
				List eej = worldIn.getEntitiesWithinAABB(EntityElytraJet.class, aabb);

				for (int i = 0; i < eej.size(); ++i) {
					EntityElytraJet entity = (EntityElytraJet) eej.get(i);
					if (entity != null) {
						if (entity.getBoostedEntity() == playerIn.getEntityId()) {
							foundPriorEntity = true;
							if (entity.isBoostActive()) {
								entity.DeactivateBoost();
							} else {
								entity.ActivateBoost();
							}
						}
					}
				}
				if (!foundPriorEntity) {
					EntityElytraJet entityElytraJet = new EntityElytraJet(worldIn);
					entityElytraJet.posX = playerIn.posX;
					entityElytraJet.posY = playerIn.posY;
					entityElytraJet.posZ = playerIn.posZ;

					entityElytraJet.setBoostedEntity(playerIn);
					entityElytraJet.ActivateBoost();
					worldIn.spawnEntity(entityElytraJet);

					if (!playerIn.capabilities.isCreativeMode) {
						// itemstack.shrink(1);
					}
				}
			}

			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
		} else {
			return new ActionResult<ItemStack>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
		}
	}

}
