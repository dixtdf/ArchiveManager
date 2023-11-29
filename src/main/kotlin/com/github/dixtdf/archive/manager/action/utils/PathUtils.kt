package com.github.dixtdf.archive.manager.action.utils

import java.io.File

class PathUtils {

    companion object {
        fun checkCommonPath(fullPath: String, fileName: String, index: Int, extension: String): Int {
            if (File(fullPath + fileName + "_$index.$extension").exists()) {
                var i = index + 1
                return checkCommonPath(fullPath, fileName, i, extension)
            } else {
                return index
            }
        }

        fun findCommonPath(paths: List<String>): String {
            if (paths.isEmpty()) {
                return ""
            }
            val pathPartsList = paths.map { it.split(File.separator) }
            val minLength = pathPartsList.map { it.size }.minOrNull() ?: 0
            val commonPathParts = mutableListOf<String>()
            for (i in 0 until minLength) {
                val part = pathPartsList[0][i]
                if (pathPartsList.all { it[i] == part }) {
                    commonPathParts.add(part)
                } else {
                    break
                }
            }
            return commonPathParts.joinToString(separator = File.separator) + File.separator
        }
    }

}
