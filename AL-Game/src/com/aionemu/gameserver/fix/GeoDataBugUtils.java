package com.aionemu.gameserver.fix;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.MapRegion;
import com.aionemu.gameserver.world.geo.GeoService;

public class GeoDataBugUtils {
	/**
	 * Verifica se o atacante consegue ver seu target
	 * @param attacked
	 * @param attacker
	 * @return
	 */
	public static boolean canSee(Creature attacked, Creature attacker) {
		try {

			if (attacked != null && attacked.getTarget() != null && !(attacked instanceof Player)) {
				MapRegion map = attacker.getActiveRegion();
				if (map != null && !GeoService.getInstance().canSee(attacked, attacker)
						&& !MathUtil.isInRange(attacked, attacker, 15)) {
					return false;
				}
			}
			return true;

		} catch (Exception e) {
			Util.printSection("GeoDataBugUtils Error - returning false");
			return false;
		}
	}
}
