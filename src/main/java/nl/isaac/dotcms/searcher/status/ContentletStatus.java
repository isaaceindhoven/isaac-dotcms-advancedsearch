package nl.isaac.dotcms.searcher.status;

import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.DotStateException;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.contentlet.model.Contentlet;

import nl.isaac.dotcms.searcher.shared.Status;
import nl.isaac.dotcms.searcher.shared.Status.StatusEnum;

public class ContentletStatus implements StatusInterface {

	@Override
	public Status getStatus(Object object, Status expectingStatus) {
		if(object instanceof Contentlet) {
			Status status = new Status();
			Contentlet contentlet = (Contentlet) object;
			try {
				if(!contentlet.isArchived() && !contentlet.isLive()) {
					Contentlet published = null;
					try {
						published = APILocator.getContentletAPI().findContentletByIdentifier(contentlet.getIdentifier(), true, contentlet.getLanguageId(), APILocator.getUserAPI().getSystemUser(), false);
					} catch(Exception e) {
						/* Do nothing, Published version doesn't exists */
					}
						
					if(published != null && !published.getInode().equalsIgnoreCase(contentlet.getInode())) {
						status.setStatus(StatusEnum.SAVED);
						status.setObject(published);
						return status;
					} else {
						status.setStatus(StatusEnum.UNPUBLISHED);
						return status;
					}
				} else if(!contentlet.isArchived() && contentlet.isLive()) {
					status.setStatus(StatusEnum.PUBLISHED);
					return status;
				} else if(contentlet.isArchived() && !contentlet.isLive()) {
					status.setStatus(StatusEnum.ARCHIVED);
					return status;
				}
			} catch (DotStateException | DotDataException |DotSecurityException e) {
				throw new RuntimeException(e.toString(), e);
			}
		}
		return null;
	}

}
