package org.tastefuljava.json;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class JSonTest {
    private static final Logger LOG
            = Logger.getLogger(JSonTest.class.getName());

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
        LOG.info("JSon: " + json1);
        TestObject obj2 = JSon.parse(json1, TestObject.class);
        assertEquals(obj1, obj2);
        String json2 = JSon.stringify(obj2, false);
        assertEquals(json1, json2);
    }

    @Test
    public void testFormatted() {
        try {
            TestObject obj1 = new TestObject(
                    BigDecimal.valueOf(123, 2), new Date(), "Hello world!!!",
                    new int[] {1,2,3});
            String json1 = JSon.stringify(obj1, true);
            LOG.info("JSon: " + json1);
            TestObject obj2 = JSon.parse(json1, TestObject.class);
            assertEquals(obj1, obj2);
            String json2 = JSon.stringify(obj2, true);
            assertEquals(json1, json2);
        } catch (Throwable ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    public static class TestObject {
        private final BigDecimal number;
        private final Date date;
        private final String string;
        private final int[] array;
        private final List<Integer> list;

        public TestObject() {
            this(null, null, null, null);
        }

        public TestObject(BigDecimal number, Date date, String string,
                int[] array) {
            this.number = number;
            this.date = date;
            this.string = string;
            this.array = array;
            if (array == null) {
                this.list = null;
            } else {
                this.list = new ArrayList<>();
                for (int val: array) {
                    list.add(val);
                }
            }
        }

        public String getMessage() {
            return "Test message";
        }

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
