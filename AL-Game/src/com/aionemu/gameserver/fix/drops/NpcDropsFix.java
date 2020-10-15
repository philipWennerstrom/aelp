package com.aionemu.gameserver.fix.drops;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.drop.Drop;
import com.aionemu.gameserver.model.drop.DropGroup;
import com.aionemu.gameserver.model.drop.NpcDrop;

public class NpcDropsFix {
	private static final String MANASTONE_RARE = "MANASTONE_RARE";
	private static final String ARMOR_RARE = "ARMOR_RARE";
	private static final String WEAPON_RARE = "WEAPON_RARE";
	private static final String ACCESSORY_COMMON = "ACCESSORY_COMMON";
	private static final String ARMOR_COMMON = "ARMOR_COMMON";
	private static final String MANASTONE_COMMON = "MANASTONE_COMMON";
	private static final String FLUX_RARE = "FLUX_RARE";
	private static final String ARMOR_LEGEND = "ARMOR_LEGEND";
	private static final String ACCESSORY_RARE = "ACCESSORY_RARE";
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
			generalItens(dropGroup);
			
			godstones(dropGroup);
			
			uniqueAmorAndWeapons(dropGroup);
			legendAmorAndWeapons(dropGroup);
			
			namedLegendAmorAndWeapons(dropGroup);
			namedUniqueAmorAndWeapons(dropGroup);
		}
	}

	private static void generalItens(DropGroup dropGroup) {
		accessoryRare(dropGroup);
		fluxRare(dropGroup);
		manastoneCommon(dropGroup);
		armorCommon(dropGroup);
		rareItens(dropGroup);
	}

	private static void godstones(DropGroup dropGroup) {
		if (dropGroup.getGroupName().equals(GODSTONES)) {
			/**ArrayList<Drop> drop = new ArrayList<Drop>();
			drop.add(new Drop(141000001, 100, 200, 1.7f, false));
			dropGroup.setDrop(drop);**/
			for (Drop dropIndex : dropGroup.getDrop()) {
				//setChance(dropIndex, 0.00001f, 0.0001f);
				dropIndex.setChance(0.001f);
			}
		}
	}
	
	private static void armorCommon(DropGroup dropGroup) {
		if (dropGroup.getGroupName().equals(ARMOR_COMMON)||
			dropGroup.getGroupName().equals(ACCESSORY_COMMON)) {
			for (Drop dropIndex : dropGroup.getDrop()) {
				setChance(dropIndex, 1f, 3f);
			}
		}
	}
	
	private static void rareItens(DropGroup dropGroup) {
		if (dropGroup.getGroupName().equals(WEAPON_RARE)||
			dropGroup.getGroupName().equals(ARMOR_RARE)||
			dropGroup.getGroupName().equals(MANASTONE_RARE)) {
			for (Drop dropIndex : dropGroup.getDrop()) {
				setChance(dropIndex, 1f, 3f);
			}
		}
	}
	
	private static void accessoryRare(DropGroup dropGroup) {
		if (dropGroup.getGroupName().equals(ACCESSORY_RARE)) {
			for (Drop dropIndex : dropGroup.getDrop()) {
				setChance(dropIndex, 0.7f, 1.2f);
			}
		}
	}
	
	private static void manastoneCommon(DropGroup dropGroup) {
		if (dropGroup.getGroupName().equals(MANASTONE_COMMON)) {
			for (Drop dropIndex : dropGroup.getDrop()) {
				setChance(dropIndex, 0.8f, 2.5f);
			}
		}
	}
	
	private static void fluxRare(DropGroup dropGroup) {
		if (dropGroup.getGroupName().equals(FLUX_RARE)) {
			for (Drop dropIndex : dropGroup.getDrop()) {
				setChance(dropIndex, 0.9f, 2.2f);
			}
		}
	}

	public static void setChance(Drop dropIndex, float minRate, float maxRate) {
		Random r = new Random();
		float random = (float) (minRate + r.nextFloat() * (maxRate - minRate));
		BigDecimal bd = new BigDecimal(random).setScale(2, RoundingMode.DOWN);
		float originalFloat = bd.floatValue();
		float floatValue = originalFloat * CustomConfig.DOUBLE_XP;
		dropIndex.setChance(floatValue);
	}

	private static void uniqueAmorAndWeapons(DropGroup dropGroup) {
		if(dropGroup.getGroupName().equals(ARMOR_UNIQUE)||
		   dropGroup.getGroupName().equals(WEAPON_UNIQUE)) {
			for(Drop dropIndex: dropGroup.getDrop()) {
					setChance(dropIndex, 0.01f, 0.3f);
			}
		}
	}
	
	private static void legendAmorAndWeapons(DropGroup dropGroup) {
		if(dropGroup.getGroupName().equals(WEAPON_LEGEND)||
		   dropGroup.getGroupName().equals(ARMOR_LEGEND)||
		   dropGroup.getGroupName().equals(ACCESSORY_LEGEND)) {
			for(Drop dropIndex: dropGroup.getDrop()) {
					setChance(dropIndex, 0.25f, 0.7f);
			}
		}
	}
	
	private static void namedLegendAmorAndWeapons(DropGroup dropGroup) {
		if(dropGroup.getGroupName().equals(NAMED_LEGEND_ARMOR)||
		   dropGroup.getGroupName().equals(NAMED_LEGEND_WEAPON)) {
			for(Drop dropIndex: dropGroup.getDrop()) {
					setChance(dropIndex, 2.0f, 3.5f);
			}
		}
	}
	
	private static void namedUniqueAmorAndWeapons(DropGroup dropGroup) {
		if(dropGroup.getGroupName().equals(NAMED_UNIQUE_SHIELD)||
		   dropGroup.getGroupName().equals(NAMED_UNIQUE_ACCESSORY)||
		   dropGroup.getGroupName().equals(NAMED_UNIQUE_ARMOR)||
		   dropGroup.getGroupName().equals(NAMED_UNIQUE_WEAPON)) {
			
			for(Drop dropIndex: dropGroup.getDrop()) {
					setChance(dropIndex, 1.6f, 2.3f);
			}
		}
	}
}