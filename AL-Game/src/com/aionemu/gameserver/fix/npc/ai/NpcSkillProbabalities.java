package com.aionemu.gameserver.fix.npc.ai;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.templates.npcskill.NpcSkillTemplate;
import com.aionemu.gameserver.model.templates.npcskill.NpcSkillTemplates;

public class NpcSkillProbabalities {
 public static void calc (NpcSkillTemplates npcSkillList) {
	 if(npcSkillList.getNpcSkills().size()>=3) {
			for(NpcSkillTemplate nst: npcSkillList.getNpcSkills()) {
				nst.setProbability(Rnd.get(25, 55));
			}
		}
 }
}
