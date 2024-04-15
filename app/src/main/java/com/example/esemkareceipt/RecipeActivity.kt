package com.example.esemkareceipt

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.esemkareceipt.databinding.ActivityRecipeBinding
import com.example.esemkareceipt.databinding.RecipeCardBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.net.URL

class RecipeActivity : AppCompatActivity() {
    lateinit var bind: ActivityRecipeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityRecipeBinding.inflate(layoutInflater)
        setContentView(bind.root)

        setSupportActionBar(bind.recipeTb)
        supportActionBar?.title = "${intent.getStringExtra("name")}"

        bind.recipeTb.setNavigationOnClickListener {
            finish()
        }

        getData()
    }

    fun getData() {
        var data = intent.getStringExtra("id")

        GlobalScope.launch(Dispatchers.IO) {
            var conn = URL("http://10.0.2.2:5000/api/recipes?categoryId=$data").openStream().bufferedReader().readText()
            var json = JSONArray(conn)


            if (json.length() >= 1) {
                runOnUiThread {
                    var adapter = object : RecyclerView.Adapter<RecipeVH>() {
                        override fun onCreateViewHolder(
                            parent: ViewGroup,
                            viewType: Int
                        ): RecipeVH {
                            var infalte = RecipeCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                            return RecipeVH(infalte)
                        }

                        override fun getItemCount(): Int {
                            return json.length()
                        }

                        override fun onBindViewHolder(holder: RecipeVH, position: Int) {
                            var cek = json.getJSONObject(position)
                            bind.recipeEt.setHint("Search ${json.length()} recipes")
                            holder.binding.recipeName.text = cek.getString("title")
                            holder.binding.recipeDesc.text = cek.getString("description")

                            holder.itemView.setOnClickListener {
                                startActivity(Intent(this@RecipeActivity, DetailActivity::class.java).apply {
                                    putExtra("recipeid", cek.getString("id"))
                                })
                            }

                            GlobalScope.launch(Dispatchers.IO) {
                                var url = BitmapFactory.decodeStream(URL("http://10.0.2.2:5000/images/recipes/${cek.getString("image")}").openStream())
                                runOnUiThread {
                                    holder.binding.recipeImg.setImageBitmap(url)
                                }
                            }
                        }
                    }
                    bind.recipeRv.adapter = adapter
                    bind.recipeRv.layoutManager = LinearLayoutManager(this@RecipeActivity)
                    bind.recipeEt.addTextChangedListener {
                        GlobalScope.launch(Dispatchers.IO) {
                            var url = URL("http://10.0.2.2:5000/api/recipes?categoryId=$data&search=$it").openStream().bufferedReader().readText()
                            var dat = JSONArray(url)
                            runOnUiThread {
                                var apter = object : RecyclerView.Adapter<RecipeVH>() {
                                    override fun onCreateViewHolder(
                                        parent: ViewGroup,
                                        viewType: Int
                                    ): RecipeVH {
                                        var inflter = RecipeCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                                        return RecipeVH(inflter)
                                    }

                                    override fun getItemCount(): Int {
                                        return dat.length()
                                    }

                                    override fun onBindViewHolder(holder: RecipeVH, position: Int) {
                                        var cek = dat.getJSONObject(position)
                                        holder.binding.recipeName.text = cek.getString("title")
                                        holder.binding.recipeDesc.text = cek.getString("description")

                                        holder.itemView.setOnClickListener {
                                            startActivity(Intent(this@RecipeActivity, DetailActivity::class.java).apply {
                                                putExtra("recipeid", cek.getString("id"))
                                            })
                                        }

                                        GlobalScope.launch(Dispatchers.IO) {
                                            var url = BitmapFactory.decodeStream(URL("http://10.0.2.2:5000/images/recipes/${cek.getString("image")}").openStream())
                                            runOnUiThread {
                                                holder.binding.recipeImg.setImageBitmap(url)
                                            }
                                        }
                                    }
                                }
                                bind.recipeRv.adapter = apter
                                bind.recipeRv.layoutManager = LinearLayoutManager(this@RecipeActivity)
                            }
                        }
                    }
                }
            }
            else {
                bind.recipeLl1.visibility = View.VISIBLE
                bind.recipeLl2.visibility = View.INVISIBLE
            }
        }
    }

    class RecipeVH(var binding: RecipeCardBinding) : RecyclerView.ViewHolder(binding.root)
}