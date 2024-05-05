package com.example.newsapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.newsapp.FragmentCommunicator
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentViewNewsBinding


class ViewNewsFragment : Fragment() {
    lateinit var binding: FragmentViewNewsBinding
    private lateinit var communicator: FragmentCommunicator
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewNewsBinding.inflate(inflater, container, false)
        communicator = activity as FragmentCommunicator
        val output1 = arguments?.getString("headLine")
        val output2 = arguments?.getString("authorName")
        val output4 = arguments?.getString("newsDescription")
        val output5 = arguments?.getString("newsDate")
        val output6 = arguments?.getString("newsImage")

        binding.dateNewsShow.text = output5
        binding.descriptionShow.text = output4
        binding.authorNameShow.text = "Author: $output2 "
        binding.headlineNewsShow.text = output1
        Glide.with(requireActivity()).load(output6).into(binding.newsImageShow)
        binding.btnBack.setOnClickListener {
            fragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, HomeFragment())?.commit()
        }
        return binding.root
    }

}