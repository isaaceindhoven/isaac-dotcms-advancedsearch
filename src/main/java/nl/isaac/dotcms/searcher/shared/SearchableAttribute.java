package nl.isaac.dotcms.searcher.shared;

public class SearchableAttribute {

	private final Type type;
	private final String title;
	private final String name;
	private final String value;

	public SearchableAttribute(Type type, String title, String name, String value) {
		super();
		this.type = type;
		this.title = title;
		this.name = name;
		this.value = value;
	}

	public String getTitle() {
		return title;
	}

	public Type getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

}
