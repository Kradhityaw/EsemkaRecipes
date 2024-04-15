package com.example.esemkareceipt

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.esemkareceipt.databinding.ActivityRegisterBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.LocalTime

class RegisterActivity : AppCompatActivity() {
    lateinit var bind : ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.sigmupDatebirth.setOnClickListener {
            getDate()
        }

        bind.signupLogin.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
            finish()
        }

        bind.signupBtn.setOnClickListener {
//            Toast.makeText(this@RegisterActivity, "${bind.signupDateTv.text}T${LocalTime.now()}", Toast.LENGTH_SHORT).show()
            if (bind.signupConfPass.text.toString() != bind.signupPass.text.toString() || bind.signupPass.text.toString() != bind.signupConfPass.text.toString()) {
                Toast.makeText(this@RegisterActivity, "Confirm Password Incorrect", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (bind.signupUsername.text!!.isEmpty() || bind.signupFullName.text!!.isEmpty() || bind.signupDateTv.text == "Date of Birth" || bind.signupPass.text!!.isEmpty() || bind.signupConfPass.text!!.isEmpty()) {
                Toast.makeText(this@RegisterActivity, "Make sure input not Empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            userRegister()
        }
    }

    fun userRegister() {
        GlobalScope.launch(Dispatchers.IO) {
            var conn = URL("http://10.0.2.2:5000/api/sign-up").openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")

            conn.outputStream.write(
                JSONObject().apply {
                    put("username", bind.signupUsername.text)
                    put("fullName", bind.signupFullName.text)
                    put("password", bind.signupConfPass.text)
                    put("dateOfBirth", "${bind.signupDateTv.text}T${LocalTime.now()}")
                }.toString().toByteArray()
            )
            
            if (conn.responseCode in 200..299) {
                runOnUiThread {
                    Toast.makeText(this@RegisterActivity, "Register Success", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finish()
                }
            }
            else {
                runOnUiThread {
                    Log.d("err", conn.errorStream.bufferedReader().readText())
//                    Toast.makeText(this@RegisterActivity, "${conn.errorStream.bufferedReader().readText()}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun getDate() {
        var instance = Calendar.getInstance()
        var calendar = DatePickerDialog(this@RegisterActivity)
        calendar.setOnDateSetListener(object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
                var correctMonth = p2 + 1
                var tanggal = p3.toString()
                var bulan = p2.toString()

                if (p3 < 10) {
                    tanggal = "0${p3}"
                }

                if (correctMonth < 10) {
                    var bln = p2 + 1
                    bulan = "0${bln}"
                }
                else {
                    bulan = correctMonth.toString()
                }

                instance.set(Calendar.YEAR, p1)
                instance.set(Calendar.MONTH, correctMonth)
                instance.set(Calendar.DAY_OF_MONTH, p3)

                bind.signupDateTv.text = "${p1}-${bulan}-${tanggal}"
                bind.signupDateTv.setTextColor(Color.BLACK)
            }
        })
        calendar.show()
    }
}