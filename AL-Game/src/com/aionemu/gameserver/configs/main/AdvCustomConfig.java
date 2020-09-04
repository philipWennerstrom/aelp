package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class AdvCustomConfig {

	/**
	 * InGameShop Limit
	 */
	@Property(key = "gameserver.gameshop.limit", defaultValue = "false")
	public static boolean GAMESHOP_LIMIT;

	@Property(key = "gameserver.gameshop.category", defaultValue = "0")
	public static byte GAMESHOP_CATEGORY;

	@Property(key = "gameserver.gameshop.limit.time", defaultValue = "60")
	public static long GAMESHOP_LIMIT_TIME;
}