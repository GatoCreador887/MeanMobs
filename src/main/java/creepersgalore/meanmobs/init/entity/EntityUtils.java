package creepersgalore.meanmobs.init.entity;

import java.util.Random;

import creepersgalore.meanmobs.init.MeanMobsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class EntityUtils {
	
	public static boolean applyGear(EntityLiving entity, String gearFormat) {
		
		String[] strs = gearFormat.split(",");
		
		if (entity.getRNG().nextInt(Integer.parseInt(strs[1])) == 0) {
			
			if (!strs[0].equals("default")) {
				
				entity.setCustomNameTag(strs[0]);
				
			}
			
			entity.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Item.getByNameOrId(strs[2])));
			entity.setHeldItem(EnumHand.OFF_HAND, new ItemStack(Item.getByNameOrId(strs[3])));
			entity.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(Item.getByNameOrId(strs[4])));
			entity.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(Item.getByNameOrId(strs[5])));
			entity.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Item.getByNameOrId(strs[6])));
			entity.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Item.getByNameOrId(strs[7])));
			
			entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D + getSpeedMod(entity.getRNG()) * Double.parseDouble(strs[8]));
			entity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(80.0D + getFollowRangeMod(entity.getRNG()) * Double.parseDouble(strs[9]));
			entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D + getHealthMod(entity.getRNG()) * Double.parseDouble(strs[10]));
			
			return true;
			
		} else {
			
			return false;
			
		}
		
	}
	
	public static double getSpeedMod(Random rand) {
		
		return rand.nextDouble() * MeanMobsConfig.mobBuffs.maxSpeedBoost;
		
	}
	
	public static double getFollowRangeMod(Random rand) {
		
		return rand.nextDouble() * MeanMobsConfig.mobBuffs.maxFollowRangeBoost;
		
	}
	
	public static double getHealthMod(Random rand) {
		
		return rand.nextDouble() * MeanMobsConfig.mobBuffs.maxHealthBoost;
		
	}
	
	public static void summonMob(EntityMob summoner, int chance, String[] helpers, LivingHurtEvent event) {
		
		EntityMob newMob = null;
		boolean otherSummoned = false;
		
		for (String str : helpers) {
			
			String[] strs = str.split(",");
			
			String id = strs[0];
			double chance1 = Double.parseDouble(strs[1]);
			
			if (summoner.getRNG().nextDouble() < chance1) {
				
				newMob = (EntityMob) EntityList.createEntityByIDFromName(new ResourceLocation(id), summoner.world);
				otherSummoned = true;
				break;
				
			}
			
		}
		
		if (!otherSummoned) {
			
			newMob = (EntityMob) EntityList.createEntityByIDFromName(EntityList.getKey(summoner), summoner.world);
			
		}
		
		EntityLivingBase entitylivingbase = summoner.getAttackTarget();
		DamageSource source = event.getSource();
		Vec3d location = new Vec3d(summoner.posX, summoner.posY + summoner.getEyeHeight(), summoner.posZ);
		Vec3d newMobLocation;
		Vec3d direction;

        if (entitylivingbase == null && source.getTrueSource() instanceof EntityLivingBase)
        {
            entitylivingbase = (EntityLivingBase)source.getTrueSource();
        }
        
        int i = MathHelper.floor(summoner.posX);
        int j = MathHelper.floor(summoner.posY);
        int k = MathHelper.floor(summoner.posZ);
		
		if (summoner.getRNG().nextInt(chance) == 0) {
			
			for (int l = 0; l < 50; ++l)
	        {
	            int i1 = i + MathHelper.getInt(summoner.getRNG(), 7, 40) * MathHelper.getInt(summoner.getRNG(), -1, 1);
	            int j1 = j + MathHelper.getInt(summoner.getRNG(), 7, 40) * MathHelper.getInt(summoner.getRNG(), -1, 1);
	            int k1 = k + MathHelper.getInt(summoner.getRNG(), 7, 40) * MathHelper.getInt(summoner.getRNG(), -1, 1);

	            if (summoner.world.getBlockState(new BlockPos(i1, j1 - 1, k1)).isSideSolid(summoner.world, new BlockPos(i1, j1 - 1, k1), net.minecraft.util.EnumFacing.UP) && summoner.world.getLightFromNeighbors(new BlockPos(i1, j1, k1)) < 10)
	            {
	                newMob.setPosition((double)i1, (double)j1, (double)k1);
	                newMobLocation = new Vec3d(newMob.posX, newMob.posY + newMob.getEyeHeight(), newMob.posZ);
	                direction = location.subtract(newMobLocation).normalize();
	                //Invert (turn to negative) direction
	                direction = direction.subtract(direction.add(direction));
	                net.minecraftforge.fml.common.eventhandler.Event.Result canSpawn = net.minecraftforge.event.ForgeEventFactory.canEntitySpawn(newMob, summoner.world, (float)newMob.posX, (float)newMob.posY, (float)newMob.posZ, null);

	                if (!summoner.world.isAnyPlayerWithinRangeAt((double)i1, (double)j1, (double)k1, 7.0D) && summoner.world.checkNoEntityCollision(newMob.getEntityBoundingBox(), newMob) && summoner.world.getCollisionBoxes(newMob, newMob.getEntityBoundingBox()).isEmpty() && !summoner.world.containsAnyLiquid(newMob.getEntityBoundingBox()) && (canSpawn == net.minecraftforge.fml.common.eventhandler.Event.Result.ALLOW || canSpawn == net.minecraftforge.fml.common.eventhandler.Event.Result.DEFAULT))
	                {
	                    summoner.world.spawnEntity(newMob);
	                    if (entitylivingbase != null) newMob.setAttackTarget(entitylivingbase);
	                    if (!net.minecraftforge.event.ForgeEventFactory.doSpecialSpawn(newMob, summoner.world, (float)newMob.posX, (float)newMob.posY, (float)newMob.posZ, null)) newMob.onInitialSpawn(summoner.world.getDifficultyForLocation(new BlockPos(newMob)), (IEntityLivingData)null);

	                    if (MeanMobsConfig.summoning.summoningEffects)
	                    {
	                    	summoner.playSound(SoundEvents.ENTITY_WITHER_SHOOT, 1.0F, 2.0F);
		                    newMob.playSound(SoundEvents.ENTITY_ZOMBIE_INFECT, 1.0F, 0.85F);

		                    if (Minecraft.getMinecraft().world != null && Minecraft.getMinecraft().world.isRemote)
		                    {
		                    	for (int e1 = 0; e1 < 10; ++e1)
		                    	{
		                    		location = location.add(direction);
		                    		Minecraft.getMinecraft().world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, location.x, location.y, location.z, 0.0D, 0.0D, 0.0D);
		                    	}

			                    for (int e = 0; e < 40; ++e)
			                    {
			                        double d2 = newMob.getRNG().nextGaussian() * 0.02D;
			                        double d0 = newMob.getRNG().nextGaussian() * 0.02D;
			                        double d1 = newMob.getRNG().nextGaussian() * 0.02D;
			                        Minecraft.getMinecraft().world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, newMob.posX + (double)(newMob.getRNG().nextFloat() * newMob.width * 2.0F) - (double)newMob.width, newMob.posY + (double)(newMob.getRNG().nextFloat() * newMob.height), newMob.posZ + (double)(newMob.getRNG().nextFloat() * newMob.width * 2.0F) - (double)newMob.width, d2, d0, d1);
			                    }
		                    }
	                    }

	                    break;
	                }
	            }
	        }
			
		}
		
	}
	
}
