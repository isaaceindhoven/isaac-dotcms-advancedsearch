package nl.isaac.dotcms.searcher.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dotmarketing.beans.Host;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.portlets.containers.model.Container;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.structure.factories.StructureFactory;
import com.dotmarketing.portlets.structure.model.Structure;
import com.dotmarketing.portlets.templates.model.Template;
import com.dotmarketing.util.Logger;

import nl.isaac.dotcms.searcher.service.BufferedSearchResultIterator;
import nl.isaac.dotcms.searcher.shared.Status;
import nl.isaac.dotcms.searcher.shared.Type;
import nl.isaac.dotcms.searcher.util.ContentletQuery;

@SuppressWarnings("deprecation")
public class PortletDAO {

	public Map<Type, Collection<? extends Object>> getAllByBuffer(BufferedSearchResultIterator buff) {
		Map<Type, Collection<? extends Object>> portletsToFilter = new LinkedHashMap<>();

		while (buff.hasNext()) {
			portletsToFilter.putAll(buff.next());
		}

		return portletsToFilter;
	}

	public List<Container> getAllContainers(Host host) {
		try {
			return new ArrayList<>(APILocator.getContainerAPI().findContainersUnder(host));
		} catch (DotDataException e) {
			Logger.warn(this.getClass().getName(), "Error while getting all containers", e);
		}

		return new ArrayList<>();
	}

	public List<Template> getAllTemplates(Host host) {
		try {
			return APILocator.getTemplateAPI().findTemplatesAssignedTo(host);
		} catch (DotDataException e) {
			Logger.warn(this.getClass().getName(), "Error while getting all templates", e);
		}

		return new ArrayList<>();
	}

	public List<Structure> getAllStructures(Host host) {
		return StructureFactory.getStructures().stream().filter(s -> s.getHost().equals(host.getIdentifier()))
				.collect(Collectors.toList());
	}

	public List<Contentlet> getWidgetContentlets(Host host, String languageId, Status status, boolean includeSystemHost) {
		return getContentletsByStructureType(Structure.STRUCTURE_TYPE_WIDGET, host, languageId, status, includeSystemHost);
	}

	public List<Contentlet> getContentContentlets(Host host, String languageId, Status status, boolean includeSystemHost) {
		return getContentletsByStructureType(Structure.STRUCTURE_TYPE_CONTENT, host, languageId, status, includeSystemHost);
	}

	public List<Contentlet> getFileContentlets(Host host, Status status, boolean includeSystemHost) {
		return getContentletsByStructureType(Structure.STRUCTURE_TYPE_FILEASSET, host, null, status, includeSystemHost);
	}

	public List<Contentlet> getHtmlContentlets(Host host, String languageId, Status status, boolean includeSystemHost) {
		return getContentletsByStructureType(Structure.STRUCTURE_TYPE_HTMLPAGE, host, languageId, status, includeSystemHost);
	}

	private List<Structure> getStructuresPerType(int structureType) {
		List<Structure> structuresPerType = new ArrayList<>();

		List<Structure> structures = StructureFactory.getStructures();

		if (structures != null && !structures.isEmpty()) {
			for (Structure structure : structures) {
				if (structure != null && structure.getStructureType() == structureType) {
					structuresPerType.add(structure);
				}
			}
		}

		return structuresPerType;
	}

	private List<Contentlet> getContentletsByStructureType(int structureType, Host host, String languageId,	Status status, boolean includeSystemHost) {
		List<Structure> structuresPerType = getStructuresPerType(structureType);

		if (structuresPerType.size() != 0) {
			ContentletQuery cq = new ContentletQuery(structuresPerType);

			if (structureType == Structure.STRUCTURE_TYPE_FILEASSET) {
				cq.addHost(host.getIdentifier());
			} else {

				if (!languageId.equalsIgnoreCase("0")) {
					cq.addLanguage(languageId);
				}

				if (includeSystemHost) {
					cq.addHostAndIncludeSystemHost(host.getIdentifier());
				} else {
					cq.addHost(host.getIdentifier());
				}
			}

			if (!(status.getStatus().getLive() == false && status.getStatus().getWorking() == false
					&& status.getStatus().getArchived() == false)) {
				cq.addDeleted(status.getStatus().getArchived());
				cq.addWorking(status.getStatus().getWorking());
				cq.addLive(status.getStatus().getLive());
			}

			return cq.executeSafe();
		}

		return new ArrayList<>();
	}

}
