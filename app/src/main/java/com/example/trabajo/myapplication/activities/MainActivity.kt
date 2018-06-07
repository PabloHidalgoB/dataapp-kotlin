package com.example.trabajo.myapplication.activities

import android.app.ActivityManager
import android.content.Context
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.trabajo.myapplication.R
import com.example.trabajo.myapplication.models.OneData
import com.google.gson.Gson
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var oneData: OneData

        oneData = OneData()
        oneData.appName = getApplicationName(this)
        //oneData.appPkgName = getAppPackageName()
        oneData.usedRam = currentAppRamUsage()
        oneData.freeRam = freeRamMemorySize()
        oneData.totalRam = totalRamMemorySize()
        oneData.freeMemory = getAvailableInternalMemorySize()
        oneData.usedMemory = (getTotalInternalMemorySize() - getAvailableInternalMemorySize())
        oneData.totalMemory = getTotalInternalMemorySize()

        Log.d("RAM USAGE", "Actualmente se esta usando ${oneData.usedRam} MB de memoria RAM")
        Log.d("RAM FREE", "Hay ${oneData.freeRam} MB libre de memoria RAM")
        Log.d("RAM TOTAL", "Hay ${oneData.totalRam} MB total de memoria RAM")
        Log.d("AVAILABLE MEMORY", "Hay ${oneData.freeMemory} MB disponible de memoria de almacenamiento")
        Log.d("USED MEMORY", "Hay ${oneData.usedMemory} MB usado de memoria de almacenamiento")
        Log.d("TOTAL MEMORY", "Hay ${oneData.totalMemory} MB total de memoria de almacenamiento")

        Log.d("APP NAME", "El nombre de la app es: ${oneData.appName}")
        Log.d("APP PACKAGE NAME", "El nombre de la app es: ${oneData.appPkgName}")

        //TODO agregar version android aparato
        //TODO listar todas app con nombre com.retailsbs.
        //

        generateJson(oneData)

    }

    private fun currentAppRamUsage(): Long {

        val memInfo = Debug.MemoryInfo()
        Debug.getMemoryInfo(memInfo)
        var res = memInfo.totalPrivateDirty.toLong()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            res += memInfo.totalPrivateClean.toLong()

        return res / 1024L
    }

    private fun freeRamMemorySize(): Long {
        val mi = ActivityManager.MemoryInfo()
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(mi)

        return mi.availMem / 1048576L
    }

    private fun totalRamMemorySize(): Long {
        val mi = ActivityManager.MemoryInfo()
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(mi)
        return mi.totalMem / 1048576L
    }

    private fun externalMemoryAvailable(): Boolean {
        return android.os.Environment.getExternalStorageState() == android.os.Environment.MEDIA_MOUNTED
    }

    private fun getAvailableInternalMemorySize(): Long {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSize.toLong()
        val availableBlocks = stat.availableBlocks.toLong()
        return (availableBlocks * blockSize) / 1048576L
    }

    private fun getTotalInternalMemorySize(): Long {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSize.toLong()
        val totalBlocks = stat.blockCount.toLong()
        return (totalBlocks * blockSize) / 1048576L
    }

    fun getAvailableExternalMemorySize(): Long {
        return if (externalMemoryAvailable()) {
            val path = Environment.getExternalStorageDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSize.toLong()
            val availableBlocks = stat.availableBlocks.toLong()
            availableBlocks * blockSize
        } else {
            0
        }
    }

    fun getTotalExternalMemorySize(): Long {
        return if (externalMemoryAvailable()) {
            val path = Environment.getExternalStorageDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSize.toLong()
            val totalBlocks = stat.blockCount.toLong()
            totalBlocks * blockSize
        } else {
            0
        }
    }

    private fun formatSize(size: Long): String {
        var size = size
        var suffix: String? = null

        if (size >= 1024) {
            suffix = " KB"
            size /= 1024
            if (size >= 1024) {
                suffix = " MB"
                size /= 1024
            }
        }
        val resultBuffer = StringBuilder(java.lang.Long.toString(size))

        var commaOffset = resultBuffer.length - 3
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, '.')
            commaOffset -= 3
        }
        if (suffix != null) resultBuffer.append(suffix)
        return resultBuffer.toString()
    }

    private fun getApplicationName(context: Context): String {
        val applicationInfo = context.applicationInfo
        val stringId = applicationInfo.labelRes
        return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(stringId)
    }

    private fun getAppPackageName(): String {
        return applicationContext.packageName
    }

    private fun generateJson(oneData: OneData): String? {

        var gson : Gson

        gson = Gson()

        Log.d("JSON TAG",gson.toJson(oneData))

        return gson.toJson(oneData)

    }


}
