package eu.yeger.apollo.utils

import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

infix fun Any?.shouldBe(expected: Any?) =
  assertEquals(expected = expected, actual = this)

infix fun Any?.shouldNotBe(illegal: Any?) =
  assertNotEquals(illegal = illegal, actual = this)

fun <T> checkRecursive(current: T, getNext: (T) -> List<T>?, check: (T) -> Unit) {
  check(current)
  getNext(current)?.forEach { next -> checkRecursive(next, getNext, check) }
}
