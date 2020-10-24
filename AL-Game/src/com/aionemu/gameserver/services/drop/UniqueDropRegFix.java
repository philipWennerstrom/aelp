package com.aionemu.gameserver.services.drop;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.aionemu.gameserver.model.drop.Drop;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.templates.item.EquipType;
import com.aionemu.gameserver.model.templates.item.ItemQuality;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

public class UniqueDropRegFix {
	private static final String ACCESSORY = "ACCESSORY";
	private static final String WEAPON = "WEAPON";
	private static final String ARMOR = "ARMOR";
	private Map<String, Integer> uniqueCategoryMap;
	private int maxItens;
	
	public UniqueDropRegFix() {
		uniqueCategoryMap = new HashMap<String, Integer>();
		uniqueCategoryMap.put(ARMOR, Integer.valueOf(0));
		uniqueCategoryMap.put(WEAPON, Integer.valueOf(0));
		uniqueCategoryMap.put(ACCESSORY, Integer.valueOf(0));
		maxItens = (Math.random() <= 0.3) ? 2 : 3;
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
		if (itemQuality == ItemQuality.UNIQUE) {
			checkUniqueArmor(iterator, itemId, itemTemplate.getEquipmentType());
			checkUniqueWeapon(iterator, itemId, itemTemplate.getEquipmentType());
		}
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
