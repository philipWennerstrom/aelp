package com.aionemu.gameserver.fix;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class ServerUtils {
	private static final String _1_9 = "1.9";
	
	public static void openAdvStigmaSlots(Player player) {
		if(is1_9()) {
			if(isLevel50(player)) {
				checkElyosQuest(player);
				checkAsmodianQuest(player);
			}
		}
	}
	
	private static void checkAsmodianQuest(Player player) {
		if(isAsmodian(player)) {
			if(player.isCompleteQuest(4936)) {
				additionalSlots(player,3);
				openForAsmodians(player);
			}
		}
	}
	
	private static void checkElyosQuest(Player player) {
		if(isElyos(player)) {
			if(player.isCompleteQuest(3932)) {
				additionalSlots(player,3);
				openForElyos(player);
			}
		}
	}

	private static boolean isLevel50(Player player) {
		return player.getCommonData().getLevel()>=50;
	}

	private static boolean is1_9() {
		return CustomConfig.GAMESERVER_AION_VERSION.equals(_1_9);
	}

	private static void additionalSlots(Player player, int slotsToadd) {
		int currentStigmaSlots = player.getCommonData().getAdvencedStigmaSlotSize();
		if (!(currentStigmaSlots > 5)) {
			player.getCommonData().setAdvencedStigmaSlotSize(currentStigmaSlots + 3);
		}
	}

	private static void openForAsmodians(Player player) {
		if (isAsmodian(player)) {
			ServerUtils.completeQuest(player, 2900);
		}
	}

	private static boolean isAsmodian(Player player) {
		return player.getCommonData().getRace() == Race.ASMODIANS;
	}

	private static void openForElyos(Player player) {
		if (isElyos(player)) {
			ServerUtils.completeQuest(player, 1929);
		}
	}

	private static boolean isElyos(Player player) {
		return player.getCommonData().getRace() == Race.ELYOS;
	}
	
	public static void completeQuest(Player player, int questId) {
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null) {
			player.getQuestStateList().addQuest(questId, new QuestState(questId, QuestStatus.COMPLETE, 1, 1, null, 0, null));
			PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(questId, QuestStatus.COMPLETE.value(), 0));
		}
		else {
			qs.setStatus(QuestStatus.COMPLETE);
			qs.getQuestVars().setVar(1);
			qs.setCompleteCount(1);
			PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(questId, qs.getStatus(), qs.getQuestVars().getQuestVars()));
		}
	}
}