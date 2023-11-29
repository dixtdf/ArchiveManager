package com.github.dixtdf.archive.manager.browser

import com.github.dixtdf.archive.manager.browser.util.Psi.Companion.processChildren
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.util.containers.ContainerUtil

class PsiGenericDirectoryNode(
    project: Project?, value: PsiDirectory,
    viewSettings: ViewSettings?
) : PsiDirectoryNode(project, value, viewSettings) {
    override fun getChildrenImpl(): MutableCollection<AbstractTreeNode<*>> {
        val project = project
        if (project != null) {
            val psiDirectory = value
            if (psiDirectory != null) {
                return processChildren(psiDirectory)
            }
        }
        return ContainerUtil.emptyList()
    }
}
