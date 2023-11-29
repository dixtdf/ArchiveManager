package com.github.dixtdf.archive.manager.action

import com.github.dixtdf.archive.manager.action.utils.MessageUtils
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup

class PackGroupAction : DefaultActionGroup() {

    override fun update(e: AnActionEvent) {
        e.presentation.text = MessageUtils().messages("packGroupActionText")
        super.update(e)
    }

}
