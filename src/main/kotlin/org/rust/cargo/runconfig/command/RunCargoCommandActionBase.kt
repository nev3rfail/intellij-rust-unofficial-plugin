/*
* Use of this source code is governed by the MIT license that can be
* found in the LICENSE file.
*/

package org.rust.cargo.runconfig.command

import com.intellij.execution.ExecutorRegistry
import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManagerEx
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import org.rust.cargo.project.CargoToolWindowPanel
import org.rust.cargo.project.model.CargoProject
import org.rust.cargo.project.model.cargoProjects
import org.rust.cargo.runconfig.createCargoCommandRunConfiguration
import org.rust.cargo.runconfig.hasCargoProject
import org.rust.cargo.toolchain.CargoCommandLine
import javax.swing.Icon

abstract class RunCargoCommandActionBase(icon: Icon) : AnAction(icon) {
    override fun update(e: AnActionEvent) {
        val hasCargoProject = e.project?.hasCargoProject == true
        e.presentation.isEnabledAndVisible = hasCargoProject
    }

    protected fun getAppropriateCargoProject(e: AnActionEvent): CargoProject? {
        val cargoProjects = e.project?.cargoProjects ?: return null
        cargoProjects.allProjects.singleOrNull()?.let { return it }

        e.getData(CommonDataKeys.VIRTUAL_FILE)
            ?.let { cargoProjects.findProjectForFile(it) }
            ?.let { return it }

        val cargoPanel = ToolWindowManager.getInstance(e.project!!)
            ?.getToolWindow("Cargo")
            ?.contentManager
            ?.getContent(0)?.component as? CargoToolWindowPanel


        return cargoPanel?.selectedProject ?: cargoProjects.allProjects.firstOrNull()
    }

    protected fun runCommand(project: Project, cargoCommandLine: CargoCommandLine, cargoProject: CargoProject) {
        cargoCommandLine.name +=
            if (project.cargoProjects.allProjects.size > 1) " [" + cargoProject.presentableName + "]" else ""

        val runConfiguration = createRunConfiguration(project, cargoCommandLine)
        val executor = ExecutorRegistry.getInstance().getExecutorById(DefaultRunExecutor.EXECUTOR_ID)
        ProgramRunnerUtil.executeConfiguration(runConfiguration, executor)
    }

    private fun createRunConfiguration(project: Project, cargoCommandLine: CargoCommandLine): RunnerAndConfigurationSettings {
        val runManager = RunManagerEx.getInstanceEx(project)

        return runManager.createCargoCommandRunConfiguration(cargoCommandLine).apply {
            runManager.setTemporaryConfiguration(this)
        }
    }
}
