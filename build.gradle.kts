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
   // the intellij plugin is not an API and doesn't need its api to be validated
   ignoredProjects.addAll(listOf("kotest-intellij-plugin"))
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
   nmcpAggregation(projects.kotestAssertions.kotestAssertionsCompiler)
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
   nmcpAggregation(projects.kotestExtensions.kotestExtensionsKoin)

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


   // Linux-only modules: only included in the build when running on a Linux runner (or locally).
   // Use findProject so this gracefully no-ops when the module is absent from settings.
   findProject(":kotest-extensions:kotest-extensions-allure")?.let { nmcpAggregation(it) }
   findProject(":kotest-extensions:kotest-extensions-blockhound")?.let { nmcpAggregation(it) }
   findProject(":kotest-extensions:kotest-extensions-decoroutinator")?.let { nmcpAggregation(it) }
   findProject(":kotest-extensions:kotest-extensions-junit5")?.let { nmcpAggregation(it) }
   findProject(":kotest-extensions:kotest-extensions-mockserver")?.let { nmcpAggregation(it) }
   findProject(":kotest-extensions:kotest-extensions-now")?.let { nmcpAggregation(it) }
   findProject(":kotest-extensions:kotest-extensions-pitest")?.let { nmcpAggregation(it) }
   findProject(":kotest-extensions:kotest-extensions-spring")?.let { nmcpAggregation(it) }
   findProject(":kotest-extensions:kotest-extensions-testcontainers")?.let { nmcpAggregation(it) }
   findProject(":kotest-extensions:kotest-extensions-wiremock")?.let { nmcpAggregation(it) }

   // Runners
   findProject(":kotest-runner:kotest-runner-junit4")?.let { nmcpAggregation(it) }
   findProject(":kotest-runner:kotest-runner-junit6")?.let { nmcpAggregation(it) }

   // Runners
   nmcpAggregation(projects.kotestRunner.kotestRunnerJunitPlatform)
   nmcpAggregation(projects.kotestRunner.kotestRunnerJunit5)

}

configureGradleDaemonJvm(
   project = project,
   updateDaemonJvm = tasks.updateDaemonJvm,
   gradleDaemonJvmVersion = libs.versions.gradleDaemonJvm,
)
