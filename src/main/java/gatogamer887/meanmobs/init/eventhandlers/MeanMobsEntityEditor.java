package gatogamer887.meanmobs.init.eventhandlers;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;

import com.google.common.collect.Sets;

import gatogamer887.meanmobs.MeanMobs;
import gatogamer887.meanmobs.init.MeanMobsConfig;
import gatogamer887.meanmobs.init.entity.EntityUtils;
import gatogamer887.meanmobs.init.entity.ai.EntityAIAttackDamager;
import gatogamer887.meanmobs.init.entity.ai.EntityAIBreakDoorBetter;
import gatogamer887.meanmobs.init.entity.ai.EntityAIBreakFenceGate;
import gatogamer887.meanmobs.init.entity.ai.EntityAIBreakTorch;
import gatogamer887.meanmobs.init.entity.ai.EntityAIBreakTrapdoor;
import gatogamer887.meanmobs.init.entity.ai.EntityAISwimmingBetter;
import gatogamer887.meanmobs.init.entity.ai.SpiderAISpiderTarget;
import gatogamer887.meanmobs.init.entity.pathfinding.PathNavigateGroundBetter;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingPackSizeEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MeanMobsEntityEditor {
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onEntitySpawnCheck(LivingSpawnEvent.CheckSpawn event) {
		
		if (MeanMobsConfig.apocalypseMode.enabled) {
			
			boolean disabled = true;
			
			for (String str : MeanMobsConfig.apocalypseMode.allowedSpawns) {
				
				if (EntityList.getKey(event.getEntity()).toString().equals(str)) {
					
					disabled = false;
					
					if (MeanMobsConfig.debugging.logSpawningCancellations) {
						
						MeanMobs.logger.debug("Not cancelling spawn of type " + EntityList.getKey(event.getEntity()) + " because it is in the allowed spawns list");
						
					}
					
					break;
					
				}
				
			}
			
			if (event.getEntityLiving().getCreatureAttribute() != EnumCreatureAttribute.UNDEAD && disabled) {
				
				event.setResult(Result.DENY);
				
				if (MeanMobsConfig.debugging.logSpawningCancellations) {
					
					MeanMobs.logger.debug("Cancelled spawn of type " + EntityList.getKey(event.getEntity()));
					
				}
				
			} else {
				
				event.setResult(Result.DEFAULT);
				
				if (MeanMobsConfig.apocalypseMode.extraSpawns && event.getEntityLiving() instanceof EntityLiving) {
					
					int times = event.getEntityLiving().getRNG().nextInt(MeanMobsConfig.apocalypseMode.maxSummonTimes + 1);
					
					for (int i = 0; i < times; i++) {
						
						IEntityLivingData ientitylivingdata = null;
						EntityLiving newMob = (EntityLiving) EntityList.createEntityByIDFromName(EntityList.getKey(event.getEntity()), event.getWorld());
						newMob.onInitialSpawn(event.getWorld().getDifficultyForLocation(new BlockPos(event.getX(), event.getY(), event.getZ())), null);
						float x = event.getX() + (newMob.getRNG().nextFloat() - newMob.getRNG().nextFloat()) * 5.0F;
						float y = event.getY();
						float z = event.getZ() + (newMob.getRNG().nextFloat() - newMob.getRNG().nextFloat()) * 5.0F;
						newMob.setLocationAndAngles(x, y, z, newMob.getRNG().nextFloat() * 360.0F, 0.0F);
						
						//Result canSpawn = ForgeEventFactory.canEntitySpawn(newMob, event.getWorld(), x, y, z, null);
						
						if (newMob.getCanSpawnHere() && newMob.isNotColliding()) {
							
							if (!ForgeEventFactory.doSpecialSpawn(newMob, event.getWorld(), x, y, z, null)) {
								
								ientitylivingdata = newMob.onInitialSpawn(event.getWorld().getDifficultyForLocation(new BlockPos(newMob)), ientitylivingdata);
								
							}
							
							if (newMob.isNotColliding()) {
								
								event.getWorld().spawnEntity(newMob);
								
							} else {
								
								newMob.setDead();
								
							}
							
						}
						
					}
					
					MeanMobs.logger.debug("Spawned extra mobs of " + EntityList.getKey(event.getEntity()) + " " + times + " times");
					
				}
				
			}
			
		}
		
	}
	
	@SubscribeEvent
	public static void onZombieJoinWorld(EntityJoinWorldEvent event) {
		
		if ((event.getEntity() instanceof EntityZombie && !(event.getEntity() instanceof EntityPigZombie)) || (EntityList.getKey(event.getEntity()) != null ? Arrays.asList(MeanMobsConfig.zombieEntities).contains(EntityList.getKey(event.getEntity()).toString()) : false)) {
			
			EntityZombie zombie = (EntityZombie) event.getEntity();
			
			try {
				
				Field navigatorField = EntityLiving.class.getDeclaredField("navigator");
				navigatorField.setAccessible(true);
				navigatorField.set(zombie, new PathNavigateGroundBetter(zombie, zombie.getEntityWorld()));
				
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				
				MeanMobs.logger.error("Problem editing navigator: " + e);
				
			}
			
			/*try {
				
				Field moveHelperField = EntityLiving.class.getDeclaredField("moveHelper");
				moveHelperField.setAccessible(true);
				moveHelperField.set(zombie, new EntityMoveHelperBetter(zombie));
				
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				
				MeanMobs.logger.error("Problem editing movehelper: " + e);
				
			}*/
			
			zombie.setBreakDoorsAItask(false);
			zombie.setCanPickUpLoot(MeanMobsConfig.mobBuffs.lootSystemMode > 0);
			
			((PathNavigateGround) zombie.getNavigator()).setBreakDoors(true);
			((PathNavigateGround) zombie.getNavigator()).setEnterDoors(true);
			
			Set<EntityAITaskEntry> taskEntries = Sets.<EntityAITaskEntry>newLinkedHashSet();
			taskEntries.addAll(zombie.tasks.taskEntries);
			
			for (EntityAITaskEntry task : zombie.tasks.taskEntries) {
				
				if (task.action instanceof EntityAISwimming) {
					
					taskEntries.remove(task);
					
				}
				
			}
			
			zombie.tasks.taskEntries.clear();
			zombie.tasks.taskEntries.addAll(taskEntries);
			
			zombie.tasks.addTask(0, new EntityAISwimmingBetter(zombie));
			zombie.tasks.addTask(1, new EntityAIBreakDoorBetter(zombie));
			zombie.tasks.addTask(1, new EntityAIBreakTrapdoor(zombie));
			zombie.tasks.addTask(1, new EntityAIBreakFenceGate(zombie));
			//zombie.tasks.addTask(2, new EntityAIAttackRangedBow());
			zombie.tasks.addTask(3, new EntityAIBreakTorch(zombie, 1.1D, 60));
			
			if (!(zombie instanceof EntityHusk) && !zombie.isChild()) {
				
				zombie.tasks.addTask(3, new EntityAIRestrictSun(zombie));
				zombie.tasks.addTask(4, new EntityAIFleeSun(zombie, 1.0D));
				
			}
			
			zombie.targetTasks.removeTask(new EntityAIHurtByTarget(zombie, true, new Class[] {EntityPigZombie.class}));
			zombie.targetTasks.addTask(1, new EntityAIAttackDamager(zombie, true, new Class[] {EntityPigZombie.class}, AbstractSkeleton.class));
			zombie.targetTasks.removeTask(new EntityAINearestAttackableTarget(zombie, EntityPlayer.class, true));
			zombie.targetTasks.addTask(2, new EntityAINearestAttackableTarget(zombie, EntityPlayer.class, !MeanMobsConfig.mobBuffs.mobsSeeThroughWalls));
			
		}
		
	}
	
	@SubscribeEvent
	public static void onZombieSpawn(LivingSpawnEvent.SpecialSpawn event) {
		
		if ((event.getEntity() instanceof EntityZombie && !(event.getEntity() instanceof EntityPigZombie)) || (EntityList.getKey(event.getEntity()) != null ? Arrays.asList(MeanMobsConfig.zombieEntities).contains(EntityList.getKey(event.getEntity()).toString()) : false)) {
			
			EntityZombie zombie = (EntityZombie) event.getEntity();
			
			EntityUtils.applyZombieSpawnBuffs(zombie);
			
		}
		
	}
	
	@SubscribeEvent
	public static void onZombieSpawn(LivingPackSizeEvent event) {
		
		if ((event.getEntity() instanceof EntityZombie && !(event.getEntity() instanceof EntityPigZombie)) || (EntityList.getKey(event.getEntity()) != null ? Arrays.asList(MeanMobsConfig.zombieEntities).contains(EntityList.getKey(event.getEntity()).toString()) : false)) {
			
			EntityZombie zombie = (EntityZombie) event.getEntity();
			
			EntityUtils.applyZombieSpawnBuffs(zombie);
			
		}
		
	}
	
	@SubscribeEvent
	public static void onSkeletonJoinWorld(EntityJoinWorldEvent event) {
		
		if (event.getEntity() instanceof AbstractSkeleton || (EntityList.getKey(event.getEntity()) != null ? Arrays.asList(MeanMobsConfig.skeletonEntities).contains(EntityList.getKey(event.getEntity()).toString()) : false)) {
			
			AbstractSkeleton skeleton = (AbstractSkeleton) event.getEntity();
			
			try {
				
				Field navigatorField = EntityLiving.class.getDeclaredField("navigator");
				navigatorField.setAccessible(true);
				navigatorField.set(skeleton, new PathNavigateGroundBetter(skeleton, skeleton.getEntityWorld()));
				
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				
				MeanMobs.logger.error("Problem editing navigator: " + e);
				
			}
			
			skeleton.setCanPickUpLoot(MeanMobsConfig.mobBuffs.lootSystemMode > 0);
			
			((PathNavigateGround) skeleton.getNavigator()).setBreakDoors(true);
			((PathNavigateGround) skeleton.getNavigator()).setEnterDoors(true);
			
			Set<EntityAITaskEntry> taskEntries = Sets.<EntityAITaskEntry>newLinkedHashSet();
			taskEntries.addAll(skeleton.tasks.taskEntries);
			
			for (EntityAITaskEntry task : skeleton.tasks.taskEntries) {
				
				if (task.action instanceof EntityAISwimming) {
					
					taskEntries.remove(task);
					
				}
				
			}
			
			skeleton.tasks.taskEntries.clear();
			skeleton.tasks.taskEntries.addAll(taskEntries);
			
			skeleton.tasks.addTask(0, new EntityAISwimmingBetter(skeleton));
			skeleton.tasks.addTask(1, new EntityAIBreakDoorBetter(skeleton));
			skeleton.tasks.addTask(1, new EntityAIBreakTrapdoor(skeleton));
			skeleton.tasks.addTask(1, new EntityAIBreakFenceGate(skeleton));
			skeleton.tasks.addTask(3, new EntityAIBreakTorch(skeleton, 1.1D, 60));
			
			skeleton.targetTasks.removeTask(new EntityAIHurtByTarget(skeleton, false, new Class[0]));
			skeleton.targetTasks.addTask(1, new EntityAIAttackDamager(skeleton, true, new Class[0], EntityZombie.class));
			skeleton.targetTasks.removeTask(new EntityAINearestAttackableTarget(skeleton, EntityPlayer.class, true));
			skeleton.targetTasks.addTask(2, new EntityAINearestAttackableTarget(skeleton, EntityPlayer.class, !MeanMobsConfig.mobBuffs.mobsSeeThroughWalls));
			skeleton.targetTasks.addTask(3, new EntityAINearestAttackableTarget(skeleton, EntityVillager.class, !MeanMobsConfig.mobBuffs.mobsSeeThroughWalls));
			
		}
		
	}
	
	@SubscribeEvent
	public static void onSkeletonSpawn(LivingSpawnEvent.SpecialSpawn event) {
		
		if (event.getEntity().isEntityAlive() && event.getEntity() instanceof AbstractSkeleton || (EntityList.getKey(event.getEntity()) != null ? Arrays.asList(MeanMobsConfig.skeletonEntities).contains(EntityList.getKey(event.getEntity()).toString()) : false)) {
			
			AbstractSkeleton skeleton = (AbstractSkeleton) event.getEntity();
			
			EntityUtils.applySkeletonSpawnBuffs(skeleton);
			
		}
		
	}
	
	@SubscribeEvent
	public static void onSkeletonSpawn(LivingPackSizeEvent event) {
		
		if (event.getEntity().isEntityAlive() && event.getEntity() instanceof AbstractSkeleton || (EntityList.getKey(event.getEntity()) != null ? Arrays.asList(MeanMobsConfig.skeletonEntities).contains(EntityList.getKey(event.getEntity()).toString()) : false)) {
			
			AbstractSkeleton skeleton = (AbstractSkeleton) event.getEntity();
			
			EntityUtils.applySkeletonSpawnBuffs(skeleton);
			
		}
		
	}
	
	@SubscribeEvent
	public static void onSpiderJoinWorld(EntityJoinWorldEvent event) {
		
		if (event.getEntity() instanceof EntitySpider || (EntityList.getKey(event.getEntity()) != null ? Arrays.asList(MeanMobsConfig.spiderEntities).contains(EntityList.getKey(event.getEntity()).toString()) : false)) {
			
			EntitySpider spider = (EntitySpider) event.getEntity();
			
			spider.tasks.addTask(0, new EntityAIFleeSun(spider, 1.0D));
			//spider.tasks.addTask(0, new EntityAIRestrictSun(spider));
			//spider.targetTasks.addTask(3, new SpiderAISpiderTarget(spider, EntityChicken.class));
			//spider.targetTasks.addTask(3, new SpiderAISpiderTarget(spider, EntityRabbit.class));
			spider.targetTasks.addTask(3, new SpiderAISpiderTarget(spider, EntityVillager.class));
			spider.targetTasks.addTask(3, new SpiderAISpiderTarget(spider, EntityWitch.class));
			
		}
		
	}
	
	@SubscribeEvent
	public static void onSpiderSpawn(LivingSpawnEvent.SpecialSpawn event) {
		
		if (event.getEntity() instanceof EntitySpider || (EntityList.getKey(event.getEntity()) != null ? Arrays.asList(MeanMobsConfig.spiderEntities).contains(EntityList.getKey(event.getEntity()).toString()) : false)) {
			
			EntitySpider spider = (EntitySpider) event.getEntity();
			
			spider.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(EntityUtils.getSpeedMod(spider.getRNG()));
			spider.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).applyModifier(EntityUtils.getFollowRangeMod(spider.getRNG(), 1.0D, 19.0D));
			
		}
		
	}
	
	@SubscribeEvent
	public static void onBlazeSpawn(LivingSpawnEvent.SpecialSpawn event) {
		
		if (event.getEntity() instanceof EntityBlaze || (EntityList.getKey(event.getEntity()) != null ? Arrays.asList(MeanMobsConfig.blazeEntities).contains(EntityList.getKey(event.getEntity()).toString()) : false)) {
			
			EntityBlaze blaze = (EntityBlaze) event.getEntity();
			
			blaze.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(EntityUtils.getSpeedMod(blaze.getRNG()));
			blaze.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).applyModifier(EntityUtils.getFollowRangeMod(blaze.getRNG(), 2.0D));
			
		}
		
	}
	
	@SubscribeEvent
	public static void onZombieHurt(LivingHurtEvent event) {
		
		if (event.getEntity() instanceof EntityZombie || (EntityList.getKey(event.getEntity()) != null ? Arrays.asList(MeanMobsConfig.zombieEntities).contains(EntityList.getKey(event.getEntity()).toString()) : false)) {
			
			EntityZombie zombie = (EntityZombie) event.getEntity();
			
			EntityUtils.summonMob(zombie, MeanMobsConfig.summoning.zombieSummonChance, MeanMobsConfig.summoning.zombieHelpers, event);
			
		}
		
	}
	
	@SubscribeEvent
	public static void onSkeletonHurt(LivingHurtEvent event) {
		
		if (event.getEntity() instanceof AbstractSkeleton || (EntityList.getKey(event.getEntity()) != null ? Arrays.asList(MeanMobsConfig.skeletonEntities).contains(EntityList.getKey(event.getEntity()).toString()) : false)) {
			
			AbstractSkeleton skeleton = (AbstractSkeleton) event.getEntity();
			
			EntityUtils.summonMob(skeleton, MeanMobsConfig.summoning.skeletonSummonChance, MeanMobsConfig.summoning.skeletonHelpers, event);
			
		}
		
	}
	
}
