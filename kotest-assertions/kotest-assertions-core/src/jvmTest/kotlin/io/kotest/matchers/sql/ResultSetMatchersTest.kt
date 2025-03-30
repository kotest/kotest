package io.kotest.matchers.sql

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import java.sql.ResultSet

@EnabledIf(NotMacOnGithubCondition::class)
class ResultSetMatchersTest : StringSpec() {

   private val resultSet = mockk<ResultSet>().also {
      every { it.row } returns 1
      every { it.metaData.columnCount } returns 1
      every { it.metaData.columnCount } returns 1
      every { it.metaData.getColumnLabel(1) } returns TEST_COLUMN
      every { it.next() } returnsMany listOf(true, true, true, false)
      every { it.getObject(TEST_COLUMN) } returnsMany TEST_COLUMN_VALUES
   }

   init {
      "ResultSet should have rows" {
         resultSet.shouldHaveRows(1)
         resultSet shouldHaveRows 1
         resultSet.shouldNotHaveRows(2)
         resultSet shouldNotHaveRows 2
      }
      "ResultSet should have columns" {
         resultSet.shouldHaveColumns(1)
         resultSet shouldHaveColumns 1
         resultSet.shouldNotHaveColumns(2)
         resultSet shouldNotHaveColumns 2
      }
      "ResultSet should contain column" {
         resultSet.shouldContainColumn(TEST_COLUMN)
         resultSet shouldContainColumn TEST_COLUMN
         resultSet.shouldNotContainColumn("WRONG-$TEST_COLUMN")
         resultSet shouldNotContainColumn "WRONG-$TEST_COLUMN"
      }
      "ResultSet should have column" {
         resultSet.shouldHaveColumn<String>(TEST_COLUMN) {
            it shouldBe TEST_COLUMN_VALUES
         }
      }
      "ResultSet should have column with diff type" {
         clearMocks(resultSet)

         every { resultSet.next() } returnsMany listOf(true, true, true, false)
         every { resultSet.metaData.columnCount } returns 1
         every { resultSet.metaData.getColumnLabel(1) } returns TEST_COLUMN
         every { resultSet.getObject(TEST_COLUMN) } returnsMany TEST_COLUMN_VALUES2

         resultSet.shouldHaveColumn<Int>(TEST_COLUMN) {
            it shouldBe TEST_COLUMN_VALUES2
         }
      }
      "ResultSet should have row" {
         val resultSet = mockk<ResultSet>(relaxed = true)
         every { resultSet.metaData.columnCount } returns TEST_ROW_VALUES.size
         every { resultSet.getObject(any<Int>()) } returnsMany TEST_ROW_VALUES

         resultSet.shouldHaveRow(1) {
            it shouldContainAll TEST_ROW_VALUES
            it shouldContain TEST_ROW_VALUES[0]
            it shouldContain TEST_ROW_VALUES[1]
            it shouldContain TEST_ROW_VALUES[2]
            it shouldContainExactly TEST_ROW_VALUES
            it shouldNotContain "RANDOM_ROW_VALUE"
         }
      }
   }

   companion object {
      private const val TEST_COLUMN = "Test-Column"
      private val TEST_COLUMN_VALUES = listOf("Test1", "Test2", "Test3")
      private val TEST_COLUMN_VALUES2 = listOf(1, 2, 3)
      private val TEST_ROW_VALUES = listOf(1, "SomeName", true)
   }
}
