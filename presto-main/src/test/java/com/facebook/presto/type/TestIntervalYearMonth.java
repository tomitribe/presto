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
import com.facebook.presto.spi.type.IntervalYearMonth;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestIntervalYearMonth
{
    private FunctionAssertions functionAssertions;

    @BeforeClass
    public void setUp()
    {
        functionAssertions = new FunctionAssertions();
    }

    private void assertFunction(String projection, Object expected)
    {
        functionAssertions.assertFunction(projection, expected);
    }

    @Test
    public void testLiteral()
            throws Exception
    {
        assertFunction("INTERVAL '124-30' YEAR TO MONTH", new IntervalYearMonth(124, 30));
        assertFunction("INTERVAL '124' YEAR TO MONTH", new IntervalYearMonth(124, 0));

        assertFunction("INTERVAL '124' YEAR", new IntervalYearMonth(124, 0));

        assertFunction("INTERVAL '30' MONTH", new IntervalYearMonth(0, 30));
    }

    @Test
    public void testAdd()
            throws Exception
    {
        assertFunction("INTERVAL '3' MONTH + INTERVAL '3' MONTH", new IntervalYearMonth(6));
        assertFunction("INTERVAL '6' YEAR + INTERVAL '6' YEAR", new IntervalYearMonth(12 * 12));
        assertFunction("INTERVAL '3' MONTH + INTERVAL '6' YEAR", new IntervalYearMonth((6 * 12) + (3)));
    }

    @Test
    public void testSubtract()
            throws Exception
    {
        assertFunction("INTERVAL '6' MONTH - INTERVAL '3' MONTH", new IntervalYearMonth(3));
        assertFunction("INTERVAL '9' YEAR - INTERVAL '6' YEAR", new IntervalYearMonth(3 * 12));
        assertFunction("INTERVAL '3' MONTH - INTERVAL '6' YEAR", new IntervalYearMonth((3) - (6 * 12)));
    }

    @Test
    public void testMultiply()
            throws Exception
    {
        assertFunction("INTERVAL '6' MONTH * 2", new IntervalYearMonth(12));
        assertFunction("2 * INTERVAL '6' MONTH", new IntervalYearMonth(12));
        assertFunction("INTERVAL '10' MONTH * 2.5", new IntervalYearMonth(25));
        assertFunction("2.5 * INTERVAL '10' MONTH", new IntervalYearMonth(25));

        assertFunction("INTERVAL '6' YEAR * 2", new IntervalYearMonth(12 * 12));
        assertFunction("2 * INTERVAL '6' YEAR", new IntervalYearMonth(12 * 12));
        assertFunction("INTERVAL '1' YEAR * 2.5", new IntervalYearMonth((long) (2.5 * 12)));
        assertFunction("2.5 * INTERVAL '1' YEAR", new IntervalYearMonth((long) (2.5 * 12)));
    }

    @Test
    public void testDivide()
            throws Exception
    {
        assertFunction("INTERVAL '30' MONTH / 2", new IntervalYearMonth(15));
        assertFunction("INTERVAL '60' MONTH / 2.5", new IntervalYearMonth(24));

        assertFunction("INTERVAL '3' YEAR / 2", new IntervalYearMonth(18));
        assertFunction("INTERVAL '4' YEAR / 4.8", new IntervalYearMonth(10));
    }

    @Test
    public void testNegation()
            throws Exception
    {
        assertFunction("- INTERVAL '3' MONTH", new IntervalYearMonth(-3));
        assertFunction("- INTERVAL '6' YEAR", new IntervalYearMonth(-72));
    }

    @Test
    public void testEqual()
            throws Exception
    {
        assertFunction("INTERVAL '3' MONTH = INTERVAL '3' MONTH", true);
        assertFunction("INTERVAL '6' YEAR = INTERVAL '6' YEAR", true);

        assertFunction("INTERVAL '3' MONTH = INTERVAL '4' MONTH", false);
        assertFunction("INTERVAL '7' YEAR = INTERVAL '6' YEAR", false);
    }

    @Test
    public void testNotEqual()
            throws Exception
    {
        assertFunction("INTERVAL '3' MONTH <> INTERVAL '4' MONTH", true);
        assertFunction("INTERVAL '6' YEAR <> INTERVAL '7' YEAR", true);

        assertFunction("INTERVAL '3' MONTH <> INTERVAL '3' MONTH", false);
        assertFunction("INTERVAL '6' YEAR <> INTERVAL '6' YEAR", false);
    }

    @Test
    public void testLessThan()
            throws Exception
    {
        assertFunction("INTERVAL '3' MONTH < INTERVAL '4' MONTH", true);
        assertFunction("INTERVAL '6' YEAR < INTERVAL '7' YEAR", true);

        assertFunction("INTERVAL '3' MONTH < INTERVAL '3' MONTH", false);
        assertFunction("INTERVAL '3' MONTH < INTERVAL '2' MONTH", false);
        assertFunction("INTERVAL '6' YEAR < INTERVAL '6' YEAR", false);
        assertFunction("INTERVAL '6' YEAR < INTERVAL '5' YEAR", false);
    }

    @Test
    public void testLessThanOrEqual()
            throws Exception
    {
        assertFunction("INTERVAL '3' MONTH <= INTERVAL '4' MONTH", true);
        assertFunction("INTERVAL '3' MONTH <= INTERVAL '3' MONTH", true);
        assertFunction("INTERVAL '6' YEAR <= INTERVAL '6' YEAR", true);
        assertFunction("INTERVAL '6' YEAR <= INTERVAL '7' YEAR", true);

        assertFunction("INTERVAL '3' MONTH <= INTERVAL '2' MONTH", false);
        assertFunction("INTERVAL '6' YEAR <= INTERVAL '5' YEAR", false);
    }

    @Test
    public void testGreaterThan()
            throws Exception
    {
        assertFunction("INTERVAL '3' MONTH > INTERVAL '2' MONTH", true);
        assertFunction("INTERVAL '6' YEAR > INTERVAL '5' YEAR", true);

        assertFunction("INTERVAL '3' MONTH > INTERVAL '3' MONTH", false);
        assertFunction("INTERVAL '3' MONTH > INTERVAL '4' MONTH", false);
        assertFunction("INTERVAL '6' YEAR > INTERVAL '6' YEAR", false);
        assertFunction("INTERVAL '6' YEAR > INTERVAL '7' YEAR", false);
    }

    @Test
    public void testGreaterThanOrEqual()
            throws Exception
    {
        assertFunction("INTERVAL '3' MONTH >= INTERVAL '2' MONTH", true);
        assertFunction("INTERVAL '3' MONTH >= INTERVAL '3' MONTH", true);
        assertFunction("INTERVAL '6' YEAR >= INTERVAL '5' YEAR", true);
        assertFunction("INTERVAL '6' YEAR >= INTERVAL '6' YEAR", true);

        assertFunction("INTERVAL '3' MONTH >= INTERVAL '4' MONTH", false);
        assertFunction("INTERVAL '6' YEAR >= INTERVAL '7' YEAR", false);
    }

    @Test
    public void testBetween()
            throws Exception
    {
        assertFunction("INTERVAL '3' MONTH between INTERVAL '2' MONTH and INTERVAL '4' MONTH", true);
        assertFunction("INTERVAL '3' MONTH between INTERVAL '3' MONTH and INTERVAL '4' MONTH", true);
        assertFunction("INTERVAL '3' MONTH between INTERVAL '2' MONTH and INTERVAL '3' MONTH", true);
        assertFunction("INTERVAL '3' MONTH between INTERVAL '3' MONTH and INTERVAL '3' MONTH", true);

        assertFunction("INTERVAL '3' MONTH between INTERVAL '4' MONTH and INTERVAL '5' MONTH", false);
        assertFunction("INTERVAL '3' MONTH between INTERVAL '1' MONTH and INTERVAL '2' MONTH", false);
        assertFunction("INTERVAL '3' MONTH between INTERVAL '4' MONTH and INTERVAL '2' MONTH", false);
    }

    @Test
    public void testCastToSlice()
            throws Exception
    {
        assertFunction("cast(INTERVAL '124-30' YEAR TO MONTH as varchar)", "126-6");
        assertFunction("cast(INTERVAL '124-30' YEAR TO MONTH as varchar)", new IntervalYearMonth(124, 30).toString());

        assertFunction("cast(INTERVAL '124' YEAR TO MONTH as varchar)", new IntervalYearMonth(124, 0).toString());
        assertFunction("cast(INTERVAL '124' YEAR as varchar)", new IntervalYearMonth(124, 0).toString());

        assertFunction("cast(INTERVAL '30' MONTH as varchar)", new IntervalYearMonth(0, 30).toString());
    }
}
