package com.kakapo.happyplaces.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.kakapo.happyplaces.R
import com.kakapo.happyplaces.database.DatabaseHandler
import com.kakapo.happyplaces.model.HappyPlaceModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {

    companion object{
        private const val GALLERY_REQUEST_CODE = 1
        private const val CAMERA_REQUEST_CODE = 2
        private const val IMAGE_DIRECTORY = "HappyPlacesImages"
    }

    private lateinit var toolbar: Toolbar
    private lateinit var etDate: EditText
    private lateinit var tvAddImage: TextView
    private lateinit var ivPlaceImage: ImageView
    private lateinit var btnSave: Button
    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var etLocation: EditText

    private var calendar = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private var saveImageToInternalStorage: Uri? = null
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0

    private var mHappyPlaceDetails: HappyPlaceModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_happy_place)

        toolbar = findViewById(R.id.toolbar_add_place)
        etDate = findViewById(R.id.et_date)
        tvAddImage = findViewById(R.id.tv_add_image)
        ivPlaceImage = findViewById(R.id.iv_place_image)
        btnSave = findViewById(R.id.btn_save)
        etTitle = findViewById(R.id.et_title)
        etDescription = findViewById(R.id.et_description)
        etLocation = findViewById(R.id.et_location)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{
            onBackPressed()
        }

        if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAIL)){
            mHappyPlaceDetails = intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAIL)
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

        updateDateInView()

        if(mHappyPlaceDetails != null){
            supportActionBar?.title = "Edit Happy Place"

            etTitle.setText(mHappyPlaceDetails!!.title)
            etDescription.setText(mHappyPlaceDetails!!.description)
            etDate.setText(mHappyPlaceDetails!!.date)
            etLocation.setText(mHappyPlaceDetails!!.location)
            mLatitude = mHappyPlaceDetails!!.latitude
            mLongitude = mHappyPlaceDetails!!.longitude

            saveImageToInternalStorage = Uri.parse(
                    mHappyPlaceDetails!!.image
            )

            ivPlaceImage.setImageURI(saveImageToInternalStorage)
            btnSave.text = ("UPDATE")

        }

        etDate.setOnClickListener(this)
        tvAddImage.setOnClickListener(this)
        btnSave.setOnClickListener(this)
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
                        1 -> takePhotoFromCamera()
                    }
                }
                pictureDialog.show()
            }

            R.id.btn_save ->{
                when{
                    etTitle.text.isNullOrEmpty() ->{
                        Toast.makeText(
                                this,
                                "Please Enter title",
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                    etDescription.text.isNullOrEmpty() ->{
                        Toast.makeText(
                                this,
                                "Please Enter Description",
                                Toast.LENGTH_SHORT
                        ).show()
                    }

                    etLocation.text.isNullOrEmpty() ->{
                        Toast.makeText(
                                this,
                                "Please Enter Location",
                                Toast.LENGTH_SHORT
                        ).show()
                    }

                    saveImageToInternalStorage == null ->{
                        Toast.makeText(
                                this,
                                "Please Select an Image",
                                Toast.LENGTH_SHORT
                        ).show()
                    }

                    else ->{
                        val happyPlaceModel = HappyPlaceModel(
                                if(mHappyPlaceDetails == null) 0 else mHappyPlaceDetails!!.id,
                                etTitle.text.toString(),
                                saveImageToInternalStorage.toString(),
                                etDescription.text.toString(),
                                etDate.text.toString(),
                                etLocation.text.toString(),
                                mLatitude,
                                mLongitude
                        )

                        val dbHandler = DatabaseHandler(this)

                        if(mHappyPlaceDetails == null){
                            val addHappyPlace = dbHandler.addHappyPlace(happyPlaceModel)
                            if(addHappyPlace > 0){
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        }else{
                            val updateHappyPlace = dbHandler.updateHappyPlace(happyPlaceModel)
                            if(updateHappyPlace > 0){
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        }

                    }
                }
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){

            if(requestCode == GALLERY_REQUEST_CODE){

                if(data != null){
                    val contentUri = data.data
                    try{

                        val selectedImageBitmap =
                                MediaStore.Images.Media.getBitmap(this.contentResolver, contentUri)
                        saveImageToInternalStorage =
                                saveImageToInternalStorage(selectedImageBitmap)

                        Log.e("saveImage", "Path :: $saveImageToInternalStorage")

                        ivPlaceImage.setImageBitmap(selectedImageBitmap)

                    }catch (e: IOException){

                        println("Error try get image from gallery: ${e.message}")
                        e.printStackTrace()
                        Toast.makeText(
                                this@AddHappyPlaceActivity,
                                "Failed to load image from Gallery!",
                                Toast.LENGTH_SHORT
                        ).show()

                    }
                }

            }else if(requestCode == CAMERA_REQUEST_CODE){

                val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap
                saveImageToInternalStorage =
                        saveImageToInternalStorage(thumbnail)

                Log.e("saveImage", "Path :: $saveImageToInternalStorage")

                ivPlaceImage.setImageBitmap(thumbnail)

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
                    val galleryIntent = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )

                    startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
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

    private fun takePhotoFromCamera(){
        Dexter.withActivity(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        ).withListener(object: MultiplePermissionsListener {

            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if(report!!.areAllPermissionsGranted()){
                    val galleryIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                    startActivityForResult(galleryIntent, CAMERA_REQUEST_CODE)
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

    private fun saveImageToInternalStorage(bitmap: Bitmap) : Uri{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try{

            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()

        }catch (e: IOException){
            println("Error try to save image to directory : ${e.message}")
            e.printStackTrace()
        }

        return Uri.parse(file.absolutePath)
    }
}