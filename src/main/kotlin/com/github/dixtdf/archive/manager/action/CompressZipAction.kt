package com.github.dixtdf.archive.manager.action

import com.github.dixtdf.archive.manager.action.utils.CompressUtils
import com.github.dixtdf.archive.manager.action.utils.MessageUtils
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import net.sf.sevenzipjbinding.ArchiveFormat

class CompressZipAction : AnAction() {

    override fun update(e: AnActionEvent) {
        e.presentation.text = MessageUtils().messages("zipActionText")
    }

    override fun actionPerformed(e: AnActionEvent) {
        CompressUtils.compress(e, "zip", ArchiveFormat.ZIP)
    }

}
