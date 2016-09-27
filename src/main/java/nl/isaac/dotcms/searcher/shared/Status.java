package nl.isaac.dotcms.searcher.shared;

/**
 * @author yasin.dilekci
 *
 */
public class Status {
	
	private Object object;
	public StatusEnum status; 

	public Status() {
		
	}
	
	public enum StatusEnum {
		ALL {
			public boolean getLive() { return false; }
			public boolean getWorking() { return false; }
			public boolean getArchived() { return false; }
		}, 
		PUBLISHED {
			public boolean getLive() { return true; }
			public boolean getWorking() { return true; }
			public boolean getArchived() { return false; }
		},
		UNPUBLISHED {
			public boolean getLive() { return false; }
			public boolean getWorking() { return true; }
			public boolean getArchived() { return false; }
		},
		ARCHIVED {
			public boolean getLive() { return false; }
			public boolean getWorking() { return true; }
			public boolean getArchived() { return true; }
		}, 
		SAVED {
			public boolean getLive() { return false; }
			public boolean getWorking() { return true; }
			public boolean getArchived() { return false; }
		};
		public abstract boolean getLive();
		public abstract boolean getWorking();
		public abstract boolean getArchived();
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public StatusEnum getStatus() {
		return status;
	}

	public void setStatus(StatusEnum status) {
		this.status = status;
	}

}


