package gatogamer887.meanmobs.init.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIDoorInteract;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.EnumDifficulty;

public class EntityAIBreakDoorBetter extends EntityAIDoorInteractBetter
{
    private int breakingTime;
    private int previousBreakProgress = -1;

    public EntityAIBreakDoorBetter(EntityLiving entityIn)
    {
        super(entityIn);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!super.shouldExecute())
        {
            return false;
        }
        else if (!this.entity.world.getGameRules().getBoolean("mobGriefing") || !this.entity.world.getBlockState(this.doorPosition).getBlock().canEntityDestroy(this.entity.world.getBlockState(this.doorPosition), this.entity.world, this.doorPosition, this.entity) || !net.minecraftforge.event.ForgeEventFactory.onEntityDestroyBlock(this.entity, this.doorPosition, this.entity.world.getBlockState(this.doorPosition)))
        {
            return false;
        }
        else
        {
            BlockDoor blockdoor = this.doorBlock;
            return !BlockDoor.isOpen(this.entity.world, this.doorPosition);
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        super.startExecuting();
        this.breakingTime = 0;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
        double d0 = this.entity.getDistanceSq(this.doorPosition);
        boolean flag;

        if (this.breakingTime <= 240 && this.entity.world.getBlockState(this.doorPosition).getBlock() instanceof BlockDoor)
        {
            BlockDoor blockdoor = this.doorBlock;

            if (!BlockDoor.isOpen(this.entity.world, this.doorPosition) && d0 < 4.0D)
            {
                flag = true;
                return flag;
            }
        }

        flag = false;
        return flag;
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask()
    {
        super.resetTask();
        this.entity.world.sendBlockBreakProgress(this.entity.getEntityId(), this.doorPosition, -1);
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void updateTask()
    {
        super.updateTask();
        IBlockState state = this.entity.world.getBlockState(this.doorPosition);
        Item item = this.entity.getHeldItem(EnumHand.MAIN_HAND).getItem();

        if (state.getMaterial() == Material.WOOD)
        {
        	this.breakingTime += WoodBreakUtils.getWoodBreakAmount(item, this.entity.getRNG());
        }
        else if (state.getMaterial() == Material.IRON)
        {
        	this.breakingTime += (item instanceof ItemPickaxe ? 1 + ToolMaterial.valueOf(((ItemPickaxe)item).getToolMaterialName()).getEfficiency() / 6 : 1);
        }

        int i = (int)((float)this.breakingTime / 240.0F * 10.0F);

        if (i != this.previousBreakProgress)
        {
            if (state.getMaterial() == Material.WOOD)
            {
            	this.entity.world.sendBlockBreakProgress(this.entity.getEntityId(), this.doorPosition, i);
                this.entity.world.playEvent(1019, this.doorPosition, 0);
            }
            else if (state.getMaterial() == Material.IRON && !(item instanceof ItemPickaxe))
            {
                this.entity.world.playEvent(1020, this.doorPosition, 0);
                this.breakingTime = 0;
                i = 0;
            }
            else if (state.getMaterial() == Material.IRON && item instanceof ItemPickaxe)
            {
            	this.entity.world.sendBlockBreakProgress(this.entity.getEntityId(), this.doorPosition, i);
                this.entity.world.playEvent(1020, this.doorPosition, 0);
            }

            this.entity.swingArm(EnumHand.MAIN_HAND);
            this.entity.getHeldItem(EnumHand.MAIN_HAND).damageItem(1, this.entity);
            this.previousBreakProgress = i;
        }

        if (this.breakingTime >= 240)
        {
        	if (state.getMaterial() == Material.WOOD)
        	{
        		this.entity.world.setBlockToAir(this.doorPosition.down());
                this.entity.world.setBlockToAir(this.doorPosition);
                this.entity.world.playEvent(1021, this.doorPosition, 0);
                this.entity.world.playEvent(2001, this.doorPosition, Block.getIdFromBlock(this.doorBlock));
        	}
        	else if (state.getMaterial() == Material.IRON && item instanceof ItemPickaxe)
        	{
        		this.entity.world.setBlockToAir(this.doorPosition.down());
                this.entity.world.setBlockToAir(this.doorPosition);
                this.entity.world.playSound(this.doorPosition.getX() + 0.5D, this.doorPosition.getY() + 0.5D, this.doorPosition.getZ() + 0.5D, SoundEvents.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, SoundCategory.HOSTILE, 2.0F, (this.entity.getRNG().nextFloat() - this.entity.getRNG().nextFloat()) * 0.2F + 1.0F, false);
                this.entity.world.playEvent(2001, this.doorPosition, Block.getIdFromBlock(this.doorBlock));
        	}
        }
    }
}