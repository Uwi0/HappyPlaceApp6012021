package com.kakapo.happyplaces.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kakapo.happyplaces.R
import com.kakapo.happyplaces.database.DatabaseHandler
import com.kakapo.happyplaces.model.HappyPlaceModel

class MainActivity : AppCompatActivity() {

    private lateinit var fabAddHappyPlace: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fabAddHappyPlace = findViewById(R.id.fab_add_happy_place)


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
            for (i in getHappyPlaceList){
                Log.e("Title", i.title)
                Log.e("Description", i.description)
            }
        }
    }
}