package com.example.esemkareceipt

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.esemkareceipt.databinding.ActivityHomeBinding
import com.google.android.material.tabs.TabLayout

class HomeActivity : AppCompatActivity() {
    lateinit var bind : ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(bind.root)

        var tl = bind.homeTl
        tl.addTab(tl.newTab().setText("Categories").setIcon(R.drawable.ic_category))
        tl.addTab(tl.newTab().setText("My Profile").setIcon(R.drawable.ic_my_profile))

        var trans = supportFragmentManager.beginTransaction()
        trans.replace(R.id.home_fragment, CategoriesFragment())
        trans.commit()

        tl.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(p0: TabLayout.Tab?) {
                var dat = p0?.position
                when (dat) {
                    0 -> {
                        var trans = supportFragmentManager.beginTransaction()
                        trans.replace(R.id.home_fragment, CategoriesFragment())
                        trans.commit()
                        setSupportActionBar(bind.homeToolbar)
                        supportActionBar?.title = "Categories"
                        var color = ContextCompat.getColor(this@HomeActivity, R.color.red)
                        p0?.icon?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                    }
                    1 -> {
                        var trans = supportFragmentManager.beginTransaction()
                        trans.replace(R.id.home_fragment, ProfileFragment())
                        trans.commit()
                        setSupportActionBar(bind.homeToolbar)
                        supportActionBar?.title = "My Profile"
                        var color = ContextCompat.getColor(this@HomeActivity, R.color.red)
                        p0?.icon?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                    }
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
                var dat = p0?.position
                when (dat) {
                    0 -> {
                        var color = ContextCompat.getColor(this@HomeActivity, R.color.gray)
                        p0?.icon?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                    }
                    1 -> {
                        var color = ContextCompat.getColor(this@HomeActivity, R.color.gray)
                        p0?.icon?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                    }
                }
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {}

        })
    }
}