/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.geometry;

import org.elasticsearch.geo.GeometryTestUtils;
import org.elasticsearch.geometry.utils.GeographyValidator;
import org.elasticsearch.geometry.utils.StandardValidator;
import org.elasticsearch.geometry.utils.WellKnownText;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MultiPointTests extends BaseGeometryTestCase<MultiPoint> {

    @Override
    protected MultiPoint createTestInstance(boolean hasAlt) {
        int size = randomIntBetween(1, 10);
        List<Point> arr = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            arr.add(GeometryTestUtils.randomPoint(hasAlt));
        }
        return new MultiPoint(arr);
    }

    public void testBasicSerialization() throws IOException, ParseException {
        WellKnownText wkt = new WellKnownText(true, new GeographyValidator(true));
        assertEquals("MULTIPOINT (2.0 1.0)", wkt.toWKT(
            new MultiPoint(Collections.singletonList(new Point(2, 1)))));
        assertEquals(new MultiPoint(Collections.singletonList(new Point(2, 1))),
            wkt.fromWKT("MULTIPOINT (2 1)"));

        assertEquals("MULTIPOINT (2.0 1.0, 3.0 4.0)",
            wkt.toWKT(new MultiPoint(Arrays.asList(new Point(2, 1), new Point(3, 4)))));
        assertEquals(new MultiPoint(Arrays.asList(new Point(2, 1), new Point(3, 4))),
            wkt.fromWKT("MULTIPOINT (2 1, 3 4)"));

        assertEquals("MULTIPOINT (2.0 1.0 10.0, 3.0 4.0 20.0)",
            wkt.toWKT(new MultiPoint(Arrays.asList(new Point(2, 1, 10), new Point(3, 4, 20)))));
        assertEquals(new MultiPoint(Arrays.asList(new Point(2, 1, 10), new Point(3, 4, 20))),
            wkt.fromWKT("MULTIPOINT (2 1 10, 3 4 20)"));

        assertEquals("MULTIPOINT EMPTY", wkt.toWKT(MultiPoint.EMPTY));
        assertEquals(MultiPoint.EMPTY, wkt.fromWKT("MULTIPOINT EMPTY)"));
    }

    public void testValidation() {
        IllegalArgumentException ex = expectThrows(IllegalArgumentException.class, () -> new StandardValidator(false).validate(
            new MultiPoint(Collections.singletonList(new Point(2, 1, 3)))));
        assertEquals("found Z value [3.0] but [ignore_z_value] parameter is [false]", ex.getMessage());

        new StandardValidator(true).validate(new MultiPoint(Collections.singletonList(new Point(2, 1, 3))));
    }
}
