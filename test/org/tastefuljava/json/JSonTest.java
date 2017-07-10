package org.tastefuljava.json;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class JSonTest {

    public JSonTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testUnformatted() throws Exception {
        TestObject obj1 = new TestObject();
        String json1 = JSon.stringify(obj1, false);
        TestObject obj2 = JSon.parse(json1, TestObject.class);
        assertEquals(obj1, obj2);
        String json2 = JSon.stringify(obj2, false);
        assertEquals(json1, json2);
    }

    @Test
    public void testFormatted() throws Exception {
        TestObject obj1 = new TestObject();
        String json1 = JSon.stringify(obj1, true);
        TestObject obj2 = JSon.parse(json1, TestObject.class);
        assertEquals(obj1, obj2);
        String json2 = JSon.stringify(obj2, true);
        assertEquals(json1, json2);
    }

    public static class TestObject {
        private final BigDecimal number = BigDecimal.valueOf(123, 2);
        private final Date date = new Date();
        private final String string = "Hello world!!!";

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 67 * hash + Objects.hashCode(this.number);
            hash = 67 * hash + Objects.hashCode(this.date);
            hash = 67 * hash + Objects.hashCode(this.string);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TestObject other = (TestObject) obj;
            if (!Objects.equals(this.string, other.string)) {
                return false;
            }
            if (!Objects.equals(this.number, other.number)) {
                return false;
            }
            if (!Objects.equals(this.date, other.date)) {
                return false;
            }
            return true;
        }
    }
}
