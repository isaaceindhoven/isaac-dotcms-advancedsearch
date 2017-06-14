package nl.isaac.dotcms.searcher.status;

import com.dotmarketing.portlets.containers.model.Container;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.structure.model.Structure;
import com.dotmarketing.portlets.templates.model.Template;

import nl.isaac.dotcms.searcher.shared.Status;
import nl.isaac.dotcms.searcher.shared.Status.StatusEnum;
import nl.isaac.dotcms.searcher.shared.Type;
import nl.isaac.dotcms.searcher.shared.Version;

public class StatusValidator {

	private final StatusFactory statusFactory;
	private final Type type;
	private final Status actualStatus;
	
	private Object portlet;
	
	public StatusValidator(Type type, Object portlet, Status expectingStatus) {
		super();
		this.statusFactory = new StatusFactory();
		this.type = type;
		this.portlet = portlet;
		this.actualStatus = statusFactory.getStatusForObject(type, portlet, expectingStatus);
	}

	public boolean isValid() {
		return actualStatus != null;
	}

	public Object getObject() {
		if (isSaved() && isLive()) {
			switch (this.type) {
			case CONTENT:
			case FILE:
			case WIDGET:
			case HTML_CONTENTLET:
				this.portlet = (Contentlet) this.portlet;
				break;
			case CONTAINER:
				this.portlet = (Container) this.portlet;
				break;
			case STRUCTURE:
				this.portlet = (Structure) this.portlet;
				break;
			case TEMPLATE:
				this.portlet = (Template) this.portlet;
				break;
			default:
				throw new IllegalArgumentException("Unknown type " + type);
			}
		}

		return this.portlet;
	}

	public Status getActualStatus() {
		return this.actualStatus;
	}

	private boolean isSaved() {
		return this.actualStatus.getStatus().equals(StatusEnum.SAVED);
	}

	private boolean isLive() {
		return this.actualStatus.getStatus().equals(Version.LIVE);
	}

}
