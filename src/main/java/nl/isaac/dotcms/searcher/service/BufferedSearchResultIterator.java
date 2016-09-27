package nl.isaac.dotcms.searcher.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dotmarketing.beans.Host;
import com.dotmarketing.portlets.containers.model.Container;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.portlets.htmlpages.model.HTMLPage;
import com.dotmarketing.portlets.structure.model.Structure;
import com.dotmarketing.portlets.templates.model.Template;
import com.dotmarketing.util.Logger;

import nl.isaac.dotcms.searcher.dao.FolderDAO;
import nl.isaac.dotcms.searcher.dao.PortletDAO;
import nl.isaac.dotcms.searcher.shared.Status;
import nl.isaac.dotcms.searcher.shared.Type;

public class BufferedSearchResultIterator implements Iterator<Map<Type, Collection<? extends Object>>> {

	private List<Type> types = new ArrayList<Type>() {
		{
			add(Type.CONTENT);
			add(Type.FILE);
			add(Type.WIDGET);
			add(Type.CONTAINER);
			add(Type.STRUCTURE);
			add(Type.TEMPLATE);
			add(Type.HTMLPAGE);
			add(Type.FOLDER);
		}
	};

	private PortletDAO portletDAO = new PortletDAO();
	private FolderDAO folderDAO = new FolderDAO();

	private Type type;
	private Host host;
	private String languageId;
	private Status status;

	private final int offset;
	private Integer limit;
	private boolean searchAll;

	private int currentTypeIndex;
	private boolean doneSearchingCurrentHost;
	private SearcherFilter searcherFilter;

	public Type getType() {
		return type;
	}

	public String getLanguageId() {
		return languageId;
	}

	public Status getStatus() {
		return status;
	}

	public int getLimit() {
		return limit;
	}

	public Host getHost() {
		return host;
	}

	public BufferedSearchResultIterator(SearcherFilter searcherFilter, Type type, String languageId, Status status, Integer limit) {
		this.searcherFilter = searcherFilter;
		this.type = type;
		this.languageId = !languageId.equalsIgnoreCase("0") ? languageId : "0";
		this.status = status;
		this.offset = 0;
		this.limit = limit;
	}

	public void setBufferForNewHost(Host host) {
		Logger.info(this, "Buffer is set for host: " + host.getHostname());
		
		this.host = host;
		
		if (searcherFilter.spotsLeft()) {
			doneSearchingCurrentHost = false;
		}
		
		if (type == Type.ALL) {
			searchAll = true;
			currentTypeIndex = 0;
		} else {
			searchAll = false;
			currentTypeIndex = types.indexOf(type);
		}
	}

	@Override
	public boolean hasNext() {
		// spots left? still more other type portlets to retrieve?
		return !doneSearchingCurrentHost;
	}

	@Override
	public Map<Type, Collection<? extends Object>> next() {
		return getPortlets();
	}

	private void increaseTypeIndex() {
		// increase index only if index is in bounds of Types List
		if (currentTypeIndex + 1 < types.size()) {
			currentTypeIndex++;
		} else {
			doneSearchingCurrentHost = true;
		}
	}

	private Map<Type, Collection<? extends Object>> getPortlets() {
		Map<Type, Collection<? extends Object>> portlets = new LinkedHashMap<>();

		Type type = types.get(currentTypeIndex);

		switch (type) {
		case CONTENT:
			Collection<Contentlet> contents = portletDAO.getContentContentlets(host, languageId, status, offset, searcherFilter.spotsLeftAmount());
			portlets.put(Type.CONTENT, contents);
			break;
		case FILE:
			Collection<Contentlet> files = portletDAO.getFileContentlets(host, status, offset, searcherFilter.spotsLeftAmount());
			portlets.put(Type.FILE, files);
			break;
		case WIDGET:
			Collection<Contentlet> widgets = portletDAO.getWidgetContentlets(host, languageId, status, offset, searcherFilter.spotsLeftAmount());
			portlets.put(Type.WIDGET, widgets);
			break;
		case CONTAINER:
			Collection<Container> containers = portletDAO.getAllContainers(host);
			portlets.put(Type.CONTAINER, containers);
			break;
		case STRUCTURE:
			Collection<Structure> structures = portletDAO.getAllStructures(host, offset, searcherFilter.spotsLeftAmount());
			portlets.put(Type.STRUCTURE, structures);
			break;
		case TEMPLATE:
			Collection<Template> templates = portletDAO.getAllTemplates(host);
			portlets.put(Type.TEMPLATE, templates);
			break;
		case HTMLPAGE:
			Collection<HTMLPage> htmlPages = portletDAO.getAllHTMLPages(host);
			portlets.put(Type.HTMLPAGE, htmlPages);
			break;
		case FOLDER:
			Collection<Folder> folders = folderDAO.getAllFolders(host);
			portlets.put(Type.FOLDER, folders);
			break;
		default:
			break;
		}
		
		Logger.info(this, "Found [unfiltered] " + portlets.size() + " " + type + "(s) for host: " + host.getHostname());

		if (searcherFilter.spotsLeft()) {
			if (searchAll) {
				increaseTypeIndex();
			} else {
				doneSearchingCurrentHost = true;
			}
		} else {
			doneSearchingCurrentHost = true;
		}

		return portlets;
	}

}
