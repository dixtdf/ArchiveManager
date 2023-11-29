package com.github.dixtdf.archive.manager.action

import com.github.dixtdf.archive.manager.action.utils.MessageUtils
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class PackGroupAction : ActionGroup() {
    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        TODO("Not yet implemented")
    }

    override fun update(e: AnActionEvent) {
        e.presentation.text = MessageUtils().messages("packGroupActionText")
        super.update(e)
    }

}
