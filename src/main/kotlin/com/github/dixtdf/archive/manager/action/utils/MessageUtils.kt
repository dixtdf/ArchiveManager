package com.github.dixtdf.archive.manager.action.utils

import java.util.*

class MessageUtils {

    private val baseName = "messages.Language"
    private var bundle: ResourceBundle? = null

    init {
        bundle = ResourceBundle.getBundle(baseName, Locale.getDefault())
    }

    fun messages(key: String?): String? {
        return bundle?.getString(key)
    }

}
