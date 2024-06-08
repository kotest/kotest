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
import org.w3c.dom.Node
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

         val docFactory = DocumentBuilderFactory.newInstance()
         val docBuilder = docFactory.newDocumentBuilder()

         val jvmPomFile = jvmPom.get()

         val jvmDoc = docBuilder.parse(jvmPomFile)
         val jvmGroupId = jvmDoc.getElement("groupId").textContent
         val jvmArtifactId = jvmDoc.getElement("artifactId").textContent
         val jvmVersion = jvmDoc.getElement("version").textContent

         val kmpPomDoc = docBuilder.parse(destination).apply {
            // strip whitespace, otherwise pretty-printing output has blank lines
            removeWhitespaceNodes()
            // set standalone=true to prevent `standalone="no"` in the output
            xmlStandalone = true
         }

         val kmpPom = kmpPomDoc.documentElement

         val dependencies = kmpPom.getElement("dependencies")

         // Remove the original platform dependencies...
         while (dependencies.hasChildNodes()) {
            dependencies.removeChild(dependencies.firstChild)
         }
         // instead, add a single dependency on the platform module
         dependencies.appendChild(
            kmpPomDoc.createElement("dependency") {
               appendChild(kmpPomDoc.createElement("groupId", jvmGroupId))
               appendChild(kmpPomDoc.createElement("artifactId", jvmArtifactId))
               appendChild(kmpPomDoc.createElement("version", jvmVersion))
               appendChild(kmpPomDoc.createElement("scope", "compile"))
            }
         )

         // Set packaging to POM to indicate that there's no artifact
         kmpPom.appendChild(
            kmpPomDoc.createElement("packaging", "pom")
         )

         // Write the updated XML to the destination file
         val transformer = TransformerFactory.newInstance().newTransformer().apply {
            // pretty printing options
            setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no")
            setOutputProperty(OutputKeys.INDENT, "yes")
            setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
         }

         transformer.transform(DOMSource(kmpPomDoc), StreamResult(destination))

         if (logger.isInfoEnabled) {
            val updated = destination.readText()
            logger.info(
               """
               [$path] Re-wrote KMP POM
               ${"=".repeat(25)} original ${"=".repeat(25)}
               $original
               ${"=".repeat(25)} updated  ${"=".repeat(25)}
               $updated
               ${"=".repeat(25)}==========${"=".repeat(25)}
               """.trimIndent()
            )
         }
      }
   }
}


private fun Document.getElement(tagName: String): Node =
   getElementsByTagName(tagName).item(0)
      ?: error("No element named '$tagName' in Document $this")

private fun Element.getElement(tagName: String): Node =
   getElementsByTagName(tagName).item(0)
      ?: error("No element named '$tagName' in Element $this")

private fun Document.createElement(name: String, content: String): Element {
   val element = createElement(name)
   element.textContent = content
   return element
}

private fun Document.createElement(name: String, configure: Element.() -> Unit = {}): Element =
   createElement(name).apply(configure)

// https://stackoverflow.com/a/979606/4161471
private fun Node.removeWhitespaceNodes() {
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
