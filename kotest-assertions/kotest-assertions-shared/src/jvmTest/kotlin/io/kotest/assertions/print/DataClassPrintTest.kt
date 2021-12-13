package io.kotest.assertions.print

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DataClassPrintTest : FunSpec() {
   init {

      test("Detect show for data class") {
         data class Starship(
            val name: String,
            val pennnant: String,
            val displacement: Long,
            val `class`: String,
            val leadShip: Boolean
         )
         Starship("HMS Queen Elizabeth", "R08", 65000, "Queen Elizbeth", true).print().value shouldBe """Starship(
- class: "Queen Elizbeth"
- displacement: 65000L
- leadShip: true
- name: "HMS Queen Elizabeth"
- pennnant: "R08"
)"""
      }

      test("should be resilient to direct cyclic references") {
         val foo1 = Foo(null)
         val foo2 = Foo(foo1)
         foo1.other = foo2
         foo1.print().value shouldBe """Foo(
- other: Foo(
  - other: Foo(
    - other: Foo(
      - other: Foo(
        - other: Foo(
          - other: Foo(
            - other: Foo(
              - other: Foo(
                - other: Foo(
                  - other: <...>
                  )
                )
              )
            )
          )
        )
      )
    )
  )
)"""
      }

      test("should be resilient to indirect cyclic references") {
         val bar = Bar(4, mutableListOf())
         bar.list.add(bar)
         bar.print().value shouldBe """Bar(
- a: 4
- list: [Bar(
  - a: 4
  - list: [Bar(
    - a: 4
    - list: [Bar(
      - a: 4
      - list: [Bar(
        - a: 4
        - list: [Bar(
          - a: 4
          - list: [Bar(
            - a: 4
            - list: [Bar(
              - a: 4
              - list: [Bar(
                - a: 4
                - list: [Bar(
                  - a: 4
                  - list: [<...>]
                  )]
                )]
              )]
            )]
          )]
        )]
      )]
    )]
  )]
)"""
      }
   }
}

data class Foo(var other: Foo?)
data class Bar(val a: Int, val list: MutableList<Bar>)
