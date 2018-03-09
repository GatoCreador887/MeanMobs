package creepersgalore.meanmobs;

import creepersgalore.meanmobs.init.eventhandlers.MeanMobsEntityEditor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = MeanMobsReference.ID, name = MeanMobsReference.NAME, version = MeanMobsReference.VERSION)
public class MeanMobs {
	
	@EventHandler
	public static void preInit(FMLPreInitializationEvent event) {
		
		MinecraftForge.EVENT_BUS.register(MeanMobsEntityEditor.class);
		
	}
	
	@EventHandler
	public static void init(FMLInitializationEvent event) {
		
		
		
	}
	
	@EventHandler
	public static void postInit(FMLPostInitializationEvent event) {
		
	}
	
}
