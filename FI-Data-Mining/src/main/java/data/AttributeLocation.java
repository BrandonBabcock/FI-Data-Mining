package data;

import java.util.ArrayList;
import java.util.List;

/**
 * Data type representing the locations of attributes within a file
 */
public class AttributeLocation {

	/* The column of the group by attribute */
	private int groupByIndex;

	/* A list of the column numbers of file attributes */
	private List<Integer> attributeIndexes = new ArrayList<Integer>();

	/**
	 * Gets the group by attribute's index
	 * 
	 * @return the group by attribute's index
	 */
	public int getGroupByIndex() {
		return groupByIndex;
	}

	/**
	 * Sets the group by attribute's index
	 * 
	 * @param groupByIndex
	 *            the group by attribute's index to set
	 */
	public void setGroupByIndex(int groupByIndex) {
		this.groupByIndex = groupByIndex;
	}

	/**
	 * Gets the list of attribute indexes
	 * 
	 * @return the list of attribute indexes
	 */
	public List<Integer> getAttributeIndexes() {
		return attributeIndexes;
	}

	/**
	 * Adds an attribute index
	 * 
	 * @param index
	 *            an attribute index to add
	 */
	public void addAttributeIndex(int index) {
		attributeIndexes.add(index);
	}

}