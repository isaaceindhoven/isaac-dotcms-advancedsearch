package nl.isaac.dotcms.searcher.service;
/**
* dotCMS Searcher plugin by ISAAC - The Full Service Internet Agency is licensed
* under a Creative Commons Attribution 3.0 Unported License
* - http://creativecommons.org/licenses/by/3.0/
* - http://www.geekyplugins.com/
*
* @copyright Copyright (c) 2012 ISAAC Software Solutions B.V. (http://www.isaac.nl)
*/

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.dotmarketing.beans.Host;
import com.dotmarketing.util.Logger;

import nl.isaac.dotcms.searcher.dao.HostDAO;
import nl.isaac.dotcms.searcher.dao.PortletDAO;
import nl.isaac.dotcms.searcher.shared.PortletHit;
import nl.isaac.dotcms.searcher.shared.Type;
import nl.isaac.dotcms.searcher.shared.UserSearchValues;

public class SearcherService {

	private UserSearchValues userSearchValues;
	private SearcherFilter searcherFilter;

	public SearcherService(UserSearchValues userSearchValues) {
		super();
		this.userSearchValues = userSearchValues;
		this.searcherFilter = new SearcherFilter(userSearchValues);
	}

	public Collection<PortletHit> getPortletHits() {
		ArrayList<PortletHit> hits = new ArrayList<>();

		Logger.info(this.getClass().getName(), "Searching for: " + userSearchValues.toString());

		Collection<Host> hosts = new HostDAO().getHosts(userSearchValues.getHost());

		BufferedSearchResultIterator buffer = new BufferedSearchResultIterator(searcherFilter,
				userSearchValues.getType(), userSearchValues.getLanguageId(), userSearchValues.getStatus());

		for (Host host : hosts) {
			searcherFilter.setHost(host);
			buffer.setBufferForNewHost(host);
			hits.addAll(getHitsByHost(buffer));
		}

		return hits;
	}

	private Collection<PortletHit> getHitsByHost(BufferedSearchResultIterator buff) {
		Map<Type, Collection<? extends Object>> portletsToFilter = getPortletsByBuffer(buff);
		Collection<PortletHit> hits = getHits(portletsToFilter);
		return hits;
	}

	private Map<Type, Collection<? extends Object>> getPortletsByBuffer(BufferedSearchResultIterator buff) {
		return new PortletDAO().getAllByBuffer(buff);
	}

	private Collection<PortletHit> getHits(Map<Type, Collection<? extends Object>> portlets) {
		return searcherFilter.filter(portlets);
	}

}
