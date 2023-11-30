package com.github.dixtdf.archive.manager.action.utils

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.vfs.LocalFileSystem
import net.sf.sevenzipjbinding.SevenZip
import net.sf.sevenzipjbinding.SevenZipException
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream
import net.sf.sevenzipjbinding.simple.ISimpleInArchive
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.math.BigDecimal
import java.math.RoundingMode


class ExtractUtils {

    companion object {
        fun extract(fileName: String, fullPath: String, password: String?, event: AnActionEvent) {
            TaskUtils.compressBackgroundable(project = event.getData(PlatformDataKeys.PROJECT)!!,
                run = { indicator ->
                    run {
                        var randomAccessFileInStream: RandomAccessFileInStream? = null
                        var simpleInArchive: ISimpleInArchive? = null
                        var randomAccessFile: RandomAccessFile? = null
                        try {
                            randomAccessFile = RandomAccessFile(fileName, "r")
                            randomAccessFileInStream = RandomAccessFileInStream(randomAccessFile)
                            simpleInArchive = SevenZip.openInArchive(null, randomAccessFileInStream).simpleInterface

                            val count = simpleInArchive.archiveItems.size
                            var index = 1
                            for (item in simpleInArchive.archiveItems) {
                                if (indicator.isCanceled) {
                                    throw Exception("indicator.isCanceled")
                                }
                                val data = (BigDecimal.valueOf(index.toLong())
                                    .divide(BigDecimal.valueOf(count.toLong()), 2, RoundingMode.HALF_UP)).toDouble()
                                indicator.fraction = data
                                indicator.text = "进度:$index/$count"
                                var file = File(fullPath + item.path.toString())
                                if (item.isFolder) {
                                    file.mkdirs()
                                    continue
                                }
                                file.parentFile.mkdirs()
                                var randomAccessFileOutStream: RandomAccessFileOutStream? = null
                                try {
                                    randomAccessFileOutStream = RandomAccessFileOutStream(RandomAccessFile(file, "rw"))
                                    if (StringUtils.isEmpty(password)) {
                                        item.extractSlow(randomAccessFileOutStream)
                                    } else {
                                        item.extractSlow(randomAccessFileOutStream, password)
                                    }
                                } finally {
                                    randomAccessFileOutStream?.close()
                                }
                                index += 1
                            }
                            LocalFileSystem.getInstance().refresh(false)
                            NotificationUtils.info("执行完成", "执行完成")
                        } catch (e: SevenZipException) {
                            NotificationUtils.error("执行出错啦", "Error occurs: ${e.message.toString()}")
                        } catch (e: Exception) {
                            NotificationUtils.error("执行出错啦", "Error occurs: ${e.message.toString()}")
                        } finally {
                            if (randomAccessFile != null) {
                                try {
                                    randomAccessFile.close()
                                } catch (e: IOException) {
                                    NotificationUtils.error(
                                        "执行出错啦",
                                        "Error closing archive: ${e.message.toString()}"
                                    )
                                }
                            }
                            if (simpleInArchive != null) {
                                try {
                                    simpleInArchive.close()
                                } catch (e: IOException) {
                                    NotificationUtils.error(
                                        "执行出错啦",
                                        "Error closing archive: ${e.message.toString()}"
                                    )
                                }
                            }
                            if (randomAccessFileInStream != null) {
                                try {
                                    randomAccessFileInStream.close()
                                } catch (e: IOException) {
                                    NotificationUtils.error("执行出错啦", "Error closing file: ${e.message.toString()}")
                                }
                            }
                        }
                    }
                })
        }
    }
}
