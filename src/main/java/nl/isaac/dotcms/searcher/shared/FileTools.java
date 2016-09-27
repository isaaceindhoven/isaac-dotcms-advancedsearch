package nl.isaac.dotcms.searcher.shared;

import java.util.ArrayList;
import java.util.List;

import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.DotStateException;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.fileassets.business.FileAsset;

public class FileTools {

	public static List<FileAsset> getWorkingFileAssets() {
		List<FileAsset> workingFiles = new ArrayList<FileAsset>();
		try {
			List<Contentlet> allContent = APILocator.getContentletAPI().findAllContent(0, 0);
			
			for(Contentlet contentlet : allContent) {
				Boolean isFileAsset = APILocator.getFileAssetAPI().isFileAsset(contentlet);
				
				if(isFileAsset && contentlet.isWorking()) {
					workingFiles.add(APILocator.getFileAssetAPI().fromContentlet(contentlet));
				}
			}
			return workingFiles;
		} catch (DotDataException | DotStateException | DotSecurityException e) {
			throw new RuntimeException(e);
		}
	}
}
