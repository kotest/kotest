MockK
=====================================
pure kotlin mocking framework

### Nice features

 [x] pure kotlin mocking DSL
 [x] signature based partial arguments matchers (innovation)
 [x] chained calls by default aka mockito deep stubs (but better)
 [x] removing finals
 [x] instrumenting no args constructor
 [x] matcher expressions
 [x] mocking coroutines
 [x] capturing lambdas
 [x] bunch of matchers, verification modes and useful DSL constructs

### DSL

  Simplest example

  ```
    val car = mockk<Car>()
    every { car.drive(Direction.NORTH) } returns Outcome.OK

    car.drive(Direction.NORTH) // returns OK

    verify { car.drive(Direction.NORTH) }
  ```

### Partial argument matching

You can skip parameters while specifying matchers.
MockK runs your block few times, build so called signature and
auto-detect places where matchers appear.

  ```
    class MockedClass {
        fun op(a: Int, b: Int) = a + b
    }

    val obj = mockk<MockedClass>()
    every { obj.op(1, eq(2)) } returns 5

    obj.op(1, 2) // returns 5

    verify { obj.op(eq(1), 2) }

  ```

### Chained calls

Mock can have child mocks. This allows to mock chains of calls

  ```
    class MockedClass1 {
        fun op1(a: Int, b: Int) = a + b
    }

    class MockedClass2 {
        fun op2(c: Int, d: Int): MockedClass1 = ...
    }

    val obj = mockk<MockedClass2>()
    every { obj.op2(1, eq(2)).op1(3, any()) } returns 5

    obj.op2(1, 2) // returns child mock
    obj.op2(1, 2).op1(3, 22) // returns 5

    verify { obj.op2(any(), 2).op2(3, 22) }

  ```

... TBD ...