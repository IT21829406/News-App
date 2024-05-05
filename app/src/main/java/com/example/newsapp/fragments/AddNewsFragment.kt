package com.example.newsapp.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.newsapp.FragmentCommunicator
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentAddNewsBinding
import com.example.onlinebeamsandroidapp.NewsDataClass
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class AddNewsFragment : Fragment() {
    private lateinit var binding: FragmentAddNewsBinding
    private lateinit var databaseRef: DatabaseReference
    private lateinit var databaseRef2: DatabaseReference
    private lateinit var communicator: FragmentCommunicator
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddNewsBinding.inflate(inflater, container, false)
        communicator = activity as FragmentCommunicator

        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.add
        binding.proceedBtn.setOnClickListener {
            val newsHeadline = binding.newsHeadingEditText.text.toString()
            val descriptionNews = binding.descriptionEditText.text.toString()
            Log.d(newsHeadline, "news Head Line : ")
            Log.d(descriptionNews, "news description : ")
            if (newsHeadline == "" || descriptionNews == "") {
                communicator.toastMessage("Headline or description cannot be null !")

            } else {
                saveDataToDatabase()
            }

        }
        binding.cancelBtn.setOnClickListener {
            binding.descriptionEditText.setText("")
            binding.newsHeadingEditText.setText("")
            communicator.passLoginData(MyNewsFragment())
        }

        binding.selectImageBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
        return binding.root
    }

    var selectedImageUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            val bitmap =
                MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedImageUri)
            val bitmapDrawable = BitmapDrawable(bitmap)
            binding.addImage.setBackgroundDrawable(bitmapDrawable)

        }
    }

    fun saveDataToDatabase() {
        val username = arguments?.getString("authorName").toString()
        databaseRef = FirebaseDatabase.getInstance().getReference("News All")
        databaseRef2 = FirebaseDatabase.getInstance().getReference("News particular/$username")
        val newsHeadline = binding.newsHeadingEditText.text.toString()
        val descriptionNews = binding.descriptionEditText.text.toString()
        var imgUrl = ""
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val date = current.format(formatter)
        val itemId = databaseRef.push().key

        if (selectedImageUri == null) {
            communicator.toastMessage("Please select an image for the news to continue")
            return
        } else {
            communicator.toastMessage("Wait.Saving Process will take some time.")
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/image/$filename")
            ref.putFile(selectedImageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            imgUrl = uri.toString()
                            val product =
                                NewsDataClass(
                                    itemId,
                                    username,
                                    newsHeadline,
                                    descriptionNews,
                                    imgUrl,
                                    date
                                )
                            databaseRef.child(itemId!!).setValue(product).addOnSuccessListener {
                                databaseRef2.child(itemId!!).setValue(product)
                                    .addOnSuccessListener {
                                        Log.e("saved", "to particular user")
                                    }.addOnFailureListener {
                                        Log.e("saving failed", "to particular user")
                                    }
                                communicator.toastMessage("News saved")
                                binding.newsHeadingEditText.text.clear()
                                binding.descriptionEditText.text.clear()
                                fragmentManager?.beginTransaction()
                                    ?.replace(R.id.fragment_container, HomeFragment())?.commit()

                            }.addOnFailureListener {
                                communicator.toastMessage("Failed to save the news")
                            }
                        }
                }.addOnFailureListener {
                    communicator.toastMessage("Failed to attach image to database")
                }


        }
    }
}
