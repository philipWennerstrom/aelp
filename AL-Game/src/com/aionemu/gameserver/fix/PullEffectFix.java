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
				if (ntzFromEffectorZ > 0 && ntzFromEffectorZ < 2) {
					z = (z - zFactor) + 0.5F;
					if(z<npcTargetZ) {
						z=npcTargetZ;
					}
				}else if(ntzFromEffectorZ >= 2) {
						z=z - 1.9F;
				}
				else if(ntzFromEffectorZ< 0) {
					//z = (z - ntzFromEffectorZ)+0.25F;
					z=z - 1.9F;
				}else  {
					z = (z - ntzFromEffectorZ)- 0.75F;
				}
			}
		}
		
		return z;
	}
}
