package com.example.wsy.xpuzzle;

import android.test.InstrumentationTestCase;

import utils.GameUtils;

/**
 * Created by WSY on 2016-02-03-0003.
 */
public class TestClass extends InstrumentationTestCase {

    public void test() throws Exception {
        assertEquals(true, GameUtils.inOushuhang(16, 5));
    }
}
