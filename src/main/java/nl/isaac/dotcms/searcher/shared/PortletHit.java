package nl.isaac.dotcms.searcher.shared;

public class PortletHit {

	private final Type type;
	private final String hostName;
	private final Object object;
	private final Status status;
	private final String title;
	private final String name;
	private final String value;

	public PortletHit(Type type, String hostName, Object object, Status status, String title, String name,
			String value) {
		super();
		this.type = type;
		this.hostName = hostName;
		this.object = object;
		this.status = status;
		this.title = title;
		this.name = name;
		this.value = value;
	}

	public Type getType() {
		return type;
	}

	public String getHostName() {
		return hostName;
	}

	public Object getObject() {
		return object;
	}

	public Status getStatus() {
		return status;
	}

	public String getTitle() {
		return title;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

}
