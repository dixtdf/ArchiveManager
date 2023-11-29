package com.github.dixtdf.archive.manager.action

import com.github.dixtdf.archive.manager.action.utils.ExtractUtils
import com.github.dixtdf.archive.manager.action.utils.MessageUtils
import com.github.dixtdf.archive.manager.action.utils.PathUtils
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.nio.file.FileSystems

class UnPackCurrentLocationAction : AnAction() {

    override fun update(e: AnActionEvent) {
        e.presentation.text = MessageUtils().messages("unPackCurrentLocation")
    }

    override fun actionPerformed(event: AnActionEvent) {
        val canonicalFile = event.getData(CommonDataKeys.VIRTUAL_FILE)?.canonicalFile;

        if (canonicalFile != null && canonicalFile.exists() && !canonicalFile.isDirectory) {
            val compressSeparatorsFileName = FilenameUtils.separatorsToSystem(canonicalFile.canonicalPath)

            val fullPath = FilenameUtils.getFullPath(compressSeparatorsFileName) + FilenameUtils.getBaseName(
                compressSeparatorsFileName
            )
            if (File(fullPath).exists()) {
                val index = PathUtils.checkFullPath(fullPath, 1)
                ExtractUtils.extract(
                    compressSeparatorsFileName,
                    "$fullPath$index${FileSystems.getDefault().separator}",
                    event
                )
            } else {
                ExtractUtils.extract(
                    compressSeparatorsFileName,
                    fullPath + FileSystems.getDefault().separator,
                    event
                )
            }
        } else {
            event.presentation.isVisible = false
        }
    }

}
