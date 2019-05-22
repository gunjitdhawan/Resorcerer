package com.grappes.resourcedownloader

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.util.Log
import kotlinx.coroutines.*
import java.io.*
import java.math.BigInteger
import java.net.URL
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ReSorcerer(context: Context, arrayList: ArrayList<String>, onResultInterface: OnResultInterface?) {

    enum class Error {
        NO_INTERNET,
        SERVER_ERROR
    }

    private lateinit var executor: ExecutorService

    var context = context
    var arrayList : ArrayList<String> = arrayList
    var failedList : ArrayList<String> = ArrayList()
    var onResultInterface = onResultInterface
    var progressDialog = ProgressDialog(context)
    var successCount : Int = 0

    init {

        startDownloadingAsyncronously();
    }

    private fun startDownloadingAsyncronously() {

        if(!isConnected(context)) {
            onResultInterface?.onFailure("No Internet")
            return
        }

        progressDialog.setMessage("Downloading("+successCount+"/"+arrayList.size+")")
        progressDialog.show()

        CoroutineScope(Dispatchers.IO).launch {

            val task = async {
                downloadTestImages(arrayList)
            }

            withTimeoutOrNull(5000) { task.await() }
            CoroutineScope(Dispatchers.Main).launch {
                progressDialog.dismiss()

                if(failedList.size == arrayList.size) {
                    onResultInterface?.onFailure("Check exception")
                } else if(failedList.size==0) {
                    onResultInterface?.onSuccess()
                } else {
                    onResultInterface?.onPartialSuccess(failedList)
                }
            }
        }
    }

    class Builder {
        private var arrayList : ArrayList<String> = ArrayList<String>()
        private lateinit var context: Context
        fun setContext(context: Context) = apply { this.context = context }
        fun addLink(link : String)  = apply { this.arrayList.add(link) }
        fun addMultipleLinks(links : ArrayList<String>)  = apply { this.arrayList.addAll(links) }
        fun build() = ReSorcerer(context, arrayList, null)
        fun buildWithListener(onResultInterface: OnResultInterface) = ReSorcerer(context, arrayList, onResultInterface)
    }

    private fun downloadTestImages(urls: ArrayList<String>?) {
        if (urls != null) {
            executor = Executors.newFixedThreadPool(7)
            val coroutineDispatcher = executor.asCoroutineDispatcher()
            runBlocking(coroutineDispatcher) {
                urls.forEach { url: String ->
                    launch {
                        downloadFile(url)
                        //downloadSingleImage(context, url)
                    }
                }
            }
        }
    }

    private fun downloadFile(url: String) {

        var localPath = getLocalPath(context, url)

        if(localPath!=null) {
            incrementDownloadCount();
            return
        }

        try {
            val u = URL(url)
            val conn = u.openConnection()
            val contentLength = conn.contentLength
            val type = conn.contentType
            val stream = DataInputStream(u.openStream())
            val dest = File(context.getExternalFilesDir(null), getMd5(url))
            val buffer = ByteArray(contentLength)
            stream.readFully(buffer)
            stream.close()

            val fos = DataOutputStream(FileOutputStream(dest))
            fos.write(buffer)
            fos.flush()
            fos.close()

            incrementDownloadCount();
        } catch (e: FileNotFoundException) {
            return  // swallow a 404
            failedList.add(url)
        } catch (e: IOException) {
            return  // swallow a 404
            failedList.add(url)
        }

    }

    private fun incrementDownloadCount() {
        successCount++
        progressDialog.setMessage("Downloading("+successCount+"/"+arrayList.size+")")
        progressDialog.show()
    }

    interface OnResultInterface {
        fun onSuccess()
        fun onFailure(error : String)
        fun onPartialSuccess(failedList : ArrayList<String>)
    }

    companion object Utils {

        var EXTERNAL_PATH : String? = null

        fun getLocalPath(context: Context?, serverUrl: String): String? {

            var localPath: String? = null
            if (context != null) {
                if (getExternalPath(context) != null) {

                    localPath = getExternalPath(context) + "/" + getMd5(serverUrl)
                }

                if (localPath != null && localPath.length > 0) {

                    if (File(localPath).exists()) {
                        return localPath
                    }
                }
            }
            return null
        }

        internal fun getExternalPath(context: Context): String? {
            if (EXTERNAL_PATH == null) {
                if (context.getExternalFilesDir(null) != null) {
                    EXTERNAL_PATH = context.getExternalFilesDir(null)!!.absolutePath
                }
            }
            return EXTERNAL_PATH
        }

        fun ordinalIndexOf(str: String, substr: String, n: Int): Int {
            var n = n
            var pos = str.indexOf(substr)
            while (--n > 0 && pos != -1)
                pos = str.indexOf(substr, pos + 1)
            return pos
        }

        internal fun getMd5(input: String): String {
            try {

                // Static getInstance method is called with hashing MD5
                val md = MessageDigest.getInstance("MD5")

                // digest() method is called to calculate message digest
                //  of an input digest() return array of byte
                val messageDigest = md.digest(input.toByteArray())

                // Convert byte array into signum representation
                val no = BigInteger(1, messageDigest)

                // Convert message digest into hex value
                var hashtext = no.toString(16)
                while (hashtext.length < 32) {
                    hashtext = "0$hashtext"
                }
                val extension = input.substringAfterLast(".")

                return hashtext+"."+extension
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException(e)
            }
            // For specifying wrong message digest algorithms
        }
    }

    internal fun isConnected(context: Context?): Boolean {
        if (context == null) {
            return false
        }

        val cm = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }
}
