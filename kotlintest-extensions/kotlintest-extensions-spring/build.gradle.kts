dependencies {
  api(project(":kotlintest-core"))
  api(project(":kotlintest-assertions"))
  api("org.springframework:spring-test:4.3.21.RELEASE")
  api("org.springframework:spring-context:4.3.21.RELEASE")
  testImplementation(project(":kotlintest-runner:kotlintest-runner-junit5"))
}

val test by tasks.getting(Test::class) {
  useJUnitPlatform {
    includeEngines("kotlintest")
  }

  testLogging {
    events("PASSED", "FAILED", "SKIPPED")
  }
}