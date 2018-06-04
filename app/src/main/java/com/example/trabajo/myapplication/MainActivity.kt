package com.example.trabajo.myapplication

import android.app.ActivityManager
import android.content.Context
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.util.Log

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("RAM USAGE", "Actualmente se esta usando ${currentAppRamUsage()} MB de memoria RAM")
        Log.d("RAM FREE", "Hay ${freeRamMemorySize()} MB libre de memoria RAM")
        Log.d("RAM TOTAL", "Hay ${totalRamMemorySize()} MB total de memoria RAM")
        Log.d("AVAILABLE MEMORY", "Hay ${formatSize(getAvailableInternalMemorySize())} disponible de memoria de almacenamiento")
        Log.d("USED MEMORY", "Hay ${formatSize(getTotalInternalMemorySize() - getAvailableInternalMemorySize())} usado de memoria de almacenamiento")
        Log.d("TOTAL MEMORY", "Hay ${formatSize(getTotalInternalMemorySize())} total de memoria de almacenamiento")

        Log.d("APP NAME", "El nombre de la app es: ${getApplicationName(this)}")
        Log.d("APP PACKAGE NAME", "El nombre de la app es: ${getAppPackageName()}")


    }

    private fun currentAppRamUsage(): Long {

        val memInfo = Debug.MemoryInfo()
        Debug.getMemoryInfo(memInfo)
        var res = memInfo.totalPrivateDirty.toLong()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            res += memInfo.totalPrivateClean.toLong()

        return res
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

    fun externalMemoryAvailable(): Boolean {
        return android.os.Environment.getExternalStorageState() == android.os.Environment.MEDIA_MOUNTED
    }

    fun getAvailableInternalMemorySize(): Long {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSize.toLong()
        val availableBlocks = stat.availableBlocks.toLong()
        return availableBlocks * blockSize
    }

    fun getTotalInternalMemorySize(): Long {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSize.toLong()
        val totalBlocks = stat.blockCount.toLong()
        return totalBlocks * blockSize
    }

    fun getAvailableExternalMemorySize(): Long {
        if (externalMemoryAvailable()) {
            val path = Environment.getExternalStorageDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSize.toLong()
            val availableBlocks = stat.availableBlocks.toLong()
            return availableBlocks * blockSize
        } else {
            return 0
        }
    }

    fun getTotalExternalMemorySize(): Long {
        if (externalMemoryAvailable()) {
            val path = Environment.getExternalStorageDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSize.toLong()
            val totalBlocks = stat.blockCount.toLong()
            return totalBlocks * blockSize
        } else {
            return 0
        }
    }

    fun formatSize(size: Long): String {
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

    fun getApplicationName(context: Context): String {
        val applicationInfo = context.applicationInfo
        val stringId = applicationInfo.labelRes
        return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(stringId)
    }

    fun getAppPackageName(): String {
        return applicationContext.packageName
    }
}
