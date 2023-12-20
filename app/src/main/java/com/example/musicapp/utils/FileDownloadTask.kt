package com.example.musicapp.utils

import android.os.AsyncTask
import android.os.Environment
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class FileDownloadTask(private val songName: String) : AsyncTask<String, Void, Boolean>() {

    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg params: String?): Boolean {
        val fileUrl = params[0] ?: return false

        try {
            val url = URL(fileUrl)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.connect()

            // Check if the server returned a successful response code (HTTP 200-299)
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = BufferedInputStream(connection.inputStream)
                val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/MusicApp")
                if (!directory.exists()) {
                    directory.mkdirs()
                }

                val outputFile = FileOutputStream(File(directory, "${songName}.mp3"))

                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputFile.write(buffer, 0, bytesRead)
                }

                // Close the streams
                inputStream.close()
                outputFile.close()

                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }
}
