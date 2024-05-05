package com.example.newsapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.FragmentCommunicator
import com.example.newsapp.R
import com.example.newsapp.adaptors.CompleteNewsAdaptor
import com.example.newsapp.databinding.FragmentMyNewsBinding
import com.example.onlinebeamsandroidapp.NewsDataClass
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class MyNewsFragment : Fragment() {
    lateinit var binding: FragmentMyNewsBinding
    private lateinit var databaseRef: DatabaseReference
    private lateinit var communicator: FragmentCommunicator
    private lateinit var itemArrayList: ArrayList<NewsDataClass>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyNewsBinding.inflate(inflater, container, false)
        communicator = activity as FragmentCommunicator
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.viewPost

        val layoutManager = LinearLayoutManager(activity)
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.setHasFixedSize(true)
        itemArrayList = arrayListOf<NewsDataClass>()
        getUserData()
        return binding.root
    }

    private fun getUserData() {
        val username = arguments?.getString("authorName").toString()
        databaseRef = FirebaseDatabase.getInstance().getReference("News particular/$username")
        val query: Query = databaseRef.orderByChild("$username/dateNews")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    for (userSnapshot in snapshot.children) {

                        val item = userSnapshot.getValue(NewsDataClass::class.java)
                        itemArrayList.add(item!!)

                    }
                    val mAdapter = CompleteNewsAdaptor(itemArrayList, this@MyNewsFragment)
                    binding.recyclerView.adapter = mAdapter
                    binding.progressBar.visibility = View.GONE
                    mAdapter.setOnItemClickListener(object :
                        CompleteNewsAdaptor.onItemClickListener {
                        override fun onItemClick(position: Int) {
                            communicator.passNewsData(
                                itemArrayList[position].newsId,
                                itemArrayList[position].autherName,
                                itemArrayList[position].itemImage,
                                itemArrayList[position].newsDescription,
                                itemArrayList[position].dateNews,
                                itemArrayList[position].newsHeading,
                                EditNewsFragment()
                            )
                        }

                    })


                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

    }


}