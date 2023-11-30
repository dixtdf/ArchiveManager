package com.github.dixtdf.archive.manager.action

import com.github.dixtdf.archive.manager.action.utils.ExtractUtils
import com.github.dixtdf.archive.manager.action.utils.MessageUtils
import com.github.dixtdf.archive.manager.action.utils.PathUtils
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.nio.file.FileSystems
import java.util.*

class UnPackCurrentLocationAction : AnAction() {

    override fun update(e: AnActionEvent) {
        val selectedItems = e.dataContext.getData("selectedItems") as? Array<Any>
        e.presentation.isVisible = Arrays.stream(selectedItems).allMatch {
            when (it) {
                is PsiFileNode -> true
                else -> false
            }
        }
        val sb = StringBuilder()
        selectedItems!!.forEachIndexed { index, item ->
            when (item) {
                is PsiFileNode -> sb.append(FilenameUtils.getName(item.virtualFile!!.canonicalPath))
            }
            if (index + 1 != selectedItems.size) {
                sb.append(";")
            }
        }
        e.presentation.text = "${MessageUtils().messages("unPackCurrentLocation")} $sb"
    }

    override fun actionPerformed(event: AnActionEvent) {
        val selectedItems = event.dataContext.getData("selectedItems") as? Array<Any>
        selectedItems!!.forEach {
            val psiFileNode = it as PsiFileNode
            val psiFileNodePath = FilenameUtils.separatorsToSystem(psiFileNode.virtualFile!!.canonicalPath)
            val fullPath = FilenameUtils.getFullPath(psiFileNodePath) + FilenameUtils.getBaseName(
                psiFileNodePath
            )
            if (File(fullPath).exists()) {
                val index = PathUtils.checkFullPath(fullPath, 1)
                ExtractUtils.extract(
                    psiFileNodePath,
                    "$fullPath$index${FileSystems.getDefault().separator}",
                    event
                )
            } else {
                ExtractUtils.extract(
                    psiFileNodePath,
                    fullPath + FileSystems.getDefault().separator,
                    event
                )
            }
        }

    }

}
