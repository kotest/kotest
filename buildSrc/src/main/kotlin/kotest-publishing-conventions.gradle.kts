import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.common
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.jvm

plugins {
   signing
   `maven-publish`
   id("dev.adamko.dev-publish")
}

group = "io.kotest"
version = Ci.publishVersion

val javadocJar by tasks.registering(Jar::class) {
   group = JavaBasePlugin.DOCUMENTATION_GROUP
   description = "Assembles java doc to jar"
   archiveClassifier.set("javadoc")
   val javadoc = tasks.named("javadoc")
   from(javadoc)
}

val ossrhUsername: String by project
val ossrhPassword: String by project
val signingKey: String? by project
val signingPassword: String? by project

val mavenCentralRepoName = "Deploy"

signing {
   if (signingKey != null && signingPassword != null) {
      useGpgCmd()
      useInMemoryPgpKeys(signingKey, signingPassword)
   }
   sign(publishing.publications)
   setRequired { Ci.isRelease } // only require signing when releasing
}

//region Only enabling signing when publishing to Maven Central.
// (Otherwise signing is required for dev-publish, which prevents testing if the credentials aren't present.)
gradle.taskGraph.whenReady {
   val isPublishingToMavenCentral = allTasks
      .filterIsInstance<PublishToMavenRepository>()
      .any { it.repository?.name == mavenCentralRepoName }

   signing.setRequired({ isPublishingToMavenCentral })

   tasks.withType<Sign>().configureEach {
      // redefine val for Config Cache compatibility
      val isPublishingToMavenCentral_ = isPublishingToMavenCentral
      inputs.property("isPublishingToMavenCentral", isPublishingToMavenCentral_)
      onlyIf("publishing to Maven Central") { isPublishingToMavenCentral_ }
   }
}
//endregion

publishing {
   repositories {
      maven {
         val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
         val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
         name = mavenCentralRepoName
         url = if (Ci.isRelease) releasesRepoUrl else snapshotsRepoUrl
         credentials {
            username = System.getenv("OSSRH_USERNAME") ?: ossrhUsername
            password = System.getenv("OSSRH_PASSWORD") ?: ossrhPassword
         }
      }
      maven(rootDir.resolve("build/maven-repo")) {
         // Publish to a project-local directory, for easier verification of published artifacts
         // Run ./gradlew publishAllPublicationsToRootBuildDirRepository, and check `$rootDir/build/maven-repo/`
         name = "RootBuildDir"
      }
   }

   publications.withType<MavenPublication>().configureEach {
      pom {
         name.set("Kotest")
         description.set("Kotlin Test Framework")
         url.set("https://github.com/kotest/kotest")

         scm {
            connection.set("scm:git:https://github.com/kotest/kotest/")
            developerConnection.set("scm:git:https://github.com/sksamuel/")
            url.set("https://github.com/kotest/kotest/")
         }

         licenses {
            license {
               name.set("Apache-2.0")
               url.set("https://opensource.org/licenses/Apache-2.0")
            }
         }

         developers {
            developer {
               id.set("sksamuel")
               name.set("Stephen Samuel")
               email.set("sam@sksamuel.com")
            }
         }
      }
   }
}

pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
   publishing.publications.withType<MavenPublication>().configureEach {
      artifact(javadocJar)
   }

   publishPlatformArtifactsInRootModule(project)
}

pluginManager.withPlugin("java-gradle-plugin") {
   extensions.configure<JavaPluginExtension> {
      withSourcesJar()
   }
}

//region Maven Central can't handle parallel uploads, so limit parallel uploads with a BuildService
abstract class MavenPublishLimiter : BuildService<BuildServiceParameters.None>

val mavenPublishLimiter =
   gradle.sharedServices.registerIfAbsent("mavenPublishLimiter", MavenPublishLimiter::class) {
      maxParallelUsages = 1
   }

tasks.withType<PublishToMavenRepository>()
   .matching { it.name.endsWith("PublicationTo${mavenCentralRepoName}Repository") }
   .configureEach {
      usesService(mavenPublishLimiter)
   }
//endregion


//region KotestBomService

/**
 * Create a service for collecting the coordinates of all Kotest artifacts that should be included in the kotest-bom.
 */
abstract class KotestBomService : BuildService<BuildServiceParameters.None> {
   /** Coordinates that will be included in the Kotest BOM. */
   abstract val coordinates: SetProperty<String>
}

val kotestBomService: KotestBomService =
   gradle.sharedServices.registerIfAbsent("kotestBomService", KotestBomService::class).get()

extensions.add("kotestBomService", kotestBomService)

/** Controls whether the current subproject will be included in the kotest-bom. */
val includeInKotestBom: Property<Boolean> =
   objects.property<Boolean>().convention(project.name != "kotest-bom")

extensions.add<Property<Boolean>>("includeInKotestBom", includeInKotestBom)

pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
   extensions.configure<KotlinMultiplatformExtension> {
      targets
         .matching { target ->
            target.publishable &&
               // Skip platform artifacts (like *-linuxx64, *-macosx64)
               // It leads to inconsistent bom when publishing from different platforms
               // (e.g. on linux it will include only linuxx64 artifacts and no macosx64)
               // It shouldn't be a problem as usually consumers need to use generic *-native artifact
               // Gradle will choose correct variant by using metadata attributes
               (target.platformType == common || target.platformType == jvm)
         }
         .all {
            mavenPublication publication@{
               kotestBomService.coordinates.addAll(
                  providers
                     .provider {
                        "${this@publication.groupId}:${this@publication.artifactId}:${this@publication.version}"
                     }
                     .zip(includeInKotestBom) { coords, enabled ->
                        if (enabled) listOf(coords) else emptyList()
                     }
               )
            }
         }
   }
}

//region Fix Gradle error Reason: Task <publish> uses this output of task <sign> without declaring an explicit or implicit dependency.
// https://github.com/gradle/gradle/issues/26091
tasks.withType<AbstractPublishToMaven>().configureEach {
   val signingTasks = tasks.withType<Sign>()
   mustRunAfter(signingTasks)
}
//endregion
