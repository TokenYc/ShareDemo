package com.qianfanyun.sharedemo

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import android.provider.MediaStore
import android.content.ContentValues
import android.content.Context


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            shareImg();
        }
    }

    private fun shareImg() {
        val imagePath = Environment.getExternalStorageDirectory().absolutePath + "/test.jpg"
//        val imagePath2 = Environment.getExternalStorageDirectory().absolutePath + "/test2.jpg"

        val uriList: ArrayList<Uri?> = ArrayList()
        uriList.add(getImageContentUri(this, File(imagePath)))
//        uriList.add(getImageContentUri(this,File(imagePath2)))

        var shareIntent = Intent()
        shareIntent.setAction(Intent.ACTION_VIEW)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        shareIntent.setDataAndType(getImageContentUri(this, File(imagePath)), "image/*")
//        shareIntent.putExtra(Intent.EXTRA_STREAM,getImageContentUri(this,File(imagePath)))
        shareIntent = Intent.createChooser(shareIntent, "分享图片")
        startActivity(shareIntent)
    }

    fun getImageContentUri(context: Context, imageFile: File): Uri? {
        val filePath = imageFile.absolutePath
        val cursor = context.getContentResolver().query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf<String>(MediaStore.Images.Media._ID), MediaStore.Images.Media.DATA + "=? ",
            arrayOf<String>(filePath), null
        )
        var uri: Uri? = null

        if (cursor != null) {
            if (cursor!!.moveToFirst()) {
                val id = cursor!!.getInt(cursor!!.getColumnIndex(MediaStore.MediaColumns._ID))
                val baseUri = Uri.parse("content://media/external/images/media")
                uri = Uri.withAppendedPath(baseUri, "" + id)
            }

            cursor!!.close()
        }

        if (uri == null) {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.DATA, filePath)
            uri = context.getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        }

        return uri
    }

}
