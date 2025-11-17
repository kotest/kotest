import utils.configureGradleDaemonJvm

plugins {
   id("kotest-base")
   id("com.gradleup.nmcp.aggregation")
   java
   alias(libs.plugins.kotlinBinaryCompatibilityValidator)
   //TODO this fails. why?? alias(libs.plugins.android.library) apply false
}

apiValidation {
   ignoredPackages.addAll(
      listOf(
         "io.kotest.framework.multiplatform.gradle",
         "io.kotest.framework.multiplatform.native"
      )
   )
   nonPublicMarkers.addAll(
      listOf(
         "io.kotest.common.KotestInternal",
      )
   )
}

nmcpAggregation {
   centralPortal {
      username.set(System.getenv("NEW_MAVEN_CENTRAL_USERNAME"))
      password.set(System.getenv("NEW_MAVEN_CENTRAL_PASSWORD"))
      publishingType = "USER_MANAGED"
      publicationName = "Kotest ${Ci.publishVersion} ${kotestSettings.enabledPublicationNamePrefixes.get()}"
   }
}

val publishToAppropriateCentralRepository by tasks.registering {
   group = "publishing"
   if (Ci.isRelease) {
      dependsOn(tasks.named("publishAggregationToCentralPortal"))
   } else {
      dependsOn(tasks.named("publishAggregationToCentralPortalSnapshots"))
   }
}

// List all projects which should be included in publishing
dependencies {
   // Assertions
   nmcpAggregation(projects.kotestAssertions.kotestAssertionsArrow)
   nmcpAggregation(projects.kotestAssertions.kotestAssertionsArrowFxCoroutines)
   nmcpAggregation(projects.kotestAssertions.kotestAssertionsCore)
   nmcpAggregation(projects.kotestAssertions.kotestAssertionsJson)
   nmcpAggregation(projects.kotestAssertions.kotestAssertionsKonform)
   nmcpAggregation(projects.kotestAssertions.kotestAssertionsKotlinxDatetime)
   nmcpAggregation(projects.kotestAssertions.kotestAssertionsKtor)
   nmcpAggregation(projects.kotestAssertions.kotestAssertionsShared)
   nmcpAggregation(projects.kotestAssertions.kotestAssertionsTable)
   nmcpAggregation(projects.kotestAssertions.kotestAssertionsYaml)

   // bom
   nmcpAggregation(projects.kotestBom)

   nmcpAggregation(projects.kotestCommon)

   // Extensions
   nmcpAggregation(projects.kotestExtensions)

   nmcpAggregation(projects.kotestExtensions.kotestExtensionsHtmlreporter)
   nmcpAggregation(projects.kotestExtensions.kotestExtensionsJunitxml)
//   nmcpAggregation(projects.kotestExtensions.kotestExtensionsKoin)


   // Framework
   nmcpAggregation(projects.kotestFramework.kotestFrameworkEngine)
   nmcpAggregation(projects.kotestFramework.kotestFrameworkStandalone)
   nmcpAggregation(projects.kotestFramework.kotestFrameworkSymbolProcessor)

   // Property
   nmcpAggregation(projects.kotestProperty)
   nmcpAggregation(projects.kotestProperty.kotestPropertyArrow)
//   nmcpAggregation(projects.kotestProperty.kotestPropertyArrowOptics)
   nmcpAggregation(projects.kotestProperty.kotestPropertyDatetime)
   nmcpAggregation(projects.kotestProperty.kotestPropertyLifecycle)
   nmcpAggregation(projects.kotestProperty.kotestPropertyPermutations)


   if (System.getenv("CI") != "true" || System.getenv("RUNNER_OS") == "Linux") {
      nmcpAggregation(project(":kotest-assertions:kotest-assertions-compiler"))

      nmcpAggregation(project(":kotest-extensions:kotest-extensions-allure"))
      nmcpAggregation(project(":kotest-extensions:kotest-extensions-blockhound"))
      nmcpAggregation(project(":kotest-extensions:kotest-extensions-decoroutinator"))
      nmcpAggregation(project(":kotest-extensions:kotest-extensions-junit5"))
      nmcpAggregation(project(":kotest-extensions:kotest-extensions-mockserver"))
      nmcpAggregation(project(":kotest-extensions:kotest-extensions-now"))
      nmcpAggregation(project(":kotest-extensions:kotest-extensions-spring"))
      nmcpAggregation(project(":kotest-extensions:kotest-extensions-pitest"))
      nmcpAggregation(project(":kotest-extensions:kotest-extensions-testcontainers"))
      nmcpAggregation(project(":kotest-extensions:kotest-extensions-wiremock"))

      nmcpAggregation(project(":kotest-runner:kotest-runner-junit5"))
      nmcpAggregation(project(":kotest-runner:kotest-runner-junit4"))
   }
}

configureGradleDaemonJvm(
   project = project,
   updateDaemonJvm = tasks.updateDaemonJvm,
   gradleDaemonJvmVersion = libs.versions.gradleDaemonJvm,
)
