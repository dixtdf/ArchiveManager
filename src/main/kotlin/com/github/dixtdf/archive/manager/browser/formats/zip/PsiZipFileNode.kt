package com.github.dixtdf.archive.manager.browser.formats.zip

import com.github.dixtdf.archive.manager.browser.base.BasePsiFileNode
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.JarFileSystem
import com.intellij.psi.PsiFile

class PsiZipFileNode(
    project: Project?, value: PsiFile,
    viewSettings: ViewSettings?
) : BasePsiFileNode(project, value, viewSettings) {
    override fun getChildrenImpl(): MutableCollection<AbstractTreeNode<*>> {
        val parentChildren = super.getChildrenImpl()
        // If parent can traverse children, return as is
        return if (!parentChildren.isNullOrEmpty()) {
            parentChildren
        } else {
            getChildrenForVirtualFile(virtualFile?.let { JarFileSystem.getInstance().getJarRootForLocalFile(it) })
        }
    }
}
