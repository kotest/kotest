package io.kotest.core.test

enum class TestCaseSeverityLevel(val level: Int) {
   BLOCKER(4),
   CRITICAL(3),
   NORMAL(2),
   MINOR(1),
   TRIVIAL(0);
}
