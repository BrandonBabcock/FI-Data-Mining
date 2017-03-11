package data;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by shawn on 3/11/2017.
 * Purpose: To Test to Attribute Class
 */
public class AttributeTest {
    private Attribute testAttribute;

    @Before
    public void setup(){
        testAttribute = new Attribute("myAttribute", "test_value");
    }

    @After
    public void tearDown(){
        testAttribute = null;
    }

    @Test
    public void getTitleTest(){
        assertThat(testAttribute.getTitle(), equalTo("myAttribute"));
    }

    @Test
    public void getValueTest(){
        assertThat(testAttribute.getValue(), equalTo("test_value"));
    }

    @Test
    public void setTitleTest(){
        testAttribute.setTitle("new_title");
        assertThat(testAttribute.getTitle(), equalTo("new_title"));
    }

    @Test
    public void setValueTest(){
        testAttribute.setValue("new_Value");
        assertThat(testAttribute.getValue(), equalTo("new_Value"));
    }
}
