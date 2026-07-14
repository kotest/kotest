package io.kotest.matchers.sql

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.sql.ResultSet

@IgnorableReturnValue
infix fun ResultSet.shouldHaveRows(rowCount: Int) = this should haveRowCount(
   rowCount
)

@IgnorableReturnValue
infix fun ResultSet.shouldNotHaveRows(rowCount: Int) = this shouldNot haveRowCount(
   rowCount
)

fun haveRowCount(rowCount: Int) = object : Matcher<ResultSet> {
   override fun test(value: ResultSet) =
      MatcherResult(
         value.row == rowCount,
         { "$value should have $rowCount rows" },
         { "$value should not have $rowCount rows" }
      )
}

@IgnorableReturnValue
infix fun ResultSet.shouldHaveColumns(columnCount: Int) = this should haveColumnCount(
   columnCount
)

@IgnorableReturnValue
infix fun ResultSet.shouldNotHaveColumns(columnCount: Int) = this shouldNot haveColumnCount(
   columnCount
)

fun haveColumnCount(columnCount: Int) = object : Matcher<ResultSet> {
   override fun test(value: ResultSet) =
      MatcherResult(
         value.metaData.columnCount == columnCount,
         { "$value should have $columnCount columns" },
         { "$value should not have $columnCount columns" }
      )
}

@IgnorableReturnValue
infix fun ResultSet.shouldContainColumn(columnName: String) = this should containColumn(
   columnName
)

@IgnorableReturnValue
infix fun ResultSet.shouldNotContainColumn(columnName: String) = this shouldNot containColumn(
   columnName
)

fun containColumn(columnName: String) = object : Matcher<ResultSet> {
   override fun test(value: ResultSet): MatcherResult {
      val metaData = value.metaData
      val colCount = metaData.columnCount
      return MatcherResult(
         (1..colCount).any { metaData.getColumnLabel(colCount) == columnName },
         { "$value should have $columnName column" },
         { "$value should not have $columnName column" }
      )
   }
}

@Suppress("UNCHECKED_CAST")
@IgnorableReturnValue
fun <T> ResultSet.shouldHaveColumn(columnName: String, next: (List<T>) -> Unit) {
   this shouldContainColumn columnName
   val data = mutableListOf<T>()
   while (this.next()) {
      data += this.getObject(columnName) as T
   }
   next(data)
}

@IgnorableReturnValue
fun ResultSet.shouldHaveRow(rowNum: Int, next: (List<Any>) -> Unit) {
   val metaData = this.metaData
   val colCount = metaData.columnCount
   val row = mutableListOf<Any>()
   this.absolute(rowNum)
   (1..colCount).forEach { colNum ->
      row += this.getObject(colNum)
   }
   next(row)
}


