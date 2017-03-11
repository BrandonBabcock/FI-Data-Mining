package data;

/**
 * Data type representing a file attribute
 */
public class Attribute {

	private String title;

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
	 * Sets the attribute's title
	 * 
	 * @param title
	 *            the attribute's title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the attribute's value
	 * 
	 * @return the attribute's value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the attribute's value
	 * 
	 * @param value
	 *            the attribute's value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
