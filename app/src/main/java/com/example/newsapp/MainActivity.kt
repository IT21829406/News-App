package com.example.newsapp

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.newsapp.databinding.ActivityMainBinding
import com.example.newsapp.fragments.AddNewsFragment
import com.example.newsapp.fragments.HomeFragment
import com.example.newsapp.fragments.MyNewsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), FragmentCommunicator {
    lateinit var binding: ActivityMainBinding
    val Home_Fragment = HomeFragment()
    val My_News_Fragment = MyNewsFragment()
    val Add_News_Fragment = AddNewsFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        passLoginData(Home_Fragment)
        val positiveButtonClick = { dialog: DialogInterface, which: Int ->
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.add -> {
                    passLoginData(Add_News_Fragment)
                }

                R.id.home -> {
                    setTheme(android.R.style.Holo_Light_ButtonBar)
                    passLoginData(Home_Fragment)
                }

                R.id.viewPost -> {
                    passLoginData(My_News_Fragment)
                }

                R.id.logOut -> {
                    val builderExit =
                        AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                            .setTitle("News App")
                            .setMessage("Do you want to Logout ?")
                            .setPositiveButton("Logout") { dialog: DialogInterface, which: Int ->
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .setNegativeButton("cancel", null)
                    val dialog1 = builderExit.create()
                    dialog1.setCancelable(false)
                    dialog1.setCanceledOnTouchOutside(false)
                    dialog1.show()
                    dialog1.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                    dialog1.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK)

                }
            }
            true
        }
    }

    override fun toastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun passLoginData(fragment: Fragment) {
        val bundle = Bundle()
        val authorName = intent.getStringExtra("username")
        bundle.putString("authorName", authorName)
        val transaction = supportFragmentManager.beginTransaction()
        fragment.arguments = bundle
        transaction.replace(R.id.fragment_container, fragment).addToBackStack(null).commit()

    }

    override fun selectMenuItem(id: Int?) {
        if (id != null) {
            binding.bottomNavigation.selectedItemId = id
        };
    }

    override fun passNewsHeadLine(headLine: String?, authorName: String?, fragment: Fragment) {
        val bundle = Bundle()
        bundle.putString("headLine", headLine)
        bundle.putString("authorName", authorName)
        val transaction = supportFragmentManager.beginTransaction()
        fragment.arguments = bundle
        transaction.replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
    }

    override fun passNewsData(
        newsId: String?,
        authorName: String?,
        newsImage: String?,
        newsDescription: String?,
        newsDate: String?,
        headLine: String?,
        fragment: Fragment
    ) {
        val bundle = Bundle()
        bundle.putString("headLine", headLine)
        bundle.putString("authorName", authorName)
        bundle.putString("newsID", newsId)
        bundle.putString("newsDescription", newsDescription)
        bundle.putString("newsDate", newsDate)
        bundle.putString("newsImage", newsImage)
        val transaction = supportFragmentManager.beginTransaction()
        fragment.arguments = bundle
        transaction.replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
    }


}