package gatogamer887.meanmobs.init.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntitySpider;

public class SpiderAISpiderTarget<T extends EntityLivingBase> extends EntityAINearestAttackableTarget<T>
{
    public SpiderAISpiderTarget(EntitySpider spider, Class<T> classTarget)
    {
        super(spider, classTarget, true);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        float f = this.taskOwner.getBrightness();
        return f >= 0.5F ? false : super.shouldExecute();
    }
}