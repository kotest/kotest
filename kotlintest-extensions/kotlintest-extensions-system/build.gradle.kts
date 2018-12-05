dependencies {
    api(project(":kotlintest-core"))
    api(project(":kotlintest-assertions"))
    testImplementation(project(":kotlintest-runner:kotlintest-runner-junit5"))
    testImplementation("log4j:log4j:1.2.17")
    testImplementation("org.slf4j:slf4j-log4j12:1.7.25")
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform {
        includeEngines("kotlintest")
    }

    testLogging {
        events("PASSED", "FAILED", "SKIPPED")
    }
}