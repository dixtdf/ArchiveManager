package com.github.dixtdf.archive.manager.browser.base.sevenzip

import com.github.dixtdf.archive.manager.browser.formats.sevenzip.SevenZipArchiveHolder
import net.sf.sevenzipjbinding.ExtractOperationResult
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.nio.file.Files


class SevenZipInputStream(
    private val holder: SevenZipArchiveHolder,
    private val item: ISimpleInArchiveItem
) : InputStream() {
    private val stream = ByteArrayInputStream(InputStream.nullInputStream().readAllBytes())

    override fun read(): Int = stream.read()

    override fun read(b: ByteArray?, off: Int, len: Int): Int = stream.read(b, off, len)

    override fun available(): Int = stream.available()

    override fun reset() = stream.reset()

    override fun skip(n: Long): Long = stream.skip(n)

    override fun mark(readlimit: Int) = stream.mark(readlimit)

    override fun markSupported(): Boolean = stream.markSupported()

    override fun close() {
        stream.close()
        holder.closeStream()
    }

    fun extract(file: File): ExtractOperationResult {
        val output = Files.newOutputStream(file.toPath())
        return output.use {
            holder.useStream {
                item.extractSlow { data -> data?.also(output::write)?.size ?: 0 }
            }
        }
    }

    companion object {
        const val BUFFER_SIZE = 32768
    }
}
