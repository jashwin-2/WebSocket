package com.example.local_server

import android.content.res.AssetManager
import android.text.TextUtils
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream


object Utils {
    private const val TAG = "Utils"

    fun detectMimeType(fileName: String): String? {
        return if (TextUtils.isEmpty(fileName)) {
            null
        } else if (fileName.endsWith(".html")) {
            "text/html"
        } else if (fileName.endsWith(".js")) {
            "application/javascript"
        } else if (fileName.endsWith(".css")) {
            "text/css"
        } else {
            "application/octet-stream"
        }
    }

    @Throws(IOException::class)
    fun loadContent(fileName: String?, assetManager: AssetManager): ByteArray? {
        var input: InputStream? = null
        return try {
            val output = ByteArrayOutputStream()
            input = assetManager.open(fileName!!)
            val buffer = ByteArray(1024)
            var size: Int
            while (-1 != input.read(buffer).also { size = it }) {
                output.write(buffer, 0, size)
            }
            output.flush()
            output.toByteArray()
        } catch (e: FileNotFoundException) {
            Log.d("route", "Not found")
            null
        } finally {
            try {
                input?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getJsonFromAssets(manager: AssetManager, fileName: String?): String? {
        return try {
            val inputStream: InputStream = manager.open(fileName!!)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, charset("UTF-8"))
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

}