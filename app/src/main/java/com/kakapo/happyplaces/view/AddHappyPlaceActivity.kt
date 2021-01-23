package com.kakapo.happyplaces.view

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.kakapo.happyplaces.R
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.text.SimpleDateFormat
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var toolbar: Toolbar
    private lateinit var etDate: EditText
    private lateinit var tvAddImage: TextView

    private var calendar = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_happy_place)

        toolbar = findViewById(R.id.toolbar_add_place)
        etDate = findViewById(R.id.et_date)
        tvAddImage = findViewById(R.id.tv_add_image)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{
            onBackPressed()
        }

        dateSetListener = DatePickerDialog.OnDateSetListener {
            _,
            year,
            month,
            dayOfMonth ->

            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            updateDateInView()

        }

        etDate.setOnClickListener(this)
        tvAddImage.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.et_date ->{
                DatePickerDialog(
                        this@AddHappyPlaceActivity,
                        dateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            R.id.tv_add_image ->{
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems = arrayOf(
                        "Select photo from Gallery",
                        "Capture photo from camera"
                )

                pictureDialog.setItems(pictureDialogItems){ _, which ->
                    when(which){
                        0 -> choosePhotoFromGallery()
                        1 -> Toast.makeText(
                                this@AddHappyPlaceActivity,
                                "Camera Selection Coming Soon....",
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                pictureDialog.show()
            }
        }
    }

    private fun updateDateInView(){
        val myFormat = "dd.MM.yyyy"
        val simpleDateFormat = SimpleDateFormat(myFormat, Locale.getDefault())
        etDate.setText(simpleDateFormat.format(calendar.time).toString())
    }

    private fun choosePhotoFromGallery(){
        Dexter.withActivity(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object: MultiplePermissionsListener {

            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if(report!!.areAllPermissionsGranted()){
                    Toast.makeText(
                            this@AddHappyPlaceActivity,
                            "READ/WRITE permission are granted. Now you can select an " +
                                    "image from gallery",
                            Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
            ) {
                showRationalDialogFormPermissions()
            }

        }).onSameThread().check()
    }

    private fun showRationalDialogFormPermissions() {
        AlertDialog
                .Builder(this)
                .setMessage("It look like you have turned" +
                        " off permission required for this" +
                        " feature. it can be enabled in the" +
                        " Application Settings")
                .setPositiveButton("GO TO SETTINGS"){ _, _ ->
                    try{
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }catch (e :ActivityNotFoundException){
                        print("Error try to open applications settings ${e.message}")
                        e.printStackTrace()
                    }
                }
                .setNegativeButton("Cancel"){ dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }
}