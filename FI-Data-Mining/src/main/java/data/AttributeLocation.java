package data;

import java.util.ArrayList;

/**
 * Data type representing the locations of attributes within a file
 */
public class AttributeLocation {

	/* The column of the group by attribute */
	private int groupByIndex;

	/* A list of the column numbers of file attributes */
	private ArrayList<Integer> attributeIndexes = new ArrayList<Integer>();

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
	 * Get's the list of attribute indexes
	 * 
	 * @return the list of attribute indexes
	 */
	public ArrayList<Integer> getAttributeIndexes() {
		return attributeIndexes;
	}

	/**
	 * Set's the list of attribute indexes
	 * 
	 * @param attributeIndexes
	 *            the list of attribute indexes to set
	 */
	public void setAttributeIndexes(ArrayList<Integer> attributeIndexes) {
		this.attributeIndexes = attributeIndexes;
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
