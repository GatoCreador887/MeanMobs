package creepersgalore.meanmobs.init.eventhandlers;

import creepersgalore.meanmobs.init.MeanMobsConfig;
import creepersgalore.meanmobs.init.entity.EntityUtils;
import creepersgalore.meanmobs.init.entity.ai.EntityAIAttackDamager;
import creepersgalore.meanmobs.init.entity.ai.EntityAIBreakDoorBetter;
import creepersgalore.meanmobs.init.entity.ai.EntityAIBreakFenceGate;
import creepersgalore.meanmobs.init.entity.ai.EntityAIBreakTorch;
import creepersgalore.meanmobs.init.entity.ai.EntityAIBreakTrapdoor;
import creepersgalore.meanmobs.init.entity.ai.SpiderAISpiderTarget;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIRestrictSun;
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
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class MeanMobsEntityEditor {
	
	@SubscribeEvent
	public static void onEntitySpawn(LivingSpawnEvent.SpecialSpawn event) {
		
		if (event.getEntityLiving().getCreatureAttribute() != EnumCreatureAttribute.UNDEAD && MeanMobsConfig.apocalypseMode) {
			
			event.setCanceled(true);
			
		}
		
	}
	
	@SubscribeEvent
	public static void onZombieJoinWorld(EntityJoinWorldEvent event) {
		
		if (event.getEntity() instanceof EntityZombie && !(event.getEntity() instanceof EntityPigZombie)) {
			
			EntityZombie zombie = (EntityZombie) event.getEntity();
			
			zombie.setBreakDoorsAItask(false);
			zombie.setCanPickUpLoot(true);
			
			((PathNavigateGround) zombie.getNavigator()).setBreakDoors(true);
			((PathNavigateGround) zombie.getNavigator()).setEnterDoors(true);
			
			zombie.tasks.addTask(1, new EntityAIBreakDoorBetter(zombie));
			zombie.tasks.addTask(1, new EntityAIBreakTrapdoor(zombie));
			zombie.tasks.addTask(1, new EntityAIBreakFenceGate(zombie));
			zombie.tasks.addTask(3, new EntityAIBreakTorch(zombie, 1.1D, 60));
			
			if (!(zombie instanceof EntityHusk) && !zombie.isChild()) {
				
				zombie.tasks.addTask(3, new EntityAIRestrictSun(zombie));
				zombie.tasks.addTask(4, new EntityAIFleeSun(zombie, 1.0D));
				
			}
			
			zombie.targetTasks.removeTask(new EntityAIHurtByTarget(zombie, true, new Class[] {EntityPigZombie.class}));
			zombie.targetTasks.addTask(1, new EntityAIAttackDamager(zombie, true, new Class[] {EntityPigZombie.class}, AbstractSkeleton.class));
			zombie.targetTasks.removeTask(new EntityAINearestAttackableTarget(zombie, EntityPlayer.class, true));
			zombie.targetTasks.addTask(2, new EntityAINearestAttackableTarget(zombie, EntityPlayer.class, false));
			
		}
		
	}
	
	@SubscribeEvent
	public static void onZombieSpawn(LivingSpawnEvent.SpecialSpawn event) {
		
		if (event.getEntity() instanceof EntityZombie && !(event.getEntity() instanceof EntityPigZombie)) {
			
			EntityZombie zombie = (EntityZombie) event.getEntity();
			
			zombie.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513D + EntityUtils.getSpeedMod(zombie.getRNG()));
			zombie.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(60.0D + EntityUtils.getFollowRangeMod(zombie.getRNG()));
			zombie.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D + EntityUtils.getHealthMod(zombie.getRNG()));
			
			for (String str : MeanMobsConfig.mobBuffs.zombieGearPresets) {
				
				if (EntityUtils.applyGear(zombie, str)) {
					
					break;
					
				}
				
			}
			
		}
		
	}
	
	@SubscribeEvent
	public static void onSkeletonJoinWorld(EntityJoinWorldEvent event) {
		
		if (event.getEntity() instanceof AbstractSkeleton) {
			
			AbstractSkeleton skeleton = (AbstractSkeleton) event.getEntity();
			
			skeleton.setCanPickUpLoot(true);
			
			((PathNavigateGround) skeleton.getNavigator()).setBreakDoors(true);
			((PathNavigateGround) skeleton.getNavigator()).setEnterDoors(true);
			
			skeleton.tasks.addTask(1, new EntityAIBreakDoorBetter(skeleton));
			skeleton.tasks.addTask(1, new EntityAIBreakTrapdoor(skeleton));
			skeleton.tasks.addTask(1, new EntityAIBreakFenceGate(skeleton));
			skeleton.tasks.addTask(3, new EntityAIBreakTorch(skeleton, 1.1D, 60));
			
			skeleton.targetTasks.removeTask(new EntityAIHurtByTarget(skeleton, false, new Class[0]));
			skeleton.targetTasks.addTask(1, new EntityAIAttackDamager(skeleton, true, new Class[0], EntityZombie.class));
			skeleton.targetTasks.removeTask(new EntityAINearestAttackableTarget(skeleton, EntityPlayer.class, true));
			skeleton.targetTasks.addTask(2, new EntityAINearestAttackableTarget(skeleton, EntityPlayer.class, false));
			skeleton.targetTasks.addTask(3, new EntityAINearestAttackableTarget(skeleton, EntityVillager.class, false));
			
		}
		
	}
	
	@SubscribeEvent
	public static void onSkeletonSpawn(LivingSpawnEvent.SpecialSpawn event) {
		
		if (event.getEntity() instanceof AbstractSkeleton) {
			
			AbstractSkeleton skeleton = (AbstractSkeleton) event.getEntity();
			
			skeleton.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513D + EntityUtils.getSpeedMod(skeleton.getRNG()));
			skeleton.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(60.0D + EntityUtils.getFollowRangeMod(skeleton.getRNG()));
			skeleton.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D + EntityUtils.getHealthMod(skeleton.getRNG()));
			
			for (String str : MeanMobsConfig.mobBuffs.skeletonGearPresets) {
				
				if (EntityUtils.applyGear(skeleton, str)) {
					
					break;
					
				}
				
			}
			
		}
		
	}
	
	@SubscribeEvent
	public static void onSpiderJoinWorld(EntityJoinWorldEvent event) {
		
		if (event.getEntity() instanceof EntitySpider) {
			
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
		
		if (event.getEntity() instanceof EntitySpider) {
			
			EntitySpider spider = (EntitySpider) event.getEntity();
			
			spider.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D + EntityUtils.getSpeedMod(spider.getRNG()));
			spider.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D + EntityUtils.getFollowRangeMod(spider.getRNG()));
			
		}
		
	}
	
	@SubscribeEvent
	public static void onBlazeSpawn(LivingSpawnEvent.SpecialSpawn event) {
		
		if (event.getEntity() instanceof EntityBlaze) {
			
			EntityBlaze blaze = (EntityBlaze) event.getEntity();
			
			blaze.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513D + EntityUtils.getSpeedMod(blaze.getRNG()));
			blaze.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(60.0D + EntityUtils.getFollowRangeMod(blaze.getRNG()));
			
		}
		
	}
	
	@SubscribeEvent
	public static void onZombieHurt(LivingHurtEvent event) {
		
		if (event.getEntity() instanceof EntityZombie) {
			
			EntityZombie zombie = (EntityZombie) event.getEntity();
			
			EntityUtils.summonMob(zombie, MeanMobsConfig.summoning.zombieSummonChance, MeanMobsConfig.summoning.zombieHelpers, event);
			
		}
		
	}
	
	@SubscribeEvent
	public static void onSkeletonHurt(LivingHurtEvent event) {
		
		if (event.getEntity() instanceof AbstractSkeleton) {
			
			AbstractSkeleton skeleton = (AbstractSkeleton) event.getEntity();
			
			EntityUtils.summonMob(skeleton, MeanMobsConfig.summoning.skeletonSummonChance, MeanMobsConfig.summoning.skeletonHelpers, event);
			
		}
		
	}
	
	@SubscribeEvent
	public static void onTick(TickEvent.WorldTickEvent event) {
		
		if (event.world.getGameRules().getBoolean("doDaylightCycle") && MeanMobsConfig.endlessNightmare && event.world.getWorldTime() == 18000L) {
			
			event.world.getGameRules().setOrCreateGameRule("doDaylightCycle", "false");
			
		}
		
	}
	
}
