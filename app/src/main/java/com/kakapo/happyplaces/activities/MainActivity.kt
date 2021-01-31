package com.kakapo.happyplaces.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kakapo.happyplaces.R
import com.kakapo.happyplaces.adapter.HappyPlaceAdapter
import com.kakapo.happyplaces.database.DatabaseHandler
import com.kakapo.happyplaces.model.HappyPlaceModel
import com.kakapo.happyplaces.utils.SwipeToEditCallBack

class MainActivity : AppCompatActivity() {

    companion object{
        private const val ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
        const val EXTRA_PLACE_DETAIL = "extra_place_detail"
    }

    private lateinit var fabAddHappyPlace: FloatingActionButton
    private lateinit var rvHappyPlaceList: RecyclerView
    private lateinit var tvNoRecordsAvailable: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fabAddHappyPlace = findViewById(R.id.fab_add_happy_place)
        rvHappyPlaceList = findViewById(R.id.rv_happy_place_list)
        tvNoRecordsAvailable = findViewById(R.id.tv_no_record_available)

        fabAddHappyPlace.setOnClickListener {
            val intent = Intent(this, AddHappyPlaceActivity::class.java)
            startActivityForResult(intent, ADD_PLACE_ACTIVITY_REQUEST_CODE)
        }

        getHappyPlaceListFromLocalDatabase()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                getHappyPlaceListFromLocalDatabase()
            }
        }else{
            Log.e("Activity", "cancelled or back pressed")
        }
    }

    private fun getHappyPlaceListFromLocalDatabase(){
        val dbHandler = DatabaseHandler(this)
        val getHappyPlaceList: ArrayList<HappyPlaceModel> = dbHandler.getHappyPlaceList()

        if(getHappyPlaceList.size > 0){

            rvHappyPlaceList.visibility = View.VISIBLE
            tvNoRecordsAvailable.visibility = View.GONE
            setupHappyPlaceRecyclerView(getHappyPlaceList)

        }else{

            rvHappyPlaceList.visibility = View.GONE
            tvNoRecordsAvailable.visibility = View.VISIBLE

        }
    }

    private fun setupHappyPlaceRecyclerView(happyPlaceList: ArrayList<HappyPlaceModel>){
        rvHappyPlaceList.layoutManager = LinearLayoutManager(this)
        rvHappyPlaceList.setHasFixedSize(true)

        val adapter = HappyPlaceAdapter(happyPlaceList)
        rvHappyPlaceList.adapter = adapter

        adapter.setOnclickListener(object : HappyPlaceAdapter.OnclickListener {
            override fun onclick(position: Int, model: HappyPlaceModel) {
                val intent = Intent(
                        this@MainActivity,
                        HappyPlaceDetailActivity::class.java
                )
                intent.putExtra(EXTRA_PLACE_DETAIL, model)
                startActivity(intent)
            }
        })

        val editSwipeHandler = object : SwipeToEditCallBack(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapterSwipe = rvHappyPlaceList.adapter as HappyPlaceAdapter
                adapterSwipe.notifyEditItem(
                        this@MainActivity,
                        this@MainActivity,
                        viewHolder.adapterPosition,
                        ADD_PLACE_ACTIVITY_REQUEST_CODE
                )
            }

        }

        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(rvHappyPlaceList)
    }
}