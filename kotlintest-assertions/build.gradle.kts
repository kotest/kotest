dependencies {
    api("org.jetbrains.kotlin:kotlin-reflect")
    api("com.univocity:univocity-parsers:2.7.6")
    api("com.github.wumpz:diffutils:2.2")
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform {
        includeEngines("kotlintest")
    }

    testLogging {
        events("PASSED", "FAILED", "SKIPPED")
    }
}