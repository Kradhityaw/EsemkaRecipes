package com.example.esemkareceipt

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.esemkareceipt.databinding.CategoryCardBinding
import com.example.esemkareceipt.databinding.FragmentCategoriesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.net.URL

class CategoriesFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bind = FragmentCategoriesBinding.inflate(inflater, container, false)
        GlobalScope.launch(Dispatchers.IO) {
            var conn = URL("http://10.0.2.2:5000/api/categories").openStream().bufferedReader().readText()
            var data = JSONArray(conn)

            GlobalScope.launch(Dispatchers.Main) {
                var adapter = object : RecyclerView.Adapter<CatVH>() {
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatVH {
                        var infalte = CategoryCardBinding.inflate(inflater, parent, false)
                        return CatVH(infalte)
                    }

                    override fun getItemCount(): Int {
                        return data.length()
                    }

                    override fun onBindViewHolder(holder: CatVH, position: Int) {
                        var cek = data.getJSONObject(position)
                        holder.binding.catName.text = cek.getString("name")

                        holder.itemView.setOnClickListener {
                            startActivity(Intent(context, RecipeActivity::class.java).apply {
                                putExtra("id", cek.getString("id"))
                                putExtra("name", cek.getString("name"))
                            })
                        }

                        GlobalScope.launch(Dispatchers.IO) {
                            var img = BitmapFactory.decodeStream(URL("http://10.0.2.2:5000/images/categories/${cek.getString("icon")}").openStream())
                            GlobalScope.launch(Dispatchers.Main) {
                                holder.binding.catImg.setImageBitmap(img)
                            }
                        }
                    }
                }
                bind.catRv.adapter = adapter
                bind.catRv.layoutManager = GridLayoutManager(context, 2)
            }
        }
        return bind.root
    }

    class CatVH(var binding: CategoryCardBinding) : RecyclerView.ViewHolder(binding.root)
}