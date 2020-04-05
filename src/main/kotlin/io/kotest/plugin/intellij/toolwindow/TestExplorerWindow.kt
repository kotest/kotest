package io.kotest.plugin.intellij.toolwindow

import com.intellij.execution.ExecutorRegistry
import com.intellij.execution.RunManager
import com.intellij.execution.runners.ExecutionUtil
import com.intellij.icons.AllIcons
import com.intellij.ide.util.treeView.NodeRenderer
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.IndexNotReadyException
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.ScrollPaneFactory
import io.kotest.plugin.intellij.KotestConfigurationFactory
import io.kotest.plugin.intellij.KotestConfigurationType
import io.kotest.plugin.intellij.KotestRunConfiguration
import io.kotest.plugin.intellij.styles.psi.specs
import org.jetbrains.kotlin.idea.core.util.toPsiFile
import org.jetbrains.kotlin.idea.util.projectStructure.getModule
import java.awt.Color
import javax.swing.JComponent
import javax.swing.JTree
import javax.swing.tree.TreeSelectionModel

class TestExplorerWindow(private val project: Project) : SimpleToolWindowPanel(true, false) {

   private val tree = createTree()

   init {
      background = Color.WHITE
      toolbar = createToolbar()
      setContent(ScrollPaneFactory.createScrollPane(tree))
      setInitialContent()
      listenForSelectedFileChanges()
   }

   private fun createToolbar(): JComponent {
      return ActionManager.getInstance().createActionToolbar(
         ActionPlaces.STRUCTURE_VIEW_TOOLBAR,
         createActionGroup(),
         true
      ).component
   }

   private fun createActionGroup(): DefaultActionGroup {

      val result = DefaultActionGroup()

      result.add(object : AnAction(AllIcons.RunConfigurations.TestState.Run_run) {
         override fun actionPerformed(e: AnActionEvent) {
         }
      })

      result.add(object : AnAction(AllIcons.RunConfigurations.TestState.Run) {
         override fun actionPerformed(e: AnActionEvent) {
            val manager = RunManager.getInstance(project)
            val executor = ExecutorRegistry.getInstance().getExecutorById("Run")
            val path = tree.selectionPath
            if (path != null) {
               val node = path.node()
               println("path.node()=$node")
               when (node) {
                  is SpecNodeDescriptor -> {
                     val name = node.fqn.shortName().asString() + " [run all]"
                     println(name)
                     val config = manager.createConfiguration(name, KotestConfigurationFactory(KotestConfigurationType))
                     val run = config.configuration as KotestRunConfiguration
                     run.setSpecName(node.fqn.asString())
                     run.setModule(node.module)
                     run.setGeneratedName()
                     manager.addConfiguration(config)
                     ExecutionUtil.runConfiguration(config, executor)
                  }
                  is TestNodeDescriptor -> {
                     val name = node.test.test.name + " [run test]"
                     println(name)
                     val config = manager.createConfiguration(name, KotestConfigurationFactory(KotestConfigurationType))
                     val run = config.configuration as KotestRunConfiguration
                     run.setTestName(node.test.test.name)
                     run.setSpecName(node.spec.fqn.asString())
                     run.setModule(node.module)
                     run.setGeneratedName()
                     manager.addConfiguration(config)
                     ExecutionUtil.runConfiguration(config, executor)
                  }
               }
            }
         }
      })

      result.add(object : AnAction(AllIcons.Actions.StartDebugger) {
         override fun actionPerformed(e: AnActionEvent) {
            val manager = RunManager.getInstance(project)
            val executor = ExecutorRegistry.getInstance().getExecutorById("Debug")
            val path = tree.selectionPath
            if (path != null) {
               when (val node = path.node()) {
                  is SpecNodeDescriptor -> {
                     val name = node.name + " [debug all]"
                     println(name)
                     val config = manager.createConfiguration(name, KotestConfigurationFactory(KotestConfigurationType))
                     manager.addConfiguration(config)
                     ExecutionUtil.runConfiguration(config, executor)
                  }
                  is TestNodeDescriptor -> {
                     val name = node.name + " [debug test]"
                     println(name)
                     val config = manager.createConfiguration(name, KotestConfigurationFactory(KotestConfigurationType))
                     manager.addConfiguration(config)
                     ExecutionUtil.runConfiguration(config, executor)
                  }
               }
            }
         }
      })

      return result
   }

   private fun listenForSelectedFileChanges() {
      project.messageBus.connect().subscribe(
         FileEditorManagerListener.FILE_EDITOR_MANAGER,
         object : FileEditorManagerListener {
            override fun selectionChanged(event: FileEditorManagerEvent) {
               refreshContent(event.newFile)
               val file = event.newFile
               if (file != null) {
                  FileDocumentManager.getInstance().getDocument(file)?.addDocumentListener(object :
                     DocumentListener {
                     override fun documentChanged(event: DocumentEvent) {
                        refreshContent(file)
                     }
                  })
               }
            }
         }
      )
   }

   private fun setInitialContent() {
      try {
         val editor = FileEditorManager.getInstance(project).selectedEditor
         val file = editor?.file
         refreshContent(file)
      } catch (e: IndexNotReadyException) {
      }
   }

   private fun refreshContent(file: VirtualFile?) {
      if (file == null) {
         tree.model = emptyTreeModel()
      } else {
         try {
            val module = file.getModule(project)
            if (module == null) {
               tree.model = emptyTreeModel()
            } else {
               val specs = file.toPsiFile(project)?.specs() ?: emptyList()
               val model = treeModel(project, specs, module)
               tree.model = model
               tree.expandAllNodes()
            }
         } catch (e: IndexNotReadyException) {
         }
      }
   }

   private fun createTree(): JTree {
      val tree = com.intellij.ui.treeStructure.Tree()
      tree.selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION
      tree.showsRootHandles = true
      tree.cellRenderer = NodeRenderer()
      tree.addTreeSelectionListener(TestExplorerTreeSelectionListener)
      return tree
   }
}
