package com.projectreddog.machinemod.world;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.projectreddog.machinemod.init.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class ModExplosion extends Explosion {

	private World worldObj;
	private Random explosionRNG = new Random();
	private final double explosionX;
	private final double explosionY;
	private final double explosionZ;
	private final Entity exploder;
	private final float explosionSize;
	private List affectedBlockPositions;
	public boolean isSmoking;

	public ModExplosion(World world, Entity exploder, double explosionX, double explosionY, double explosionZ, float explosionSize) {
		super(world, exploder, explosionX, explosionY, explosionZ, explosionSize, false, true);

		worldObj = world;

		this.exploder = exploder;
		this.affectedBlockPositions = Lists.newArrayList();
		this.explosionX = explosionX;
		this.explosionY = explosionY;
		this.explosionZ = explosionZ;
		this.explosionSize = explosionSize;
		this.isSmoking = true;
	}

	@Override
	public void doExplosionB(boolean p_77279_1_) {

		this.affectedBlockPositions = super.getAffectedBlockPositions();

		this.worldObj.playSound(null, new BlockPos(this.explosionX, this.explosionY, this.explosionZ), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

		if (this.explosionSize >= 2.0F && this.isSmoking) {
			this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.explosionX, this.explosionY, this.explosionZ, 1.0D, 0.0D, 0.0D, new int[0]);

		} else {

			this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.explosionX, this.explosionY, this.explosionZ, 1.0D, 0.0D, 0.0D, new int[0]);
		}

		Iterator iterator;
		BlockPos blockpos;
		int i;
		int j;
		int k;
		Block block;

		if (this.isSmoking) {
			iterator = this.affectedBlockPositions.iterator();

			while (iterator.hasNext()) {
				blockpos = (BlockPos) iterator.next();
				i = blockpos.getX();
				j = blockpos.getY();
				k = blockpos.getZ();
				block = this.worldObj.getBlockState(blockpos).getBlock();

				if (p_77279_1_) {
					double d0 = (double) ((float) i + this.worldObj.rand.nextFloat());
					double d1 = (double) ((float) j + this.worldObj.rand.nextFloat());
					double d2 = (double) ((float) k + this.worldObj.rand.nextFloat());
					double d3 = d0 - this.explosionX;
					double d4 = d1 - this.explosionY;
					double d5 = d2 - this.explosionZ;
					double d6 = (double) MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
					d3 /= d6;
					d4 /= d6;
					d5 /= d6;
					double d7 = 0.5D / (d6 / (double) this.explosionSize + 0.1D);
					d7 *= (double) (this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3F);
					d3 *= d7;
					d4 *= d7;
					d5 *= d7;
					this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, (d0 + this.explosionX * 1.0D) / 2.0D, (d1 + this.explosionY * 1.0D) / 2.0D, (d2 + this.explosionZ * 1.0D) / 2.0D, d3, d4, d5);
					this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, d3, d4, d5);
				}

				if (block.getMaterial(this.worldObj.getBlockState(blockpos)) != Material.AIR) {

					// TS DO NOT DROP BLOCKS !
					// if (block.canDropFromExplosion(this))
					// {
					// block.dropBlockAsItemWithChance(this.worldObj, i, j, k,
					// this.worldObj.getBlockMetadata(i, j, k), 1.0F /
					// this.explosionSize, 0);
					// }
					// TS change next line where it actually breaks the block
					// instead do my call to set the block to the proper type
					// block.onBlockExploded(this.worldObj, i, j, k, this);

					changeBlockType(i, j, k, block);
				}
			}
		}

	}

	private void changeBlockType(int x, int y, int z, Block block) {

		if (block == ModBlocks.machineexplosivepackeddrilledstone) {
			// do the explosion! if it's blasted stone so we can propagate the
			// explosion on to the next block !
			block.onBlockExploded(this.worldObj, new BlockPos(x, y, z), this);
		} else {

			if (!this.worldObj.isRemote) {

				// Determine block to turn this block into
				BlockPos bp = new BlockPos(x, y, z);

				if (this.worldObj.getBlockState(bp).getBlock() == Blocks.STONE) {
					// its stone so get variant
					if (this.worldObj.getBlockState(bp).getValue(BlockStone.VARIANT) == BlockStone.EnumType.STONE) {
						this.worldObj.setBlockState(new BlockPos(x, y, z), ModBlocks.machineblastedstone.getDefaultState());
					} else if (this.worldObj.getBlockState(bp).getValue(BlockStone.VARIANT) == BlockStone.EnumType.GRANITE) {
						this.worldObj.setBlockState(new BlockPos(x, y, z), ModBlocks.machineblastedgranite.getDefaultState());
					} else if (this.worldObj.getBlockState(bp).getValue(BlockStone.VARIANT) == BlockStone.EnumType.DIORITE) {
						this.worldObj.setBlockState(new BlockPos(x, y, z), ModBlocks.machineblasteddiorite.getDefaultState());
					} else if (this.worldObj.getBlockState(bp).getValue(BlockStone.VARIANT) == BlockStone.EnumType.ANDESITE) {
						this.worldObj.setBlockState(new BlockPos(x, y, z), ModBlocks.machineblastedandesite.getDefaultState());
					}
				}
				// not stone
				else if (this.worldObj.getBlockState(bp).getBlock() == Blocks.GOLD_ORE) {
					this.worldObj.setBlockState(new BlockPos(x, y, z), ModBlocks.machineblastedgold.getDefaultState());
				} else if (this.worldObj.getBlockState(bp).getBlock() == Blocks.IRON_ORE) {
					this.worldObj.setBlockState(new BlockPos(x, y, z), ModBlocks.machineblastediron.getDefaultState());
				} else if (this.worldObj.getBlockState(bp).getBlock() == Blocks.COAL_ORE) {
					this.worldObj.setBlockState(new BlockPos(x, y, z), ModBlocks.machineblastedcoal.getDefaultState());
				} else if (this.worldObj.getBlockState(bp).getBlock() == Blocks.LAPIS_ORE) {
					this.worldObj.setBlockState(new BlockPos(x, y, z), ModBlocks.machineblastedlapis.getDefaultState());
				} else if (this.worldObj.getBlockState(bp).getBlock() == Blocks.DIAMOND_ORE) {
					this.worldObj.setBlockState(new BlockPos(x, y, z), ModBlocks.machineblasteddiamond.getDefaultState());
				} else if (this.worldObj.getBlockState(bp).getBlock() == Blocks.REDSTONE_ORE) {
					this.worldObj.setBlockState(new BlockPos(x, y, z), ModBlocks.machineblastedredstone.getDefaultState());
				} else if (this.worldObj.getBlockState(bp).getBlock() == Blocks.EMERALD_ORE) {
					this.worldObj.setBlockState(new BlockPos(x, y, z), ModBlocks.machineblastedemerald.getDefaultState());
				} else if (this.worldObj.getBlockState(bp).getBlock() == ModBlocks.machineblastedstone || this.worldObj.getBlockState(bp).getBlock() == ModBlocks.machineblastedgranite || this.worldObj.getBlockState(bp).getBlock() == ModBlocks.machineblasteddiorite || this.worldObj.getBlockState(bp).getBlock() == ModBlocks.machineblastedandesite
						|| this.worldObj.getBlockState(bp).getBlock() == ModBlocks.machineblastedgold || this.worldObj.getBlockState(bp).getBlock() == ModBlocks.machineblastediron || this.worldObj.getBlockState(bp).getBlock() == ModBlocks.machineblastedcoal || this.worldObj.getBlockState(bp).getBlock() == ModBlocks.machineblastedlapis
						|| this.worldObj.getBlockState(bp).getBlock() == ModBlocks.machineblasteddiamond || this.worldObj.getBlockState(bp).getBlock() == ModBlocks.machineblastedredstone || this.worldObj.getBlockState(bp).getBlock() == ModBlocks.machineblastedemerald || this.worldObj.getBlockState(bp).getBlock() == ModBlocks.machineblastedstone2) {
				} else {
					// check for mod blocks here using ore dictionary & set it
					// to ModBlocks.machinemodblastedstone2 ......

					block.dropBlockAsItem(this.worldObj, bp, this.worldObj.getBlockState(bp), 0);
					block.onBlockExploded(this.worldObj, bp, this);
					// this.worldObj.setBlockState(new BlockPos(x, y, z),
					// ModBlocks.machinemodblastedstone.getDefaultState());
				}
			}

		}

	}

}
