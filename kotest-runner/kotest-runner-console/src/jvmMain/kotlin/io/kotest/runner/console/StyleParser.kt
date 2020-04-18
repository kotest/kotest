package io.kotest.runner.console

import io.kotest.core.test.Description

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
    return paths.fold(root) { acc, name -> acc.append(name) }
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
