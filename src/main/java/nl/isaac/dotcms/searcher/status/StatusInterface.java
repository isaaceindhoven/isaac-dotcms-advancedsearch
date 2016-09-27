package nl.isaac.dotcms.searcher.status;

import nl.isaac.dotcms.searcher.shared.Status;


public interface StatusInterface  {
	public Status getStatus(Object object, Status expectingStatus);
}
