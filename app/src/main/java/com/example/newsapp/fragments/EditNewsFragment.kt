package com.example.newsapp.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.newsapp.FragmentCommunicator
import com.example.newsapp.databinding.FragmentEditNewsBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class EditNewsFragment : Fragment() {
    private lateinit var binding: FragmentEditNewsBinding
    private lateinit var databaseRef: DatabaseReference
    private lateinit var databaseRef2: DatabaseReference
    private lateinit var communicator: FragmentCommunicator
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditNewsBinding.inflate(inflater, container, false)

        val output1 = arguments?.getString("headLine")
        val output2 = arguments?.getString("authorName")
        val output4 = arguments?.getString("newsDescription")
        val output5 = arguments?.getString("newsDate")
        val output6 = arguments?.getString("newsImage")
        binding.descriptionEditText.setText(output4)
        binding.newsHeadingEditText.setText(output1)
        Glide.with(requireActivity()).load(output6).into(binding.addImage)

        communicator = activity as FragmentCommunicator


        binding.proceedBtn.setOnClickListener {
            val newsHeadline = binding.newsHeadingEditText.text.toString()
            val newsDescription = binding.descriptionEditText.text.toString()
            val newsID = arguments?.getString("newsID")
            dataAdd(newsID!!, output2!!, newsHeadline, newsDescription, output6!!, output5!!)
        }

        binding.selectImageBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        binding.cancelBtn.setOnClickListener {
            communicator.passLoginData(MyNewsFragment())
        }

        binding.deleteBtn.setOnClickListener {
            deleteData()
        }

        return binding.root
    }

    var selectedImageUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            binding.addImage.setImageResource(0)
            selectedImageUri = data.data
            val bitmap =
                MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedImageUri)
            val bitmapDrawable = BitmapDrawable(bitmap)

            binding.addImage.setBackgroundDrawable(bitmapDrawable)

        }
    }

    private fun dataAdd(
        newsId: String,
        authorName: String,
        newsHeading: String,
        newsDescription: String,
        itemImage: String,
        dateNews: String
    ) {


        if (newsId != null) {
            communicator.toastMessage("Wait saving process will take some time")
            if (selectedImageUri != null) {
                val filename = UUID.randomUUID().toString()
                val ref = FirebaseStorage.getInstance().getReference("/image/$filename")
                ref.putFile(selectedImageUri!!)
                    .addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.metadata!!.reference!!.downloadUrl
                            .addOnSuccessListener { uri ->
                                val image = uri.toString()
                                updateData(
                                    newsId,
                                    authorName,
                                    newsHeading,
                                    newsDescription,
                                    image,
                                    dateNews
                                )
                                communicator.passNewsHeadLine(newsId, authorName, MyNewsFragment())
                                communicator.toastMessage("Successfully Updated")

                            }
                    }
            } else {

                val image = itemImage
                communicator.toastMessage("Photo saved")
                updateData(newsId, authorName, newsHeading, newsDescription, image, dateNews)
                communicator.passNewsHeadLine(newsId, authorName, MyNewsFragment())
                communicator.toastMessage("Successfully Updated")
            }
        }
    }

    private fun updateData(
        newsId: String,
        authorName: String,
        newsHeading: String,
        newsDescription: String,
        itemImage: String,
        dateNews: String
    ) {
        val username = arguments?.getString("authorName").toString()
        databaseRef = FirebaseDatabase.getInstance().getReference("News particular/$username")
        databaseRef2 = FirebaseDatabase.getInstance().getReference("News All")
        val user = mapOf<String, String>(
            "newsId" to newsId,
            "autherName" to authorName,
            "itemImage" to itemImage,
            "newsHeading" to newsHeading,
            "newsDescription" to newsDescription,
            "dateNews" to dateNews
        )
        databaseRef.child(newsId).updateChildren(user).addOnSuccessListener {
            databaseRef2.child(newsId).updateChildren(user).addOnSuccessListener {

            }

        }.addOnFailureListener {

            communicator.toastMessage("Failed to Update")

        }


    }

    private fun deleteData() {
        val itemId = arguments?.getString("newsID").toString()
        val username = arguments?.getString("authorName").toString()
        databaseRef = FirebaseDatabase.getInstance().getReference("News particular/$username")
        databaseRef2 = FirebaseDatabase.getInstance().getReference("News All")
        databaseRef.child(itemId).get().addOnSuccessListener {
            databaseRef.child(itemId).removeValue().addOnSuccessListener {
                databaseRef2.child(itemId).get().addOnSuccessListener {
                    databaseRef2.child(itemId).removeValue()
                }
                communicator.toastMessage("Successfully deleted")

                communicator.passNewsHeadLine(itemId, username, MyNewsFragment())


            }.addOnFailureListener {
                communicator.toastMessage("Errors in deleting the data.")
            }

        }
        communicator.passNewsHeadLine(itemId, username, MyNewsFragment())
    }


}