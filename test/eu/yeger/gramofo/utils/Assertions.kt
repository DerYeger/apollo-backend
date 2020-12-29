package eu.yeger.gramofo.utils

import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

infix fun Any?.shouldBe(expected: Any?) =
    assertEquals(expected = expected, actual = this)

infix fun Any?.shouldNotBe(illegal: Any?) =
    assertNotEquals(illegal = illegal, actual = this)
