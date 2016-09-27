package nl.isaac.dotcms.searcher;
/**
* dotCMS Searcher plugin by ISAAC - The Full Service Internet Agency is licensed 
* under a Creative Commons Attribution 3.0 Unported License
* - http://creativecommons.org/licenses/by/3.0/
* - http://www.geekyplugins.com/
* 
* @copyright Copyright (c) 2012 ISAAC Software Solutions B.V. (http://www.isaac.nl)
*/

public class SearchResult {
	private final Object item;
	private final String fieldName;
	private final String fieldValue;
	private final String snippet;
	private final String hostName;
	private final String title;
	public SearchResult(Object item, String title, String fieldName, String fieldValue, String snippet, String hostName) {
		super();
		this.item = item;
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
		this.snippet = snippet;
		this.hostName = hostName;
		this.title = title;
	}
	public Object getItem() {
		return item;
	}
	public String getFieldName() {
		return fieldName;
	}
	public String getFieldValue() {
		return fieldValue;
	}
	public String getSnippet() {
		return snippet;
	}
	public String getHostName() {
		return hostName;
	}
	public String getTitle() {
		return title;
	}	
}

enum SearchResultType {
	STRUCTURE,
	CONTENTLET,
	TEMPLATE,
	CONTAINER
}
