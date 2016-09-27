package nl.isaac.dotcms.searcher;

import java.util.List;

import nl.isaac.dotcms.searcher.shared.Status.StatusEnum;
import nl.isaac.dotcms.searcher.shared.Type;

/**
* dotCMS Searcher plugin by ISAAC - The Full Service Internet Agency is licensed 
* under a Creative Commons Attribution 3.0 Unported License
* - http://creativecommons.org/licenses/by/3.0/
* - http://www.geekyplugins.com/
* 
* @copyright Copyright (c) 2012 ISAAC Software Solutions B.V. (http://www.isaac.nl)
*/

public class SearchResult {
	
	private final Type type;
	private final Object item;
	private final String fieldName;
	private final String fieldValue;
	private final List<String> snippets;
	private final String hostName;
	private final String title;
	private final StatusEnum status;
	
	public SearchResult(Type type, Object item, String title, String fieldName, String fieldValue, List<String> snippets, String hostName, StatusEnum status) {
		super();
		this.type = type;
		this.item = item;
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
		this.snippets = snippets;
		this.hostName = hostName;
		this.title = title;
		this.status = status;
	}
	
	public Type getType() {
		return type;
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

	public List<String> getSnippets() {
		return snippets;
	}

	public String getHostName() {
		return hostName;
	}

	public String getTitle() {
		return title;
	}

	public String getStatus() {
		return status.toString();
	}
	
}

enum SearchResultType {
	STRUCTURE,
	CONTENTLET,
	TEMPLATE,
	CONTAINER
}
