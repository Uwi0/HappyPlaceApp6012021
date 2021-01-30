package com.kakapo.happyplaces.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.kakapo.happyplaces.R
import com.kakapo.happyplaces.model.HappyPlaceModel

class HappyPlaceDetailActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var ivPlaceImage: ImageView
    private lateinit var tvDescription: TextView
    private lateinit var tvLocation: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_happy_place_detail)

        toolbar = findViewById(R.id.toolbar_happy_place_detail)
        ivPlaceImage = findViewById(R.id.iv_place_image)
        tvDescription = findViewById(R.id.tv_description)
        tvLocation = findViewById(R.id.tv_location)
        var happyPlaceDetailModel: HappyPlaceModel? = null

        if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAIL)){
            happyPlaceDetailModel =
                    intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAIL)
        }

        if(happyPlaceDetailModel != null){
            setSupportActionBar(toolbar)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = happyPlaceDetailModel.title

            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }

            ivPlaceImage.setImageURI(Uri.parse(happyPlaceDetailModel.image))
            tvDescription.text = happyPlaceDetailModel.description
            tvLocation.text = happyPlaceDetailModel.location
        }
    }
}