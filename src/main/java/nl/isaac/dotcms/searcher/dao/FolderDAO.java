package nl.isaac.dotcms.searcher.dao;

import java.util.ArrayList;
import java.util.List;

import com.dotmarketing.beans.Host;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotHibernateException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.util.Logger;
import com.liferay.portal.model.User;

public class FolderDAO {

	public FolderDAO() {
		super();
	}

	public List<Folder> getAllFolders(Host host) {
		User systemUser;
		try {
			systemUser = APILocator.getUserAPI().getSystemUser();
		} catch (DotDataException e) {
			throw new RuntimeException("Exception while retrieving system user: " + e.toString(), e);
		}

		List<Folder> folders = new ArrayList<Folder>();

		try {
			folders.addAll(APILocator.getFolderAPI().findFoldersByHost(host, systemUser, false));
		} catch (DotSecurityException e) {
			Logger.warn(this.getClass().getName(), "Error while getting all folders", e);
		}

		return folders;
	}
}
