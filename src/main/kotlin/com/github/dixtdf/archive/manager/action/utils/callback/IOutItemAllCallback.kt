package com.github.dixtdf.archive.manager.action.utils.callback

import com.github.dixtdf.archive.manager.action.utils.CompressItem
import net.sf.sevenzipjbinding.ICryptoGetTextPassword
import net.sf.sevenzipjbinding.IOutCreateCallback
import net.sf.sevenzipjbinding.IOutItemAllFormats
import net.sf.sevenzipjbinding.ISequentialInStream
import net.sf.sevenzipjbinding.impl.OutItemFactory
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream
import java.io.RandomAccessFile

class IOutItemAllCallback(
    private val items: Array<CompressItem>,
    private val setTotalCallback: (total: Long) -> Unit = {},
    private val setCompletedCallback: (complete: Long) -> Unit = {},
    private val setOperationResultCallback: (operationResultOk: Boolean) -> Unit = {},
    private val getItemInformationCallback: (index: Int, outItemFactory: OutItemFactory<IOutItemAllFormats>) -> Unit = { _, _ -> },
    private val getStreamCallback: (index: Int) -> Unit = {},
    private val password: String?
) : IOutCreateCallback<IOutItemAllFormats>, ICryptoGetTextPassword {

    override fun setTotal(total: Long) {
        setTotalCallback(total)
    }

    override fun setCompleted(complete: Long) {
        setCompletedCallback(complete)
    }

    override fun setOperationResult(operationResultOk: Boolean) {
        setOperationResultCallback(operationResultOk)
    }

    override fun getItemInformation(
        index: Int,
        outItemFactory: OutItemFactory<IOutItemAllFormats>?
    ): IOutItemAllFormats {
        val item = outItemFactory!!.createOutItem()
        val localFile = items[index].localFile
        if (localFile.isDirectory) {
            // Directory
            item.propertyIsDir = true
        } else {
            // File
            item.dataSize = localFile.length()
        }
        item.propertyPath = items[index].archivePath

        getItemInformationCallback(index, outItemFactory)
        return item
    }

    override fun getStream(index: Int): ISequentialInStream? {
        getStreamCallback(index)
        val localFile = items[index].localFile
        return if (localFile.isDirectory) null else RandomAccessFileInStream(RandomAccessFile(localFile, "r"))
    }

    override fun cryptoGetTextPassword(): String? {
        return password
    }

}
