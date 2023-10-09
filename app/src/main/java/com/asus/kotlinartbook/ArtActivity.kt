package com.asus.kotlinartbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.asus.kotlinartbook.databinding.ActivityArtBinding
import com.asus.kotlinartbook.databinding.ActivityMainBinding


private lateinit var binding: ActivityArtBinding

class ArtActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArtBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)



    }



    fun save (view : View){

    }


    fun selectImage(View : View){

    }


}