package com.github.dixtdf.archive.manager.action.utils.callback

import com.github.dixtdf.archive.manager.action.utils.CompressItem
import net.sf.sevenzipjbinding.IOutCreateCallback
import net.sf.sevenzipjbinding.IOutItemZip
import net.sf.sevenzipjbinding.ISequentialInStream
import net.sf.sevenzipjbinding.PropID
import net.sf.sevenzipjbinding.impl.OutItemFactory
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream
import java.io.RandomAccessFile


class IOutItemZipCallback(
    private val items: Array<CompressItem>,
    private val setTotalCallback: (total: Long) -> Unit = {},
    private val setCompletedCallback: (complete: Long) -> Unit = {},
    private val setOperationResultCallback: (operationResultOk: Boolean) -> Unit = {},
    private val getItemInformationCallback: (index: Int, outItemFactory: OutItemFactory<IOutItemZip>) -> Unit = { _, _ -> },
    private val getStreamCallback: (index: Int) -> Unit = {}
) : IOutCreateCallback<IOutItemZip> {

    override fun setTotal(total: Long) {
        setTotalCallback(total)
    }

    override fun setCompleted(complete: Long) {
        setCompletedCallback(complete)
    }

    override fun setOperationResult(operationResultOk: Boolean) {
        setOperationResultCallback(operationResultOk)
    }

    override fun getItemInformation(index: Int, outItemFactory: OutItemFactory<IOutItemZip>?): IOutItemZip {
        var attr = PropID.AttributesBitMask.FILE_ATTRIBUTE_UNIX_EXTENSION
        val item = outItemFactory!!.createOutItem()
        val localFile = items[index].localFile
        if (localFile.isDirectory) {
            // Directory
            item.propertyIsDir = true
            attr = attr or PropID.AttributesBitMask.FILE_ATTRIBUTE_DIRECTORY
            attr = attr or (0x81ED shl 16) // permissions: drwxr-xr-x
        } else {
            // File
            item.dataSize = localFile.length()
            attr = attr or (0x81a4 shl 16) // permissions: -rw-r--r--
        }
        item.propertyPath = items[index].archivePath
        item.propertyAttributes = attr

        getItemInformationCallback(index, outItemFactory)
        return item
    }

    override fun getStream(index: Int): ISequentialInStream? {
        getStreamCallback(index)
        val localFile = items[index].localFile
        return if (localFile.isDirectory) null else RandomAccessFileInStream(RandomAccessFile(localFile, "r"))
    }

}
