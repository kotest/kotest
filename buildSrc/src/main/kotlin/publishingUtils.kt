import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.tasks.GenerateMavenPom
import org.gradle.api.tasks.PathSensitivity.NAME_ONLY
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.plugins.signing.SigningExtension
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList

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
   val jvmPomTask = project.tasks.named<GenerateMavenPom>("generatePomFileForJvmPublication")

   project.tasks.named<GenerateMavenPom>("generatePomFileForKotlinMultiplatformPublication").configure {

      val jvmPom = jvmPomTask.map { it.destination }
      inputs.file(jvmPom)
         .withPropertyName("jvmPom")
         .normalizeLineEndings()
         .withPathSensitivity(NAME_ONLY)

      doLast("re-write KMP common POM") {
         val original = destination.readText()

         val jvmPomFile = jvmPom.get()

         val docFactory = DocumentBuilderFactory.newInstance()
         val docBuilder = docFactory.newDocumentBuilder()

         val doc = docBuilder.parse(destination).apply {
            removeWhitespaceNodes()
            // set standalone=true to prevent `standalone="no"` in the output
            xmlStandalone = true
         }

         val jvmDoc = docBuilder.parse(jvmPomFile)
         val jvmGroupId = jvmDoc.getElementsByTagName("groupId").item(0).textContent
         val jvmArtifactId = jvmDoc.getElementsByTagName("artifactId").item(0).textContent
         val jvmVersion = jvmDoc.getElementsByTagName("version").item(0).textContent

         val root = doc.documentElement

         val dependencies = root.getElementsByTagName("dependencies").item(0)

         // Remove the original platform dependencies
         while (dependencies.hasChildNodes()) {
            dependencies.removeChild(dependencies.firstChild)
         }
         // and add a single dependency on the platform module:
         dependencies.appendChild(
            doc.createElement("dependency") {
               appendChild(doc.createElement("groupId", jvmGroupId))
               appendChild(doc.createElement("artifactId", jvmArtifactId))
               appendChild(doc.createElement("version", jvmVersion))
               appendChild(doc.createElement("scope", "compile"))
            }
         )

         // Set packaging to POM to indicate that there's no artifact:
         doc.createElement("packaging", "pom")

         // Write the updated XML to the destination file
         val transformer = TransformerFactory.newInstance().newTransformer().apply {
            setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no")
            setOutputProperty(OutputKeys.INDENT, "yes")
            setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
         }
         val source = DOMSource(doc)
         val result = StreamResult(destination)
         transformer.transform(source, result)
      }
   }
}

private fun Document.createElement(name: String, content: String): Element {
   val element = createElement(name)
   element.textContent = content
   return element
}

private fun Document.createElement(name: String, configure: Element.() -> Unit): Element =
   createElement(name).apply(configure)

// https://stackoverflow.com/a/979606/4161471
private fun Document.removeWhitespaceNodes() {
   val xpathFactory = XPathFactory.newInstance()

   // XPath to find empty text nodes
   val xpathExp = xpathFactory.newXPath().compile("//text()[normalize-space(.) = '']")
   val emptyTextNodes = xpathExp.evaluate(this, XPathConstants.NODESET) as NodeList

   // Remove each empty text node from document
   for (i in 0 until emptyTextNodes.length) {
      val emptyTextNode = emptyTextNodes.item(i)
      emptyTextNode.getParentNode().removeChild(emptyTextNode)
   }
}
