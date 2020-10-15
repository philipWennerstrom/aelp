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

public class HeroAndLegendaryFixes {
	private static final Logger log = LoggerFactory.getLogger(VisibleObjectSpawner.class);

	private HeroAndLegendaryFixes() {
		super();
	}

	public static void fixDrops(SpawnTemplate spawn, NpcTemplate npcTemplate) {
		NpcRating npcRating = npcTemplate.getRating();
		byte level = npcTemplate.getLevel();
		switch (npcRating) {
		case HERO:
		case LEGENDARY:
			fixBoss(spawn, npcTemplate, npcRating, level);
			break;
		default:
			fixNamedKey(spawn, npcTemplate, npcRating);
			break;
		}
	}

	/**
	 * @param spawn
	 * @param npcTemplate
	 * @param npcRating
	 * @param level
	 */
	private static void fixBoss(SpawnTemplate spawn, NpcTemplate npcTemplate, NpcRating npcRating, byte level) {
		fixGostones(spawn, npcTemplate, npcRating);
		addEchantStone(spawn, npcTemplate, npcRating);
		fixNamedKey(spawn, npcTemplate, npcRating);
		StringBuilder sb = new StringBuilder("Npc Name: ");
		sb.append(npcTemplate.getName());
		sb.append(" | Level: ");
		sb.append(level);
		sb.append(" | Rating: ");
		sb.append(npcRating.name());
		sb.append(" | local: ");
		sb.append(spawn.getWorldId());
		
		log.info(sb.toString());
	}

	private static void fixGostones(SpawnTemplate spawn, NpcTemplate npcTemplate, NpcRating npcRating) {
		NpcDrop npcDrop = npcTemplate.getNpcDrop();
		if(npcDrop!=null) {
			addGodstoneDrops(new ArrayList<Drop>(), npcDrop);
		}
	}
	
	private static void fixNamedKey(SpawnTemplate spawn, NpcTemplate npcTemplate, NpcRating npcRating) {
		NpcDrop npcDrop = npcTemplate.getNpcDrop();
		if(npcDrop!=null) {
			for(DropGroup dg:npcDrop.getDropGroup()) {
				if(dg.getGroupName().equals("NAMED_KEY_ALONE")) {
					for(Drop keyDrop:dg.getDrop()) {
						NpcDropsFix.setChance(keyDrop, 99f, 120f);
					}
				}
			}
			
		}
	}
	
	private static void addEchantStone(SpawnTemplate spawn, NpcTemplate npcTemplate, NpcRating npcRating) {
		NpcDrop npcDrop = npcTemplate.getNpcDrop();
		if(npcDrop!=null) {
			for(DropGroup dg:npcDrop.getDropGroup()) {
				if(dg.getGroupName().equals("POWER_SHARDS")) {
					//adiciona enchant stones
					addEnchant(dg, 166000080);
					addEnchant(dg, 166000081);
					addEnchant(dg, 166000082);
					addEnchant(dg, 166000083);
					addEnchant(dg, 166000084);
					addEnchant(dg, 166000085);
					addEnchant(dg, 166000086);
					addEnchant(dg, 166000087);
					addEnchant(dg, 166000088);
					addEnchant(dg, 166000088);
					addEnchant(dg, 166000090);
					addEnchant(dg, 166000091);
					addEnchant(dg, 166000092);
					addEnchant(dg, 166000093);
					addEnchant(dg, 166000094);
					addEnchant(dg, 166000095);
				}
			}
		}
	}

	private static void addEnchant(DropGroup dg, int itemId) {
		Drop nde1 = new Drop(itemId, 1, 4, 0.1f, false);
		NpcDropsFix.setChance(nde1, 1.4f, 3.1f);
		dg.getDrop().add(nde1);
	}

	private static void addGodstoneDrops(List<Drop> godstoneDropList, NpcDrop npcDrop) {
		DropGroup ddgp = new DropGroup(godstoneDropList, Race.PC_ALL, true, NpcDropData.GODSTONES);
		for (Map.Entry<Integer,Drop> pair : NpcDropData.godstoneDrops.entrySet()) {
		    Drop gdrop = pair.getValue();
			NpcDropsFix.setChance(gdrop, 1.1f, 2.7f);
		    godstoneDropList.add(gdrop);
		}
		List<DropGroup> dropGroup = npcDrop.getDropGroup();
		if(!dropGroup.isEmpty()) {
			dropGroup.add(ddgp);
		}
	}
}
