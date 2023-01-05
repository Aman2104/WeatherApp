package com.example.weatherapp


import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Adapter(private val itemList : ArrayList<weather>) : RecyclerView.Adapter<Adapter.UserViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view, parent , false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.textTemp.text = currentItem.temp
        holder.textRain.text = currentItem.rain
        holder.textTime.text = currentItem.time
        holder.image.setImageResource(currentItem.image)

    }

    override fun getItemCount(): Int {
        return itemList.size
    }
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var textTemp : TextView = itemView.findViewById(R.id.txttemp1)
        var textRain :TextView = itemView.findViewById(R.id.txtrain1)
        val textTime: TextView = itemView.findViewById(R.id.txttime1)
        val image: ImageView = itemView.findViewById(R.id.image)
    }
}