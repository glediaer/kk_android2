package com.krosskomics.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.os.StatFs
import android.util.Log
import com.krosskomics.KJKomicsApp
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DecimalFormat
import javax.crypto.spec.SecretKeySpec

object FileUtils {
    //AESCrypt-ObjC uses SHA-256 (and so a 256-bit key)
    private const val HASH_ALGORITHM = "SHA-256"
    var secretKeySpec: SecretKeySpec? = null

    fun close(reader: Reader?) {
        if (reader != null) try {
            reader.close()
        } catch (e: IOException) {
        }
    }

    fun close(writer: Writer?) {
        if (writer != null) try {
            writer.close()
        } catch (e: IOException) {
        }
    }

    fun close(inputStream: InputStream?) {
        if (inputStream != null) try {
            inputStream.close()
        } catch (e: IOException) {
        }
    }

    fun close(outputStream: OutputStream?) {
        if (outputStream != null) try {
            outputStream.close()
        } catch (e: IOException) {
        }
    }

    fun getBitmapFromURL(src: String?): Bitmap? {
        var connection: HttpURLConnection? = null
        return try {
            val url = URL(src)
            connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection!!.connect()
            val input = connection.inputStream
            val myBitmap = BitmapFactory.decodeStream(input)
            close(input)
            myBitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            connection?.disconnect()
        }
    }

    /**
     * 폴더에 파일로 쓰기
     *
     * @param path
     * @param name
     * @param data
     */
    fun writeFile(
        path: String,
        name: String,
        data: ByteArray?
    ) {
        Log.d("jj", "writeFile = $path/$name")
        try {
            val writefile = File(path)
            if (!writefile.isDirectory) {
                Log.d("jj", "make = $path/$name")
                writefile.mkdirs()
                writefile.createNewFile()
            }
            val fo =
                FileOutputStream("$writefile/$name")
            fo.write(data)
            fo.flush()
            fo.close()
            Log.d("jj", "writeFile DOWNLOAD_COUNT = " + KJKomicsApp.DOWNLOAD_COUNT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 폴더에 파일로 쓰기
     *
     * @param path
     * @param name
     * @param data
     */
    fun writeFile2(
        path: String,
        name: String,
        data: ByteArray?
    ) {
        Log.d("jj", "writeFile = $path/$name")
        try {
            val writefile = File(path)
            if (!writefile.isDirectory) {
                Log.d("jj", "make = $path/$name")
                writefile.mkdirs()
                writefile.createNewFile()
            }
            val fo =
                FileOutputStream("$writefile/$name")
            fo.write(data)
            fo.flush()
            fo.close()
            KJKomicsApp.DOWNLOAD_COUNT++
            Log.d("jj", "writeFile DOWNLOAD_COUNT = " + KJKomicsApp.DOWNLOAD_COUNT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getFolderSize(f: File): Long {
        var size: Long = 0
        if (f.isDirectory) {
            for (file in f.listFiles()) {
                size += getFolderSize(file)
            }
        } else {
            size = f.length()
        }
        return size
    }

    fun getCacheSizeMB(size: Long): String? {
        var mega: String? = ""
        mega = if (size == 0L || size < 0) {
            "0"
        } else {
            val digitGroups =
                (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
            DecimalFormat("#,##0.#")
                .format(size / Math.pow(1024.0, digitGroups.toDouble()))
        }
        return mega
    }

    //파일 & 폴더 삭제
    fun deleteDir(a_path: String?): Int {
        try {
            val file = File(a_path)
            return if (file.exists()) {
                val childFileList = file.listFiles()
                for (childFile in childFileList) {
                    if (childFile.isDirectory) {
                        deleteDir(childFile.absolutePath)
                    } else {
                        childFile.delete()
                    }
                }
                file.delete()
                1
            } else {
                0
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        return 0
    }

    //파일 & 폴더 삭제
    fun deleteFile(a_path: String?): Int {
        try {
            val file = File(a_path)
            return if (file.exists()) {
                file.delete()
                1
            } else {
                0
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        return 0
    }

    /**
     * 전체 내장 메모리 크기를 가져온다
     */
    fun getTotalInternalMemorySize(): Long {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSize.toLong()
        val totalBlocks = stat.blockCount.toLong()
        return totalBlocks * blockSize
    }

    /**
     * 사용가능한 내장 메모리 크기를 가져온다
     */
    fun getInternalMemorySize(): Long {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSize.toLong()
        val availableBlocks = stat.availableBlocks.toLong()
        return availableBlocks * blockSize
    }

    /**
     * 전체 외장 메모리 크기를 가져온다
     */
    fun getTotalExternalMemorySize(): Long {
        return if (isStorage(true) == true) {
            val path = Environment.getExternalStorageDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSize.toLong()
            val totalBlocks = stat.blockCount.toLong()
            totalBlocks * blockSize
        } else {
            -1
        }
    }

    /**
     * 사용가능한 외장 메모리 크기를 가져온다
     */
    fun getExternalMemorySize(): Long {
        return if (isStorage(true) == true) {
            val path = Environment.getExternalStorageDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSize.toLong()
            val availableBlocks = stat.availableBlocks.toLong()
            availableBlocks * blockSize
        } else {
            -1
        }
    }

    /** 외장메모리 sdcard 사용가능한지에 대한 여부 판단  */
    fun isStorage(requireWriteAccess: Boolean): Boolean {
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED == state) {
            return true
        } else if (!requireWriteAccess && Environment.MEDIA_MOUNTED_READ_ONLY == state) {
            return true
        }
        return false
    }

    fun inputStreamToByteArray(`is`: InputStream): ByteArray? {
        var resBytes: ByteArray? = null
        val bos = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var read = -1
        try {
            while (`is`.read(buffer).also { read = it } != -1) {
                bos.write(buffer, 0, read)
            }
            resBytes = bos.toByteArray()
            bos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return resBytes
    }

//    /**
//     * Reads the contents of a file into a byte array.
//     * The file is always closed.
//     *
//     * @param file the file to read, must not be `null`
//     * @return the file contents, never `null`
//     * @throws IOException in case of an I/O error
//     * @since 1.1
//     */
//    @Throws(IOException::class)
//    fun readFileToByteArray(file: File): ByteArray? {
//        var `in`: InputStream? = null
//        return try {
//            `in` = openInputStream(file)
//            IOUtils.toByteArray(`in`) // Do NOT use file.length() - see IO-453
//        } finally {
//            IOUtils.closeQuietly(`in`)
//        }
//    }

    @Throws(IOException::class)
    fun openInputStream(file: File): FileInputStream? {
        if (file.exists()) {
            if (file.isDirectory) {
                throw IOException("File '$file' exists but is a directory")
            }
            if (file.canRead() == false) {
                throw IOException("File '$file' cannot be read")
            }
        } else {
            throw FileNotFoundException("File '$file' does not exist")
        }
        return FileInputStream(file)
    }

    /**
     * 파일을 읽어서 바이트로 변환
     * @param Path
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun fileToByte(`is`: InputStream): ByteArray? {
        val data = ByteArray(`is`.available())
        `is`.read(data)
        return data
    }

    /**
     * 스트림 가져옴
     * @param path
     * @return
     */
    fun getStream(path: String): InputStream? {
//        Log.d(
//            com.krosskomics.util.FileUtils::class.java.getName(),
//            "getStream = $path"
//        )
        try {
            return FileInputStream(path)
        } catch (e: Exception) {
            // TODO: handle exception
        }
        return null
    }

    /**
     * Generates SHA256 hash of the password which is used as key
     *
     * @param password used to generated key
     * @return SHA256 of the password
     */
    @Throws(
        NoSuchAlgorithmException::class,
        UnsupportedEncodingException::class
    )
    fun generateKey(password: String): SecretKeySpec? {
        val digest =
            MessageDigest.getInstance(HASH_ALGORITHM)
        val bytes = password.toByteArray(charset("UTF-8"))
        digest.update(bytes, 0, bytes.size)
        val key = digest.digest()
        Log.e("SHA-256 key ", "key : $key")
        secretKeySpec = SecretKeySpec(key, "AES")
        return secretKeySpec
    }

    /**
     * 파일 이름에 사용할 수 없는 캐릭터를 바꿔서 유효한 파일로 만든다.
     *
     * @author mcsong@gmail.com
     * @param fileName 파일 이름, Path를 제외한 순수한 파일의 이름..
     * @param replaceStr 파일 이름에 사용할 수 없는 캐릭터의 교체 문자
     * @return
     */
    fun makeValidFileName(
        fileName: String?,
        replaceStr: String?
    ): String? {
        val ILLEGAL_EXP = "[:\\\\/%*?:|\"<>]"
        return if (fileName == null || fileName.trim { it <= ' ' }.length == 0 || replaceStr == null) System.currentTimeMillis()
            .toString() else fileName.replace(ILLEGAL_EXP.toRegex(), replaceStr)
    }

    // Bitmap to File
    fun saveBitmapToFileCache(
        bitmap: Bitmap,
        strFilePath: String,
        filename: String
    ): File? {
        val file = File(strFilePath)

        // If no folders
        if (!file.exists()) {
            file.mkdirs()
            // Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        }
        val fileCacheItem = File(strFilePath + filename)
        var out: OutputStream? = null
        try {
            fileCacheItem.createNewFile()
            out = FileOutputStream(fileCacheItem)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                out!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return fileCacheItem
    }
}