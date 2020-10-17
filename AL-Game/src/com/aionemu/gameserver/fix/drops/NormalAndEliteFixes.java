package com.aionemu.gameserver.fix.drops;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.NpcDropData;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.drop.Drop;
import com.aionemu.gameserver.model.drop.DropGroup;
import com.aionemu.gameserver.model.drop.NpcDrop;
import com.aionemu.gameserver.model.templates.npc.NpcRating;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.spawnengine.VisibleObjectSpawner;

public class NormalAndEliteFixes {
	private static final Logger log = LoggerFactory.getLogger(VisibleObjectSpawner.class);

	private NormalAndEliteFixes() {
		super();
	}

	public static void fixDrops(SpawnTemplate spawn, NpcTemplate npcTemplate) {
		NpcRating npcRating = npcTemplate.getRating();
		byte level = npcTemplate.getLevel();
		switch (npcRating) {
		case NORMAL:
		case ELITE:
			fix(spawn, npcTemplate, npcRating, level);
			break;
		default:
			break;
		}
	}

	/**
	 * @param spawn
	 * @param npcTemplate
	 * @param npcRating
	 * @param level
	 */
	private static void fix(SpawnTemplate spawn, NpcTemplate npcTemplate, NpcRating npcRating, byte level) {
		fixGostones(spawn, npcTemplate, npcRating);
		StringBuilder sb = new StringBuilder("Npc Name: ");
		sb.append(npcTemplate.getName());
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
