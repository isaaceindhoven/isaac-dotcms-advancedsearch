package nl.isaac.dotcms.searcher.status;

import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.DotStateException;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.templates.model.Template;

import nl.isaac.dotcms.searcher.shared.Status;
import nl.isaac.dotcms.searcher.shared.Status.StatusEnum;

public class TemplateStatus implements StatusInterface {

	@Override
	public Status getStatus(Object object, Status expectingStatus) {
		if(object instanceof Template) {
			Status status = new Status();
			Template template = (Template) object;
			try {
				if((expectingStatus.getStatus().equals(StatusEnum.ALL) || expectingStatus.getStatus().equals(StatusEnum.UNPUBLISHED)) 
						&& !template.isArchived() && !template.isLive()) {
					Template published = null;
					try {
						published = APILocator.getTemplateAPI().findLiveTemplate(template.getIdentifier(), APILocator.getUserAPI().getSystemUser(), false);
					} catch(Exception e) {
						/* Do nothing, Published version doesn't exists */
					}
						
					if(published != null && !published.getInode().equalsIgnoreCase(template.getInode())) {
						status.setStatus(StatusEnum.SAVED);
						status.setObject(published);
						return status;
					} else {
						status.setStatus(StatusEnum.UNPUBLISHED);
						return status;
					}
				} else if((expectingStatus.getStatus().equals(StatusEnum.ALL) || expectingStatus.getStatus().equals(StatusEnum.PUBLISHED)) 
						&& !template.isArchived() && template.isLive()) {
					status.setStatus(StatusEnum.PUBLISHED);
					return status;
				} else if((expectingStatus.getStatus().equals(StatusEnum.ALL) || expectingStatus.getStatus().equals(StatusEnum.ARCHIVED)) 
						&& template.isArchived() && !template.isLive()) {
					status.setStatus(StatusEnum.ARCHIVED);
					return status;
				}
			} catch (DotStateException | DotDataException | DotSecurityException e) {
				throw new RuntimeException(e.toString(), e);
			}
		}
		return null;
	}

}
