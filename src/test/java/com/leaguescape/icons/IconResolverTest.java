package com.leaguescape.icons;

import com.leaguescape.constants.TaskTypes;
import org.junit.Assert;
import org.junit.Test;

public class IconResolverTest
{
	@Test
	public void killCountWithoutBossUsesCombatIcon()
	{
		String path = IconResolver.resolveTaskTileLocalIconPath(TaskTypes.KILL_COUNT, "Kill 5 goblins", null);
		Assert.assertNotNull(path);
		Assert.assertTrue(path.contains("Combat_icon"));
	}

	@Test
	public void killCountWithBossUsesBossIcon()
	{
		String path = IconResolver.resolveTaskTileLocalIconPath(TaskTypes.KILL_COUNT, "Kill Zulrah", "zulrah");
		Assert.assertNotNull(path);
		Assert.assertTrue(path.contains("bossicons"));
		Assert.assertTrue(path.contains("zulrah"));
	}
}
