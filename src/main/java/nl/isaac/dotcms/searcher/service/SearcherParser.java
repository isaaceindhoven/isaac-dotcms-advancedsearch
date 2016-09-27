package nl.isaac.dotcms.searcher.service;

import java.util.ArrayList;
import java.util.Collection;

import nl.isaac.dotcms.searcher.SearchResult;
import nl.isaac.dotcms.searcher.shared.PortletHit;
import nl.isaac.dotcms.searcher.util.TextUtil;

public class SearcherParser {

	private final String searchString;
	private final TextUtil textUtil;

	public SearcherParser(String searchString, String searchMode, int snippetSizeBefore, int snippetSizeAfter, String excludeText) {
		super();
		this.searchString = searchString;
		this.textUtil = new TextUtil(searchMode, snippetSizeBefore, snippetSizeAfter, excludeText);
	}

	public Collection<SearchResult> parse(Collection<PortletHit> portletHits) {
		Collection<SearchResult> searchResults = new ArrayList<>();

		for (PortletHit hit : portletHits) {
			searchResults.add(new SearchResult(hit.getType(), hit.getObject(), hit.getTitle(),
					hit.getName(), hit.getValue(),
					this.textUtil.getSnippetFromText(hit.getValue(), this.searchString), hit.getHostName(),
					hit.getStatus().getStatus()));
		}

		return searchResults;
	}

}
