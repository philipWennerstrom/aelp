package com.aionemu.gameserver.fix;

import org.joda.time.Duration;

import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.controllers.movement.NpcMoveController;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.Util;

public class AttackEventBugFix {

	public static void checkGeoDataBug(NpcAI2 npcAI, Creature attacker) {
		Creature attacked = npcAI.getOwner();
		VisibleObject target = attacked.getTarget();

		if (target instanceof Player) {
			playerTarget(npcAI, target);
		}

	}

	/**
	 * @param npcAI
	 * @param attacked
	 * @param target
	 */
	public static void playerTarget(NpcAI2 npcAI, VisibleObject target) {
		Creature attacked = npcAI.getOwner();
		Player player = (Player) target;
		if (attacked instanceof Npc) {
			Npc nAttacked = ((Npc) attacked);
			if (isFreezing(player, nAttacked)) {
				giveUpOnTarget(npcAI, attacked, player);
			}
		}
	}

	/**
	 * Verifica quanto tempo o npc esta parado e o target nao recebe um ataque
	 * @param attackedAtFromNow
	 * @param secFromLastNpcMove
	 * @return
	 */
	private static boolean isFreezing(Player target, Npc npc) {
	   long attackedAtFromNow =  new Duration(target.getController().getLastAttackedTime(), System.currentTimeMillis()).getStandardSeconds();
	   long lastPlayerAttack =  new Duration(target.getController().getLastAttackTime(), System.currentTimeMillis()).getMillis();
	   NpcMoveController moveController = npc.getMoveController();
	   long secFromLastNpcMove =  new Duration(moveController.getLastMoveUpdate(), System.currentTimeMillis()).getMillis();
	   long lastNpcAttack =  new Duration(npc.getLastAttackAt(),System.currentTimeMillis()).getMillis();
	   
		if (lastNpcAttack > 2000 && lastPlayerAttack > 2250) {
			if (npc.getAi2().getSubState() != AISubState.CAST) {
				return secFromLastNpcMove > 2500 && attackedAtFromNow > 2;
			}
		}
	  return false;
	}

	/**
	 * Envia o comando para AI desistir do Target
	 * 
	 * @param npcAI
	 * @param attacked
	 * @param player
	 */
	private static void giveUpOnTarget(NpcAI2 npcAI, Creature attacked, Player player) {
		npcAI.onGeneralEvent(AIEventType.TARGET_GIVEUP);
		Util.printSection("Fixing Geo bug exploit - Player: " + player.getAcountName() + " < -- > " + " Npc: " + attacked.getName());
	}
}