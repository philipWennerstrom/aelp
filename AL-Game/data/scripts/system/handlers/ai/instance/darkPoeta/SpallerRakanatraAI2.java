package ai.instance.darkPoeta;

import java.util.List;
import java.util.concurrent.Future;

import org.joda.time.Duration;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.handler.TalkEventHandler;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.SkillData;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * @author Ritsu
 */

@AIName("spaller_rakanatra")
public class SpallerRakanatraAI2 extends AggressiveNpcAI2
{
	private long paralyzedAt;
	private boolean isParalyzed = false;
	private int paralyzedHits = 0;
	private Future<?> skillTask;
	private Future<?> skill2Task;
	
	@Override
	protected void handleAttack(Creature creature)
	{
		super.handleAttack(creature);
		checkDirection();
	}

	private void checkDirection()
	{
		List<Npc> npcs = getPosition().getWorldMapInstance().getNpcs(281178);
		//SkillData data = new SkillData();
		SkillTemplate paralyze = DataManager.SKILL_DATA.getSkillTemplate(8256);
		if(npcs != null)
		{
			for (Npc npc : npcs) 
			{
				int npcId = npc.getNpcId();
				double distance = MathUtil.getDistance(getOwner(), npc);
				if(distance <= 3.5)
				{
					if(isParalyzed) {
						long paralyzedDuration =  new Duration(paralyzedAt, System.currentTimeMillis()).getMillis();
						if(paralyzedDuration>=5000) {
							isParalyzed = false;
							paralyzedHits = 0;
							npc.getSpawn().getSpawnGroup().setRespawnTime(10000);
							npc.getController().onDie(this.getOwner());
							break;
						}
					}
					TalkEventHandler.onTalk(this, npc);
					applyParalyzeEffect(paralyze);
					skillTask = ThreadPoolManager.getInstance().schedule(new Runnable()
					{

						@Override
						public void run() 
						{
							SkillEngine.getInstance().getSkill(getOwner(), 18534, 50, getOwner()).useSkill();
							/**skillTask = ThreadPoolManager.getInstance().schedule(new Runnable()
							{

								@Override
								public void run() 
								{
									SkillEngine.getInstance().getSkill(getOwner(), 18574, 50, getOwner()).useSkill();
								}
							}, 3000);**/
						}
					},5000);//28000
				}
			}
		}
	}

	private void applyParalyzeEffect(SkillTemplate paralyze) {
		AI2Actions.applyEffect(this, paralyze, getOwner());
		getOwner().getEffectController().setAbnormal(4);
		getOwner().getController().cancelCurrentSkill();
		getOwner().getMoveController().abortMove();
		getOwner().getEffectController().setAbnormal(AbnormalState.PARALYZE.getId());
		isParalyzed = true;
		if (paralyzedHits == 0) {
			paralyzedAt = System.currentTimeMillis();
		}
		paralyzedHits++;
	}

	private void cancelTask()
	{
		if(skillTask != null && !skillTask.isDone())
			skillTask.cancel(true);
		else if(skill2Task != null && !skill2Task.isDone())
			skill2Task.cancel(true);
	}

	@Override
	protected void handleBackHome()
	{
		cancelTask();
		super.handleBackHome();
	}

	@Override
	protected void handleDespawned()
	{
		cancelTask();
		super.handleDespawned();
	}

	@Override
	protected void handleDied() 
	{
		cancelTask();
		super.handleDied();
	}
}