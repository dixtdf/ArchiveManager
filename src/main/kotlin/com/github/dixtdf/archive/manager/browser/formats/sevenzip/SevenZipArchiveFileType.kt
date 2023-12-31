package com.github.dixtdf.archive.manager.browser.formats.sevenzip

import com.github.dixtdf.archive.manager.browser.base.BaseArchiveFileType
import com.intellij.icons.AllIcons.FileTypes
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import javax.swing.Icon

class SevenZipArchiveFileType : BaseArchiveFileType {
    override fun getDefaultExtension(): String = ""

    override fun getIcon(): Icon = FileTypes.Archive

    override fun getCharset(file: VirtualFile, content: ByteArray): String? = null

    override fun getName() = "ARCHIVE_MANAGER"

    override fun getDescription() = "ARCHIVE_MANAGER"

    override fun isBinary() = true

    override fun createPsiNode(project: Project?, value: PsiFile, viewSettings: ViewSettings?): PsiFileNode {
        return SevenZipPsiFileNode(project, value, viewSettings)
    }

    companion object {
        @Suppress("unused")
        @JvmStatic
        val INSTANCE = SevenZipArchiveFileType()
    }
}
