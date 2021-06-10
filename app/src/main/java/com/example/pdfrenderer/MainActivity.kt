package com.example.pdfrenderer

import android.content.ContentUris
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var pdfPages : ArrayList<Bitmap>
    private  val PICK_PDF_CODE = 900
    private lateinit var  rvPdf : RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnOpen = findViewById<Button>(R.id.btnSelectPdf)
        rvPdf = findViewById(R.id.rvPdf)
        btnOpen.setOnClickListener {
//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "application/pdf"
//            intent.addCategory(Intent.CATEGORY_OPENABLE)
//
//            startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_CODE)
            getContent.launch("application/pdf")
        }
    }



    private fun renderPDFToBitmap(pdfUri: Uri){
        pdfPages = ArrayList()
        val file = File(FileUtils.getPath(this, pdfUri))
//        val file = FileUtils.getFile(this, pdfUri)
        val pdfDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        val pdfRenderer = PdfRenderer(pdfDescriptor)
        for (i in 0 until pdfRenderer.pageCount){
            val doc = pdfRenderer.openPage(i)
            val bitmap = Bitmap.createBitmap(doc.width, doc.height, Bitmap.Config.ARGB_8888)
            doc.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            doc.close()
            pdfPages.add(bitmap)
        }
        pdfRenderer.close()
        val rvAdapter = RVAdapter(pdfPages)

        rvPdf.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = rvAdapter
            setHasFixedSize(true)
        }

    }

    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()){
        renderPDFToBitmap(it)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(requestCode ==  PICK_PDF_CODE && resultCode == RESULT_OK){
//            data?.let {
//               val uri = it.data
//                uri?.let {
//                    renderPDFToBitmap(uri)
//                }
//            }
//        }
//    }

    fun getPDFPath(uri: Uri?): String? {
        val id = DocumentsContract.getDocumentId(uri)
        val contentUri = ContentUris.withAppendedId(
            Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
        )
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = contentResolver.query(contentUri, projection, null, null, null)
        val column_index = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        return column_index?.let { cursor.getString(it) }
    }

    fun getImagePath(uri: Uri?): String? {
        var cursor = contentResolver.query(uri!!, null, null, null, null)
        cursor!!.moveToFirst()
        var document_id = cursor.getString(0)
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1)
        cursor.close()
        cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null, MediaStore.Images.Media._ID + " = ? ", arrayOf(document_id), null
        )
        cursor!!.moveToFirst()
        val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
        cursor.close()
        return path
    }
}