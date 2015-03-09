package net.jhorstmann.sprocwrapperng;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class NamingTest {
    private final String underscored;
    private final String camelcased;

    public NamingTest(String underscored, String camelcased) {
        this.underscored = underscored;
        this.camelcased = camelcased;
    }

    @Parameterized.Parameters(name = "{1} -> {2}")
    public static Collection<Object[]> parameters() {
        return asList(new Object[][]{
                {"order_number", "orderNumber"}
        });
    }

    @Test
    public void shouldConvertToCamelCase() {

        assertEquals(camelcased, Naming.underscoreToCamel(underscored));
    }
}
