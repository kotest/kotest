package io.kotlintest.properties

import io.kotlintest.TestBase
import io.kotlintest.TestCase
import io.kotlintest.TestFailedException

abstract class TableTesting : TestBase() {

  fun headers(a: String) = Headers1(a)

  fun headers(a: String, b: String) = Headers2(a, b)

  fun headers(a: String, b: String, c: String) = Headers3(a, b, c)

  fun headers(a: String, b: String, c: String, d: String) = Headers4(a, b, c, d)

  fun headers(a: String, b: String, c: String, d: String, e: String) = Headers5(a, b, c, d, e)

  fun <A> row(a: A) = Row1(a)

  fun <A, B> row(a: A, b: B) = Row2(a, b)

  fun <A, B, C> row(a: A, b: B, c: C) = Row3(a, b, c)

  fun <A, B, C, D> row(a: A, b: B, c: C, d: D) = Row4(a, b, c, d)

  fun <A, B, C, D, E> row(a: A, b: B, c: C, d: D, e: E) = Row5(a, b, c, d, e)

  fun <A> table(headers: Headers1, vararg rows: Row1<A>) = Table1(headers, rows.asList())

  fun <A, B> table(headers: Headers2, vararg rows: Row2<A, B>) = Table2(headers, rows.asList())

  fun <A, B, C> table(headers: Headers3, vararg rows: Row3<A, B, C>) = Table3(headers, rows.asList())

  fun <A, B, C, D> table(headers: Headers4, vararg rows: Row4<A, B, C, D>) = Table4(headers, rows.asList())

  fun <A, B, C, D, E> table(headers: Headers5, vararg rows: Row5<A, B, C, D, E>) = Table5(headers, rows.asList())

  fun error(e: TestFailedException, headers: List<String>, values: List<*>): TestFailedException {
    val params = headers.zip(values).joinToString(", ")
    return TestFailedException("Test failed for $params with error ${e.message}")
  }

  fun <A> String.forAll(table: Table1<A>, fn: (A) -> Unit): Unit {
    root.cases.add(TestCase(root, this, {
      for (row in table.rows) {
        try {
          fn(row.a)
        } catch (e: TestFailedException) {
          throw error(e, table.headers.values(), row.values())
        }
      }
    }))
  }

  fun <A, B> String.forAll(table: Table2<A, B>, fn: (A, B) -> Unit): Unit {
    root.cases.add(TestCase(root, this, {
      for (row in table.rows) {
        try {
          fn(row.a, row.b)
        } catch (e: TestFailedException) {
          throw error(e, table.headers.values(), row.values())
        }
      }
    }))
  }

  fun <A, B, C> String.forAll(table: Table3<A, B, C>, fn: (A, B, C) -> Unit): Unit {
    root.cases.add(TestCase(root, this, {
      for (row in table.rows) {
        try {
          fn(row.a, row.b, row.c)
        } catch (e: TestFailedException) {
          throw error(e, table.headers.values(), row.values())
        }
      }
    }))
  }

  fun <A, B, C, D> String.forAll(table: Table4<A, B, C, D>, fn: (A, B, C, D) -> Unit): TestCase {
    val case = TestCase(root, this, {
      for (row in table.rows) {
        try {
          fn(row.a, row.b, row.c, row.d)
        } catch (e: TestFailedException) {
          throw error(e, table.headers.values(), row.values())
        }
      }
    })
    root.cases.add(case)
    return case
  }

  fun <A, B, C, D, E> String.forAll(table: Table5<A, B, C, D, E>, fn: (A, B, C, D, E) -> Unit): Unit {
    root.cases.add(TestCase(root, this, {
      for (row in table.rows) {
        try {
          fn(row.a, row.b, row.c, row.d, row.e)
        } catch (e: TestFailedException) {
          throw error(e, table.headers.values(), row.values())
        }
      }
    }))
  }

}

data class Table1<A>(val headers: Headers1, val rows: List<Row1<A>>)

data class Table2<A, B>(val headers: Headers2, val rows: List<Row2<A, B>>)

data class Table3<A, B, C>(val headers: Headers3, val rows: List<Row3<A, B, C>>)

data class Table4<A, B, C, D>(val headers: Headers4, val rows: List<Row4<A, B, C, D>>)

data class Table5<A, B, C, D, E>(val headers: Headers5, val rows: List<Row5<A, B, C, D, E>>)

data class Headers1(val labelA: String) {
  fun values() = listOf(labelA)
}

data class Headers2(val labelA: String, val labelB: String) {
  fun values() = listOf(labelA, labelB)
}

data class Headers3(val labelA: String, val labelB: String, val labelC: String) {
  fun values() = listOf(labelA, labelB, labelC)
}

data class Headers4(val labelA: String, val labelB: String, val labelC: String, val labelD: String) {
  fun values() = listOf(labelA, labelB, labelC, labelD)
}

data class Headers5(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String) {
  fun values() = listOf(labelA, labelB, labelC, labelD, labelE)
}

data class Row1<A>(val a: A) {
  fun values() = listOf(a)
}

data class Row2<A, B>(val a: A, val b: B) {
  fun values() = listOf(a, b)
}

data class Row3<A, B, C>(val a: A, val b: B, val c: C) {
  fun values() = listOf(a, b, c)
}

data class Row4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D) {
  fun values() = listOf(a, b, c, d)
}

data class Row5<A, B, C, D, E>(val a: A, val b: B, val c: C, val d: D, val e: E) {
  fun values() = listOf(a, b, c, d, e)
}