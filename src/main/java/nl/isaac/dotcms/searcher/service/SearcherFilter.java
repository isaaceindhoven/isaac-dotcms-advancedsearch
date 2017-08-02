package nl.isaac.dotcms.searcher.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.dotmarketing.beans.Host;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.portlets.containers.model.Container;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.fileassets.business.FileAsset;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.portlets.structure.model.Structure;
import com.dotmarketing.portlets.templates.model.Template;

import nl.isaac.dotcms.searcher.shared.PortletHit;
import nl.isaac.dotcms.searcher.shared.SearchableAttribute;
import nl.isaac.dotcms.searcher.shared.Status;
import nl.isaac.dotcms.searcher.shared.Status.StatusEnum;
import nl.isaac.dotcms.searcher.shared.Type;
import nl.isaac.dotcms.searcher.shared.UserSearchValues;
import nl.isaac.dotcms.searcher.status.StatusValidator;
import nl.isaac.dotcms.searcher.util.SearchableAttributesUtil;
import nl.isaac.dotcms.searcher.util.TextUtil;

@SuppressWarnings("deprecation")
public class SearcherFilter {

	private Host host;
	private boolean includeSystemHost;
	private final UserSearchValues userSearchValues;
	private final TextUtil textUtil;
	private int filteredPortletHitsSize;

	public SearcherFilter(UserSearchValues userSearchValues) {
		super();
		this.userSearchValues = userSearchValues;
		this.textUtil = new TextUtil(userSearchValues.getSearchMode().toString(),
				userSearchValues.getSnippetSizeBefore(), userSearchValues.getSnippetSizeAfter(),
				userSearchValues.getExcludeText());
		this.filteredPortletHitsSize = 0;
		this.includeSystemHost = !userSearchValues.isFilterSystemHost();
	}

	public void setHost(Host host) {
		this.host = host;
	}

	public boolean isIncludeSystemHost() {
		return this.includeSystemHost;
	}

	public Collection<PortletHit> filter(Map<Type, Collection<? extends Object>> portletsToFilter) {
		ArrayList<PortletHit> hits = new ArrayList<PortletHit>() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean addAll(Collection<? extends PortletHit> c) {
				if (c != null) {
					return super.addAll(c);
				}
				return false;
			}
		};

		portletsToFilter.forEach((portletType, portletCollection) -> {

			Iterator<? extends Object> portletsToFilterIterator = portletCollection.iterator();

			while (portletsToFilterIterator.hasNext()) {
				Object portletToFilter = portletsToFilterIterator.next();

				switch (portletType) {
				case CONTAINER:			hits.addAll(filterContainer((Container) portletToFilter)); break;
				case CONTENT:			hits.addAll(filterContent((Contentlet) portletToFilter)); break;
				case FILE:				hits.addAll(filterFile((Contentlet) portletToFilter)); break;
				case HTML_CONTENTLET:	hits.addAll(filterHtmlContentlet((Contentlet) portletToFilter)); break;
				case STRUCTURE:			hits.addAll(filterStructure((Structure) portletToFilter)); break;
				case TEMPLATE:			hits.addAll(filterTemplate((Template) portletToFilter)); break;
				case WIDGET:			hits.addAll(filterWidget((Contentlet) portletToFilter)); break;
				case FOLDER:			hits.addAll(filterFolder((Folder) portletToFilter)); break;
				default:				throw new IllegalArgumentException("Unknown type " + portletType);
				}
			}
		});

		return hits;
	}

	public boolean spotsLeft() {
		if (userSearchValues.searchAllResults())
			return true;
		return userSearchValues.getMaxResults() > filteredPortletHitsSize;
	}

	public Integer spotsLeftAmount() {
		if (userSearchValues.getMaxResults() == null)
			return null;
		return userSearchValues.getMaxResults() - filteredPortletHitsSize;
	}

	public int getFilteredPortletHitsSize() {
		return filteredPortletHitsSize;
	}

	private Collection<PortletHit> filterContent(Contentlet content) {
		return filterContentlet(Type.CONTENT, content);
	}

	private Collection<PortletHit> filterWidget(Contentlet widget) {
		return filterContentlet(Type.WIDGET, widget);
	}

	private Collection<PortletHit> filterHtmlContentlet(Contentlet html) {
		return filterContentlet(Type.HTML_CONTENTLET, html);
	}

	// Only content or widget or htmlpage, no file
	private Collection<PortletHit> filterContentlet(Type type, Contentlet contentlet) {
		StatusValidator status = new StatusValidator(Type.CONTENT, contentlet, this.userSearchValues.getStatus());

		if (status.isValid()) {
			Map<String, Object> row = contentlet.getMap();
			row.put("structureName", contentlet.getStructure().getName());
			return filterAttributesAndGetHits(SearchableAttributesUtil.getContentletAttributes(type, contentlet),
					host.getHostname(), row, status.getActualStatus());
		}

		return null;
	}

	private Collection<PortletHit> filterContainer(Container container) {
		StatusValidator status = new StatusValidator(Type.CONTAINER, container, this.userSearchValues.getStatus());

		if (status.isValid()) {
			return filterAttributesAndGetHits(SearchableAttributesUtil.getContainerAttributes(container),
					host.getHostname(), status.getObject(), status.getActualStatus());
		}

		return null;
	}

	private Collection<PortletHit> filterTemplate(Template template) {
		StatusValidator status = new StatusValidator(Type.TEMPLATE, template, this.userSearchValues.getStatus());

		if (status.isValid()) {
			return filterAttributesAndGetHits(SearchableAttributesUtil.getTemplateAttributes(template),
					host.getHostname(), status.getObject(), status.getActualStatus());
		}

		return null;
	}

	private Collection<PortletHit> filterStructure(Structure structure) {
		return filterAttributesAndGetHits(SearchableAttributesUtil.getStructureAttributes(structure),
				host.getHostname(), structure, this.userSearchValues.getStatus());
	}

	private Collection<PortletHit> filterFile(Contentlet fileContentlet) {
		StatusValidator status = new StatusValidator(Type.CONTENT, fileContentlet, this.userSearchValues.getStatus());

		if (status.isValid()) {
			FileAsset file = APILocator.getFileAssetAPI().fromContentlet(fileContentlet);
			return filterAttributesAndGetHits(SearchableAttributesUtil.getFileAttributes(fileContentlet, file),
					host.getHostname(), file, status.getActualStatus());
		}

		return null;
	}

	private Collection<PortletHit> filterFolder(Folder folder) {
		// A folder doesn't have a status, so always show it as 'Published'
		Status status = new Status();
		status.setStatus(StatusEnum.PUBLISHED);

		return filterAttributesAndGetHits(SearchableAttributesUtil.getFolderAttributes(folder), host.getHostname(),
				folder, status);
	}

	private Collection<PortletHit> filterAttributesAndGetHits(Collection<SearchableAttribute> searchableAttributes,
			String hostName, Object object, Status status) {
		Collection<PortletHit> portletHits = new ArrayList<>();

		for (SearchableAttribute attribute : searchableAttributes) {

			// Check if max result size exceeded
			if (!spotsLeft()) {
				break;
			}

			if (textUtil.checkValueForText(attribute.getValue(), this.userSearchValues.getSearchString())) {
				portletHits.add(new PortletHit(attribute.getType(), hostName, object, status, attribute.getTitle(),
						attribute.getName(), attribute.getValue()));
				filteredPortletHitsSize++;
			}
		}

		return portletHits;
	}

}
