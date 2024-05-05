package com.example.newsapp.adaptors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.init
import com.example.newsapp.R
import com.example.onlinebeamsandroidapp.NewsDataClass

class CompleteNewsAdaptor(private var itemList: ArrayList<NewsDataClass>, val fragment: Fragment) :
    RecyclerView.Adapter<CompleteNewsAdaptor.myViewHolder>() {
    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(clickListener: onItemClickListener) {
        mListener = clickListener
    }

    class myViewHolder(val itemView: View, clickListener: onItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        val authorName = itemView.findViewById<TextView>(R.id.newsAuthor)
        val newsHeadline = itemView.findViewById<TextView>(R.id.newsHeading)
        val itemImage = itemView.findViewById<ImageView>(R.id.imgItem)
        val dateNews=itemView.findViewById<TextView>(R.id.datenews)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CompleteNewsAdaptor.myViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.cardview,
            parent, false
        )
        return myViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: CompleteNewsAdaptor.myViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.authorName.text = currentItem.autherName
        holder.newsHeadline.text = currentItem.newsHeading
        holder.dateNews.text=currentItem.dateNews
        Glide.with(fragment)
            .load(itemList[position].itemImage)
            .into(holder.itemImage)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun setFilteredList(itemArrayList: ArrayList<NewsDataClass>) {
        this.itemList = itemArrayList
        notifyDataSetChanged()
    }
}