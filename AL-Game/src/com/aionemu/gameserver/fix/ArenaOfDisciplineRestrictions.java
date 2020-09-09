/**
 * 
 */
package com.aionemu.gameserver.fix;

import java.util.Map;
import java.util.Map.Entry;

import com.aionemu.gameserver.model.autogroup.AGPlayer;
import com.aionemu.gameserver.model.autogroup.AGQuestion;
import com.aionemu.gameserver.model.autogroup.AutoGroupType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.utils.audit.AuditLogger;

/**
 * @author luizw
 *
 */
public class ArenaOfDisciplineRestrictions {
	
	public static AGQuestion checkPlayerOnAdd(Player player, Map<Integer, AGPlayer> players,AutoGroupType agt) {
		switch (agt) {
		case ARENA_OF_CHAOS_1:
		case ARENA_OF_CHAOS_2:
		case ARENA_OF_CHAOS_3:
		case ARENA_OF_DISCIPLINE_3:

			AionConnection connectionToAdd = player.getClientConnection();
			if(!players.isEmpty()) {
				for(Entry<Integer, AGPlayer> entry:players.entrySet()) {
					
					AGPlayer playerAlreadyInInstance = entry.getValue();
					AionConnection pAIGConnection = playerAlreadyInInstance.getClientConnection();
					if(pAIGConnection.getMacAddress().equals(connectionToAdd.getMacAddress())) {
						return logAndFail(player, playerAlreadyInInstance);
					}
				}
			}
		
			break;

		default:
			break;
		}
		return AGQuestion.ADDED;
	}

	private static AGQuestion logAndFail(Player player, AGPlayer playerAlreadyInInstance) {
		AuditLogger.info(player, "[Arena of Discipline protection] - Player "+ playerAlreadyInInstance.getName() +" nao pode entrar na arena of discipline/chaos com um mesmo net adress");
		return AGQuestion.FAILED;
	}
}