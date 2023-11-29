package com.github.dixtdf.archive.manager.action

import com.github.dixtdf.archive.manager.action.utils.ExtractUtils
import com.github.dixtdf.archive.manager.action.utils.MessageUtils
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFileManager
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
            // 处理用户选择的目录
            ExtractUtils.extract(
                compressSeparatorsFileName,
                FilenameUtils.getFullPath(compressSeparatorsFileName) + FileSystems.getDefault().separator + FilenameUtils.getBaseName(
                    compressSeparatorsFileName
                ) + FileSystems.getDefault().separator,
                event
            )
            val directory = LocalFileSystem.getInstance()
                .findFileByIoFile(File(FilenameUtils.getFullPath(compressSeparatorsFileName)))
            VirtualFileManager.getInstance().refreshAndFindFileByUrl(directory!!.url)
        } else {
            event.presentation.isVisible = false
        }
    }

}
