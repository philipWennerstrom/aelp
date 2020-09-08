package com.aionemu.gameserver.services.player;

import com.aionemu.gameserver.configs.main.EnchantsConfig;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * 
 * @author luizw
 *
 */
public class EnchLv {
	public static void checkEnchLv(Player player) {
		for (Item playerItem : player.getEquipment().getEquippedItems()) {
			setEnchLv(playerItem.getItemTemplate());
		}
		
		for (Item inventoryItem : player.getInventory().getItems()) {
			setEnchLv(inventoryItem.getItemTemplate());
		}		
	}

	private static void setEnchLv(ItemTemplate itemTemplate) {
		switch (itemTemplate.getEquipmentType()) {
		case ARMOR:
		case WEAPON:
			switch (itemTemplate.getItemQuality()) {
			case UNIQUE:
			case MYTHIC:
			case EPIC:
				itemTemplate.setMaxEnchantLevel(EnchantsConfig.ENCHANT_MAX_LEVEL_TYPE2);
				break;
			default:
				itemTemplate.setMaxEnchantLevel(EnchantsConfig.ENCHANT_MAX_LEVEL_TYPE1);
				break;
			}
			break;
		default:
			break;
		}
	}
}