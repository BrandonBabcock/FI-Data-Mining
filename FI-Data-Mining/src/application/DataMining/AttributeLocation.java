package application.DataMining;
import java.util.ArrayList;

public class AttributeLocation {

	private int groupByIndex;
	private ArrayList<Integer> attributeIndexes = new ArrayList<Integer>();
	private boolean hasGroupByIndex = false;

	public int getGroupByIndex() {
		return groupByIndex;
	}

	public void setGroupByIndex(int groupByIndex) {
		this.groupByIndex = groupByIndex;
	}

	public ArrayList<Integer> getAttributeIndexes() {
		return attributeIndexes;
	}

	public void setAttributeIndexes(ArrayList<Integer> attributeIndexes) {
		this.attributeIndexes = attributeIndexes;
	}

	public void addAttributeIndex(int index) {
		attributeIndexes.add(index);
	}

	public void setHasGroupByIndex(boolean hasGroupByIndex) {
		this.hasGroupByIndex = hasGroupByIndex;
	}

	public boolean getHasGroupByIndex() {
		return hasGroupByIndex;
	}

}
