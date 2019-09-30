package io.kotest.runner.console

import io.kotest.Description

interface StyleParser {
  fun parse(root: Description, testPath: String): Description
}

object BehaviorSpecStyleParser : StyleParser {

  private val regex = "(Given: .*?)\\s?(When: .*?)?\\s?(Then: .*?)?".toRegex()

  override fun parse(root: Description, testPath: String): Description {
    val match = regex.matchEntire(testPath) ?: return root
    return match.groupValues.drop(1).filter { it.isNotEmpty() }.fold(root) { acc, name -> acc.append(name) }
  }
}

object DescribeSpecStyleParser : StyleParser {

  private val regex = "(Describe: .*?)\\s?(It: .*?)?".toRegex()

  override fun parse(root: Description, testPath: String): Description {
    val match = regex.matchEntire(testPath) ?: return root
    return match.groupValues.drop(1).filter { it.isNotEmpty() }.fold(root) { acc, name -> acc.append(name) }
  }
}

object FeatureSpecStyleParser : StyleParser {

  private val regex = "(Feature: .*?)\\s?(Scenario: .*?)?".toRegex()

  override fun parse(root: Description, testPath: String): Description {
    val match = regex.matchEntire(testPath) ?: return root
    return match.groupValues.drop(1).filter { it.isNotEmpty() }.fold(root) { acc, name -> acc.append(name) }
  }
}

object DelimitedTestPathParser : StyleParser {
  override fun parse(root: Description, testPath: String): Description =
      testPath.split(" -- ")
          .filterNot { it.isEmpty() }
          .fold(root) { acc, name -> acc.append(name) }
}

object ShouldSpecStyleParser : StyleParser {
  override fun parse(root: Description, testPath: String): Description {
    // -- is used by parents of parents
    val paths = testPath.split(" -- ")
    val parents = paths.dropLast(1).fold(root) { acc, name -> acc.append(name) }
    // must support both 'foo should wibble' and 'should wibble' as the final component
    val index = paths.last().indexOf(" should ")
    return when (index) {
      -1 -> parents.append(paths.last())
      else -> parents.append(paths.last().take(index)).append(paths.last().drop(index + 1))
    }
  }
}

object StringSpecStyleParser : StyleParser {
  override fun parse(root: Description, testPath: String): Description = root.append(testPath)
}

object WordSpecStyleParser : StyleParser {
  override fun parse(root: Description, testPath: String): Description {
    val paths = testPath.split(" should ")
    return root.append(paths.first() + " should").append(paths.last())
  }
}