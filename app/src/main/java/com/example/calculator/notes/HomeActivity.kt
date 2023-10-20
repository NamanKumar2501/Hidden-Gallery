package com.example.calculator.notes

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.calculator.LockerActivity
import com.example.calculator.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class HomeActivity :  AppCompatActivity(), NoteAdapter.MyOnClickListener {

    private lateinit var database : DatabaseReference
    private lateinit var dataRecyclerview : RecyclerView
    private lateinit var dataArrayList : ArrayList<Data>
    private lateinit var tempArrayList : ArrayList<Data>

    private var mAuth: FirebaseAuth? = null
    private lateinit var auth: FirebaseAuth
    private var onlineUserId = ""
    private lateinit var rvSearchView : SearchView

    private lateinit var loadCreate: TextView
    private lateinit var pbload : ProgressBar
    private lateinit var noInternet : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        val fab = findViewById<TextView>(R.id.fab_add_note)
        auth = FirebaseAuth.getInstance()

        fab.setOnClickListener{
            startActivity(Intent(this, CreateNoteActivity::class.java))
        }
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        toolbar.setNavigationOnClickListener(View.OnClickListener {
            val main = Intent(applicationContext, LockerActivity::class.java)
            startActivity(main)
        })

        loadCreate = findViewById(R.id.load_create)
        pbload = findViewById(R.id.pbload)
        noInternet = findViewById(R.id.tv_internet)

        loadCreate.visibility = View.GONE
        pbload.visibility = View.VISIBLE

        rvSearchView = findViewById(R.id.rv_searchView)
        rvSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(newText: String?): Boolean {
                tempArrayList.clear()
                val searchText = newText!!.lowercase(Locale.getDefault())
                if (searchText.isNotEmpty()){
                    dataArrayList.forEach {
                        if (it.title!!.lowercase(Locale.getDefault()).contains(searchText) ||
                            it.note!!.lowercase(Locale.getDefault()).contains(searchText)){
                            tempArrayList.add(it)
                            dataRecyclerview.adapter = NoteAdapter(tempArrayList,this@HomeActivity)
                            val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                            dataRecyclerview.layoutManager = staggeredGridLayoutManager
                        }
                    }
                    dataRecyclerview.adapter!!.notifyDataSetChanged()
                }else{
                    tempArrayList.clear()
                    tempArrayList.addAll(dataArrayList)
                    dataRecyclerview.adapter!!.notifyDataSetChanged()
                }
                return false
            }
        })


        dataRecyclerview = findViewById(R.id.recyclerview)
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        dataRecyclerview.layoutManager = staggeredGridLayoutManager
        dataRecyclerview.setHasFixedSize(true)
        dataArrayList = arrayListOf()
        tempArrayList = arrayListOf()
        getUserData()
    }




    private fun getUserData() {

        mAuth = FirebaseAuth.getInstance()
        onlineUserId = mAuth!!.currentUser?.uid.toString()

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        database =  FirebaseDatabase.getInstance().getReference(uid).child("note")
        database.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (userSnapshot in snapshot.children){
                        val data = userSnapshot.getValue(Data::class.java)
                        dataArrayList.add(0,data!!)
                    }
                    pbload.visibility = View.GONE
                    noInternet.visibility = View.GONE
                    dataRecyclerview.adapter = NoteAdapter(dataArrayList,this@HomeActivity)
                }else{
                    loadCreate.visibility = View.VISIBLE
                    pbload.visibility = View.GONE
                    noInternet.visibility = View.GONE
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    override fun onClick(position: Int) {
        val intent = Intent(this@HomeActivity, UpdateNoteActivity::class.java)
        intent.putExtra("title",dataArrayList[position].title)
        intent.putExtra("note",dataArrayList[position].note)
        intent.putExtra("id",dataArrayList[position].id)
        startActivity(intent)
    }


}