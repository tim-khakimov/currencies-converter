package com.timkhakimov.currenciesconverter

import io.mockk.MockKAnnotations
import org.junit.Before

abstract class BaseTestCase {

    @Before
    open fun before() {
        MockKAnnotations.init(this)
        initBeforeEachTest()
    }

    open fun initBeforeEachTest() {}
}