package com.aionemu.gameserver.services.drop;

import java.util.Iterator;
import java.util.Set;

import com.aionemu.gameserver.dataholders.NpcDropData;
import com.aionemu.gameserver.model.drop.DropItem;

public class GodstoneDropRegFix {
	private int qtd = 0;

	public int getQtd() {
		return qtd;
	}
	
	public void fix(Set<DropItem> droppedItens) {
		Iterator<DropItem> iterator = droppedItens.iterator();
		while (iterator.hasNext()) {
			DropItem next = iterator.next();
			int itemId = next.getDropTemplate().getItemId();
			checkItemId(iterator, itemId);
		}
	}

	/**
	 * @param iterator
	 * @param itemId
	 */
	private void checkItemId(Iterator<DropItem> iterator, int itemId) {
		if(itemId>166000060 &&itemId<166000099) {
			return;
		}
		if (NpcDropData.godstoneDrops.containsKey(Integer.valueOf(itemId))) {
			if (qtd >= 1) {
				iterator.remove();
			}
			qtd++;
		}
	}
}
