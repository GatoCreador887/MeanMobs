package creepersgalore.meanmobs.init;

import java.util.HashMap;
import java.util.Map;

import creepersgalore.meanmobs.MeanMobs;
import creepersgalore.meanmobs.MeanMobsReference;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = MeanMobsReference.ID)
@Config.LangKey("meanmobs.config.title")
public class MeanMobsConfig {
	
	public static boolean endlessNightmare = false;
	
	public static final MobBuffs mobBuffs = new MobBuffs();
	public static final Summoning summoning = new Summoning();
	
	public static class MobBuffs {
		
		@Config.RangeDouble(min = 0.0D, max = Double.MAX_VALUE)
		public double maxSpeedBoost = 0.15D;
		@Config.RangeDouble(min = 0.0D, max = Double.MAX_VALUE)
		public double maxFollowRangeBoost = 20.0D;
		@Config.RangeDouble(min = 0.0D, max = Double.MAX_VALUE)
		public double maxHealthBoost = 3.0D;
		
		public String[] zombieGearPresets = {
				
				"default,30,minecraft:stone_pickaxe,empty,empty,empty,empty,empty,1.0,1.0,1.0",
				"default,50,minecraft:iron_pickaxe,empty,empty,empty,empty,empty,1.0,1.0,1.0",
				"default,40,minecraft:stone_axe,empty,empty,empty,empty,empty,1.0,1.0,1.0",
				"default,65,minecraft:iron_axe,empty,empty,empty,empty,empty,1.0,1.0,1.0",
				"Fallen Warrior,80,minecraft:iron_sword,empty,minecraft:leather_boots,minecraft:leather_leggings,minecraft:chainmail_chestplate,minecraft:iron_helmet,1.0,1.5,1.5",
				"Fallen Knight,100,minecraft:iron_sword,minecraft:shield,minecraft:iron_boots,minecraft:iron_leggings,minecraft:iron_chestplate,minecraft:iron_helmet,1.0,1.5,2.0",
				"Undying Zombie,90,minecraft:totem_of_undying,minecraft:totem_of_undying,empty,empty,empty,empty,1.0,1.0,1.0"
				
		};
		
		public String[] skeletonGearPresets = {
				
				"Fallen Archer,80,minecraft:bow,empty,minecraft:leather_boots,minecraft:leather_leggings,minecraft:chainmail_chestplate,minecraft:iron_helmet,1.0,1.5,1.5",
				"Undying Skeleton,90,minecraft:bow,minecraft:totem_of_undying,empty,empty,empty,empty,1.0,1.0,1.0"
				
		};
		
	}
	
	public static class Summoning {
		
		public boolean summoningEffects = true;
		
		public String[] zombieHelpers = {"minecraft:skeleton,0.1"};
		public String[] skeletonHelpers = {"minecraft:zombie,0.1"};
		@Config.RangeInt(min = 1, max = Integer.MAX_VALUE)
		public int zombieSummonChance = 80;
		@Config.RangeInt(min = 1, max = Integer.MAX_VALUE)
		public int skeletonSummonChance = 120;
		
	}
	
	@Mod.EventBusSubscriber(modid = MeanMobsReference.ID)
	private static class EventHandler {
		
		@SubscribeEvent
		public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
			
			if (event.getModID().equals(MeanMobsReference.ID)) {
				
				ConfigManager.sync(MeanMobsReference.ID, Config.Type.INSTANCE);
				
			}
			
		}
		
	}
	
}
