package io.kotest.data

// TODO I'm only supporting table with 3 arguments until the API stabilizes

fun <A, B, C> table(
   headers: Headers3,
   fileContent: String,
   transform: (String, String, String) -> Row3<A, B, C>
): Table3<A, B, C> {
   val table = StringTable(headers.values(), fileContent.lines())
   val rows = table.mapRows { (a, b, c) -> transform(a, b, c) }
   return Table3(headers, rows)
}

