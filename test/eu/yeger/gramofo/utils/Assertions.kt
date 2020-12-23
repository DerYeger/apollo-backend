package eu.yeger.gramofo.utils

import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

infix fun Any?.shouldBe(expected: Any?) =
    assertEquals(expected = expected, actual = this)

inline infix fun <reified T> Any?.shouldBeInstanceOf(clazz: Class<T>) {
    assertTrue(this is T)
}

infix fun Any?.shouldNotBe(illegal: Any?) =
    assertNotEquals(illegal = illegal, actual = this)
