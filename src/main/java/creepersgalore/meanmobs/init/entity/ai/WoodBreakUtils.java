package creepersgalore.meanmobs.init.entity.ai;

import java.util.Random;

import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.Item.ToolMaterial;

public class WoodBreakUtils {
	
	public static int getWoodBreakAmount(Item item, Random rand) {
		
		return (int) (item instanceof ItemAxe ? 1 + ToolMaterial.valueOf(((ItemAxe) item).getToolMaterialName()).getEfficiency() / 4 + rand.nextInt(3) : 1);
		
	}
	
}
