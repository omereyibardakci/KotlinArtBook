package com.asus.kotlinartbook

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.asus.kotlinartbook.databinding.ActivityMainBinding
import java.lang.Exception


private lateinit var binding: ActivityMainBinding

private lateinit var databaese : SQLiteDatabase
private lateinit var artArrayList: ArrayList<Art>
private lateinit var artAdapter: ArtAdapter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // initialize
        artArrayList = ArrayList<Art>()



        // RecyclerView

        binding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        artAdapter = ArtAdapter(artArrayList)
        binding.recyclerView.adapter = artAdapter





        try {

            databaese = this.openOrCreateDatabase("ART", MODE_PRIVATE,null)

            val cursor = databaese.rawQuery("SELECT * FROM Art",null)
            val artIdIndex = cursor.getColumnIndex("id")
            val artNameIndex = cursor.getColumnIndex("artName")

            while (cursor.moveToNext()){

                val id = cursor.getInt(artIdIndex)
                val name = cursor.getString(artNameIndex)

                val art = Art(id, name)
                artArrayList.add(art)


            }
            artAdapter.notifyDataSetChanged()     // SO IMPORTANT !!!

            cursor.close()


        }catch (e : Exception){
            e.printStackTrace()
        }


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

            val intent = Intent(this@MainActivity, ArtActivity::class.java)

            intent.putExtra("info","new")
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)

    }



}