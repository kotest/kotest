package io.kotlintest.properties

class TableTestingTest : TableTesting() {
  init {

    val table1 = table(headers("name"), row("sam"), row("billy"), row("christian"))

    "names should not be empty strings".forAll(table1) {
      it.isEmpty() shouldBe false
    }

    val table2 = table(
        headers("a", "b"),
        row(5, 5),
        row(4, 6),
        row(3, 7)
    )

    "numbers should add up to ten".forAll(table2) { a, b ->
      a + b == 10
    }

    val table3 = table(
        headers("x", "y", "z"),
        row(3, 4, 5),
        row(5, 12, 13),
        row(9, 12, 15)
    )

    "numbers should be py triples".forAll(table3) { a, b, c ->
      a * a + b * b shouldBe c * c
    }

    val table4 = table(
        headers("a", "b", "c", "d"),
        row("sam", "bam", "dam", "sambamdam"),
        row("", "sam", "", "sam"),
        row("sa", "", "m", "sam")
    )

    "testing triple concat".forAll(table4) { a, b, c, d ->
      a + b + c shouldBe d
    }

  }
}
