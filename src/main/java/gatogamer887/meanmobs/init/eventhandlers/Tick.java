package gatogamer887.meanmobs.init.eventhandlers;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import gatogamer887.meanmobs.MeanMobs;
import gatogamer887.meanmobs.init.entity.pathfinding.PathNavigateGroundBetter;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class Tick {
	
	@SubscribeEvent
	public static void tick(WorldTickEvent event) {
		
		World world = event.world;
		
		world.profiler.startSection("MeanMobs-looting");
		
		for (EntityLiving living : world.getEntities(EntityLiving.class, entity -> { return true; })) {
			
			if ((living instanceof EntityZombie && !(living instanceof EntityPigZombie)) || living instanceof AbstractSkeleton) {
				
				if (!living.world.isRemote && living.canPickUpLoot() && !living.isDead && living.world.getGameRules().getBoolean("mobGriefing"))
		        {
		            for (EntityItem entityitem : living.world.getEntitiesWithinAABB(EntityItem.class, living.getEntityBoundingBox().grow(1.0D, 0.0D, 1.0D)))
		            {
		                if (!entityitem.isDead && !entityitem.getItem().isEmpty() && !entityitem.cannotPickup())
		                {
		                    ItemStack itemstack = entityitem.getItem();
		                    EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);
		                    boolean flag = true;
		                    ItemStack itemstack1 = living.getItemStackFromSlot(entityequipmentslot);
		                    
		                    if (!itemstack1.isEmpty())
		                    {
		                        if (entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.HAND)
		                        {
		                            if (itemstack.getItem() instanceof ItemSword && !(itemstack1.getItem() instanceof ItemSword))
		                            {
		                                flag = true;
		                            }
		                            else if (itemstack.getItem() instanceof ItemSword && itemstack1.getItem() instanceof ItemSword)
		                            {
		                                ItemSword itemsword = (ItemSword)itemstack.getItem();
		                                ItemSword itemsword1 = (ItemSword)itemstack1.getItem();

		                                if (itemsword.getAttackDamage() == itemsword1.getAttackDamage())
		                                {
		                                    flag = itemstack.getMetadata() > itemstack1.getMetadata() || itemstack.hasTagCompound() && !itemstack1.hasTagCompound();
		                                }
		                                else
		                                {
		                                    flag = itemsword.getAttackDamage() > itemsword1.getAttackDamage();
		                                }
		                            }
		                            else if (itemstack.getItem() instanceof ItemBow && !(itemstack1.getItem() instanceof ItemBow) && living instanceof IRangedAttackMob)
		                            {
		                                flag = true;
		                            }
		                            else if (itemstack.getItem() instanceof ItemBow && itemstack1.getItem() instanceof ItemBow)
		                            {
		                                flag = itemstack.getMetadata() > itemstack1.getMetadata() || itemstack.hasTagCompound() && !itemstack1.hasTagCompound();
		                            }
		                            else if (itemstack.getItem() instanceof ItemTool && !(itemstack1.getItem() instanceof ItemTool) && !(itemstack1.getItem() instanceof ItemSword) && (living instanceof IRangedAttackMob ? !(itemstack1.getItem() instanceof ItemBow) : true))
		                            {
		                                flag = true;
		                            }
		                            else if (itemstack.getItem() instanceof ItemTool && itemstack1.getItem() instanceof ItemTool)
		                            {
		                            	ItemTool itemtool = (ItemTool)itemstack.getItem();
		                                ItemTool itemtool1 = (ItemTool)itemstack1.getItem();
		                                
		                                if (itemtool.getClass() == itemtool1.getClass()) {
		                                	MeanMobs.logger.info(ToolMaterial.valueOf(itemtool.getToolMaterialName()).getHarvestLevel());
		                                	MeanMobs.logger.info(ToolMaterial.valueOf(itemtool1.getToolMaterialName()).getHarvestLevel());
		                                	MeanMobs.logger.info(ToolMaterial.valueOf(itemtool.getToolMaterialName()).getHarvestLevel() > ToolMaterial.valueOf(itemtool1.getToolMaterialName()).getHarvestLevel());
		                                	if (ToolMaterial.valueOf(itemtool.getToolMaterialName()).getHarvestLevel() > ToolMaterial.valueOf(itemtool1.getToolMaterialName()).getHarvestLevel()) {
		                                		
		                                		flag = true;
		                                		MeanMobs.logger.info("picking item with better harvestlevel");
		                                	} else if (ToolMaterial.valueOf(itemtool.getToolMaterialName()).getHarvestLevel() == ToolMaterial.valueOf(itemtool1.getToolMaterialName()).getHarvestLevel()) {
		                                		
		                                		flag = itemstack.getMetadata() > itemstack1.getMetadata() || itemstack.hasTagCompound() && !itemstack1.hasTagCompound();
		                                		MeanMobs.logger.info(itemstack.getMetadata());
			                                	MeanMobs.logger.info(itemstack1.getMetadata());
			                                	MeanMobs.logger.info(itemstack.getMetadata() > itemstack1.getMetadata());
			                                	MeanMobs.logger.info(itemstack.hasTagCompound());
			                                	MeanMobs.logger.info(itemstack1.hasTagCompound());
			                                	MeanMobs.logger.info(itemstack.hasTagCompound() && !itemstack1.hasTagCompound());
		                                	}
		                                	MeanMobs.logger.info(flag);
		                                } else {
		                                	
											try {
												
												Field toolAttackDamage = ItemTool.class.getDeclaredField("attackDamage");
												toolAttackDamage.setAccessible(true);
												
												if (toolAttackDamage.getFloat(itemtool) == toolAttackDamage.getFloat(itemtool1)) {
													
													flag = itemstack.getMetadata() > itemstack1.getMetadata() || itemstack.hasTagCompound() && !itemstack1.hasTagCompound();
													
												} else {
													
													flag = toolAttackDamage.getFloat(itemtool) > toolAttackDamage.getFloat(itemtool1);
													
												}
												
											} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
												
												MeanMobs.logger.error("Problem getting tool attack damage: " + e);
												flag = false;
												
											}
		                                	
		                                }
		                                
		                            }
		                            else
		                            {
		                                flag = false;
		                            }
		                        }
		                        else if (itemstack.getItem() instanceof ItemArmor && !(itemstack1.getItem() instanceof ItemArmor))
		                        {
		                            flag = true;
		                        }
		                        else if (itemstack.getItem() instanceof ItemArmor && itemstack1.getItem() instanceof ItemArmor && !EnchantmentHelper.hasBindingCurse(itemstack1))
		                        {
		                            ItemArmor itemarmor = (ItemArmor)itemstack.getItem();
		                            ItemArmor itemarmor1 = (ItemArmor)itemstack1.getItem();

		                            if (itemarmor.damageReduceAmount == itemarmor1.damageReduceAmount)
		                            {
		                                flag = itemstack.getMetadata() > itemstack1.getMetadata() || itemstack.hasTagCompound() && !itemstack1.hasTagCompound();
		                            }
		                            else
		                            {
		                                flag = itemarmor.damageReduceAmount > itemarmor1.damageReduceAmount;
		                            }
		                        }
		                        else
		                        {
		                            flag = false;
		                        }
		                    }

		                    if (flag)
		                    {
		                        double d0;
		                        float[] inventoryHandsDropChances = new float[2];
		                        float[] inventoryArmorDropChances = new float[4];
		                        
		                        try {
		            				
		            				Field inventoryHandsDropChancesField = EntityLiving.class.getDeclaredField("inventoryHandsDropChances");
		            				inventoryHandsDropChancesField.setAccessible(true);
		            				inventoryHandsDropChances = (float[]) inventoryHandsDropChancesField.get(living);
		            				
		            				Field inventoryArmorDropChancesField = EntityLiving.class.getDeclaredField("inventoryArmorDropChances");
		            				inventoryArmorDropChancesField.setAccessible(true);
		            				inventoryArmorDropChances = (float[]) inventoryArmorDropChancesField.get(living);
		            				
		            			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
		            				
		            				MeanMobs.logger.error("Problem getting drop chances: " + e);
		            				
		            			}

		                        switch (entityequipmentslot.getSlotType())
		                        {
		                            case HAND:
		                            	d0 = Array.getFloat(inventoryHandsDropChances, entityequipmentslot.getIndex());
		                                break;
		                            case ARMOR:
		                            	d0 = Array.getFloat(inventoryArmorDropChances, entityequipmentslot.getIndex());
		                                break;
		                            default:
		                                d0 = 0.0D;
		                        }

		                        if (!itemstack1.isEmpty() && (double)(living.getRNG().nextFloat() - 0.1F) < d0)
		                        {
		                            living.entityDropItem(itemstack1, 0.0F);
		                        }

		                        living.setItemStackToSlot(entityequipmentslot, itemstack);

		                        living.setDropChance(entityequipmentslot, 2.0F);

		                        living.enablePersistence();
		                        living.onItemPickup(entityitem, itemstack.getCount());
		                        entityitem.setDead();
		                    }
		                }
		            }
		        }
				
			}
			
		}
		
		world.profiler.endSection();
		
	}
	
}
