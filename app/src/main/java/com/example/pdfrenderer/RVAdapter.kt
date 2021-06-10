package com.example.pdfrenderer

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class RVAdapter(private val list : List<Bitmap>) : RecyclerView.Adapter<RVAdapter.PDFViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PDFViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pdf_pages, parent, false)
        return PDFViewHolder(view)
    }

    override fun onBindViewHolder(holder: PDFViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    class PDFViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        private val imageView = itemView.findViewById(R.id.imageView) as ImageView
        fun bind(bitmap : Bitmap){
            imageView.setImageBitmap(bitmap)
        }
    }
}