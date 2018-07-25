package gatogamer887.meanmobs.init.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIDoorInteract;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemAxe;
import net.minecraft.util.EnumHand;
import net.minecraft.world.EnumDifficulty;

public class EntityAIBreakFenceGate extends EntityAIFenceGateInteract
{
    private int breakingTime;
    private int previousBreakProgress = -1;

    public EntityAIBreakFenceGate(EntityLiving entityIn)
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
            BlockFenceGate blockdoor = this.doorBlock;
            return !this.entity.world.getBlockState(this.doorPosition).getValue(BlockFenceGate.OPEN);
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

        if (this.breakingTime <= 240 && this.entity.world.getBlockState(this.doorPosition).getBlock() instanceof BlockFenceGate)
        {
            BlockFenceGate blockdoor = this.doorBlock;

            if (!this.entity.world.getBlockState(this.doorPosition).getValue(BlockFenceGate.OPEN) && d0 < 4.0D)
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
        Item item = this.entity.getHeldItem(EnumHand.MAIN_HAND).getItem();

        //this.entity.getLookHelper().setLookPosition((double)this.doorPosition.getX() + 0.5D, (double)(this.doorPosition.getY() + 1), (double)this.doorPosition.getZ() + 0.5D, 10.0F, (float)this.entity.getVerticalFaceSpeed());

        this.breakingTime += WoodBreakUtils.getWoodBreakAmount(item, this.entity.getRNG());
        int i = (int)((float)this.breakingTime / 240.0F * 10.0F);

        if (i != this.previousBreakProgress)
        {
            this.entity.world.sendBlockBreakProgress(this.entity.getEntityId(), this.doorPosition, i);
            this.entity.world.playEvent(1019, this.doorPosition, 0);
            this.entity.swingArm(EnumHand.MAIN_HAND);
            this.entity.getHeldItem(EnumHand.MAIN_HAND).damageItem(1, this.entity);
            this.previousBreakProgress = i;
        }

        if (this.breakingTime >= 240)
        {
            this.entity.world.setBlockToAir(this.doorPosition);
            this.entity.world.playEvent(1021, this.doorPosition, 0);
            this.entity.world.playEvent(2001, this.doorPosition, Block.getIdFromBlock(this.doorBlock));
        }
    }
}