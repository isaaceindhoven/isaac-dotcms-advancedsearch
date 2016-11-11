package nl.isaac.dotcms.searcher.util;

import com.liferay.portal.util.ReleaseInfo;

public class DotCMSVersionUtil {
	
	public static int dotCMSVersion;
	
	static {
		String[] subVersions = ReleaseInfo.getVersion().split("\\.");

		if (subVersions.length > 0) {
			String currentDotCMSVersion = subVersions[0];
			dotCMSVersion = Integer.valueOf(currentDotCMSVersion);
		} else {
			dotCMSVersion = 3;
		}
	}
}
