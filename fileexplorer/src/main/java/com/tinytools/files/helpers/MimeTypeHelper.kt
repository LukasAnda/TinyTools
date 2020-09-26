package com.tinytools.files.helpers

import android.content.Context
import android.content.res.Resources
import android.webkit.MimeTypeMap
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.tinytools.files.R
import com.tinytools.files.filesystem.HybridFile
import com.tinytools.files.helpers.MimeType.Apk
import com.tinytools.files.helpers.MimeType.Archive
import com.tinytools.files.helpers.MimeType.Audio
import com.tinytools.files.helpers.MimeType.Certificate
import com.tinytools.files.helpers.MimeType.Code
import com.tinytools.files.helpers.MimeType.Contact
import com.tinytools.files.helpers.MimeType.Document
import com.tinytools.files.helpers.MimeType.Event
import com.tinytools.files.helpers.MimeType.Folder
import com.tinytools.files.helpers.MimeType.Font
import com.tinytools.files.helpers.MimeType.Image
import com.tinytools.files.helpers.MimeType.Pdf
import com.tinytools.files.helpers.MimeType.Presentation
import com.tinytools.files.helpers.MimeType.Spreadsheets
import com.tinytools.files.helpers.MimeType.Text
import com.tinytools.files.helpers.MimeType.Unknown
import com.tinytools.files.helpers.MimeType.Video
import xyz.aprildown.theme.Theme
import java.util.*

enum class MimeType {
    Archive, Apk, Unknown, Text, Image, Audio, Video, Certificate, Code, Contact, Event, Font, Pdf, Presentation, Spreadsheets, Document, Folder
}

@DrawableRes
fun MimeType.getIcon() = when (this) {
    Archive -> R.drawable.ic_doc_archive
    Apk -> R.drawable.ic_doc_apk
    Unknown -> R.drawable.ic_file
    Text -> R.drawable.ic_doc_text
    Image -> R.drawable.ic_doc_image
    Audio -> R.drawable.ic_doc_audio
    Video -> R.drawable.ic_doc_video
    Certificate -> R.drawable.ic_doc_certificate
    Code -> R.drawable.ic_doc_code
    Contact -> R.drawable.ic_doc_contact
    Event -> R.drawable.ic_doc_event
    Font -> R.drawable.ic_doc_font
    Pdf -> R.drawable.ic_doc_pdf
    Presentation -> R.drawable.ic_doc_presentation
    Spreadsheets -> R.drawable.ic_doc_spreadsheet
    Document -> R.drawable.ic_doc_word
    Folder -> R.drawable.ic_folder
}

@ColorInt
fun MimeType.getColor(resources: Resources) = when(this){
    Archive -> ResourcesCompat.getColor(resources,R.color.blue_grey, null)
    Apk -> ResourcesCompat.getColor(resources,R.color.green, null)
    Unknown -> ResourcesCompat.getColor(resources,R.color.grey, null)
    Text -> ResourcesCompat.getColor(resources,R.color.blue, null)
    Image -> ResourcesCompat.getColor(resources,R.color.teal, null)
    Audio -> ResourcesCompat.getColor(resources,R.color.orange, null)
    Video -> ResourcesCompat.getColor(resources,R.color.cyan, null)
    Certificate -> ResourcesCompat.getColor(resources,R.color.light_green, null)
    Code -> ResourcesCompat.getColor(resources,R.color.light_blue, null)
    Contact -> ResourcesCompat.getColor(resources,R.color.brown, null)
    Event -> ResourcesCompat.getColor(resources,R.color.amber, null)
    Font -> ResourcesCompat.getColor(resources,R.color.blue, null)
    Pdf -> ResourcesCompat.getColor(resources,R.color.red, null)
    Presentation -> ResourcesCompat.getColor(resources,R.color.orange, null)
    Spreadsheets -> ResourcesCompat.getColor(resources,R.color.green, null)
    Document -> ResourcesCompat.getColor(resources,R.color.blue, null)
    Folder -> Theme.get().colorPrimary
}

private fun getFileType(mimeType: String?) = when (mimeType) {
    //region Folder
    null -> Folder
    //endregion Folder
    //region Archive
    "application/mac-binhex40",
    "application/rar",
    "application/zip",
    "application/java-archive",
    "application/x-apple-diskimage",
    "application/x-debian-package",
    "application/x-gtar",
    "application/x-iso9660-image",
    "application/x-lha",
    "application/x-lzh",
    "application/x-lzx",
    "application/x-stuffit",
    "application/x-tar",
    "application/x-webarchive",
    "application/x-webarchive-xml",
    "application/x-gzip",
    "application/x-7z-compressed",
    "application/x-deb",
    "application/x-rar-compressed",
    "application/x-lzma",
    "application/x-xz",
    "application/x-bzip2" -> Archive
    //endregion
    //region Apk
    "application/vnd.android.package-archive" -> Apk
    //endregion
    //region Audio
    "application/ogg",
    "application/x-flac" -> Audio
    //endregion
    //region Certificate
    "application/pgp-keys",
    "application/pgp-signature",
    "application/x-pkcs12",
    "application/x-pkcs7-certreqresp",
    "application/x-pkcs7-crl",
    "application/x-x509-ca-cert",
    "application/x-x509-user-cert",
    "application/x-pkcs7-certificates",
    "application/x-pkcs7-mime",
    "application/x-pkcs7-signature" -> Certificate
    //endregion
    //region Code
    "application/rdf+xml",
    "application/rss+xml",
    "application/x-object",
    "application/xhtml+xml",
    "text/css",
    "text/html",
    "text/xml",
    "text/x-c++hdr",
    "text/x-c++src",
    "text/x-chdr",
    "text/x-csrc",
    "text/x-dsrc",
    "text/x-csh",
    "text/x-haskell",
    "text/x-java",
    "text/x-literate-haskell",
    "text/x-pascal",
    "text/x-tcl",
    "text/x-tex",
    "application/x-latex",
    "application/x-texinfo",
    "application/atom+xml",
    "application/ecmascript",
    "application/json",
    "application/javascript",
    "application/xml",
    "text/javascript",
    "application/x-javascript" -> Code
    //endregion
    //region Contact
    "text/x-vcard",
    "text/vcard" -> Contact
    //endregion
    //region Event
    "text/calendar",
    "text/x-vcalendar" -> Event
    //endregion
    //region Font
    "application/x-font",
    "application/font-woff",
    "application/x-font-woff",
    "application/x-font-ttf" -> Font
    //endregion
    //region Image
    "application/vnd.oasis.opendocument.graphics",
    "application/vnd.oasis.opendocument.graphics-template",
    "application/vnd.oasis.opendocument.image",
    "application/vnd.stardivision.draw",
    "application/vnd.sun.xml.draw",
    "application/vnd.sun.xml.draw.template" -> Image
    //endregion
    //region Pdf
    "application/pdf" -> Pdf
    //endregion
    //region Presentation
    "application/vnd.ms-powerpoint",
    "application/vnd.openxmlformats-officedocument.presentationml.presentation",
    "application/vnd.openxmlformats-officedocument.presentationml.template",
    "application/vnd.openxmlformats-officedocument.presentationml.slideshow",
    "application/vnd.stardivision.impress",
    "application/vnd.sun.xml.impress",
    "application/vnd.sun.xml.impress.template",
    "application/x-kpresenter",
    "application/vnd.oasis.opendocument.presentation" -> Presentation
    //endregion
    //region Spreadsheets
    "application/vnd.oasis.opendocument.spreadsheet",
    "application/vnd.oasis.opendocument.spreadsheet-template",
    "application/vnd.ms-excel",
    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    "application/vnd.openxmlformats-officedocument.spreadsheetml.template",
    "application/vnd.stardivision.calc",
    "application/vnd.sun.xml.calc",
    "application/vnd.sun.xml.calc.template",
    "application/x-kspread",
    "text/comma-separated-values" -> Spreadsheets
    //endregion
    //region Document
    "application/msword",
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    "application/vnd.openxmlformats-officedocument.wordprocessingml.template",
    "application/vnd.oasis.opendocument.text",
    "application/vnd.oasis.opendocument.text-master",
    "application/vnd.oasis.opendocument.text-template",
    "application/vnd.oasis.opendocument.text-web",
    "application/vnd.stardivision.writer",
    "application/vnd.stardivision.writer-global",
    "application/vnd.sun.xml.writer",
    "application/vnd.sun.xml.writer.global",
    "application/vnd.sun.xml.writer.template",
    "application/x-abiword",
    "application/x-kword",
    "text/markdown" -> Document
    //endregion
    //region Filter by starting type
    else -> when {
        mimeType.startsWith("text") -> Text
        mimeType.startsWith("audio") -> Audio
        mimeType.startsWith("video") -> Video
        mimeType.startsWith("image") -> Image
        else -> Unknown
    }
    //endregion
}

private const val ALL_MIME_TYPES = "*/*"

private val MIME_TYPES = mapOf(
        "asm" to "text/x-asm",
        "json" to "application/json",
        "js" to "application/javascript",

        "def" to "text/plain",
        "in" to "text/plain",
        "rc" to "text/plain",
        "list" to "text/plain",
        "log" to "text/plain",
        "pl" to "text/plain",
        "prop" to "text/plain",
        "properties" to "text/plain",
        "rc" to "text/plain",
        "ini" to "text/plain",
        "md" to "text/markdown",

        "epub" to "application/epub+zip",
        "ibooks" to "application/x-ibooks+zip",

        "ifb" to "text/calendar",
        "eml" to "message/rfc822",
        "msg" to "application/vnd.ms-outlook",

        "ace" to "application/x-ace-compressed",
        "bz" to "application/x-bzip",
        "bz2" to "application/x-bzip2",
        "cab" to "application/vnd.ms-cab-compressed",
        "gz" to "application/x-gzip",
        "7z" to "application/x-7z-compressed",
        "lrf" to "application/octet-stream",
        "jar" to "application/java-archive",
        "xz" to "application/x-xz",
        "lzma" to "application/x-lzma",
        "Z" to "application/x-compress",

        "bat" to "application/x-msdownload",
        "ksh" to "text/plain",
        "sh" to "application/x-sh",

        "db" to "application/octet-stream",
        "db3" to "application/octet-stream",

        "otf" to "application/x-font-otf",
        "ttf" to "application/x-font-ttf",
        "psf" to "application/x-font-linux-psf",
        "cgm" to "image/cgm",
        "btif" to "image/prs.btif",
        "dwg" to "image/vnd.dwg",
        "dxf" to "image/vnd.dxf",
        "fbs" to "image/vnd.fastbidsheet",
        "fpx" to "image/vnd.fpx",
        "fst" to "image/vnd.fst",
        "mdi" to "image/vnd.ms-mdi",
        "npx" to "image/vnd.net-fpx",
        "xif" to "image/vnd.xiff",
        "pct" to "image/x-pict",
        "pic" to "image/x-pict",
        "gif" to "image/gif",

        "adp" to "audio/adpcm",
        "au" to "audio/basic",
        "snd" to "audio/basic",
        "m2a" to "audio/mpeg",
        "m3a" to "audio/mpeg",
        "oga" to "audio/ogg",
        "spx" to "audio/ogg",
        "aac" to "audio/x-aac",
        "mka" to "audio/x-matroska",
        "opus" to "audio/ogg",

        "jpgv" to "video/jpeg",
        "jpgm" to "video/jpm",
        "jpm" to "video/jpm",
        "mj2" to "video/mj2",
        "mjp2" to "video/mj2",
        "mpa" to "video/mpeg",
        "ogv" to "video/ogg",
        "flv" to "video/x-flv",
        "mkv" to "video/x-matroska",
        "mts" to "video/mp2t"
)

private fun getMimeType(context: Context, hybridFile: HybridFile): String? {
    if (hybridFile.isDirectory(context)) {
        return null
    }
    var type: String? = ALL_MIME_TYPES
    val extension: String = getExtension(hybridFile.path)

    // mapping extension to system mime types
    if (extension.isNotEmpty()) {
        val extensionLowerCase = extension.toLowerCase(Locale.getDefault())
        val mime = MimeTypeMap.getSingleton()
        type = mime.getMimeTypeFromExtension(extensionLowerCase)
        if (type == null) {
            type = MIME_TYPES[extensionLowerCase]
        }
    }
    if (type == null) type = ALL_MIME_TYPES
    return type
}

private fun getExtension(path: String): String {
    return if (path.contains(".")) path.substring(path.lastIndexOf(".") + 1).toLowerCase() else ""
}

fun HybridFile.getFileType(context: Context) = getFileType(getMimeType(context, this))


