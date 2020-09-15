package com.aionemu.gameserver.fix.drops;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

import com.aionemu.gameserver.model.drop.Drop;
import com.aionemu.gameserver.model.drop.DropGroup;
import com.aionemu.gameserver.model.drop.NpcDrop;

public class NpcDropsFix {
	private static final String NAMED_UNIQUE_WEAPON = "NAMED_UNIQUE_WEAPON";
	private static final String NAMED_UNIQUE_ARMOR = "NAMED_UNIQUE_ARMOR";
	private static final String NAMED_UNIQUE_ACCESSORY = "NAMED_UNIQUE_ACCESSORY";
	private static final String NAMED_UNIQUE_SHIELD = "NAMED_UNIQUE_SHIELD";
	private static final String NAMED_LEGEND_WEAPON = "NAMED_LEGEND_WEAPON";
	private static final String NAMED_LEGEND_ARMOR = "NAMED_LEGEND_ARMOR";
	private static final String ACCESSORY_LEGEND = "ACCESSORY_LEGEND";
	private static final String WEAPON_LEGEND = "WEAPON_LEGEND";
	private static final String GODSTONES = "GODSTONES";
	private static final String WEAPON_UNIQUE = "WEAPON_UNIQUE";
	private static final String ARMOR_UNIQUE = "ARMOR_UNIQUE";

	public static void fixRates(List<NpcDrop> npcDrops) {
		for (NpcDrop drop : npcDrops) {
			forEachDropGroup(drop);
		}
	}

	private static void forEachDropGroup(NpcDrop drop) {
		for(DropGroup dropGroup: drop.getDropGroup()) {
			godstones(dropGroup);
			
			uniqueAmorAndWeapons(dropGroup);
			legendAmorAndWeapons(dropGroup);
			
			namedLegendAmorAndWeapons(dropGroup);
			namedUniqueAmorAndWeapons(dropGroup);
		}
	}

	private static void godstones(DropGroup dropGroup) {
		if(dropGroup.getGroupName().equals(GODSTONES)) {
			for(Drop dropIndex: dropGroup.getDrop()) {
				if(dropIndex.getChance()>1) {
					setChance(dropIndex, 0.001f, 0.25f);
				}
			}
		}
	}

	private static void setChance(Drop dropIndex, float minRate, float maxRate) {
		Random r = new Random();
		float random = (float) (minRate + r.nextFloat() * (maxRate - minRate));
		BigDecimal bd = new BigDecimal(random).setScale(2, RoundingMode.DOWN);
		dropIndex.setChance(bd.floatValue());
	}

	private static void uniqueAmorAndWeapons(DropGroup dropGroup) {
		if(dropGroup.getGroupName().equals(ARMOR_UNIQUE)||dropGroup.getGroupName().equals(WEAPON_UNIQUE)) {
			for(Drop dropIndex: dropGroup.getDrop()) {
					setChance(dropIndex, 0.15f, 0.35f);
			}
		}
	}
	
	private static void legendAmorAndWeapons(DropGroup dropGroup) {
		if(dropGroup.getGroupName().equals(WEAPON_LEGEND)||dropGroup.getGroupName().equals(ACCESSORY_LEGEND)) {
			for(Drop dropIndex: dropGroup.getDrop()) {
					setChance(dropIndex, 0.3f, 0.65f);
			}
		}
	}
	
	private static void namedLegendAmorAndWeapons(DropGroup dropGroup) {
		if(dropGroup.getGroupName().equals(NAMED_LEGEND_ARMOR)||dropGroup.getGroupName().equals(NAMED_LEGEND_WEAPON)) {
			for(Drop dropIndex: dropGroup.getDrop()) {
					setChance(dropIndex, 0.5f, 0.8f);
			}
		}
	}
	
	private static void namedUniqueAmorAndWeapons(DropGroup dropGroup) {
		if(dropGroup.getGroupName().equals(NAMED_UNIQUE_SHIELD)||
		   dropGroup.getGroupName().equals(NAMED_UNIQUE_ACCESSORY)||
		   dropGroup.getGroupName().equals(NAMED_UNIQUE_ARMOR)||
		   dropGroup.getGroupName().equals(NAMED_UNIQUE_WEAPON)) {
			
			for(Drop dropIndex: dropGroup.getDrop()) {
					setChance(dropIndex, 0.03f, 0.35f);
			}
		}
	}
}