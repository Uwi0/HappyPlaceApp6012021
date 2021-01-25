package com.kakapo.happyplaces.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kakapo.happyplaces.R
import com.kakapo.happyplaces.adapter.HappyPlaceAdapter
import com.kakapo.happyplaces.database.DatabaseHandler
import com.kakapo.happyplaces.model.HappyPlaceModel

class MainActivity : AppCompatActivity() {

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
            startActivity(intent)
        }

        getHappyPlaceListFromLocalDatabase()
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
    }
}