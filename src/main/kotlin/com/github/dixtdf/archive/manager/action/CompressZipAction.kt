package com.github.dixtdf.archive.manager.action

import com.github.dixtdf.archive.manager.action.utils.CompressUtils
import com.github.dixtdf.archive.manager.action.utils.MessageUtils
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import net.sf.sevenzipjbinding.ArchiveFormat
import org.apache.commons.io.FilenameUtils

class CompressZipAction : AnAction() {

    override fun update(e: AnActionEvent) {
        val selectedItems = e.dataContext.getData("selectedItems") as? Array<Any>
        val sb = StringBuilder()
        selectedItems!!.forEachIndexed { index, item ->
            when (item) {
                is PsiFileNode -> sb.append(FilenameUtils.getName(item.virtualFile!!.canonicalPath))
                is PsiDirectoryNode -> sb.append(FilenameUtils.getName(item.virtualFile!!.canonicalPath))
            }
            if (index + 1 != selectedItems.size) {
                sb.append(";")
            }
        }
        e.presentation.text = "${MessageUtils().messages("zipActionText")} $sb"
    }

    override fun actionPerformed(e: AnActionEvent) {
        CompressUtils.compress(e, "zip", ArchiveFormat.ZIP)
    }

}
