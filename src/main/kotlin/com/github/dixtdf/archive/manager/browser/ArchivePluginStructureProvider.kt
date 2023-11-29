package com.github.dixtdf.archive.manager.browser

import com.github.dixtdf.archive.manager.browser.base.BaseArchiveFileType
import com.github.dixtdf.archive.manager.browser.formats.zip.PsiZipFileNode
import com.github.dixtdf.archive.manager.browser.util.FSUtils
import com.intellij.ide.highlighter.ArchiveFileType
import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiManager
import org.apache.commons.lang.StringUtils

class ArchivePluginStructureProvider : TreeStructureProvider {

    override fun modify(
        parent: AbstractTreeNode<*>,
        children: MutableCollection<AbstractTreeNode<*>>,
        settings: ViewSettings?
    ): MutableCollection<AbstractTreeNode<*>> {
        return children.mapTo(ArrayList(children.size), ::convertArchiveNode)
    }

    private fun convertArchiveNode(node: AbstractTreeNode<*>): AbstractTreeNode<*> {
        if (node is PsiFileNode) {
            val virtualFile = node.virtualFile
            if (virtualFile != null) {
                try {
                    var psiFile = node.value
                    if ((psiFile.fileType is BaseArchiveFileType || psiFile.fileType is ArchiveFileType)
                        && StringUtils.countMatches(virtualFile.path, "!/") > 0
                    ) {
                        val tempNestedFile = FSUtils.copyFileToTemp(virtualFile)
                        val nestedVirtualFile = LocalFileSystem.getInstance().findFileByIoFile(tempNestedFile)
                        if (nestedVirtualFile != null) {
                            psiFile = PsiManager.getInstance(node.project!!).findFile(nestedVirtualFile)
                        }
                    }
                    return when (val fileType = node.virtualFile?.fileType) {
                        is BaseArchiveFileType -> fileType.createPsiNode(node.project, psiFile, node.settings)
                        is ArchiveFileType -> PsiZipFileNode(node.project, psiFile, node.settings)
                        else -> node
                    }
                } catch (t: Throwable) {
                    return node
                }
            }
        }
        return node
    }

}
