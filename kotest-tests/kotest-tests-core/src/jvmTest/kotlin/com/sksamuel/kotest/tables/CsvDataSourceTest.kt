//package com.sksamuel.kotest.tables
//
//import com.univocity.parsers.csv.CsvFormat
//import io.kotest.matchers.comparables.gt
//import io.kotest.specs.WordSpec
//import io.kotest.shouldBe
//import io.kotest.shouldNotBe
//import io.kotest.tables.CsvDataSource
//import io.kotest.tables.Headers3
//import io.kotest.tables.Row3
//import io.kotest.tables.forAll
//
//class CsvDataSourceTest : WordSpec() {
//  init {
//    "CsvDataSource" should {
//      "read data from csv file" {
//        val source = CsvDataSource(javaClass.getResourceAsStream("/user_data.csv"), CsvFormat())
//        val table = source.createTable<Long, String, String>(
//            { Row3(it.getLong("id"), it.getString("name"), it.getString("location")) },
//            { Headers3(it[0], it[1], it[2]) }
//        )
//        forAll(table) { a, b, c ->
//          a shouldBe gt(0)
//          b shouldNotBe null
//          c shouldNotBe null
//        }
//      }
//    }
//  }
//}
