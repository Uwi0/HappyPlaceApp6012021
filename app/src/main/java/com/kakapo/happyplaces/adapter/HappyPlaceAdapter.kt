package com.kakapo.happyplaces.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kakapo.happyplaces.R
import com.kakapo.happyplaces.activities.AddHappyPlaceActivity
import com.kakapo.happyplaces.activities.MainActivity
import com.kakapo.happyplaces.model.HappyPlaceModel

class HappyPlaceAdapter(
    private var list: ArrayList<HappyPlaceModel>
) : RecyclerView.Adapter<HappyPlaceAdapter.ViewHolder>() {

    private var onClickListener: OnclickListener? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){

        val ivPlaceImage: ImageView = view.findViewById(R.id.iv_place_image)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.item_happy_place,
                        parent,
                        false
                )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = list[position]

        holder.ivPlaceImage.setImageURI(Uri.parse(model.image))
        holder.tvTitle.text = model.title
        holder.tvDescription.text = model.description

        holder.itemView.setOnClickListener {
            if(onClickListener != null){
                onClickListener!!.onclick(position, model)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnclickListener(onClickListener: OnclickListener){
        this.onClickListener = onClickListener
    }

    fun notifyEditItem(context: Context, activity: Activity, position: Int, requestCode: Int){
        val intent = Intent(context, AddHappyPlaceActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_PLACE_DETAIL, list[position])
        activity.startActivityForResult(intent, requestCode)
        notifyItemChanged(position)
    }

    interface OnclickListener{
        fun onclick(position: Int, model: HappyPlaceModel)
    }
}