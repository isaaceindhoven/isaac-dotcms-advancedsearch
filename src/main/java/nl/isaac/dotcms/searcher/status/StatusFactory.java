package nl.isaac.dotcms.searcher.status;

import nl.isaac.dotcms.searcher.shared.Status;
import nl.isaac.dotcms.searcher.shared.Type;


public class StatusFactory {

	public enum Types {
		CONTENT { public StatusInterface create() { return new ContentletStatus(); } },
		CONTAINER { public StatusInterface create() { return new ContainerStatus(); } },
		TEMPLATE { public StatusInterface create() { return new TemplateStatus(); } };
		public abstract StatusInterface create();
	}
	
	public Status getStatusForObject(Type type, Object object, Status expectingStatus) {
		Types typesEnum = Types.valueOf(type.toString().toUpperCase());
		StatusInterface statusService = typesEnum.create();
		return statusService.getStatus(object, expectingStatus);
	}

}
