package com.github.dixtdf.archive.manager.action.utils

import com.github.dixtdf.archive.manager.action.utils.callback.IOutItemAllCallback
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.vfs.LocalFileSystem
import net.sf.sevenzipjbinding.*
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream

class CompressUtils {
    companion object {
        fun compress(e: AnActionEvent, extension: String, archiveFormat: ArchiveFormat) {
            compress(e, extension, archiveFormat, null)
        }

        fun compress(e: AnActionEvent, extension: String, archiveFormat: ArchiveFormat, password: String?) {
            val currentFile = e.getData(CommonDataKeys.VIRTUAL_FILE)?.canonicalFile;
            val selectedItems = e.dataContext.getData("selectedItems") as? Array<Any>
            if (currentFile != null && currentFile.exists() && !selectedItems.isNullOrEmpty()) {
                // 在当前文件夹下添加选中的文件
                val currentFilePath = FilenameUtils.separatorsToSystem(currentFile.path)
                var outFile: File? = null;
                TaskUtils.compressBackgroundable(
                    project = e.getData(PlatformDataKeys.PROJECT)!!,
                    run = { indicator ->
                        run {
                            var items: Array<CompressItem> = arrayOf()
                            if (selectedItems.size == 1) {
                                val localFile = File(currentFile.path)

                                val fullPath = FilenameUtils.getFullPath(localFile.path)

                                items += CompressItem(
                                    StringUtils.removeStart(localFile.path, FilenameUtils.getFullPath(localFile.path)),
                                    localFile
                                )
                                if (localFile.isDirectory) {
                                    val listFiles = FileUtils.listFiles(
                                        localFile,
                                        null,
                                        true
                                    )
                                    if (CollectionUtils.isNotEmpty(listFiles)) {
                                        items += listFiles.stream()
                                            .map { CompressItem(StringUtils.removeStart(it.path, fullPath), it) }
                                            .toList()
                                            .toTypedArray()
                                    }
                                }
                            } else {
                                var addZipFile = Arrays.stream(selectedItems).flatMap {
                                    when (it) {
                                        is PsiFileNode -> Stream.of(File((it as PsiFileNode).virtualFile?.canonicalPath!!))
                                        is PsiDirectoryNode -> {
                                            val file = File((it as PsiDirectoryNode).virtualFile?.canonicalPath!!)
                                            val listFiles = FileUtils.listFiles(
                                                file,
                                                null,
                                                true
                                            )
                                            if (CollectionUtils.isEmpty(listFiles)) Stream.of(file) else listFiles.stream()
                                        }

                                        else -> Stream.empty()
                                    }
                                }.filter { it != null }.collect(Collectors.toList())
                                val addZipPathList = addZipFile.stream().map { it?.path }.collect(Collectors.toList())
                                val findCommonPath = PathUtils.findCommonPath(addZipPathList as MutableList<String>)

                                val indexOf = currentFilePath.indexOf(findCommonPath)
                                var samePath = ""
                                if (indexOf != -1) {
                                    val substring = currentFilePath.substring(0, indexOf)
                                    samePath = substring + findCommonPath;
                                }

                                for (file in addZipFile) {
                                    val removeStart = StringUtils.removeStart(file!!.path, samePath)
                                    items += CompressItem(
                                        removeStart, file
                                    )
                                }
                            }

                            outFile = File("$currentFilePath.$extension")
                            if (outFile!!.exists()) {
                                var fullPath = FilenameUtils.getFullPath(currentFilePath)
                                var fileName = FilenameUtils.getName(currentFilePath)
                                val checkIndex = PathUtils.checkCommonPath(fullPath, fileName, 1, extension)
                                outFile = File(fullPath + fileName + "_$checkIndex.$extension")
                            }

                            var total = 0L
                            var success = false
                            var raf: RandomAccessFile? = null
                            var outArchive: IOutCreateArchive<IOutItemAllFormats>? = null
                            try {
                                raf = RandomAccessFile(outFile!!.path, "rw")
                                outArchive = SevenZip.openOutArchive(archiveFormat);
                                if (outArchive is IOutFeatureSetLevel) {
                                    outArchive.setLevel(0)
                                }
                                if (outArchive is IOutFeatureSetMultithreading) {
                                    outArchive.setThreadCount(1)
                                }
                                outArchive.createArchive(
                                    RandomAccessFileOutStream(raf), items.size, IOutItemAllCallback(
                                        items = items,
                                        setTotalCallback = { total = it },
                                        setCompletedCallback = {
                                            if (indicator.isCanceled) {
                                                throw Exception("indicator.isCanceled")
                                            }
                                            val data = (BigDecimal.valueOf(it)
                                                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP)).toDouble()
                                            indicator.fraction = data
                                            indicator.text = "进度:${FileUtils.byteCountToDisplaySize(it)}/${
                                                FileUtils.byteCountToDisplaySize(total)
                                            }"
                                        },
                                        password = password
                                    )
                                )
                                LocalFileSystem.getInstance().refresh(false)
                                NotificationUtils.info("执行完成", "执行完成")
                                success = true
                            } catch (e: SevenZipException) {
                                NotificationUtils.error(
                                    "执行出错啦",
                                    "$extension-Error occurs: ${e.message.toString()}"
                                )
                            } catch (e: Exception) {
                                NotificationUtils.error("执行出错啦", "Error occurs: ${e.message.toString()}")
                            } finally {
                                if (outArchive != null) {
                                    try {
                                        outArchive.close()
                                    } catch (e: IOException) {
                                        NotificationUtils.error(
                                            "执行出错啦",
                                            "Error closing archive: ${e.message.toString()}"
                                        )
                                        success = false
                                    }
                                }
                                if (raf != null) {
                                    try {
                                        raf.close()
                                    } catch (e: IOException) {
                                        NotificationUtils.error(
                                            "执行出错啦",
                                            "Error closing file: ${e.message.toString()}"
                                        )
                                        success = false
                                    }
                                }
                                if (!success) {
                                    FileUtils.delete(outFile)
                                }
                            }
                        }
                    },
                    cancel = {
                        run {
                            FileUtils.delete(outFile)
                        }
                    }
                )
            } else {
                NotificationUtils.error("执行出错啦", "您选择的文件可能不存在，请重试")
            }
        }
    }
}
