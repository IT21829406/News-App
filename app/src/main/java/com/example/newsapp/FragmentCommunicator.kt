package com.example.newsapp

import androidx.fragment.app.Fragment

interface FragmentCommunicator {
    fun toastMessage(message: String)
    fun passLoginData(
        fragment: Fragment
    )

    fun selectMenuItem(
        id: Int?,
    )

    fun passNewsHeadLine(
        newsId: String?,
        authorName: String?,
        fragment: Fragment
    )

    fun passNewsData(
        newsId: String?,
        authorName: String?,
        newsImage: String?,
        newsDescription: String?,
        newsDate: String?,
        newsHeadline: String?,
        fragment: Fragment
    )
}