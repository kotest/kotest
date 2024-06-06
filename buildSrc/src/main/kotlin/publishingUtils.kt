import groovy.util.Node
import groovy.util.NodeList
import org.gradle.api.Project
import org.gradle.api.XmlProvider
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.GenerateMavenPom
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.SigningExtension

//region manually define accessors, because IntelliJ _still_ doesn't index them properly :(
internal val Project.signing get() = extensions.getByType<SigningExtension>()
internal fun Project.signing(configure: SigningExtension.() -> Unit = {}): Unit = signing.configure()
internal val Project.publishing get() = extensions.getByType<PublishingExtension>()
internal fun Project.publishing(configure: PublishingExtension.() -> Unit = {}): Unit = publishing.configure()
//endregion

/**
 * Publish the platform JAR and POM so that consumers who depend on this module and can't read Gradle module
 * metadata can still get the platform artifact and transitive dependencies from the POM
 * (see details in https://youtrack.jetbrains.com/issue/KT-39184#focus=streamItem-27-4115233.0-0)
 */
internal fun publishPlatformArtifactsInRootModule(project: Project) {
   val platformPublication: MavenPublication =
      project.publishing.publications.named<MavenPublication>("jvm").get()
   val kmpPublication: MavenPublication =
      project.publishing.publications.named<MavenPublication>("kotlinMultiplatform").get()

   lateinit var platformXml: XmlProvider
   platformPublication.pom?.withXml { platformXml = this }

   // replace pom
   kmpPublication.pom.withXml {
      val xmlProvider = this
      val root = xmlProvider.asNode()
      // Remove the original content and add the content from the platform POM:
      root.children().toList().forEach { root.remove(it as Node) }
      platformXml.asNode().children().forEach { root.append(it as Node) }

      // Adjust the self artifact ID, as it should match the root module's coordinates:
      ((root.get("artifactId") as NodeList).get(0) as Node).setValue(kmpPublication.artifactId)

      // Set packaging to POM to indicate that there's no artifact:
      root.appendNode("packaging", "pom")

      // Remove the original platform dependencies and add a single dependency on the platform
      // module:
      val dependencies = (root.get("dependencies") as NodeList).get(0) as Node
      dependencies.children().toList().forEach { dependencies.remove(it as Node) }
      val singleDependency = dependencies.appendNode("dependency")
      singleDependency.appendNode("groupId", platformPublication.groupId)
      singleDependency.appendNode("artifactId", platformPublication.artifactId)
      singleDependency.appendNode("version", platformPublication.version)
      singleDependency.appendNode("scope", "compile")
   }

   project.tasks
      .matching { it.name == "generatePomFileForKotlinMultiplatformPublication" }
      .configureEach {
         dependsOn("generatePomFileFor${platformPublication.name.capitalized()}Publication")

         // Disable Config Cache to prevent error:
         // Task `:[...]:generatePomFileForKotlinMultiplatformPublication` of type `GenerateMavenPom`:
         // cannot serialize object of type 'DefaultMavenPublication', a subtype of 'Publication',
         // as these are not supported with the configuration cache.
         notCompatibleWithConfigurationCache("publishPlatformArtifactsInRootModule")
      }
}
