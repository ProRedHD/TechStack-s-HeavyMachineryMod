package com.projectreddog.machinemod.block;

import com.projectreddog.machinemod.MachineMod;
import com.projectreddog.machinemod.creativetab.CreativeTabMachineMod;
import com.projectreddog.machinemod.init.ModItems;
import com.projectreddog.machinemod.reference.Reference;
import com.projectreddog.machinemod.tileentities.TileEntityCentrifuge;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMachineModCentrifuge extends BlockContainer {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	protected BlockMachineModCentrifuge(Material material) {
		super(material);

		// can override later ;)
		this.setCreativeTab(CreativeTabMachineMod.MACHINEMOD_BLOCKS_TAB);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));

		// 1.8
		this.setUnlocalizedName(Reference.MOD_ID.toLowerCase() + ":" + Reference.MODBLOCK_MACHINE_CENTRIFUGE);
		this.setRegistryName(Reference.MODBLOCK_MACHINE_CENTRIFUGE);

		// this.setBlockTextureName(Reference.MODBLOCK_MACHINE_BLASTED_STONE);
		// this.setHardness(15f);// not sure on the hardness
		this.setSoundType(SoundType.STONE);
		this.setHardness(1.5f);

	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = playerIn.getActiveItemStack();
		TileEntity te = worldIn.getTileEntity(pos);
		if (te != null && !playerIn.isSneaking()) {
			ItemStack playerItem = playerIn.getHeldItem(EnumHand.MAIN_HAND);

			if (playerItem != null && playerItem.getItem() == ModItems.fuelcan && playerItem.getItemDamage() < playerItem.getMaxDamage()) {

				// put its a fuel can and has fuel !

				if (te instanceof TileEntityCentrifuge) {
					TileEntityCentrifuge tEPC = (TileEntityCentrifuge) te;
					if (tEPC.fuelStorage < tEPC.maxFuelStorage) {
						// can hold more fuel.
						// calc remaining fuel in can see if it is = or > than the remaining fuel storage of this machine
						int amountInCan = (playerIn.getHeldItem(EnumHand.MAIN_HAND).getMaxDamage() - playerIn.getHeldItem(EnumHand.MAIN_HAND).getItemDamage());
						int roomInEntityTank = tEPC.maxFuelStorage - tEPC.fuelStorage;
						if (amountInCan > roomInEntityTank) {

							playerIn.getHeldItem(EnumHand.MAIN_HAND).setItemDamage(playerIn.getHeldItem(EnumHand.MAIN_HAND).getMaxDamage() - (amountInCan - roomInEntityTank));
							// will fill machine completely !
							tEPC.fuelStorage = tEPC.maxFuelStorage;
						} else {
							// can will be empty becuase entity can hold 100% of the fuel from the can :O
							playerIn.getHeldItem(EnumHand.MAIN_HAND).setItemDamage(playerIn.getHeldItem(EnumHand.MAIN_HAND).getMaxDamage());
							tEPC.fuelStorage = tEPC.fuelStorage + amountInCan;
						}
					}
				}
			} else {

				playerIn.openGui(MachineMod.instance, Reference.GUI_CENTRIFUGE, worldIn, pos.getX(), pos.getY(), pos.getZ());
				return true;
			}

			return true;
		} else {
			return false;
		}
	};

	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);

		if (stack.hasDisplayName()) {
			TileEntity tileentity = worldIn.getTileEntity(pos);

			if (tileentity instanceof TileEntityFurnace) {
				((TileEntityFurnace) tileentity).setCustomInventoryName(stack.getDisplayName());
			}
		}
	}

	/**
	 * Possibly modify the given BlockState before rendering it on an Entity (Minecarts, Endermen, ...)
	 */
	@SideOnly(Side.CLIENT)
	public IBlockState getStateForEntityRender(IBlockState state) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getFront(meta);

		if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
			enumfacing = EnumFacing.NORTH;
		}

		return this.getDefaultState().withProperty(FACING, enumfacing);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state) {
		return ((EnumFacing) state.getValue(FACING)).getIndex();
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING });
	}

	@SideOnly(Side.CLIENT)
	static final class SwitchEnumFacing {
		static final int[] field_180356_a = new int[EnumFacing.values().length];
		private static final String __OBFID = "CL_00002111";

		static {
			try {
				field_180356_a[EnumFacing.WEST.ordinal()] = 1;
			} catch (NoSuchFieldError var4) {
				;
			}

			try {
				field_180356_a[EnumFacing.EAST.ordinal()] = 2;
			} catch (NoSuchFieldError var3) {
				;
			}

			try {
				field_180356_a[EnumFacing.NORTH.ordinal()] = 3;
			} catch (NoSuchFieldError var2) {
				;
			}

			try {
				field_180356_a[EnumFacing.SOUTH.ordinal()] = 4;
			} catch (NoSuchFieldError var1) {
				;
			}
		}
	}

	public BlockMachineModCentrifuge() {
		// Generic constructor (set to rock by default)
		this(Material.ROCK);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {

		// NEED TO return the TE here
		return new TileEntityCentrifuge();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		// 3 for normal block 2 for TESR 1 liquid -1 nothing ( like air)
		// return 2;
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;

	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);

		if (tileentity instanceof IInventory) {
			InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory) tileentity);
			worldIn.updateComparatorOutputLevel(pos, this);
		}

		super.breakBlock(worldIn, pos, state);
	}
}
