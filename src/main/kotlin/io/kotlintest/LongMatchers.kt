package io.kotlintest

interface LongMatchers {

  infix fun Be<String>.gt(expected: Long): (Long) -> Unit {
    return { value: Long ->
      if (value <= expected)
        throw TestFailedException("$value is not greater than $expected")
    }
  }

  infix fun Be<String>.lt(expected: Long): (Long) -> Unit {
    return { value: Long ->
      if (value >= expected)
        throw TestFailedException("$value is not less than $expected")
    }
  }

  infix fun Be<String>.gte(expected: Long): (Long) -> Unit {
    return { value: Long ->
      if (value < expected)
        throw TestFailedException("$value is not greater than or equal to $expected")
    }
  }

  infix fun Be<String>.lte(expected: Long): (Long) -> Unit {
    return { value: Long ->
      if (value > expected)
        throw TestFailedException("$value is not less than or equal to $expected")
    }
  }
}