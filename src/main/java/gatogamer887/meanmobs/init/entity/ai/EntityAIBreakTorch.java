package gatogamer887.meanmobs.init.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityAIBreakTorch extends EntityAIMoveToBlock
{
    /** Entity that is breaking */
    private final EntityCreature entity;
    private int up;

    public EntityAIBreakTorch(EntityCreature entityIn, double speedIn, int radius)
    {
        super(entityIn, speedIn, radius);
        this.entity = entityIn;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.runDelay <= 0)
        {
            if (!this.entity.world.getGameRules().getBoolean("mobGriefing"))
            {
                return false;
            }
        }

        return super.shouldExecute();
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
        return super.shouldContinueExecuting();
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void updateTask()
    {
        super.updateTask();
        this.entity.getLookHelper().setLookPosition((double)this.destinationBlock.getX() + 0.5D, (double)(this.destinationBlock.getY() + this.up + 1), (double)this.destinationBlock.getZ() + 0.5D, 10.0F, (float)this.entity.getVerticalFaceSpeed());

        if (this.getIsAboveDestination())
        {
            World world = this.entity.world;
            BlockPos blockpos = this.destinationBlock.up();

            for (int i = 0; i <= 3; i++)
            {
            	Block block = world.getBlockState(blockpos.up(i)).getBlock();

            	if (block instanceof BlockTorch)
                {
                	world.destroyBlock(blockpos.up(i), false);
                    this.entity.swingArm(EnumHand.MAIN_HAND);
                }
            }

            this.runDelay = 10;
        }
    }

    /**
     * Return true to set given position as destination
     */
    protected boolean shouldMoveTo(World worldIn, BlockPos pos)
    {
        for (int i = 0; i <= 3; i++)
        {
        	Block block = worldIn.getBlockState(pos.up(i + 1)).getBlock();

        	if (block instanceof BlockTorch && this.entity.world.rayTraceBlocks(new Vec3d(this.entity.posX, this.entity.posY + (double)this.entity.getEyeHeight(), this.entity.posZ), new Vec3d(pos.up(i + 1)), false, true, false) == null)
            {
            	this.up = i;
            	return true;
            }
        }

        return false;
    }
}