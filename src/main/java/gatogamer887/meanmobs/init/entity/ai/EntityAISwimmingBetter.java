package gatogamer887.meanmobs.init.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNavigateGround;

public class EntityAISwimmingBetter extends EntityAIBase
{
    private final EntityLiving entity;

    public EntityAISwimmingBetter(EntityLiving entityIn)
    {
        this.entity = entityIn;
        this.setMutexBits(4);

        if (entityIn.getNavigator() instanceof PathNavigateGround)
        {
            ((PathNavigateGround)entityIn.getNavigator()).setCanSwim(true);
        }
        else if (entityIn.getNavigator() instanceof PathNavigateFlying)
        {
            ((PathNavigateFlying)entityIn.getNavigator()).setCanFloat(true);
        }
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        return (this.entity.isInWater() || this.entity.isInLava()) && (this.entity.getAttackTarget() != null && this.entity.getAir() > 100 ? this.entity.posY <= this.entity.getAttackTarget().posY - 1.0D : true);
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void updateTask()
    {
        if (this.entity.getRNG().nextFloat() < 0.8F)
        {
            this.entity.getJumpHelper().setJumping();
        }
    }
}