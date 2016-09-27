package nl.isaac.dotcms.searcher.status;

import com.dotmarketing.business.DotStateException;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.htmlpages.model.HTMLPage;

import nl.isaac.dotcms.searcher.shared.Status;
import nl.isaac.dotcms.searcher.shared.Status.StatusEnum;

@SuppressWarnings("deprecation")
public class HTMLPageStatus implements StatusInterface {

	@Override
	public Status getStatus(Object object, Status expectingStatus) {
		if(object instanceof HTMLPage) {
			Status status = new Status();
			HTMLPage htmlPage = (HTMLPage) object;
			try {
				if(!htmlPage.isArchived() && !htmlPage.isLive()) {
					status.setStatus(StatusEnum.UNPUBLISHED);
					return status;
				} else if(!htmlPage.isArchived() && htmlPage.isLive()) {
					status.setStatus(StatusEnum.PUBLISHED);
					return status;
				} else if(htmlPage.isArchived() && !htmlPage.isLive()) {
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
