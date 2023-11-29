package com.github.dixtdf.archive.manager.browser.base.nest

import com.github.dixtdf.archive.manager.browser.base.BaseArchiveHandler
import com.intellij.openapi.vfs.VirtualFile

interface SupportsNestedArchives {
    fun getHandlerForFile(file: VirtualFile): BaseArchiveHandler<*>
}
