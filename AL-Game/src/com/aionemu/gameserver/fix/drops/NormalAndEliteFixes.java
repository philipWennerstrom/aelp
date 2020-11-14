package com.aionemu.gameserver.fix.drops;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.NpcDropData;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.drop.Drop;
import com.aionemu.gameserver.model.drop.DropGroup;
import com.aionemu.gameserver.model.drop.NpcDrop;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.npc.NpcRating;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.npc.NpcTemplateType;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.spawnengine.VisibleObjectSpawner;

public class NormalAndEliteFixes {
	private static final Logger log = LoggerFactory.getLogger(VisibleObjectSpawner.class);

	private NormalAndEliteFixes() {
		super();
	}

	public static void fixDrops(SpawnTemplate spawn, Npc npc) {
		NpcRating npcRating = npc.getObjectTemplate().getRating();
		byte level = npc.getObjectTemplate().getLevel();
		switch (npcRating) {
		case NORMAL:
		case ELITE:
			fix(spawn, npc, npcRating, level);
			farmArea(spawn, npc);
			break;
		default:
			break;
		}
	}

	private static void farmArea(SpawnTemplate spawn, Npc npc) {
		if(spawn.getWorldId()==220040000 && npc.getObjectTemplate().getNpcTemplateType()==NpcTemplateType.ABYSS_GUARD && npc.getObjectTemplate().getRace()==Race.DRAKAN) {
			NpcTemplate nt = new NpcTemplate(npc.getObjectTemplate());
			npc.setObjectTemplate(nt);
			NpcDrop npcDrop = nt.getNpcDrop();
			for(DropGroup dg:npcDrop.getDropGroup()) {
				isBalicGroup(dg, nt);
				isBalaurGroup(dg, nt);
			}
		}
	}
	
	private static void isBalaurGroup(DropGroup dg, NpcTemplate npcTemplate) {
		if(dg.getGroupName().contains("BALAUR")) {
			for(Drop drop:dg.getDrop()) {
				log.info("++ Drop Name: "+ drop.getItemTemplate().getName());
				drop.setChance(5f);
			}
		}
	}

	private static void isBalicGroup(DropGroup dg, NpcTemplate npcTemplate) {
		if(dg.getGroupName().contains("BALIC")) {
			for(Drop drop:dg.getDrop()) {
				log.info("++ Drop Name: "+ drop.getItemTemplate().getName());
				if(npcTemplate.getRating()==NpcRating.ELITE) {
					drop.setChance(5f);
				}else {
					drop.setChance(2.7f);
				}
			}
		}
	}

	/**
	 * @param spawn
	 * @param npcTemplate
	 * @param npcRating
	 * @param level
	 */
	private static void fix(SpawnTemplate spawn, Npc npc, NpcRating npcRating, byte level) {
		fixGostones(spawn, npc.getObjectTemplate(), npcRating);
		StringBuilder sb = new StringBuilder("Npc Name: ");
		sb.append(npc.getObjectTemplate().getName());
		sb.append(" | Level: ");
		sb.append(level);
		sb.append(" | Rating: ");
		sb.append(npcRating.name());
		sb.append(" | local: ");
		sb.append(spawn.getWorldId());
		
		//log.info(sb.toString());
	}

	private static void fixGostones(SpawnTemplate spawn, NpcTemplate npcTemplate, NpcRating npcRating) {
		NpcDrop npcDrop = npcTemplate.getNpcDrop();
		if(npcDrop!=null) {
			fixNormalGosdstoneRate(npcDrop);
		}
	}

	private static void fixNormalGosdstoneRate(NpcDrop npcDrop) {
		for(DropGroup dg: npcDrop.getDropGroup()) {
			if(dg.getGroupName().equals(NpcDropData.GODSTONES)) {
				for(Drop normalDrop: dg.getDrop()) {
					normalDrop.setChance(0.001f);
				}
			}
		}
	}
}
