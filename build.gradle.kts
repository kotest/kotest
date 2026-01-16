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

val publishToMavenLocalAppropriately by tasks.registering {
   group = "publishing"
   description = "Publishes all enabled publications to Maven Local based on kotest_enabledPublicationNamePrefixes"
   dependsOn(
      subprojects.mapNotNull { subproject ->
         subproject.tasks.findByName("publishToMavenLocal")
      }
   )
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
   nmcpAggregation(projects.kotestExtensions.kotestExtensionsAllure)
   nmcpAggregation(projects.kotestExtensions.kotestExtensionsBlockhound)
   nmcpAggregation(projects.kotestExtensions.kotestExtensionsDecoroutinator)
   nmcpAggregation(projects.kotestExtensions.kotestExtensionsJunit5)
   nmcpAggregation(projects.kotestExtensions.kotestExtensionsMockserver)
   nmcpAggregation(projects.kotestExtensions.kotestExtensionsNow)
   nmcpAggregation(projects.kotestExtensions.kotestExtensionsSpring)
   nmcpAggregation(projects.kotestExtensions.kotestExtensionsPitest)
   nmcpAggregation(projects.kotestExtensions.kotestExtensionsTestcontainers)
   nmcpAggregation(projects.kotestExtensions.kotestExtensionsWiremock)

   // Runners
   nmcpAggregation(projects.kotestRunner.kotestRunnerJunitPlatform)
   nmcpAggregation(projects.kotestRunner.kotestRunnerJunit4)
   nmcpAggregation(projects.kotestRunner.kotestRunnerJunit5)
   nmcpAggregation(projects.kotestRunner.kotestRunnerJunit6)

}

configureGradleDaemonJvm(
   project = project,
   updateDaemonJvm = tasks.updateDaemonJvm,
   gradleDaemonJvmVersion = libs.versions.gradleDaemonJvm,
)
