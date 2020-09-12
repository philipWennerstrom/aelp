package com.aionemu.gameserver.fix.npc.ai;

import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.stats.NpcStatsTemplate;

public class NpcStatsFlix {
	private static final String AGGRESSIVE = "aggressive";

	public static void fixNpcStats(NpcTemplate npcTemplate) {
		if (hasLevelForFixes(npcTemplate)) {
			if (has1kMainAttack(npcTemplate)) {
				decreaseMainHandBy(npcTemplate, 100);
			}
		}
	}

	/**
	 * @param npcTemplate
	 */
	private static void decreaseMainHandBy(NpcTemplate npcTemplate, int valueToDecrease) {
		int newHandAttack = getMainHandAttack(npcTemplate) - valueToDecrease;
		getStatsTemplate(npcTemplate).setMainHandAttack(newHandAttack);
	}

	/**
	 * @param npcTemplate
	 * @return
	 */
	private static NpcStatsTemplate getStatsTemplate(NpcTemplate npcTemplate) {
		return npcTemplate.getStatsTemplate();
	}

	/**
	 * @param npcTemplate
	 * @return
	 */
	private static boolean hasLevelForFixes(NpcTemplate npcTemplate) {
		return npcTemplate.getLevel() >= 38 && npcTemplate.getAi().equals(AGGRESSIVE);
	}

	/**
	 * @param npcTemplate
	 * @return
	 */
	private static boolean has1kMainAttack(NpcTemplate npcTemplate) {
		return getMainHandAttack(npcTemplate) >= 1000;
	}

	/**
	 * @param npcTemplate
	 * @return
	 */
	private static int getMainHandAttack(NpcTemplate npcTemplate) {
		return getStatsTemplate(npcTemplate).getMainHandAttack();
	}
}
