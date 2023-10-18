package com.asus.kotlinartbook

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.asus.kotlinartbook.databinding.ActivityArtBinding
import com.asus.kotlinartbook.MainActivity
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import java.lang.Exception


private lateinit var binding: ActivityArtBinding

private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
private lateinit var permissionLauncher: ActivityResultLauncher<String>

var selectedBitmap : Bitmap? = null

private lateinit var database : SQLiteDatabase

class ArtActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArtBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        database = this.openOrCreateDatabase("ART",Context.MODE_PRIVATE,null)

        registerLauncher()


        val intent = intent
        val info = intent.getStringExtra("info")

        if(info.equals("new")){
            binding.button.visibility = View.VISIBLE

        }else{

            binding.button.visibility = View.INVISIBLE

            val selectedId = intent.getIntExtra("id",1)

            val cursor = database.rawQuery("SELECT * FROM Art WHERE id = ?", arrayOf(selectedId.toString()))

            val artNameIndex = cursor.getColumnIndex("artName")
            val artistNameIndex = cursor.getColumnIndex("artistName")
            val yearIndex = cursor.getColumnIndex("year")
            val imageIndex = cursor.getColumnIndex("image")

            while (cursor.moveToNext()){
                binding.editTextArtName.setText(cursor.getString(artNameIndex))
                binding.editTextArtistName.setText(cursor.getString(artistNameIndex))
                binding.editTextYear.setText(cursor.getString(yearIndex))

                val byteArray = cursor.getBlob(imageIndex)
                val bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                binding.imageView.setImageBitmap(bitmap)

            }
            cursor.close()

        }


    }



    fun save (view : View){

        val artName = binding.editTextArtName.text.toString()
        val artistName = binding.editTextArtistName.text.toString()
        val year = binding.editTextYear.text.toString()

        if(selectedBitmap != null){

            val smallBitmap = makeSmallerBitmap(selectedBitmap!!,300)

            // Saved as SQL by converting it into smallBitmap Byte Array.
            // smallBitmap'i ByteArray haline çevirerek  SQL' kaydedicez.

            val outputStream = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteArray = outputStream.toByteArray()


            try {

                database.execSQL("CREATE TABLE IF NOT EXISTS Art(id INTEGER PRIMARY KEY,artName VARCHAR, artistName VARCHAR, year VARCHAR, image BLOB)")

                val sqlString = "INSERT INTO Art (artName, artistName, year, image) VALUES (?, ?, ?, ?)"
                val statement = database.compileStatement(sqlString)
                statement.bindString(1,artName)
                statement.bindString(2,artistName)
                statement.bindString(3,year)
                statement.bindBlob(4,byteArray)

                statement.execute()


            }catch (e : Exception){
                e.printStackTrace()
            }


            val intent = Intent(this@ArtActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)         // Closes previously opened activities.
            startActivity(intent)




        }else{
            Toast.makeText(this@ArtActivity,"Please choose a image",Toast.LENGTH_LONG).show()
        }





    }


    fun selectImage(view : View){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){

            if(ContextCompat.checkSelfPermission(this@ArtActivity,Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this@ArtActivity,Manifest.permission.READ_MEDIA_IMAGES)){
                    Snackbar.make(view,"Give permission for go to gallary!",Snackbar.LENGTH_INDEFINITE).setAction("Give permission!",View.OnClickListener {
                        // reguest permission
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }).show()



                }else{
                    // reguest permission,
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)

                }

            }else{
                // go to galary
                val intentToGallary = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallary)
            }

        }else{

            if(ContextCompat.checkSelfPermission(this@ArtActivity,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this@ArtActivity,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"Give permission for go to gallary!",Snackbar.LENGTH_INDEFINITE).setAction("Give permission!",View.OnClickListener {
                        // reguest permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()



                }else{
                    // reguest permission,
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                }

            }else{
                // go to galary
                val intentToGallary = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallary)
            }

        }


    }




    fun registerLauncher(){

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
            if(result.resultCode == RESULT_OK){
                // go to gallary
                val intentFromResult = result.data
                if (intentFromResult != null){

                    val imageDataUri = intentFromResult.data
                    try {

                        if (Build.VERSION.SDK_INT >= 28){
                            val source = ImageDecoder.createSource(this@ArtActivity.contentResolver,imageDataUri!!)
                            selectedBitmap =  ImageDecoder.decodeBitmap(source)
                            binding.imageView.setImageBitmap(selectedBitmap)
                        }else{
                            selectedBitmap = MediaStore.Images.Media.getBitmap(this@ArtActivity.contentResolver,imageDataUri)
                            binding.imageView.setImageBitmap(selectedBitmap)

                        }


                    }catch (e: Exception){
                        e.printStackTrace()
                    }

                }
            }

        }


        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){result ->

            if(result){
                // permisiion granted
                val intentToGallary = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallary)

            }else{
                // permission denied
                Toast.makeText(this@ArtActivity,"Permission needed for go to gallary!",Toast.LENGTH_LONG).show()
            }

        }

    }





    fun makeSmallerBitmap(image : Bitmap , maximumSize : Int) : Bitmap {

        var width = image.width
        var height = image.height

        val bitmapRatio : Double = width.toDouble() / height.toDouble()

        if(bitmapRatio>1){
            // landscape image or horizontal image

            width = maximumSize
            height = (width / bitmapRatio).toInt()

        }else{
            // partrait image or vertical image

            height = maximumSize
            width = (height * bitmapRatio).toInt()

        }

        return Bitmap.createScaledBitmap(image,width,height,true)

    }





}