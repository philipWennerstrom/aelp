package com.aionemu.gameserver.fix;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Effect;

public class PullEffectFix {
	
	public static float fixZ(Effect effect) {
		final Creature effector = effect.getEffector();
		float npcTargetZ = effect.getEffected().getMoveController().getTargetZ2();
		float z = effector.getZ();

		if (effector instanceof Player) {
			Player effectorPlayer = (Player) effector;
			float zFactor = effectorPlayer.getMoveController().getVectorZ();

			if (zFactor > 0) {
				float ntzFromEffectorZ = z - npcTargetZ;
				if (ntzFromEffectorZ > 0 && ntzFromEffectorZ < 1.19F) {
					z = z - ntzFromEffectorZ + 0.25f;
					if (z < npcTargetZ) {
						z = npcTargetZ;
					}
				} else if (ntzFromEffectorZ >= 1.09F) {
					float dif = ntzFromEffectorZ - 1.084137F;
					if (dif > 1.2f) {
						z = z - 1.074137F;
					} else {
						z = npcTargetZ + (dif) + 1.084137f;
					}
				} else if (ntzFromEffectorZ < 0) {
					// z = (z - ntzFromEffectorZ)+0.25F;
					if(npcTargetZ<-1.084137) {
					z = z - 0.75F;
					}else {
						//z = (z+ ntzFromEffectorZ)+1.18f;
						z = z  - 0.25F;
					}
				} else {
					z = (z - ntzFromEffectorZ) - 0.75F;
				}
			}
		}
		
		return z;
	}
}
