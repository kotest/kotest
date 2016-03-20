package com.sksamuel.kotlintest

interface IntMatchers {

  infix fun Be<String>.gt(expected: Int): (Int) -> Unit {
    return { value: Int ->
      if (value <= expected)
        throw TestFailedException("$value is not greater than $expected")
    }
  }

  infix fun Be<String>.lt(expected: Int): (Int) -> Unit {
    return { value: Int ->
      if (value >= expected)
        throw TestFailedException("$value is not less than $expected")
    }
  }

  infix fun Be<String>.gte(expected: Int): (Int) -> Unit {
    return { value: Int ->
      if (value < expected)
        throw TestFailedException("$value is not greater than or equal to $expected")
    }
  }

  infix fun Be<String>.lte(expected: Int): (Int) -> Unit {
    return { value: Int ->
      if (value > expected)
        throw TestFailedException("$value is not less than or equal to $expected")
    }
  }
}