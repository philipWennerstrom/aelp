package com.aionemu.gameserver.fix.npc.ai;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;

public class AggroFix {
	
	public static void fixAggroFromJunkToElite(NpcTemplate npcTemplate) {
		switch (npcTemplate.getRating()) {
		case JUNK:
		case NORMAL:
		case ELITE:
			setAggroValue(npcTemplate, 10, 14);
			break;

		default:
			break;
		}
	}

	/**
	 * @param npcTemplate
	 */
	private static void setAggroValue(NpcTemplate npcTemplate, int actualRange, int newRange) {
		if (npcTemplate.getAggroRange() <= actualRange) {
			npcTemplate.setAggroRange(Rnd.get(12,14));
		}
	}
}
