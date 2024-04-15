package com.example.esemkareceipt

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ActionProvider.VisibilityListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.VisibilityPropagation
import com.example.esemkareceipt.databinding.FragmentProfileBinding
import com.example.esemkareceipt.databinding.LikedCardBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class ProfileFragment : Fragment() {
    lateinit var bind: FragmentProfileBinding
    lateinit var jsonsArrays: JSONArray
    var launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {it ->
        if (it.resultCode == Activity.RESULT_OK) {
//            for (i in 0 until jsonsArrays.length()) {
//                var get = jsonsArrays.getJSONObject(i)
//                if (get.getString("id") == it.data?.getStringExtra("idmenu")) {
//                    jsonsArrays.remove(i)
//                    Log.d("data", jsonsArrays.toString())
//                }
//            }
            var trans = activity?.supportFragmentManager?.beginTransaction()
            trans?.replace(R.id.home_fragment, ProfileFragment())
            trans?.commit()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentProfileBinding.inflate(inflater, container, false)
        GlobalScope.launch(Dispatchers.IO) {
            var conn = URL("http://10.0.2.2:5000/api/me/liked-recipes").openStream().bufferedReader().readText()
            var profileConn = URL("http://10.0.2.2:5000/api/me").openStream().bufferedReader().readText()
            var pp = BitmapFactory.decodeStream(URL("http://10.0.2.2:5000/images/profiles/${JSONObject(profileConn).getString("image")}").openStream())
            var json = JSONArray(conn)
            jsonsArrays = json

            if (jsonsArrays.length() == 0) {
                GlobalScope.launch(Dispatchers.Main) {
                    bind.profileName.text = "Hello, ${JSONObject(profileConn).getString("fullName")}"
                    bind.profileImg.setImageBitmap(pp)
                    bind.textView.visibility = View.VISIBLE
                    bind.textView2.visibility = View.VISIBLE
                }
            }
            else {
                GlobalScope.launch(Dispatchers.Main) {
                    bind.profileName.text = "Hello, ${JSONObject(profileConn).getString("fullName")}"
                    bind.profileImg.setImageBitmap(pp)
                    var adapter = object : RecyclerView.Adapter<LikeVH>() {
                        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikeVH {
                            var infalte = LikedCardBinding.inflate(inflater, parent, false)
                            return LikeVH(infalte)
                        }

                        override fun getItemCount(): Int {
                            return jsonsArrays.length()
                        }

                        override fun onBindViewHolder(holder: LikeVH, position: Int) {
                            var data = jsonsArrays.getJSONObject(position)

                            holder.itemView.setOnClickListener {
                                launcher.launch(Intent(context, DetailActivity::class.java).apply {
                                    putExtra("recipeid", data.getString("id"))
                                })
                            }

                            GlobalScope.launch(Dispatchers.IO) {
                                var img = BitmapFactory.decodeStream(URL("http://10.0.2.2:5000/images/recipes/${data.getString("image")}").openStream())
                                GlobalScope.launch(Dispatchers.Main) {
                                    holder.binding.likeImg.setImageBitmap(img)
                                }
                            }
                        }
                    }
                    bind.profileLikerv.adapter = adapter
                    var layout = object : GridLayoutManager(context, 2) {
                        override fun canScrollVertically(): Boolean {
                            return false
                        }
                    }
                    bind.profileLikerv.layoutManager = layout
                }
            }
        }
        return bind.root
    }

    class LikeVH(var binding: LikedCardBinding) : RecyclerView.ViewHolder(binding.root)
}