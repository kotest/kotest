package io.kotest.matchers.sql

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.sql.ResultSet


class ResultSetMatchersTest : StringSpec() {

   init {
      "ResultSet should have rows" {
         val resultSet = mockk<ResultSet>()
         every { resultSet.row } returns 1

         resultSet.shouldHaveRows(1)
         resultSet shouldHaveRows 1
         resultSet.shouldNotHaveRows(2)
         resultSet shouldNotHaveRows 2
      }
      "ResultSet should have columns" {
         val resultSet = mockk<ResultSet>()
         every { resultSet.metaData.columnCount } returns 1

         resultSet.shouldHaveColumns(1)
         resultSet shouldHaveColumns 1
         resultSet.shouldNotHaveColumns(2)
         resultSet shouldNotHaveColumns 2
      }
      "ResultSet should contain column" {
         val resultSet = mockk<ResultSet>()
         every { resultSet.metaData.columnCount } returns 1
         every { resultSet.metaData.getColumnLabel(1) } returns TEST_COLUMN

         resultSet.shouldContainColumn(TEST_COLUMN)
         resultSet shouldContainColumn TEST_COLUMN
         resultSet.shouldNotContainColumn("WRONG-$TEST_COLUMN")
         resultSet shouldNotContainColumn "WRONG-$TEST_COLUMN"
      }
      "ResultSet should have column" {
         val resultSet = mockk<ResultSet>()
         every { resultSet.next() } returnsMany listOf(true, true, true, false)
         every { resultSet.metaData.columnCount } returns 1
         every { resultSet.metaData.getColumnLabel(1) } returns TEST_COLUMN
         every { resultSet.getString(TEST_COLUMN) } returnsMany TEST_COLUMN_VALUES

         resultSet.shouldHaveColumn(TEST_COLUMN) {
            it shouldBe TEST_COLUMN_VALUES
         }
      }
   }

   companion object {
      private const val TEST_COLUMN = "Test-Column"
      private val TEST_COLUMN_VALUES = listOf("Test1", "Test2", "Test3")
   }
}
