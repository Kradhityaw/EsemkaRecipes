package com.example.esemkareceipt

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.esemkareceipt.databinding.ActivityDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class DetailActivity : AppCompatActivity() {
    lateinit var bind: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(bind.root)

        GlobalScope.launch(Dispatchers.IO) {
            var likee = URL("http://10.0.2.2:5000/api/me/liked-recipes").openStream().bufferedReader().readText()
            var conn = URL("http://10.0.2.2:5000/api/recipes/detail/${intent.getStringExtra("recipeid")}").openStream().bufferedReader().readText()
            var json = JSONObject(conn)
            var image = BitmapFactory.decodeStream(URL("http://10.0.2.2:5000/images/recipes/${json.getString("image")}").openStream())
            var img = BitmapFactory.decodeStream(URL("http://10.0.2.2:5000/images/categories/${json.getJSONObject("category").getString("icon")}").openStream())
            var desc = json.getJSONArray("ingredients")
            var steps = json.getJSONArray("steps")

            var liked = JSONArray(likee)

            runOnUiThread {
                for (i in 0 until liked.length()) {
                    var dat = liked.getJSONObject(i)
                    if (dat.getString("id") == intent.getStringExtra("recipeid")) {
                        if (dat.getBoolean("isLike") == true) {
                            bind.detailLike.setImageResource(R.drawable.liked)
                        }
                        else {
                            bind.detailLike.setImageResource(R.drawable.like)
                        }
                    }
                }

                setSupportActionBar(bind.detailTb)
                supportActionBar?.title = json.getString("title")
                bind.detailTb.setNavigationOnClickListener {
                    setResult(Activity.RESULT_OK, Intent().apply {
                        putExtra("idmenu", json.getString("id"))
                    })
                    finish()
                }
                bind.detailName.text = json.getString("title")
                bind.detailTime.text = "Cooking Time Estimate: ${json.getString("cookingTimeEstimate")} Minutes"
                bind.detailPrice.text = "Price Estimate: $${json.getString("priceEstimate")}"
                bind.detailImg.setImageBitmap(image)
                bind.detailCategory.text = json.getJSONObject("category").getString("name")
                bind.detailCategoryImg.setImageBitmap(img)
                bind.detailDesc.text = json.getString("description")
//                var builder = StringBuilder()
                var empty = ""
                for (data in 0 until desc.length()) {
                    var json = desc.getString(data)
                    empty += "$json\n"
//                    builder.append(json).append("\n")
                }
                bind.detailIngredients.text = empty

                var stp = ""
                for (dat in 0 until steps.length()) {
                    var js = steps.getString(dat)
                    stp += "$js\n"
                }
                bind.detailSteps.text = stp

                bind.detailLike.setOnClickListener {
                    GlobalScope.launch(Dispatchers.IO) {
                        var likeconn = URL("http://10.0.2.2:5000/api/recipes/like-recipe?recipeId=${intent.getStringExtra("recipeid")}").openConnection() as HttpURLConnection
                        likeconn.requestMethod = "GET"

                        if (likeconn.responseCode in 200..299) {
                            if (likeconn.inputStream.bufferedReader().readText() == "true") {
                                bind.detailLike.setImageResource(R.drawable.liked)
                            }
                            else {
                                bind.detailLike.setImageResource(R.drawable.like)
                            }
                        }
                    }
                }
            }
        }

    }
}