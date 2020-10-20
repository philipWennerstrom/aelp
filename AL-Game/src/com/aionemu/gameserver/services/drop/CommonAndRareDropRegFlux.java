package com.aionemu.gameserver.services.drop;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.aionemu.gameserver.model.drop.Drop;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.templates.item.EquipType;
import com.aionemu.gameserver.model.templates.item.ItemCategory;
import com.aionemu.gameserver.model.templates.item.ItemQuality;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

public class CommonAndRareDropRegFlux {
	private static final String ACCESSORY = "ACCESSORY";
	private static final String WEAPON = "WEAPON";
	private static final String ARMOR = "ARMOR";
	private Map<String, Integer> uniqueCategoryMap;
	private int maxItens;
	
	public CommonAndRareDropRegFlux() {
		uniqueCategoryMap = new HashMap<String, Integer>();
		uniqueCategoryMap.put(ARMOR, Integer.valueOf(0));
		uniqueCategoryMap.put(WEAPON, Integer.valueOf(0));
		uniqueCategoryMap.put(ACCESSORY, Integer.valueOf(0));
		maxItens = (Math.random() <= 0.3) ? 1 : 2;
	}
	
	public void fix(Set<DropItem> droppedItens) {
		Iterator<DropItem> iterator = droppedItens.iterator();
		while (iterator.hasNext()) {
			DropItem next = iterator.next();
			Drop dropTemplate = next.getDropTemplate();
			ItemTemplate itemTemplate = dropTemplate.getItemTemplate();
			int itemId = dropTemplate.getItemId();
			ItemQuality itemQuality = itemTemplate.getItemQuality();
			checkUniqueItens(iterator, itemTemplate, itemId, itemQuality);
		}
	}

	/**
	 * @param iterator
	 * @param itemTemplate
	 * @param itemId
	 * @param itemQuality
	 */
	private void checkUniqueItens(Iterator<DropItem> iterator, ItemTemplate itemTemplate, int itemId,ItemQuality itemQuality) {
		if (itemQuality == ItemQuality.COMMON || itemQuality == ItemQuality.RARE) {
			checkCategory(iterator, itemTemplate, itemId);
		}
	}

	/**
	 * @param iterator
	 * @param itemTemplate
	 * @param itemId
	 */
	private void checkCategory(Iterator<DropItem> iterator, ItemTemplate itemTemplate, int itemId) {
		if(itemTemplate.getCategory()!=ItemCategory.MANASTONE && 
				itemTemplate.getCategory()!=ItemCategory.FLUX &&
				itemTemplate.getCategory()!=ItemCategory.STIGMA &&
				itemTemplate.getCategory()!=ItemCategory.NONE) {
			
			check(iterator, itemId, itemTemplate.getEquipmentType());
		}
	}
	
	private void check(Iterator<DropItem> iterator, int itemId, EquipType equipType) {
		int qtd = uniqueCategoryMap.get(ARMOR).intValue();
		if (qtd >= maxItens) {
			iterator.remove();
		}
		uniqueCategoryMap.put(ARMOR, qtd+1);
	}
	
	/**
	 * @param iterator
	 * @param itemId
	 */
	private void checkUniqueArmor(Iterator<DropItem> iterator, int itemId, EquipType equipType) {
		if (equipType==EquipType.ARMOR) {
			int qtd = uniqueCategoryMap.get(ARMOR).intValue();
			if (qtd >= maxItens) {
				iterator.remove();
			}
			uniqueCategoryMap.put(ARMOR, qtd+1);
		}
	}
	
	private void checkUniqueWeapon(Iterator<DropItem> iterator, int itemId, EquipType equipType) {
		if (equipType==EquipType.WEAPON) {
			int qtd = uniqueCategoryMap.get(WEAPON).intValue();
			if (qtd >= maxItens) {
				iterator.remove();
			}
			uniqueCategoryMap.put(WEAPON, qtd+1);
		}
	}
}
