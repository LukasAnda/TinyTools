package com.tinytools.files.filesystem

import android.annotation.TargetApi
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.BaseColumns
import android.provider.MediaStore
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.preference.PreferenceManager
import com.tinytools.files.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.lang.reflect.InvocationTargetException
import java.util.*
import java.util.regex.Pattern


/**
 * Utility class for helping parsing file systems.
 */
object FileUtil {
    private val TAG: String = FileUtil::class.simpleName.toString()
    private val FILENAME_REGEX = Pattern.compile("[\\\\\\/:\\*\\?\"<>\\|\\x01-\\x1F\\x7F]", Pattern.CASE_INSENSITIVE)
    /**
     * Determine the camera folder. There seems to be no Android API to work for real devices, so this is a best guess.
     *
     * @return the default camera folder.
     */
    //TODO the function?
    /**
     * Copy a file. The target file may even be on external SD card for Kitkat.
     *
     * @param source The source file
     * @param target The target file
     * @return true if the copying was successful.
     */
    private suspend fun copyFile(source: File, target: File, context: Context): Boolean {

        runCatching {
            FileInputStream(source).use { fileInputStream ->
                if (isWritable(target)) {

                    FileOutputStream(target).use { fileOutputStream ->
                        val inputChannel = fileInputStream.channel
                        val outputChannel = fileOutputStream.channel

                        inputChannel.transferTo(0, inputChannel.size(), outputChannel)
                    }

                } else {

                    // If the check failed, try using this way
                    val targetDocument = getDocumentFile(target, false, context) ?: return false
                    context.contentResolver?.openOutputStream(targetDocument.uri)?.use { fileOutputStream ->

                        val buffer = ByteArray(16384) // MAGIC_NUMBER
                        var bytesRead: Int
                        while (fileInputStream.read(buffer).also { bytesRead = it } != -1) {
                            fileOutputStream.write(buffer, 0, bytesRead)
                        }
                    }

                }
            }
        }.getOrElse {
            Log.d(TAG, "Error when copying file from " + source.absolutePath + " to " + target.absolutePath, it)
            return false
        }
        return true
    }

    @Throws(FileNotFoundException::class)
    fun getOutputStream(target: File, context: Context): OutputStream? {
        var outStream: OutputStream? = null
        // First try the normal way
        if (isWritable(target)) {
            // standard way
            outStream = FileOutputStream(target)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Storage Access Framework
                val targetDocument = getDocumentFile(target, false, context) ?: return null
                outStream = context.contentResolver.openOutputStream(targetDocument.uri)
            }
        }
        return outStream
    }

    /**
     * Writes uri stream from external application to the specified path
     */
    suspend fun writeUriToStorage(context: Context, uris: ArrayList<Uri>, contentResolver: ContentResolver, currentPath: String) {
        val returnValues = mutableListOf<String>()
        withContext(Dispatchers.IO){
            uris.forEach { uri ->
                BufferedInputStream(contentResolver.openInputStream(uri)).use { bufferedInputStream ->

                }
            }
            // TODO use this when hybrid file will be included
//            for (uri in uris) {
//                var bufferedInputStream: BufferedInputStream? = null
//                try {
//                    bufferedInputStream = BufferedInputStream(contentResolver.openInputStream(uri))
//                } catch (e: FileNotFoundException) {
//                    e.printStackTrace()
//                }
//                var bufferedOutputStream: BufferedOutputStream? = null
//                try {
//                    val documentFile: DocumentFile = DocumentFile.fromSingleUri(mainActivity, uri)
//                    var filename = documentFile.name
//                    if (filename == null) {
//                        filename = uri!!.lastPathSegment
//
//                        //For cleaning up slashes. Back in #1217 there is a case of Uri.getLastPathSegment() end up with a full file path
//                        if (filename.contains("/")) filename = filename.substring(filename.lastIndexOf('/') + 1)
//                    }
//                    val finalFilePath = "$currentPath/$filename"
//                    val dataUtils: DataUtils = DataUtils.instance
//                    val hFile = HybridFile(OpenMode.UNKNOWN, currentPath)
//                    hFile.generateMode(mainActivity)
//                    when (hFile.getMode()) {
//                        OpenMode.FILE, OpenMode.ROOT -> {
//                            val targetFile = File(finalFilePath)
//                            if (!FileUtil.isWritableNormalOrSaf(targetFile.parentFile, mainActivity.getApplicationContext())) {
//                                AppConfig.toast(mainActivity, mainActivity.getResources().getString(R.string.not_allowed))
//                                return null
//                            }
//                            var targetDocumentFile = getDocumentFile(targetFile, false, mainActivity.getApplicationContext())
//
//                            //Fallback, in case getDocumentFile() didn't properly return a DocumentFile instance
//                            if (targetDocumentFile == null) targetDocumentFile = DocumentFile.fromFile(targetFile)
//
//                            //Lazy check... and in fact, different apps may pass in URI in different formats, so we could only check filename matches
//                            //FIXME?: Prompt overwrite instead of simply blocking
//                            if (targetDocumentFile.exists() && targetDocumentFile.length() > 0) {
//                                AppConfig.toast(mainActivity, mainActivity.getString(R.string.cannot_overwrite))
//                                return null
//                            }
//                            bufferedOutputStream = BufferedOutputStream(contentResolver.openOutputStream(targetDocumentFile.uri))
//                            retval.add(targetFile.path)
//                        }
//                        OpenMode.SMB -> {
//                            val targetSmbFile = SmbFile(finalFilePath)
//                            if (targetSmbFile.exists()) {
//                                AppConfig.toast(mainActivity, mainActivity.getString(R.string.cannot_overwrite))
//                                return null
//                            } else {
//                                val outputStream: OutputStream = targetSmbFile.getOutputStream()
//                                bufferedOutputStream = BufferedOutputStream(outputStream)
//                                retval.add(mainActivity.mainActivityHelper.parseSmbPath(targetSmbFile.path))
//                            }
//                        }
//                        OpenMode.SFTP -> {
//                            //FIXME: implement support
//                            AppConfig.toast(mainActivity, mainActivity.getString(R.string.not_allowed))
//                            return null
//                        }
//                        OpenMode.DROPBOX -> {
//                            val cloudStorageDropbox: CloudStorage = dataUtils.getAccount(OpenMode.DROPBOX)
//                            val path: String = CloudUtil.stripPath(OpenMode.DROPBOX, finalFilePath)
//                            cloudStorageDropbox.upload(path,
//                                    bufferedInputStream, documentFile.length(), true)
//                            retval.add(path)
//                        }
//                        OpenMode.BOX -> {
//                            val cloudStorageBox: CloudStorage = dataUtils.getAccount(OpenMode.BOX)
//                            path = CloudUtil.stripPath(OpenMode.BOX, finalFilePath)
//                            cloudStorageBox.upload(path,
//                                    bufferedInputStream, documentFile.length(), true)
//                            retval.add(path)
//                        }
//                        OpenMode.ONEDRIVE -> {
//                            val cloudStorageOneDrive: CloudStorage = dataUtils.getAccount(OpenMode.ONEDRIVE)
//                            path = CloudUtil.stripPath(OpenMode.ONEDRIVE, finalFilePath)
//                            cloudStorageOneDrive.upload(path,
//                                    bufferedInputStream, documentFile.length(), true)
//                            retval.add(path)
//                        }
//                        OpenMode.GDRIVE -> {
//                            val cloudStorageGDrive: CloudStorage = dataUtils.getAccount(OpenMode.GDRIVE)
//                            path = CloudUtil.stripPath(OpenMode.GDRIVE, finalFilePath)
//                            cloudStorageGDrive.upload(path,
//                                    bufferedInputStream, documentFile.length(), true)
//                            retval.add(path)
//                        }
//                        OpenMode.OTG -> {
//                            val documentTargetFile: DocumentFile = OTGUtil.getDocumentFile(finalFilePath,
//                                    mainActivity, true)
//                            if (documentTargetFile.exists()) {
//                                AppConfig.toast(mainActivity, mainActivity.getString(R.string.cannot_overwrite))
//                                return null
//                            }
//                            bufferedOutputStream = BufferedOutputStream(contentResolver
//                                    .openOutputStream(documentTargetFile.uri),
//                                    GenericCopyUtil.DEFAULT_BUFFER_SIZE)
//                            retval.add(documentTargetFile.uri.path)
//                        }
//                        else -> return null
//                    }
//                    var count = 0
//                    val buffer = ByteArray(GenericCopyUtil.DEFAULT_BUFFER_SIZE)
//                    while (count != -1) {
//                        count = bufferedInputStream!!.read(buffer)
//                        if (count != -1) {
//                            bufferedOutputStream!!.write(buffer, 0, count)
//                        }
//                    }
//                    bufferedOutputStream!!.flush()
//                } catch (e: FileNotFoundException) {
//                    e.printStackTrace()
//                } catch (e: MalformedURLException) {
//                    e.printStackTrace()
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                } finally {
//                    try {
//                        bufferedInputStream?.close()
//                        bufferedOutputStream?.close()
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//                }
//            }
        }

        withContext(Dispatchers.Main){
            if(returnValues.isNotEmpty()){
                if(returnValues.size == 1){
//                    Toast.makeText(context, context.getString(R.string.saved_single_file, returnValues.first()), Toast.LENGTH_LONG).show()
                } else {
//                    Toast.makeText(context, context.getString(R.string.saved_multi_files, returnValues.size), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    /**
     * Delete a file. May be even on external SD card.
     *
     * @param file the file to be deleted.
     * @return True if successfully deleted.
     */
    fun deleteFile(file: File, context: Context): Boolean {
        // First try the normal deletion.
        val fileDelete = rmdir(file, context)
        if (file.delete() || fileDelete) return true

        // Try with Storage Access Framework.
        if (isOnExtSdCard(file, context)) {
            val document = getDocumentFile(file, false, context)
            return document?.delete() ?: false
        }

        // Try the Kitkat workaround.
        return !file.exists()
    }

    private fun rename(f: File, name: String, root: Boolean): Boolean {
        val newPath = "${f.parent}/$name"
        if (f.parentFile?.canWrite() == true) {
            return f.renameTo(File(newPath))
        } else if (root) {
//            RootUtils.rename(f.path, newPath)
            return true
        }
        return false
    }

    /**
     * Rename a folder. In case of extSdCard in Kitkat, the old folder stays in place, but files are moved.
     *
     * @param source The source folder.
     * @param target The target folder.
     * @return true if the renaming was successful.
     */
    suspend fun renameFolder(source: File, target: File, context: Context): Boolean {
        // First try the normal rename.
        if (rename(source, target.name, false)) {
            return true
        }
        if (target.exists()) {
            return false
        }

        // Try the Storage Access Framework if it is just a rename within the same parent folder.
        if (source.parent == target.parent && isOnExtSdCard(source, context)) {
            val document = getDocumentFile(source, true, context) ?: return false
            if (document.renameTo(target.name)) {
                return true
            }
        }

        // Try the manual way, moving files individually.
        if (!mkdir(target, context)) {
            return false
        }
        val sourceFiles = source.listFiles() ?: return true
        for (sourceFile in sourceFiles) {
            val fileName = sourceFile!!.name
            val targetFile = File(target, fileName)
            if (!copyFile(sourceFile, targetFile, context)) {
                // stop on first error
                return false
            }
        }
        // Only after successfully copying all files, delete files on source folder.
        for (sourceFile in sourceFiles) {
            if (!deleteFile(sourceFile!!, context)) {
                // stop on first error
                return false
            }
        }
        return true
    }

    /**
     * Get a temp file.
     *
     * @param file The base file for which to create a temp file.
     * @return The temp file.
     */
    fun getTempFile(file: File, context: Context): File {
        val extDir = context.getExternalFilesDir(null)
        return File(extDir, file.name)
    }

    /**
     * Create a folder. The folder may even be on external SD card for Kitkat.
     *
     * @param file  The folder to be created.
     * @return True if creation was successful.
     */
    fun mkdir(file: File?, context: Context): Boolean {
        if (file == null) return false
        if (file.exists()) {
            // nothing to create.
            return file.isDirectory
        }

        // Try the normal way
        if (file.mkdirs()) {
            return true
        }

        // Try with Storage Access Framework.
        if (isOnExtSdCard(file, context)) {
            val document = getDocumentFile(file, true, context) ?: return false
            // getDocumentFile implicitly creates the directory.
            return document.exists()
        }

        // Try the Kitkat workaround.
        return false
    }

//    fun mkdirs(context: Context?, file: HybridFile?): Boolean {
//        var isSuccessful = true
//        when (file!!.mode) {
//            OpenMode.SMB -> try {
//                val smbFile = SmbFile(file.path)
//                smbFile.mkdirs()
//            } catch (e: MalformedURLException) {
//                e.printStackTrace()
//                isSuccessful = false
//            } catch (e: SmbException) {
//                e.printStackTrace()
//                isSuccessful = false
//            }
//            OpenMode.OTG -> {
//                val documentFile: DocumentFile = OTGUtil.getDocumentFile(file.path, context, true)
//                isSuccessful = documentFile != null
//            }
//            OpenMode.FILE -> isSuccessful = mkdir(File(file.path), context)
//            else -> isSuccessful = true
//        }
//        return isSuccessful
//    }

    fun canListFiles(f: File): Boolean {
        return f.canRead() && f.isDirectory
    }

    fun mkfile(file: File, context: Context): Boolean {
        if (file.exists()) {
            // nothing to create.
            return !file.isDirectory
        }

        // Try the normal way
        try {
            if (file.createNewFile()) {
                return true
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Try with Storage Access Framework.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isOnExtSdCard(file, context)) {
            val document = getDocumentFile(file.parentFile, true, context) ?: return false
            // getDocumentFile implicitly creates the directory.
            return try {
                // TODO implement MimeTypes
                document.createFile(/*MimeTypes.getMimeType(file.path, file.isDirectory)*/ "image", file.name) != null
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
        return false
    }

    /**
     * Delete a folder.
     *
     * @param file The folder name.
     * @return true if successful.
     */
    private fun rmdir(file: File, context: Context): Boolean {
        if (!file.exists()) return true
        val files = file.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (child in files) {
                rmdir(child!!, context)
            }
        }

        // Try the normal way
        if (file.delete()) {
            return true
        }

        // Try with Storage Access Framework.
        val document = getDocumentFile(file, true, context)
        if (document != null && document.delete()) {
            return true
        }

        // Try the Kitkat workaround.
        return !file.exists()
    }

    /**
     * Check if a file is readable.
     *
     * @param file The file
     * @return true if the file is reabable.
     */
    fun isReadable(file: File): Boolean {
        if (!file.exists()) return false
        return try {
            file.canRead()
        } catch (e: SecurityException) {
            return false
        }
    }

    /**
     * Check if a file is writable. Detects write issues on external SD card.
     *
     * @param file The file
     * @return true if the file is writable.
     */
    fun isWritable(file: File): Boolean {
        val isExisting = file.exists()
        try {
            val output = FileOutputStream(file, true)
            try {
                output.close()
            } catch (e: IOException) {
                e.printStackTrace()
                // do nothing.
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return false
        }
        val result = file.canWrite()

        // Ensure that file is not created during this process.
        if (!isExisting) {
            file.delete()
        }
        return result
    }
    // Utility methods for Android 5
    /**
     * Check for a directory if it is possible to create files within this directory, either via normal writing or via
     * Storage Access Framework.
     *
     * @param folder The directory
     * @return true if it is possible to write in this directory.
     */
    fun isWritableNormalOrSaf(folder: File, c: Context): Boolean {

        // Verify that this is a directory.
        if (!folder.exists() || !folder.isDirectory) {
            return false
        }

        // Find a non-existing file in this directory.
        var i = 0
        var file: File
        do {
            val fileName = "AugendiagnoseDummyFile" + ++i
            file = File(folder, fileName)
        } while (file.exists())

        // First check regular writability
        if (isWritable(file)) {
            return true
        }

        // Next check SAF writability.
        val document = getDocumentFile(file, false, c) ?: return false

        // This should have created the file - otherwise something is wrong with access URL.
        val result = document.canWrite() && file.exists()

        // Ensure that the dummy file is not remaining.
        deleteFile(file, c)
        return result
    }

    /**
     * Get a list of external SD card paths. (Kitkat or higher.)
     *
     * @return A list of external SD card paths.
     */
    fun getExtSdCardPaths(context: Context): Array<String> {
        val paths: MutableList<String> = ArrayList()
        for (file in context.getExternalFilesDirs("external")) {
            if (file != null && file != context.getExternalFilesDir("external")) {
                val index = file.absolutePath.lastIndexOf("/Android/data")
                if (index < 0) {
                    Log.w(TAG, "Unexpected external file dir: " + file.absolutePath)
                } else {
                    var path: String = file.absolutePath.substring(0, index)
                    try {
                        path = File(path).canonicalPath
                    } catch (e: IOException) {
                        // Keep non-canonical path.
                    }
                    paths.add(path)
                }
            }
        }
        if (paths.isEmpty()) paths.add("/storage/sdcard1")
        return paths.toTypedArray()
    }

    /**
     * Determine the main folder of the external SD card containing the given file.
     *
     * @param file the file.
     * @return The main folder of the external SD card containing this file, if the file is on an SD card. Otherwise,
     * null is returned.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun getExtSdCardFolder(file: File, context: Context): String? {
        val extSdPaths = getExtSdCardPaths(context)
        try {
            for (i in extSdPaths.indices) {
                if (file.canonicalPath.startsWith(extSdPaths[i])) {
                    return extSdPaths[i]
                }
            }
        } catch (e: IOException) {
            return null
        }
        return null
    }

    /**
     * Determine if a file is on external sd card. (Kitkat or higher.)
     *
     * @param file The file.
     * @return true if on external sd card.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun isOnExtSdCard(file: File, c: Context): Boolean {
        return getExtSdCardFolder(file, c) != null
    }

    /**
     * Get a DocumentFile corresponding to the given file (for writing on ExtSdCard on Android 5). If the file is not
     * existing, it is created.
     *
     * @param file        The file.
     * @param isDirectory flag indicating if the file should be a directory.
     * @return The DocumentFile
     */
    fun getDocumentFile(file: File, isDirectory: Boolean, context: Context): DocumentFile? {
        val baseFolder = getExtSdCardFolder(file, context)
        var originalDirectory = false
        if (baseFolder == null) {
            return null
        }
        var relativePath: String? = null
        try {
            val fullPath = file.canonicalPath
            if (baseFolder != fullPath) relativePath = fullPath.substring(baseFolder.length + 1) else originalDirectory = true
        } catch (e: IOException) {
            return null
        } catch (f: Exception) {
            originalDirectory = true
            //continue
        }
        val `as`: String? = PreferenceManager.getDefaultSharedPreferences(context).getString("PreferencesConstants.PREFERENCE_URI", null)
        var treeUri: Uri? = null
        if (`as` != null) treeUri = Uri.parse(`as`)
        if (treeUri == null) {
            return null
        }

        // start with root of SD card and then parse through document tree.
        var document = DocumentFile.fromTreeUri(context, treeUri)
        if (originalDirectory) return document
        val parts: Array<String> = relativePath!!.split("\\/").toTypedArray()
        for (i in parts.indices) {
            var nextDocument: DocumentFile? = document?.findFile(parts[i])
            if (nextDocument == null) {
                if (i < parts.size - 1 || isDirectory) {
                    nextDocument = document?.createDirectory(parts[i])
                } else {
                    nextDocument = document?.createFile("image", parts[i])
                }
            }
            document = nextDocument
        }
        return document
    }

    // Utility methods for Kitkat
    /**
     * Copy a resource file into a private target directory, if the target does not yet exist. Required for the Kitkat
     * workaround.
     *
     * @param resource   The resource file.
     * @param folderName The folder below app folder where the file is copied to.
     * @param targetName The name of the target file.
     * @return the dummy file.
     */
    @Throws(IOException::class)
    private fun copyDummyFile(resource: Int, folderName: String?, targetName: String?, context: Context?): File? {
        val externalFilesDir = context!!.getExternalFilesDir(folderName) ?: return null
        val targetFile = File(externalFilesDir, targetName)
        if (!targetFile.exists()) {
            var `in`: InputStream? = null
            var out: OutputStream? = null
            try {
                `in` = context.resources.openRawResource(resource)
                out = FileOutputStream(targetFile)
                val buffer = ByteArray(4096) // MAGIC_NUMBER
                var bytesRead: Int
                while (`in`.read(buffer).also { bytesRead = it } != -1) {
                    out.write(buffer, 0, bytesRead)
                }
            } finally {
                if (`in` != null) {
                    try {
                        `in`.close()
                    } catch (ex: IOException) {
                        // do nothing
                    }
                }
                if (out != null) {
                    try {
                        out.close()
                    } catch (ex: IOException) {
                        // do nothing
                    }
                }
            }
        }
        return targetFile
    }

    /**
     * Checks whether the target path exists or is writable
     * @param f the target path
     * @return 1 if exists or writable, 0 if not writable
     */
    fun checkFolder(f: String, context: Context): Int {
        if (f.startsWith("smb://")
                || f.startsWith("ssh://")
//                || f.startsWith(OTGUtil.PREFIX_OTG)
//                || f.startsWith(CloudHandler.CLOUD_PREFIX_BOX)
//                || f.startsWith(CloudHandler.CLOUD_PREFIX_GOOGLE_DRIVE)
//                || f.startsWith(CloudHandler.CLOUD_PREFIX_DROPBOX)
//                || f.startsWith(CloudHandler.CLOUD_PREFIX_ONE_DRIVE)
        ) return 1
        val folder = File(f)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isOnExtSdCard(folder, context)) {
            if (!folder.exists() || !folder.isDirectory) {
                return 0
            }

            // On Android 5, trigger storage access framework.
            if (isWritableNormalOrSaf(folder, context)) {
                return 1
            }
        } else return if (folder.canWrite()) {
            1
        } else {
            0
        }
        return 0
    }

    /**
     * Validate given text is a valid filename.
     *
     * @param text
     * @return true if given text is a valid filename
     */
    fun isValidFilename(text: String?): Boolean {
        //It's not easy to use regex to detect single/double dot while leaving valid values (filename.zip) behind...
        //So we simply use equality to check them
        return (!FileUtil.FILENAME_REGEX.matcher(text).find()
                && "." != text && ".." != text)
    }

    internal class MediaFile(private val context: Context, val file: File?) {
        private val contentResolver: ContentResolver = context.contentResolver
        private var filesUri: Uri = MediaStore.Files.getContentUri("external")

        /**
         * Deletes the file. Returns true if the file has been successfully deleted or otherwise does not exist. This operation is not
         * recursive.
         */
        fun delete(): Boolean {
            if (!file!!.exists()) {
                return true
            }
            val directory = file.isDirectory
            if (directory) {
                // Verify directory does not contain any files/directories within it.
                val files = file.list()
                if (files != null && files.size > 0) {
                    return false
                }
            }
            val where: String = MediaStore.MediaColumns.DATA + "=?"
            val selectionArgs = arrayOf(file.absolutePath)

            // Delete the entry from the media database. This will actually delete media files (images, audio, and video).
            contentResolver.delete(filesUri, where, selectionArgs)
            if (file.exists()) {
                // If the file is not a media file, create a new entry suggesting that this location is an image, even
                // though it is not.
                val values = ContentValues()
                values.put(MediaStore.MediaColumns.DATA, file.absolutePath)
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

                // Delete the created entry, such that content provider will delete the file.
                contentResolver.delete(filesUri, where, selectionArgs)
            }
            return !file.exists()
        }

        private val temporaryAlbumId: Int
            private get() {
                val temporaryTrack = try {
                    installTemporaryTrack()
                } catch (ex: IOException) {
                    return 0
                }
                val selectionArgs = arrayOf(temporaryTrack!!.absolutePath)
                var cursor: Cursor? = contentResolver.query(filesUri, ALBUM_PROJECTION, MediaStore.MediaColumns.DATA + "=?",
                        selectionArgs, null)
                if (cursor == null || !cursor.moveToFirst()) {
                    if (cursor != null) {
                        cursor.close()
                        cursor = null
                    }
                    val values = ContentValues()
                    values.put(MediaStore.MediaColumns.DATA, temporaryTrack.absolutePath)
                    values.put(MediaStore.MediaColumns.TITLE, "{MediaWrite Workaround}")
                    values.put(MediaStore.MediaColumns.SIZE, temporaryTrack.length())
                    values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg")
                    values.put(MediaStore.Audio.AudioColumns.IS_MUSIC, true)
                    contentResolver.insert(filesUri, values)
                }
                cursor = contentResolver.query(filesUri, ALBUM_PROJECTION, MediaStore.MediaColumns.DATA + "=?",
                        selectionArgs, null)
                if (cursor == null) {
                    return 0
                }
                if (!cursor.moveToFirst()) {
                    cursor.close()
                    return 0
                }
                val id = cursor.getInt(0)
                val albumId = cursor.getInt(1)
                val mediaType = cursor.getInt(2)
                cursor.close()
                val values = ContentValues()
                var updateRequired = false
                if (albumId == 0) {
                    values.put(MediaStore.Audio.AlbumColumns.ALBUM_ID, 13371337)
                    updateRequired = true
                }
                if (mediaType != 2) {
                    values.put("media_type", 2)
                    updateRequired = true
                }
                if (updateRequired) {
                    contentResolver.update(filesUri, values, BaseColumns._ID + "=" + id, null)
                }
                cursor = contentResolver.query(filesUri, ALBUM_PROJECTION, MediaStore.MediaColumns.DATA + "=?",
                        selectionArgs, null)
                return if (cursor == null) {
                    0
                } else try {
                    if (!cursor.moveToFirst()) {
                        0
                    } else cursor.getInt(1)
                } finally {
                    cursor.close()
                }
            }

        @Throws(IOException::class)
        private fun installTemporaryTrack(): File? {
            val externalFilesDir = getExternalFilesDir(context) ?: return null
            val temporaryTrack = File(externalFilesDir, "temptrack.mp3")
            if (!temporaryTrack.exists()) {
                var `in`: InputStream? = null
                var out: OutputStream? = null
                try {
                    // TODO add R.raw.temptrack
                    `in` = context.resources.openRawResource(0)
                    out = FileOutputStream(temporaryTrack)
                    val buffer = ByteArray(4096)
                    var bytesRead: Int
                    while (`in`.read(buffer).also { bytesRead = it } != -1) {
                        out.write(buffer, 0, bytesRead)
                    }
                } finally {
                    if (`in` != null) {
                        try {
                            `in`.close()
                        } catch (ex: IOException) {
                            return null
                        }
                    }
                    if (out != null) {
                        try {
                            out.close()
                        } catch (ex: IOException) {
                            return null
                        }
                    }
                }
            }
            return temporaryTrack
        }

        @Throws(IOException::class)
        fun mkdir(): Boolean {
            if (file!!.exists()) {
                return file.isDirectory
            }
            val tmpFile = File(file, ".MediaWriteTemp")
            val albumId = temporaryAlbumId
            if (albumId == 0) {
                throw IOException("Fail")
            }
            val albumUri = Uri.parse("$ALBUM_ART_URI/$albumId")
            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DATA, tmpFile.absolutePath)
            if (contentResolver.update(albumUri, values, null, null) == 0) {
                values.put(MediaStore.Audio.AlbumColumns.ALBUM_ID, albumId)
                contentResolver.insert(Uri.parse(ALBUM_ART_URI), values)
            }
            try {
                val fd: ParcelFileDescriptor? = contentResolver.openFileDescriptor(albumUri, "r")
                fd?.close()
            } finally {
                val tmpMediaFile = MediaFile(context, tmpFile)
                tmpMediaFile.delete()
            }
            return file.exists()
        }

        /**
         * Returns an OutputStream to write to the file. The file will be truncated immediately.
         */
        @Throws(IOException::class)
        fun write(size: Long): OutputStream? {
            if (NO_MEDIA == file!!.name.trim { it <= ' ' }) {
                throw IOException("Unable to create .nomedia file via media content provider API.")
            }
            if (file.exists() && file.isDirectory) {
                throw IOException("File exists and is a directory.")
            }

            // Delete any existing entry from the media database.
            // This may also delete the file (for media types), but that is irrelevant as it will be truncated momentarily in any case.
            val where: String = MediaStore.MediaColumns.DATA + "=?"
            val selectionArgs = arrayOf(file.absolutePath)
            contentResolver.delete(filesUri, where, selectionArgs)
            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DATA, file.absolutePath)
            values.put(MediaStore.MediaColumns.SIZE, size)
            val uri: Uri = contentResolver.insert(filesUri, values)
                    ?: // Should not occur.
                    throw IOException("Internal error.")
            return contentResolver.openOutputStream(uri)
        }

        companion object {
            private val NO_MEDIA: String? = ".nomedia"
            private val ALBUM_ART_URI: String? = "content://media/external/audio/albumart"
            private val ALBUM_PROJECTION: Array<String?>? = arrayOf(BaseColumns._ID, MediaStore.Audio.AlbumColumns.ALBUM_ID, "media_type")
            private fun getExternalFilesDir(context: Context?): File? {
                return try {
                    val method = Context::class.java.getMethod("getExternalFilesDir", String::class.java)
                    method.invoke(context, null as String?) as File
                } catch (ex: SecurityException) {
                    //   Log.d(Maui.LOG_TAG, "Unexpected reflection error.", ex);
                    null
                } catch (ex: NoSuchMethodException) {
                    //     Log.d(Maui.LOG_TAG, "Unexpected reflection error.", ex);
                    null
                } catch (ex: IllegalArgumentException) {
                    // Log.d(Maui.LOG_TAG, "Unexpected reflection error.", ex);
                    null
                } catch (ex: IllegalAccessException) {
                    //Log.d(Maui.LOG_TAG, "Unexpected reflection error.", ex);
                    null
                } catch (ex: InvocationTargetException) {
                    //Log.d(Maui.LOG_TAG, "Unexpected reflection error.", ex);
                    null
                }
            }
        }

    }
}
