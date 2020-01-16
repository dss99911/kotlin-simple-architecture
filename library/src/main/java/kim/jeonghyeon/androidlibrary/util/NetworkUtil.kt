package kim.jeonghyeon.androidlibrary.util

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.net.Uri
import android.os.Environment
import android.webkit.URLUtil
import android.webkit.WebSettings
import kim.jeonghyeon.androidlibrary.extension.ctx
import kim.jeonghyeon.androidlibrary.extension.toast
import org.jetbrains.anko.connectivityManager
import org.jetbrains.anko.downloadManager


object NetworkUtil {
    @SuppressLint("MissingPermission")
    fun isConnected(): Boolean {
        return ctx.connectivityManager.activeNetworkInfo?.isConnected ?: false
    }

    @SuppressLint("MissingPermission")
    fun isMeteredConnected(): Boolean = ctx.connectivityManager.isActiveNetworkMetered

    @SuppressLint("MissingPermission")
    fun isUnmeteredConnected(): Boolean = isConnected() && !isMeteredConnected()

    fun checkAndDownload(
        url: String,
        mimeType: String,
        headerMap: Map<String, String>?,
        downloadId: Long?
    ): Long? {

        return if (downloadId != null && isDownloading(downloadId)) {
            toast("Already downloading")
            downloadId
        } else {
            toast("Download started")
            download(url, mimeType, headerMap)
        }
    }

    fun download(url: String, mimeType: String, headerMap: Map<String, String>?): Long? {
        val fileName = URLUtil.guessFileName(url, null, mimeType)
        val request = DownloadManager.Request(Uri.parse(url))
        headerMap?.forEach { (key, value) ->
            request.addRequestHeader(key, value)
        }
        request.setMimeType(mimeType)


        request.addRequestHeader("User-Agent", WebSettings.getDefaultUserAgent(ctx))
        request.setTitle(fileName)
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

        val dm = ctx.downloadManager
        return try {
            dm.enqueue(request)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun isDownloading(downloadId: Long): Boolean {
        val downloadManager = ctx.downloadManager
        val query = DownloadManager.Query().apply {
            setFilterById(downloadId)
        }
        return downloadManager.query(query).use { c ->
            if (c.moveToFirst()) {
                return@use c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
            }
            return@use -1
        }.let {
            it == DownloadManager.STATUS_PENDING || it == DownloadManager.STATUS_RUNNING
        }

    }
}