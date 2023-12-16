/*
* This class is used to sync data from the server to the local database, by applying the pull-push
* strategy, and track what SQL command can result in the change of the database content
* - pull: which is to download the SQLite database file over the network to this device
* - push: which is to upload the SQL commands that can result in the change of the database
* - newLogFile: which is to create a new log file to track the SQL commands
* - logQuery: which is to log the SQL commands
*
* */

package com.example.telehealth.data.database

import android.util.Log
import java.io.File
import java.io.IOException
import java.net.URL
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody

class DataSync {
    companion object {
        private var retry: Int = 5
        var url: String = "http://"
        private var logFile: File? = null

        // pull: which is to download the SQLite database file over the network to this device
        fun pull(remoteUrl: String, localPath: String): DbStatus {
            for (i in 1..retry) {
                try {
                    val url = URL(remoteUrl)
                    val connection = url.openConnection()
                    connection.inputStream.use { input ->
                        File(localPath).outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    return DbStatus.OK;    // Successfully downloaded
                } catch (e: IOException) {
                    // Handle exceptions
                    Log.e("DataSync", "Error: ${e.message} --> retrying...")
                }
            }
            return DbStatus.ERROR
        }

        // newLogFile: which is to create a new log file to track the SQL commands
        fun newLogFile(path: String): DbStatus {
            logFile = File(path).apply {
                if (!exists()) {
                    createNewFile()
                }
            }
            return DbStatus.OK
        }

        // logQuery: which is to log the SQL commands
        fun logQuery(sqlQuery: String): DbStatus {
            if (logFile == null) {
                // Log file not initialized
                Log.e("DataSync", "Log file is not initialized, use newLogFile() to initialize")
                return DbStatus.ERROR
            }
            for (i in 1..retry) {
                try {
                    logFile!!.appendText(sqlQuery, Charsets.UTF_8)
                    // Add ; to the end of the SQL command if it is not there
                    if (!sqlQuery.endsWith(";")) {
                        logFile!!.appendText(";")
                    }
                    logFile!!.appendText("\n")
                    return DbStatus.OK
                } catch (e: IOException) {
                    // Handle exceptions
                    Log.e("DataSync", "Error: ${e.message} --> retrying...")
                }
            }
            return DbStatus.ERROR
        }

        // push: which is to upload the SQL commands that can result in the change of the database
        fun push(serverUrl: String): DbStatus {
            if (logFile == null) {
                // No log file created or no queries logged
                Log.e("DataSync", "No log file created or no queries logged")
                return DbStatus.ERROR
            }

            val client = OkHttpClient()
            val mediaType = "text/plain; charset=UTF-8".toMediaTypeOrNull()
            val body = logFile!!.asRequestBody(mediaType)
            val request = Request.Builder()
                .url(serverUrl)
                .post(body)
                .build()

            for (i in 1..retry) {
                try {
                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) {
                            // Handle unsuccessful response
                            Log.e("DataSync", "Error: ${response.message}")
                            return DbStatus.ERROR
                        }
                        // Clear log file after successful upload
                        logFile?.writeText("")
                    }
                    return DbStatus.OK
                } catch (e: IOException) {
                    // Handle exceptions
                    Log.e("DataSync", "Error: ${e.message} --> retrying...")
                }
            }
            return DbStatus.ERROR
        }
    }
}