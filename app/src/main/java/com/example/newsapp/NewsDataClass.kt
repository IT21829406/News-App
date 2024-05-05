package com.example.onlinebeamsandroidapp

data class NewsDataClass(
    val newsId: String? = "",
    val autherName: String? = "",
    val newsHeading: String? = "",
    val newsDescription: String? = "",
    val itemImage: String? = "",
    val dateNews: String? = "",
)

data class LoginDataClass(
    //val id:String?="",
    val authorName: String? = "",
    val password: String? = "",
)

