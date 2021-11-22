package nl.isaac.dotcms.searcher.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.dotmarketing.beans.Host;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.util.Logger;
import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;

public class HostDAO {

	public Collection<Host> getHosts(String host) {
		Collection<Host> hosts = new ArrayList<>();
		try {
			if (host.equals("all_hosts")) {
				hosts.addAll(APILocator.getHostAPI().findAll(APILocator.getUserAPI().getSystemUser(), false));
			} else {
				hosts.add(APILocator.getHostAPI().findByName(host, APILocator.getUserAPI().getSystemUser(), false));
			}
		} catch (DotDataException | DotSecurityException e) {
			Logger.warn(this.getClass().getName(), "Exception while retrieving hosts: " + host, e);
			throw new RuntimeException(e);
		}

		return hosts;
	}

	public List<String> getAllHosts() {
		return getHosts("all_hosts").stream().map(Host::getHostname).collect(Collectors.toCollection(ArrayList::new));
	}

	public Host getCurrentHost(HttpServletRequest request) {
		try {
			return WebAPILocator.getHostWebAPI().getCurrentHost(request);
		} catch (PortalException | SystemException | DotDataException | DotSecurityException e) {
			Logger.warn(this.getClass().getName(), "Exception while retrieving current host", e);
			throw new RuntimeException(e);
		}
	}
}
