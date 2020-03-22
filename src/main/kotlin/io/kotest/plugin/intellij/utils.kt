import com.intellij.execution.RunManager
import com.intellij.execution.junit.JUnitConfigurationType
import com.intellij.openapi.project.Project

// nuke any junit ones left lurking
fun removeJUnitRunConfigs(project: Project, specName: String) {
  val runManager = RunManager.getInstance(project)
  runManager.getConfigurationSettingsList(JUnitConfigurationType())
      .filter { it.name == specName }
      .forEach { runManager.removeConfiguration(it) }
}