package io.kotest.extensions.testcontainers

import com.zaxxer.hikari.HikariConfig

@Deprecated("use Flyway or another db migration tool. Will be removed in 6.3")
class TestContainerHikariConfig : HikariConfig() {

   var dbInitScripts: List<String> = emptyList()

}
