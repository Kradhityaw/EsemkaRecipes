package com.example.esemkareceipt

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.esemkareceipt.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    lateinit var bind : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.loginUsername.setText("bmoret6")
        bind.loginPass.setText("vU3m9x")

        bind.loginBtn.setOnClickListener {
            auth()
        }

        bind.loginSignup.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    fun auth() {
        GlobalScope.launch(Dispatchers.IO) {
            var conn = URL("http://10.0.2.2:5000/api/sign-in").openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")

            conn.outputStream.write(
                JSONObject().apply {
                    put("username", bind.loginUsername.text)
                    put("password", bind.loginPass.text)
                }.toString().toByteArray()
            )

            if (conn.responseCode in 200..299) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                    finish()
                }
            }
            else {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Username Or Password Incorrect", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}