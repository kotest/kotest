package io.kotlintest.experimental.mockk

import io.kotlintest.specs.StringSpec
import org.junit.Assert
import org.junit.runner.RunWith

@RunWith(MockKJUnitRunner::class)
class MockKTestSuite : StringSpec({

    val mock = mockk<MockCls>()
    val spy = spyk(MockCls())

    "partly argument matching" {

        every { mock.manyArgsOp(a = eq(false)) } returns 1.0
        every { mock.manyArgsOp(b = eq(false)) } returns 2.0
        every { mock.manyArgsOp(c = eq(33)) } returns 3.0
        every { mock.manyArgsOp(d = eq(33)) } returns 4.0
        every { mock.manyArgsOp(e = eq(33)) } returns 5.0
        every { mock.manyArgsOp(f = eq(33)) } returns 6.0
        every { mock.manyArgsOp(g = eq(33.toChar())) } returns 7.0
        every { mock.manyArgsOp(h = eq(33.toChar())) } returns 8.0
        every { mock.manyArgsOp(i = eq(33)) } returns 9.0
        every { mock.manyArgsOp(j = eq(33)) } returns 10.0
        every { mock.manyArgsOp(k = eq(33)) } returns 11.0
        every { mock.manyArgsOp(l = eq(33)) } returns 12.0
        every { mock.manyArgsOp(m = eq(33.0f)) } returns 13.0
        every { mock.manyArgsOp(n = eq(33.0f)) } returns 14.0
        every { mock.manyArgsOp(o = eq(33.0)) } returns 15.0
        every { mock.manyArgsOp(p = eq(33.0)) } returns 16.0
        every { mock.manyArgsOp(q = eq("33")) } returns 17.0
        every { mock.manyArgsOp(r = eq("33")) } returns 18.0
        every { mock.manyArgsOp(s = eq(IntWrapper(33))) } returns 19.0
        every { mock.manyArgsOp(t = eq(IntWrapper(33))) } returns 20.0

        Assert.assertEquals(155.0, spy.manyArgsOp(), 1e-6)
        Assert.assertEquals(0.0, mock.manyArgsOp(), 1e-6)
        Assert.assertEquals(1.0, mock.manyArgsOp(a = false), 1e-6)
        Assert.assertEquals(2.0, mock.manyArgsOp(b = false), 1e-6)
        Assert.assertEquals(3.0, mock.manyArgsOp(c = 33), 1e-6)
        Assert.assertEquals(4.0, mock.manyArgsOp(d = 33), 1e-6)
        Assert.assertEquals(5.0, mock.manyArgsOp(e = 33), 1e-6)
        Assert.assertEquals(6.0, mock.manyArgsOp(f = 33), 1e-6)
        Assert.assertEquals(7.0, mock.manyArgsOp(g = 33.toChar()), 1e-6)
        Assert.assertEquals(8.0, mock.manyArgsOp(h = 33.toChar()), 1e-6)
        Assert.assertEquals(9.0, mock.manyArgsOp(i = 33), 1e-6)
        Assert.assertEquals(10.0, mock.manyArgsOp(j = 33), 1e-6)
        Assert.assertEquals(11.0, mock.manyArgsOp(k = 33), 1e-6)
        Assert.assertEquals(12.0, mock.manyArgsOp(l = 33), 1e-6)
        Assert.assertEquals(13.0, mock.manyArgsOp(m = 33.0f), 1e-6)
        Assert.assertEquals(14.0, mock.manyArgsOp(n = 33.0f), 1e-6)
        Assert.assertEquals(15.0, mock.manyArgsOp(o = 33.0), 1e-6)
        Assert.assertEquals(16.0, mock.manyArgsOp(p = 33.0), 1e-6)
        Assert.assertEquals(17.0, mock.manyArgsOp(q = "33"), 1e-6)
        Assert.assertEquals(18.0, mock.manyArgsOp(r = "33"), 1e-6)
        Assert.assertEquals(19.0, mock.manyArgsOp(s = IntWrapper(33)), 1e-6)
        Assert.assertEquals(20.0, mock.manyArgsOp(t = IntWrapper(33)), 1e-6)

        verify { mock.manyArgsOp(a = eq(false)) }
        verify { mock.manyArgsOp(b = eq(false)) }
        verify { mock.manyArgsOp(c = eq(33)) }
        verify { mock.manyArgsOp(d = eq(33)) }
        verify { mock.manyArgsOp(e = eq(33)) }
        verify { mock.manyArgsOp(f = eq(33)) }
        verify { mock.manyArgsOp(g = eq(33.toChar())) }
        verify { mock.manyArgsOp(h = eq(33.toChar())) }
        verify { mock.manyArgsOp(i = eq(33)) }
        verify { mock.manyArgsOp(j = eq(33)) }
        verify { mock.manyArgsOp(k = eq(33)) }
        verify { mock.manyArgsOp(l = eq(33)) }
        verify { mock.manyArgsOp(m = eq(33.0f)) }
        verify { mock.manyArgsOp(n = eq(33.0f)) }
        verify { mock.manyArgsOp(o = eq(33.0)) }
        verify { mock.manyArgsOp(p = eq(33.0)) }
        verify { mock.manyArgsOp(q = eq("33")) }
        verify { mock.manyArgsOp(r = eq("33")) }
        verify { mock.manyArgsOp(s = eq(IntWrapper(33))) }
        verify { mock.manyArgsOp(t = eq(IntWrapper(33))) }
    }

    "chained calls" {
        every { mock.chainOp(1, 2).otherOp(3, 4) } returns 1
        every { mock.chainOp(5, 6).otherOp(7, 8) } returns 2
        every { mock.chainOp(9, 10).otherOp(11, 12) } returns 3

        Assert.assertEquals(1, mock.chainOp(1, 2).otherOp(3, 4))
        Assert.assertEquals(2, mock.chainOp(5, 6).otherOp(7, 8))
        Assert.assertEquals(3, mock.chainOp(9, 10).otherOp(11, 12))

        verify {
            mock.chainOp(1, 2).otherOp(3, 4)
            mock.chainOp(9, 10).otherOp(11, 12)
            mock.chainOp(5, 6).otherOp(7, 8)
        }
        verifyOrder {
            mock.chainOp(1, 2).otherOp(3, 4)
            mock.chainOp(5, 6).otherOp(7, 8)
        }
        verifyOrder {
            mock.chainOp(1, 2).otherOp(3, 4)
            mock.chainOp(9, 10).otherOp(11, 12)
        }
        verifyOrder {
            mock.chainOp(5, 6).otherOp(7, 8)
            mock.chainOp(9, 10).otherOp(11, 12)
        }
        verifySequence {
            mock.chainOp(1, 2).otherOp(3, 4)
            mock.chainOp(5, 6).otherOp(7, 8)
            mock.chainOp(9, 10).otherOp(11, 12)
        }
    }

    "clearMocks" {
        every { mock.otherOp(0, 2) } returns 5

        Assert.assertEquals(5, mock.otherOp(0, 2))
        clearMocks(mock, answers = false)
        Assert.assertEquals(5, mock.otherOp(0, 2))
        clearMocks(mock)
        Assert.assertEquals(0, mock.otherOp(0, 2))

        verifySequence {
            mock.otherOp(0, 2)
        }
    }

    "atLeast, atMost, exactly" {
        every { mock.otherOp(0, 2) } throws RuntimeException("test")
        every { mock.otherOp(1, 3) } returnsMany listOf(1, 2, 3)

        try {
            mock.otherOp(0, 2)
        } catch (ex: RuntimeException) {
            Assert.assertEquals("test", ex.message)
        }
        Assert.assertEquals(1, mock.otherOp(1, 3))
        Assert.assertEquals(2, mock.otherOp(1, 3))
        Assert.assertEquals(3, mock.otherOp(1, 3))
        Assert.assertEquals(3, mock.otherOp(1, 3))

        verify(atLeast = 4) {
            mock.otherOp(1, 3)
        }
        verify(atLeast = 5, inverse = true) {
            mock.otherOp(1, 3)
        }
        verify(exactly = 4) {
            mock.otherOp(1, 3)
        }
        verify(exactly = 3, inverse = true) {
            mock.otherOp(1, 3)
        }
        verify(atMost = 4) {
            mock.otherOp(1, 3)
        }
        verify(atMost = 3, inverse = true) {
            mock.otherOp(1, 3)
        }
        verify(exactly = 0) {
            mock.otherOp(1, 4)
        }
        verify(exactly = 1, inverse = true) {
            mock.otherOp(1, 4)
        }
        verify(exactly = 1) {
            mock.otherOp(0, 2)
        }
        verify(exactly = 2, inverse = true) {
            mock.otherOp(0, 2)
        }
        verify(exactly = 0, inverse = true) {
            mock.otherOp(0, 2)
        }
    }

    "stubbing actions" {
        every { mock.otherOp(0, 2) } throws RuntimeException("test")
        every { mock.otherOp(1, 3) } returnsMany listOf(1, 2, 3)

        try {
            mock.otherOp(0, 2)
        } catch (ex: RuntimeException) {
            Assert.assertEquals("test", ex.message)
        }
        Assert.assertEquals(1, mock.otherOp(1, 3))
        Assert.assertEquals(2, mock.otherOp(1, 3))
        Assert.assertEquals(3, mock.otherOp(1, 3))
        Assert.assertEquals(3, mock.otherOp(1, 3))

        verify { mock.otherOp(0, 2) }
        verifyOrder {
            mock.otherOp(1, 3)
            mock.otherOp(1, 3)
            mock.otherOp(1, 3)
            mock.otherOp(1, 3)
        }
        verifySequence {
            mock.otherOp(0, 2)
            mock.otherOp(1, 3)
            mock.otherOp(1, 3)
            mock.otherOp(1, 3)
            mock.otherOp(1, 3)
        }
    }

    "answers" {
        val lst = mutableListOf<Byte>()
        val slot = slot<() -> Int>()

        every { spy.manyArgsOp(a = any(), c = 5) } answers { if (firstArg()) 1.0 else 2.0 }
        every { spy.manyArgsOp(b = any(), c = 6) } answers { if (secondArg()) 3.0 else 4.0 }
        every { spy.manyArgsOp(c = 7) } answers { thirdArg<Byte>().toDouble() - 2 }
        every { spy.manyArgsOp(t = any(), c = 8) } answers { lastArg<IntWrapper>().data.toDouble() }
        every { spy.manyArgsOp(c = 9) } answers { nArgs.toDouble() }
        every { spy.manyArgsOp(c = 10) } answers { spiedObj<MockCls>().otherOp(args[8] as Int).toDouble() }
        every { spy.manyArgsOp(c = 11) } answers { method.parameterCount.toDouble() }
        every { spy.manyArgsOp(d = capture(lst), c = 12) } answers { lst.captured().toDouble() }
        every { spy.lambdaOp(1, capture(slot)) } answers { 1 - slot.invoke<Int>()!! }

        Assert.assertEquals(155.0, spy.manyArgsOp(), 1e-6)
        Assert.assertEquals(1.0, spy.manyArgsOp(c = 5), 1e-6)
        Assert.assertEquals(2.0, spy.manyArgsOp(false, c = 5), 1e-6)
        Assert.assertEquals(3.0, spy.manyArgsOp(c = 6), 1e-6)
        Assert.assertEquals(4.0, spy.manyArgsOp(b = false, c = 6), 1e-6)
        Assert.assertEquals(5.0, spy.manyArgsOp(c = 7), 1e-6)
        Assert.assertEquals(6.0, spy.manyArgsOp(c = 8, t = IntWrapper(6)), 1e-6)
        Assert.assertEquals(20.0, spy.manyArgsOp(c = 9), 1e-6)
        Assert.assertEquals(9.0, spy.manyArgsOp(c = 10), 1e-6)
        Assert.assertEquals(20.0, spy.manyArgsOp(c = 11), 1e-6)
        Assert.assertEquals(10.0, spy.manyArgsOp(d = 10, c = 12), 1e-6)
        Assert.assertEquals(11.0, spy.manyArgsOp(d = 11, c = 12), 1e-6)
        Assert.assertEquals(-2, spy.lambdaOp(1, { 3 }))


        Assert.assertEquals(listOf(10.toByte(), 11.toByte()), lst)

        verify { spy.manyArgsOp() }
        verify { spy.manyArgsOp(c = 5) }
        verify { spy.manyArgsOp(false, c = 5) }
        verify { spy.manyArgsOp(c = 6) }
        verify { spy.manyArgsOp(b = false, c = 6) }
        verify { spy.manyArgsOp(c = 7) }
        verify { spy.manyArgsOp(c = 8, t = IntWrapper(6)) }
        verify { spy.manyArgsOp(c = 9) }
        verify { spy.manyArgsOp(c = 10) }
        verify { spy.manyArgsOp(c = 11) }
        verify { spy.manyArgsOp(d = 10, c = 12) }
        verify { spy.manyArgsOp(d = 11, c = 12) }
        verify { spy.manyArgsOp(d = 11, c = 12) }
        verify { spy.lambdaOp(1, any()) }
    }

    "verify, verifyOrder, verifySequence" {
        every { spy.manyArgsOp(c = 5) } returns 1.0
        every { spy.manyArgsOp(c = 6) } returns 2.0
        every { spy.manyArgsOp(c = 7) } returns 3.0

        Assert.assertEquals(1.0, spy.manyArgsOp(c = 5), 1e-6)
        Assert.assertEquals(2.0, spy.manyArgsOp(c = 6), 1e-6)
        Assert.assertEquals(3.0, spy.manyArgsOp(c = 7), 1e-6)

        verify {
            spy.manyArgsOp(c = 6)
            spy.manyArgsOp(c = 5)
        }
        verify(inverse = true) {
            spy.manyArgsOp(c = 6)
            spy.manyArgsOp(c = 8)
        }
        verify(inverse = true) {
            spy.manyArgsOp(c = 4)
            spy.manyArgsOp(c = 8)
        }

        verifyOrder {
            spy.manyArgsOp(c = 5)
            spy.manyArgsOp(c = 7)
        }
        verifyOrder {
            spy.manyArgsOp(c = 5)
            spy.manyArgsOp(c = 6)
        }
        verifyOrder {
            spy.manyArgsOp(c = 6)
            spy.manyArgsOp(c = 7)
        }
        verifyOrder(inverse = true) {
            spy.manyArgsOp(c = 7)
            spy.manyArgsOp(c = 5)
        }
        verifyOrder(inverse = true) {
            spy.manyArgsOp(c = 5)
            spy.manyArgsOp(c = 4)
        }
        verifyOrder(inverse = true) {
            spy.manyArgsOp(c = 4)
            spy.manyArgsOp(c = 8)
        }
        verifySequence {
            spy.manyArgsOp(c = 5)
            spy.manyArgsOp(c = 6)
            spy.manyArgsOp(c = 7)
        }
        verifySequence(inverse = true) {
            spy.manyArgsOp(c = 6)
            spy.manyArgsOp(c = 7)
        }
        verifySequence(inverse = true) {
            spy.manyArgsOp(c = 7)
            spy.manyArgsOp(c = 6)
            spy.manyArgsOp(c = 5)
        }
        verifySequence(inverse = true) {
            spy.manyArgsOp(c = 6)
            spy.manyArgsOp(c = 5)
            spy.manyArgsOp(c = 7)
        }
    }

    "matchers" {
        val a = IntWrapper(3)
        val b = IntWrapper(4)

        every { mock.otherOp(eq(a), refEq(b)) } returns 1

        every { mock.otherOp(1, less(2)) } returns 2
        every { mock.otherOp(1, cmpEq(2)) } returns 3
        every { mock.otherOp(1, more(2)) } returns 4

        every { mock.otherOp(2, less(1, andEquals = true)) } returns 5
        every { mock.otherOp(2, cmpEq(2)) } returns 6
        every { mock.otherOp(2, more(3, andEquals = true)) } returns 7

        every { mock.otherOp(3, or(eq(3), eq(5))) } returns 8
        every { mock.otherOp(3, and(more(8), less(15))) } returns 9
        every { mock.otherOp(3, or(more(20, andEquals = true), 17)) } returns 10
        every { mock.otherOp(4, not(13)) } returns 11

        every { mock.otherOp(5, or(or(more(20), 17), 13)) } returns 12

        val v = slot<Int>()
        every { mock.otherOp(6, and(capture(v), more(20))) } answers { v.captured }

        every { mock.otherOp(a = IntWrapper(7), b = isNull()) } returns 13
        every { mock.otherOp(a = IntWrapper(8), b = isNull(true)) } returns 14

        every { mock.otherOp(a = IntWrapper(9), b = ofType(IntWrapper::class.java)) } returns 15

        Assert.assertEquals(1, mock.otherOp(a, b))
        Assert.assertEquals(0, mock.otherOp(IntWrapper(3), IntWrapper(4)))
        Assert.assertEquals(1, mock.otherOp(IntWrapper(3), b))

        Assert.assertEquals(2, mock.otherOp(1, 1))
        Assert.assertEquals(3, mock.otherOp(1, 2))
        Assert.assertEquals(4, mock.otherOp(1, 3))

        Assert.assertEquals(5, mock.otherOp(2, 1))
        Assert.assertEquals(6, mock.otherOp(2, 2))
        Assert.assertEquals(7, mock.otherOp(2, 3))

        Assert.assertEquals(8, mock.otherOp(3, 3))
        Assert.assertEquals(0, mock.otherOp(3, 4))
        Assert.assertEquals(8, mock.otherOp(3, 5))
        Assert.assertEquals(0, mock.otherOp(3, 8))
        Assert.assertEquals(9, mock.otherOp(3, 9))
        Assert.assertEquals(9, mock.otherOp(3, 11))
        Assert.assertEquals(9, mock.otherOp(3, 14))
        Assert.assertEquals(0, mock.otherOp(3, 15))
        Assert.assertEquals(0, mock.otherOp(3, 19))
        Assert.assertEquals(10, mock.otherOp(3, 20))
        Assert.assertEquals(10, mock.otherOp(3, 100))

        Assert.assertEquals(11, mock.otherOp(4, 12))
        Assert.assertEquals(0, mock.otherOp(4, 13))
        Assert.assertEquals(11, mock.otherOp(4, 14))

        Assert.assertEquals(0, mock.otherOp(5, 12))
        Assert.assertEquals(12, mock.otherOp(5, 13))
        Assert.assertEquals(0, mock.otherOp(5, 14))
        Assert.assertEquals(0, mock.otherOp(5, 16))
        Assert.assertEquals(12, mock.otherOp(5, 17))
        Assert.assertEquals(0, mock.otherOp(5, 18))
        Assert.assertEquals(0, mock.otherOp(5, 20))
        Assert.assertEquals(12, mock.otherOp(5, 21))
        Assert.assertEquals(12, mock.otherOp(5, 100))

        Assert.assertEquals(0, mock.otherOp(6, 19))
        Assert.assertEquals(0, mock.otherOp(6, 20))
        Assert.assertEquals(21, mock.otherOp(6, 21))
        Assert.assertEquals(22, mock.otherOp(6, 22))
        Assert.assertEquals(23, mock.otherOp(6, 23))
        Assert.assertEquals(100, mock.otherOp(6, 100))

        Assert.assertEquals(13, mock.otherOp(IntWrapper(7), null))
        Assert.assertEquals(0, mock.otherOp(IntWrapper(7), IntWrapper(3)))

        Assert.assertEquals(0, mock.otherOp(IntWrapper(8), null))
        Assert.assertEquals(14, mock.otherOp(IntWrapper(8), IntWrapper(3)))

        Assert.assertEquals(15, mock.otherOp(IntWrapper(9), IntWrapper(3)))
        Assert.assertEquals(0, mock.otherOp(IntWrapper(9), DoubleWrapper(3.0)))

        verify {
            mock.otherOp(a, b)
            mock.otherOp(IntWrapper(3), IntWrapper(4))
            mock.otherOp(IntWrapper(3), b)

            mock.otherOp(1, 1)
            mock.otherOp(1, 2)
            mock.otherOp(1, 3)

            mock.otherOp(2, 1)
            mock.otherOp(2, 2)
            mock.otherOp(2, 3)

            mock.otherOp(3, or(3, 5))
        }
    }

    "arrays" {
        every { mock.arrayOp(arrayOf(1, 2, 3)) } returns arrayOf(3, 2, 1).toIntArray()
        // TODO check all kinds of arrays

        Assert.assertArrayEquals(arrayOf(3, 2, 1).toIntArray(), mock.arrayOp(arrayOf(1, 2, 3)))

        verify {
            mock.arrayOp(arrayOf(1, 2, 3))
        }
    }
})

interface Wrapper

data class IntWrapper(val data: Int) : Wrapper
data class DoubleWrapper(val data: Double) : Wrapper

class MockCls {
    fun manyArgsOp(a: Boolean = true, b: Boolean = true,
                   c: Byte = 1, d: Byte = 2,
                   e: Short = 3, f: Short = 4,
                   g: Char = 5.toChar(), h: Char = 6.toChar(),
                   i: Int = 7, j: Int = 8,
                   k: Long = 9, l: Long = 10,
                   m: Float = 10.0f, n: Float = 11.0f,
                   o: Double = 12.0, p: Double = 13.0,
                   q: String = "14", r: String = "15",
                   s: IntWrapper = IntWrapper(16), t: IntWrapper = IntWrapper(17)): Double {

        return (if (a) 0 else -1) + (if (b) 0 else -2) + c + d + e + f + g.toByte() + h.toByte() +
                i + k + l + m + n + o + p + q.toInt() + r.toInt() + s.data + t.data
    }

    fun otherOp(a: Int = 1, b: Int = 2): Int = a + b
    fun lambdaOp(a: Int, b: () -> Int) = a + b()
    fun otherOp(a: Wrapper? = IntWrapper(1), b: Wrapper? = IntWrapper(2)): Int {
        return if (a is IntWrapper && b is IntWrapper) {
            a.data + b.data
        } else {
            0
        }
    }

    fun arrayOp(arr: Array<Int>) = arr.map { it + 1 }.toIntArray()

    fun chainOp(a: Int = 1, b: Int = 2) = MockCls()
}
