package com.github.dixtdf.archive.manager.browser.util

import com.intellij.util.io.FileAccessorCache

class FileAccessorCache {
    companion object {
        fun <T, R> FileAccessorCache.Handle<T>.getAndUse(block: (T) -> R): R {
            var released = false
            try {
                val value = get()
                return block(value)
            } catch (e: Exception) {
                released = true
                try {
                    release()
                } catch (ignore: Exception) {
                }
                throw e
            } finally {
                if (!released) {
                    release()
                }
            }
        }
    }
}
