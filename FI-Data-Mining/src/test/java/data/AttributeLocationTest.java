package data;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by shawn on 3/11/2017.
 * Purpose: To test the AttributeLocation Class
 */
public class AttributeLocationTest {
    private AttributeLocation testAttributeLocation;

    @Before
    public void setup(){
        testAttributeLocation = new AttributeLocation();

        // setting up the indexes
        Integer[] values = {0,1,2,3,4};
        ArrayList<Integer> attributeIndexes = new ArrayList<>(Arrays.asList(values));
        testAttributeLocation.setAttributeIndexes(attributeIndexes);
        testAttributeLocation.setGroupByIndex(0);
    }

    @After
    public void tearDown(){
        testAttributeLocation = null;
    }

    @Test
    public void setGroupByIndexTest(){
        testAttributeLocation.setGroupByIndex(1);
        assertThat(testAttributeLocation.getGroupByIndex(),equalTo(1));
    }

    @Test
    public void getGroupByIndex(){
        assertThat(testAttributeLocation.getGroupByIndex(), equalTo(0));
    }

    @Test
    public void setAttributeIndexesTest(){
        Integer[] values = {5,6,7,8};
        ArrayList<Integer> attributeIndexes = new ArrayList<>(Arrays.asList(values));
        testAttributeLocation.setAttributeIndexes(attributeIndexes); // testing the set method

        assertThat(testAttributeLocation.getAttributeIndexes().toArray(), equalTo(values));
    }

    @Test
    public void getAttributeIndexesTest(){
        Integer[] expectedValues = {0,1,2,3,4};
        assertThat(testAttributeLocation.getAttributeIndexes().toArray(), equalTo(expectedValues));
    }

    @Test
    public void addAttributeIndexTest(){
        int newIndex = 5;
        testAttributeLocation.addAttributeIndex(newIndex);
        int[] expected = {0,1,2,3,4,5};

        assertThat(testAttributeLocation.getAttributeIndexes().toArray(), equalTo(expected));
    }
}
