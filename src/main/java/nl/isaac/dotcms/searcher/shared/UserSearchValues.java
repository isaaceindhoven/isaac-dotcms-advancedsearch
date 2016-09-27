package nl.isaac.dotcms.searcher.shared;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UserSearchValues {

	private final SearchMode searchMode;
	private final Type type;
	private final String searchString;
	private final String host;
	private final String languageId;
	private final Status status;
	private final Version version;
	private final int snippetSizeBefore;
	private final int snippetSizeAfter;
	private final String excludeText;
	private final Integer maxResults;
	private final int resultsPerPage;
	
	public UserSearchValues(SearchMode searchMode, Type type, String searchString, String host, String languageId, Status status,
			Version version, int snippetSizeBefore, int snippetSizeAfter, String excludeText, Integer maxResults, int resultsPerPage) {
		super();
		this.searchMode = searchMode;
		this.type = type;
		this.host = host;
		this.searchString = searchString;
		this.languageId = languageId;
		this.status = status;
		this.version = version;
		this.snippetSizeBefore = snippetSizeBefore;
		this.snippetSizeAfter = snippetSizeAfter;
		this.excludeText = excludeText;
		this.maxResults = maxResults;
		this.resultsPerPage = resultsPerPage;
	}
	
	public String getSearchString() {
		return searchString;
	}

	public String getHost() {
		return host;
	}

	public String getLanguageId() {
		return languageId;
	}

	public Status getStatus() {
		return status;
	}

	public Version getVersion() {
		return version;
	}
	
	public SearchMode getSearchMode() {
		return searchMode;
	}

	public Type getType() {
		return type;
	}

	public int getSnippetSizeBefore() {
		return snippetSizeBefore;
	}

	public int getSnippetSizeAfter() {
		return snippetSizeAfter;
	}

	public String getExcludeText() {
		return excludeText;
	}
	
	public int getResultsPerPage() {
		return resultsPerPage;
	}
	
	public Integer getMaxResults() {
		return maxResults == 0 ? null : maxResults;
	}
	
	public boolean searchAllResults() {
		return this.maxResults == 0;
	}
	
	public Map<String, Object> getAttributesMap() {
		Map<String, Object> attributes = new HashMap<String, Object>();		
		attributes.put("searcher_time", Calendar.getInstance().getTime());
		attributes.put("searcher_mode", this.searchMode.toString().toUpperCase());
		attributes.put("searcher_text", this.searchString);
		attributes.put("searcher_host", this.host);
		attributes.put("searcher_status", this.status.status.toString().toLowerCase());
		attributes.put("searcher_version", this.version.toString().toUpperCase());
		attributes.put("searcher_languageId", this.languageId);
		attributes.put("searcher_typeId", this.type.toString().toLowerCase());
		attributes.put("searcher_snippetSizeBefore", this.snippetSizeBefore);
		attributes.put("searcher_snippetSizeAfter", this.snippetSizeAfter);
		attributes.put("searcher_excludeText", this.excludeText);
		attributes.put("searcher_maxResults", this.maxResults);
		attributes.put("searcher_resultsPerPage", this.resultsPerPage);
		return attributes;
	}
	
	@Override
	public String toString() {
		return "UserSearchValues [searchMode=" + searchMode + ", type=" + type + ", searchString=" + searchString
				+ ", host=" + host + ", languageId=" + languageId + ", status=" + status + ", version=" + version
				+ ", snippetSizeBefore=" + snippetSizeBefore + ", snippetSizeAfter=" + snippetSizeAfter
				+ ", excludeText=" + excludeText + ", maxResults=" + maxResults + ", resultsPerPage=" + resultsPerPage
				+ "]";
	}
	
}


