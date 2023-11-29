package com.github.dixtdf.archive.manager.browser.base.nest

import com.intellij.openapi.vfs.VirtualFile
import java.io.InputStream

interface SupportsStreamForVirtualFile {
    fun getInputStreamForFile(file: VirtualFile): InputStream
}
