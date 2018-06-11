package com.example.trabajo.myapplication.activities

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.trabajo.myapplication.R
import com.example.trabajo.myapplication.models.OneData
import com.google.gson.Gson
import android.os.Build
import java.util.ArrayList


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var oneData: OneData

        oneData = OneData()
        oneData.appName = getApplicationName(this)
        oneData.appPkgNames = getAppPackageNames()
        oneData.usedRam = currentAppRamUsage()
        oneData.freeRam = freeRamMemorySize()
        oneData.totalRam = totalRamMemorySize()
        oneData.freeMemory = getAvailableInternalMemorySize()
        oneData.usedMemory = (getTotalInternalMemorySize() - getAvailableInternalMemorySize())
        oneData.totalMemory = getTotalInternalMemorySize()
        oneData.freeExtMemory = getAvailableExternalMemorySize()
        oneData.usedExtMemory = (getTotalExternalMemorySize() - getAvailableExternalMemorySize())
        oneData.totalExtMemory = getTotalExternalMemorySize()

        Log.d("RAM USAGE", "Actualmente se esta usando ${oneData.usedRam} MB de memoria RAM")
        Log.d("RAM FREE", "Hay ${oneData.freeRam} MB libre de memoria RAM")
        Log.d("RAM TOTAL", "Hay ${oneData.totalRam} MB total de memoria RAM")
        Log.d("AVAILABLE MEMORY", "Hay ${oneData.freeMemory} MB disponible de memoria de almacenamiento")
        Log.d("USED MEMORY", "Hay ${oneData.usedMemory} MB usado de memoria de almacenamiento")
        Log.d("TOTAL MEMORY", "Hay ${oneData.totalMemory} MB total de memoria de almacenamiento")
        Log.d("AVAILABLE EXT MEMORY", "Hay ${oneData.freeExtMemory} MB total de memoria de almacenamiento")
        Log.d("USED MEMORY", "Hay ${oneData.usedExtMemory} MB total de memoria de almacenamiento")
        Log.d("TOTAL MEMORY", "Hay ${oneData.totalExtMemory} MB total de memoria de almacenamiento")

        Log.d("APP NAME", "El nombre de la app es: ${oneData.appName}")
        Log.d("APP PACKAGE NAME", "El nombre de la app es: ${oneData.appPkgNames}")


        Log.d("APP ANDROID RELEASE", getAndroidReleaseVersion())


        //TODO agregar version android aparato [DONE]
        //TODO listar todas app con nombre com.retailsbs.


        generateJson(oneData)

       // Log.d("COM SBS APPS", getAppPackageNames().toString())

    }

    private fun currentAppRamUsage(): Long {

        var res : Long

        try {

            val memInfo = Debug.MemoryInfo()
            Debug.getMemoryInfo(memInfo)
            res = memInfo.totalPrivateDirty.toLong()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                res += memInfo.totalPrivateClean.toLong()

            return res / 1024L

        } catch (e : Exception){
            e.printStackTrace()
        }

        return -1L
    }

    private fun freeRamMemorySize(): Long {

        var mAvailableMemory : Long

        try {

            val mi = ActivityManager.MemoryInfo()
            val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityManager.getMemoryInfo(mi)

            mAvailableMemory = mi.availMem / 1048576L

            return mAvailableMemory

        } catch (e : Exception){
            e.printStackTrace()
        }

        return -1L

    }

    private fun totalRamMemorySize(): Long {

        var mTotalMemory : Long

        try {
            val mi = ActivityManager.MemoryInfo()
            val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityManager.getMemoryInfo(mi)

            mTotalMemory = mi.totalMem / 1048576L

            return mTotalMemory

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return -1L

    }

    private fun externalMemoryAvailable(): Boolean {
        return android.os.Environment.getExternalStorageState() == android.os.Environment.MEDIA_MOUNTED
    }

    private fun getAvailableInternalMemorySize(): Long {

        var mAvInternalMemory : Long

        try {

            val path = Environment.getDataDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSize.toLong()
            val availableBlocks = stat.availableBlocks.toLong()

            mAvInternalMemory = (availableBlocks * blockSize) / 1048576L

            return mAvInternalMemory

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return -1L

    }

    private fun getTotalInternalMemorySize(): Long {

        var mTotalInternalMemory : Long

        try {

            val path = Environment.getDataDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSize.toLong()
            val totalBlocks = stat.blockCount.toLong()

            mTotalInternalMemory = (totalBlocks * blockSize) / 1048576L

            return mTotalInternalMemory

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return -1L

    }

    fun getAvailableExternalMemorySize(): Long {

        var mAvExtMemory : Long

        try {

            if (externalMemoryAvailable()) {
                val path = Environment.getExternalStorageDirectory()
                val stat = StatFs(path.path)
                val blockSize = stat.blockSize.toLong()
                val availableBlocks = stat.availableBlocks.toLong()

                mAvExtMemory = (availableBlocks * blockSize) / 1048576L

                return mAvExtMemory

            } else {

                return 0

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return -1L
    }

    fun getTotalExternalMemorySize(): Long {

        var mTotalExtMemory : Long

        try {

            if (externalMemoryAvailable()) {
                val path = Environment.getExternalStorageDirectory()
                val stat = StatFs(path.path)
                val blockSize = stat.blockSize.toLong()
                val totalBlocks = stat.blockCount.toLong()

                mTotalExtMemory = (totalBlocks * blockSize) / 1048576L

                return mTotalExtMemory

            } else {
                return 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return -1L
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

        var mAppName = "App Name couldn't be retrieved"

        try {

            val applicationInfo = context.applicationInfo
            val stringId = applicationInfo.labelRes
            if (stringId == 0) {

                mAppName = applicationInfo.nonLocalizedLabel.toString()

                return mAppName

            } else {
                mAppName = context.getString(stringId)
                return mAppName
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return mAppName

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

    private fun getAndroidReleaseVersion (): String {

        var mAndroidVersion: String = android.os.Build.VERSION.RELEASE

        return mAndroidVersion
    }

    private fun getOsName(): String? {

        var osName = ""

        val fields = Build.VERSION_CODES::class.java.fields
        osName = fields[Build.VERSION.SDK_INT+1].name

        return osName
    }

    private fun getAppPackageNames(): ArrayList<String> {

        var mAppName : String


        val res = ArrayList<String>()
        val packs = packageManager.getInstalledPackages(0)

        for (i in packs.indices) {
            val p = packs[i]
            if (isSystemPackage(p) == false && (p.packageName.contains("com.retailsbs.") || p.packageName.contains("cl.retailsbs."))) {
                mAppName = p.packageName
                res.add(mAppName)
            }
        }
        return res
    }

    private fun isSystemPackage(pkgInfo: PackageInfo): Boolean {
        return if (pkgInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) true else false
    }

}
