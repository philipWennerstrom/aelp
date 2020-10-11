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
	
	public static void fixDrops(SpawnTemplate spawn, NpcTemplate npcTemplate) {
		NpcRating npcRating = npcTemplate.getRating();
		switch (npcRating) {
		case HERO:
		case LEGENDARY:
			fix(spawn, npcTemplate, npcRating);
			addEchantStone(spawn, npcTemplate, npcRating);
			break;

		default:
			break;
		}
	}

	private static void fix(SpawnTemplate spawn, NpcTemplate npcTemplate, NpcRating npcRating) {
		byte level = npcTemplate.getLevel();
		NpcDrop npcDrop = npcTemplate.getNpcDrop();
		if(npcDrop!=null) {
			addGodstoneDrops(new ArrayList<Drop>(), npcDrop);
			log.info("Npc Name: "+ npcTemplate.getName()+ " | Level: "+level+ " | Rating: "+ npcRating.name() + " | local: "+spawn.getWorldId());
		}
	}
	
	private static void addEchantStone(SpawnTemplate spawn, NpcTemplate npcTemplate, NpcRating npcRating) {
		byte level = npcTemplate.getLevel();
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
			log.info("Npc Name: "+ npcTemplate.getName()+ " | Level: "+level+ " | Rating: "+ npcRating.name() + " | local: "+spawn.getWorldId());
		}
	}

	private static void addEnchant(DropGroup dg, int itemId) {
		Drop nde1 = new Drop(itemId, 1, 3, 0.1f, false);
		NpcDropsFix.setChance(nde1, 0.4f, 0.9f);
		dg.getDrop().add(nde1);
	}

	private static void addGodstoneDrops(List<Drop> godstoneDropList, NpcDrop npcDrop) {
		DropGroup ddgp = new DropGroup(godstoneDropList, Race.PC_ALL, true, NpcDropData.GODSTONES);
		for (Map.Entry<Integer,Drop> pair : NpcDropData.godstoneDrops.entrySet()) {
		    Drop gdrop = pair.getValue();
			NpcDropsFix.setChance(gdrop, 0.4f, 0.9f);
		    godstoneDropList.add(gdrop);
		}
		List<DropGroup> dropGroup = npcDrop.getDropGroup();
		if( dropGroup.size()!=0) {
			dropGroup.add(ddgp);
		}
	}
}
