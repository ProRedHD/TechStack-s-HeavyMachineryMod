package com.projectreddog.machinemod.container;

import com.projectreddog.machinemod.tileentities.TileEntityPrimaryCrusher;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerPrimaryCrusher extends Container {

	protected TileEntityPrimaryCrusher primaryCrusher;
	protected int lastFuelStorage;

	public ContainerPrimaryCrusher(InventoryPlayer inventoryPlayer, TileEntityPrimaryCrusher primaryCrusher) {
		this.primaryCrusher = primaryCrusher;
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(primaryCrusher, j + i * 9, 8 + j * 18, 18 + i * 18));
			}
		}
		//
		// for (int i = 0; i < 1; i++) {
		// for (int j = 0; j < 1; j++) {

		// addSlotToContainer(new Slot(fractionaldistiller, j + i * 9, 8 + j * 18, 18 + i * 18));
		// }
		// }

		// commonly used vanilla code that adds the player's inventory
		bindPlayerInventory(inventoryPlayer);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 139 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 197));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slotObject = (Slot) inventorySlots.get(slot);

		// null checks and checks if the item can be stacked (maxStackSize > 1)
		if (slotObject != null && slotObject.getHasStack()) {
			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();

			// merges the item into player inventory since its in the Entity
			if (slot < 54) {
				if (!this.mergeItemStack(stackInSlot, 54, this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			}
			// places it into the tileEntity is possible since its in the player
			// inventory
			else if (!this.mergeItemStack(stackInSlot, 0, 54, false)) {
				return ItemStack.EMPTY;
			}

			if (stackInSlot.getCount() == 0) {
				slotObject.putStack(ItemStack.EMPTY);
			} else {
				slotObject.onSlotChanged();
			}

			if (stackInSlot.getCount() == stack.getCount()) {
				return ItemStack.EMPTY;
			}
			slotObject.onTake(player, stackInSlot);
		}
		return stack;
	}

	/**
	 * Looks for changes made in the container, sends them to every listener.
	 */
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < this.listeners.size(); ++i) {
			IContainerListener icrafting = (IContainerListener) this.listeners.get(i);

			if (this.lastFuelStorage != this.primaryCrusher.getField(0)) {
				icrafting.sendWindowProperty(this, 0, this.primaryCrusher.getField(0));
			}

		}

		this.lastFuelStorage = this.primaryCrusher.getField(0);

	}

	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		this.primaryCrusher.setField(id, data);
	}
}