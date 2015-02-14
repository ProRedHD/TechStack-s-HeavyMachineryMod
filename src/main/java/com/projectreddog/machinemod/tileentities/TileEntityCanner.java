package com.projectreddog.machinemod.tileentities;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;

import com.projectreddog.machinemod.block.BlockMachineModPrimaryCrusher;
import com.projectreddog.machinemod.iface.IFuelContainer;
import com.projectreddog.machinemod.init.ModItems;
import com.projectreddog.machinemod.reference.Reference;

public class TileEntityCanner extends TileEntity implements IUpdatePlayerListBox, ISidedInventory, IFuelContainer {
	protected ItemStack[] inventory;
	private static int[] sideSlots = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
	public final int maxFuelStorage = 10000; // store up to 10k (can fill all 9 cans & have room for one more
	public int fuelStorage = 0;
	public final int inventorySize = 9;
	public final int coolDownReset = 1200;
	public int cooldown = coolDownReset;

	public TileEntityCanner() {
		inventory = new ItemStack[inventorySize];

	}

	public int addFluid(int amount) {
		int returnAmount;
		if (canAcceptFluid()) {
			if (fuelStorage + amount > maxFuelStorage) {
				// fill to brim return amount left over
				returnAmount = (fuelStorage + amount - maxFuelStorage);

				fuelStorage = maxFuelStorage;
			} else {
				// not going to return any this container can hold all of the fuel
				fuelStorage = fuelStorage + amount;
				returnAmount = 0;
			}
		} else {
			returnAmount = amount;
		}
		return returnAmount;
	}

	public boolean canAcceptFluid() {
		if (fuelStorage < maxFuelStorage) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void update() {
		if (!worldObj.isRemote) {
			// LogHelper.info("TE update entity called");

			// transfer fuel in this case to the cans
			transferFuel();

		}
	}

	public boolean transferFuel() {
		// this being the canner will transfer its fuel into the fuel cans int its slots 0-8
		if (this.fuelStorage > 1) {
			for (int i = 0; i < this.getSizeInventory(); i++) {
				ItemStack item = this.getStackInSlot(i);
				if (item != null) {
					if (item.getItem() == ModItems.fuelcan) {
						if (item.getItemDamage() > 0) {
							item.setItemDamage(item.getItemDamage() - 1);
							this.fuelStorage = this.fuelStorage - 1;
							i = this.getSizeInventory();
							if (item.getItemDamage() == 0) {
								EntityItem entityItem = new EntityItem(worldObj, this.pos.getX(), this.pos.getY(), this.pos.getZ(), item);

								entityItem.forceSpawn = true;
								entityItem.motionX = 0;
								entityItem.motionY = 0;
								entityItem.motionZ = 0;
								worldObj.spawnEntityInWorld(entityItem);
								decrStackSize(i, 1);
							}
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	protected ItemStack addToinventory(ItemStack is) {
		int i = getSizeInventory();

		for (int j = 0; j < i && is != null && is.stackSize > 0; ++j) {
			if (is != null) {

				if (getStackInSlot(j) != null) {
					if (getStackInSlot(j).getItem() == is.getItem() && getStackInSlot(j).getItemDamage() == is.getItemDamage()) {
						// same item remove from is put into slot any amt not to
						// excede stack max
						if (getStackInSlot(j).stackSize < getStackInSlot(j).getMaxStackSize()) {
							// we have room to add to this stack
							if (is.stackSize <= getStackInSlot(j).getMaxStackSize() - getStackInSlot(j).stackSize) {
								// /all of the stack will fit in this slot do
								// so.

								setInventorySlotContents(j, new ItemStack(getStackInSlot(j).getItem(), getStackInSlot(j).stackSize + is.stackSize, is.getItemDamage()));
								is = null;
							} else {
								// we have more
								int countRemain = is.stackSize - (getStackInSlot(j).getMaxStackSize() - getStackInSlot(j).stackSize);
								setInventorySlotContents(j, new ItemStack(is.getItem(), getStackInSlot(j).getMaxStackSize(), is.getItemDamage()));
								is.stackSize = countRemain;
							}

						}
					}
				} else {
					// nothign in slot so set contents
					setInventorySlotContents(j, new ItemStack(is.getItem(), is.stackSize, is.getItemDamage()));
					is = null;
				}

			}

		}

		return is;

	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {

		super.readFromNBT(compound);

		// inventory
		NBTTagList tagList = compound.getTagList(Reference.MACHINE_MOD_NBT_PREFIX + "Inventory", compound.getId());
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) tagList.getCompoundTagAt(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < inventory.length) {
				inventory[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);

		// inventory
		NBTTagList itemList = new NBTTagList();
		for (int i = 0; i < inventory.length; i++) {
			ItemStack stack = inventory[i];
			if (stack != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte) i);
				stack.writeToNBT(tag);
				itemList.appendTag(tag);
			}
		}
		compound.setTag(Reference.MACHINE_MOD_NBT_PREFIX + "Inventory", itemList);

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasCustomName() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IChatComponent getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			if (stack.stackSize <= amt) {
				setInventorySlotContents(slot, null);
			} else {
				stack = stack.splitStack(amt);
				if (stack.stackSize == 0) {
					setInventorySlotContents(slot, null);
				}

			}
		}
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			setInventorySlotContents(slot, null);
		}
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory[slot] = stack;
		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}

	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer playerIn) {
		return playerIn.getDistanceSq(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()) < 64;
	}

	@Override
	public void openInventory(EntityPlayer playerIn) {

	}

	@Override
	public void closeInventory(EntityPlayer playerIn) {

	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		for (int i = 0; i < inventory.length; ++i) {
			inventory[i] = null;
		}
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		if (side == EnumFacing.NORTH || side == EnumFacing.SOUTH || side == EnumFacing.EAST || side == EnumFacing.WEST) {
			return sideSlots;
		}
		int[] topSlots2 = new int[] { 0 };
		return topSlots2;

	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemStackIn, EnumFacing direction) {
		if (slot < inventorySize && (direction == EnumFacing.NORTH || direction == EnumFacing.SOUTH || direction == EnumFacing.EAST || direction == EnumFacing.WEST)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, EnumFacing direction) {
		if (slot < inventorySize && (direction == EnumFacing.NORTH || direction == EnumFacing.SOUTH || direction == EnumFacing.EAST || direction == EnumFacing.WEST)) {
			return true;
		}
		return false;
	}

	@Override
	public EnumFacing outputDirection() {
		EnumFacing ef = (EnumFacing) worldObj.getBlockState(this.getPos()).getValue(BlockMachineModPrimaryCrusher.FACING);
		switch (ef) {
		case NORTH:
			return EnumFacing.SOUTH;
		case SOUTH:
			return EnumFacing.NORTH;
		case EAST:
			return EnumFacing.WEST;
		case WEST:
			return EnumFacing.EAST;
		default:
			return null;
		}

	}
}
