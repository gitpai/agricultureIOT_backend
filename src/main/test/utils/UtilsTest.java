package utils;

import com.zhangfuwen.utils.Utils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

/**
 * Utils Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>四月 3, 2017</pre>
 */
public class UtilsTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: getString(Properties prop, String name)
     */
    @Test
    public void testGetString() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: getInt(Properties prop, String name)
     */
    @Test
    public void testGetInt() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: getOnlyTagAttribute(Document doc, String tagName, String attr)
     */
    @Test
    public void testGetOnlyTagAttribute() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: getBit(byte[] bytes, int bitNumber)
     */
    @Test
    public void testGetBit() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: dataToHex(byte[] data)
     */
    @Test
    public void testDataToHex() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: getSensorTypeMap()
     */
    @Test
    public void testGetSensorTypeMap() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: toRealValue(byte dataType, short value)
     */
    @Test
    public void testToRealValue() throws Exception {
        //TODO: 无符号大数的测试
        Assert.assertEquals("25.0", Utils.toRealValue((byte)0x81,(short)0x00FA));
        Assert.assertEquals("27.7", Utils.toRealValue((byte)0x01,(short)0x0115));
        Assert.assertEquals("3.30", Utils.toRealValue((byte)0x02,(short)0x014A));
    }


} 
