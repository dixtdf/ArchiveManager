package com.github.dixtdf.archive.manager.action.utils

import com.intellij.ide.IdeBundle
import com.intellij.ide.plugins.PluginManagerUISettings
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project

class TaskUtils {

    companion object {
        fun compressBackgroundable(
            project: Project,
            run: (indicator: ProgressIndicator) -> Unit = {},
            cancel: () -> Unit = {},
        ) {
            ProgressManager.getInstance().run(object : Task.Backgroundable(
                /* project = */ project,
                /* title = */ IdeBundle.message("plugin.updater.downloading"),
                /* canBeCancelled = */ true,
                /* backgroundOption = */ PluginManagerUISettings.getInstance(),
            ) {
                override fun run(indicator: ProgressIndicator) {
                    run(indicator)
                }

                override fun onCancel() {
                    cancel()
                }
            })
        }
    }

}
