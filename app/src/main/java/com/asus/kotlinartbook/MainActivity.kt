package com.asus.kotlinartbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.asus.kotlinartbook.databinding.ActivityMainBinding


private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        //val inflater : MenuInflater = menuInflater
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.art_menu,menu)

        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // It will go to the image adding page
        // ArtActivity sayfasına gidecek  intent ile

        if(item.itemId == R.id.addArt){

            val intent = Intent(this@MainActivity,ArtActivity::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)

    }



}