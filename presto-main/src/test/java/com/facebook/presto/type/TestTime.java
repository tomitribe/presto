/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.type;

import com.facebook.presto.operator.scalar.FunctionAssertions;
import com.facebook.presto.spi.Session;
import com.facebook.presto.spi.type.TimeWithTimeZone;
import com.facebook.presto.spi.type.TimeZoneKey;
import com.facebook.presto.spi.type.TimestampWithTimeZone;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Locale;

import static com.facebook.presto.spi.Session.DEFAULT_CATALOG;
import static com.facebook.presto.spi.Session.DEFAULT_SCHEMA;
import static com.facebook.presto.spi.type.TimeZoneKey.getTimeZoneKey;
import static com.facebook.presto.util.DateTimeZoneIndex.getDateTimeZone;

public class TestTime
{
    private static final TimeZoneKey TIME_ZONE_KEY = getTimeZoneKey("Europe/Berlin");
    private static final DateTimeZone DATE_TIME_ZONE = getDateTimeZone(TIME_ZONE_KEY);

    private FunctionAssertions functionAssertions;

    @BeforeClass
    public void setUp()
    {
        Session session = new Session("user", "test", DEFAULT_CATALOG, DEFAULT_SCHEMA, TIME_ZONE_KEY, Locale.ENGLISH, null, null);
        functionAssertions = new FunctionAssertions(session);
    }

    private void assertFunction(String projection, Object expected)
    {
        functionAssertions.assertFunction(projection, expected);
    }

    @Test
    public void testLiteral()
            throws Exception
    {
        assertFunction("TIME '03:04:05.321'", new Time(new DateTime(1970, 1, 1, 3, 4, 5, 321, DATE_TIME_ZONE).getMillis()));
        assertFunction("TIME '03:04:05'", new Time(new DateTime(1970, 1, 1, 3, 4, 5, 0, DATE_TIME_ZONE).getMillis()));
        assertFunction("TIME '03:04'", new Time(new DateTime(1970, 1, 1, 3, 4, 0, 0, DATE_TIME_ZONE).getMillis()));
    }

    @Test
    public void testEqual()
            throws Exception
    {
        assertFunction("TIME '03:04:05.321' = TIME '03:04:05.321'", true);

        assertFunction("TIME '03:04:05.321' = TIME '03:04:05.333'", false);
    }

    @Test
    public void testNotEqual()
            throws Exception
    {
        assertFunction("TIME '03:04:05.321' <> TIME '03:04:05.333'", true);

        assertFunction("TIME '03:04:05.321' <> TIME '03:04:05.321'", false);
    }

    @Test
    public void testLessThan()
            throws Exception
    {
        assertFunction("TIME '03:04:05.321' < TIME '03:04:05.333'", true);

        assertFunction("TIME '03:04:05.321' < TIME '03:04:05.321'", false);
        assertFunction("TIME '03:04:05.321' < TIME '03:04:05'", false);
    }

    @Test
    public void testLessThanOrEqual()
            throws Exception
    {
        assertFunction("TIME '03:04:05.321' <= TIME '03:04:05.333'", true);
        assertFunction("TIME '03:04:05.321' <= TIME '03:04:05.321'", true);

        assertFunction("TIME '03:04:05.321' <= TIME '03:04:05'", false);
    }

    @Test
    public void testGreaterThan()
            throws Exception
    {
        assertFunction("TIME '03:04:05.321' > TIME '03:04:05.111'", true);

        assertFunction("TIME '03:04:05.321' > TIME '03:04:05.321'", false);
        assertFunction("TIME '03:04:05.321' > TIME '03:04:05.333'", false);
    }

    @Test
    public void testGreaterThanOrEqual()
            throws Exception
    {
        assertFunction("TIME '03:04:05.321' >= TIME '03:04:05.111'", true);
        assertFunction("TIME '03:04:05.321' >= TIME '03:04:05.321'", true);

        assertFunction("TIME '03:04:05.321' >= TIME '03:04:05.333'", false);
    }

    @Test
    public void testBetween()
            throws Exception
    {
        assertFunction("TIME '03:04:05.321' between TIME '03:04:05.111' and TIME '03:04:05.333'", true);
        assertFunction("TIME '03:04:05.321' between TIME '03:04:05.321' and TIME '03:04:05.333'", true);
        assertFunction("TIME '03:04:05.321' between TIME '03:04:05.111' and TIME '03:04:05.321'", true);
        assertFunction("TIME '03:04:05.321' between TIME '03:04:05.321' and TIME '03:04:05.321'", true);

        assertFunction("TIME '03:04:05.321' between TIME '03:04:05.322' and TIME '03:04:05.333'", false);
        assertFunction("TIME '03:04:05.321' between TIME '03:04:05.311' and TIME '03:04:05.312'", false);
        assertFunction("TIME '03:04:05.321' between TIME '03:04:05.333' and TIME '03:04:05.111'", false);
    }

    @Test
    public void testCastToTimeWithTimeZone()
    {
        assertFunction("cast(TIME '03:04:05.321' as time with time zone)",
                new TimeWithTimeZone(new DateTime(1970, 1, 1, 3, 4, 5, 321, DATE_TIME_ZONE).getMillis(), DATE_TIME_ZONE.toTimeZone()));
    }

    @Test
    public void testCastToTimestamp()
            throws Exception
    {
        assertFunction("cast(TIME '03:04:05.321' as timestamp)", new Timestamp(new DateTime(1970, 1, 1, 3, 4, 5, 321, DATE_TIME_ZONE).getMillis()));
    }

    @Test
    public void testCastToTimestampWithTimeZone()
            throws Exception
    {
        assertFunction("cast(TIME '03:04:05.321' as timestamp with time zone)",
                new TimestampWithTimeZone(new DateTime(1970, 1, 1, 3, 4, 5, 321, DATE_TIME_ZONE).getMillis(), TIME_ZONE_KEY));
    }

    @Test
    public void testCastToSlice()
            throws Exception
    {
        assertFunction("cast(TIME '03:04:05.321' as varchar)", "03:04:05.321");
        assertFunction("cast(TIME '03:04:05' as varchar)", "03:04:05.000");
        assertFunction("cast(TIME '03:04' as varchar)", "03:04:00.000");
    }

    @Test
    public void testCastFromSlice()
            throws Exception
    {
        assertFunction("cast('03:04:05.321' as time) = TIME '03:04:05.321'", true);
        assertFunction("cast('03:04:05' as time) = TIME '03:04:05.000'", true);
        assertFunction("cast('03:04' as time) = TIME '03:04:00.000'", true);
    }
}
