package data;

/**
 * Data type representing a file attribute
 */
public class Attribute {

	/* The attribute's title */
	private String title;

	/* The attribute's value */
	private String value;

	/**
	 * Constructs a new Attribute object
	 * 
	 * @param title
	 *            the attribute title
	 * @param value
	 *            the attribute value
	 */
	public Attribute(String title, String value) {
		this.title = title;
		this.value = value;
	}

	/**
	 * Gets the attribute's title
	 * 
	 * @return the attribute's title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Gets the attribute's value
	 * 
	 * @return the attribute's value
	 */
	public String getValue() {
		return value;
	}

}
