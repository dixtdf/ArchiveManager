package com.github.dixtdf.archive.manager.action

import com.github.dixtdf.archive.manager.action.utils.ExtractUtils
import com.github.dixtdf.archive.manager.action.utils.MessageUtils
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.FileChooserDialog
import com.intellij.openapi.fileChooser.FileChooserFactory
import org.apache.commons.io.FilenameUtils
import java.nio.file.FileSystems


class UnPackFolderAction : AnAction() {

    override fun update(e: AnActionEvent) {
        e.presentation.text = MessageUtils().messages("unPackFolderAction")
    }

    override fun actionPerformed(event: AnActionEvent) {
        val canonicalFile = event.getData(CommonDataKeys.VIRTUAL_FILE)?.canonicalFile;

        if (canonicalFile != null && canonicalFile.exists() && !canonicalFile.isDirectory) {
            val compressSeparatorsFileName = FilenameUtils.separatorsToSystem(canonicalFile.canonicalPath)

            val descriptor = FileChooserDescriptor(false, true, false, false, false, false)
            val fileChooserDialog: FileChooserDialog = FileChooserFactory.getInstance()
                .createFileChooser(descriptor, event.getData(PlatformDataKeys.PROJECT), null)
            val virtualFiles = fileChooserDialog.choose(event.getData(PlatformDataKeys.PROJECT), null)

            // 处理用户选择的目录
            if (virtualFiles.isNotEmpty()) {
                val selectedDirectory = virtualFiles[0].path
                ExtractUtils.extract(
                    FilenameUtils.separatorsToSystem(compressSeparatorsFileName),
                    FilenameUtils.separatorsToSystem(selectedDirectory)
                            + FileSystems.getDefault().separator
                            + FilenameUtils.getBaseName(compressSeparatorsFileName)
                            + FileSystems.getDefault().separator,
                    event
                )
            }
        } else {
            event.presentation.isVisible = false
        }
    }

}
