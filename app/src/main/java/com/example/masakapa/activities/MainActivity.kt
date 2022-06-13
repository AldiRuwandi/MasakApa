package com.example.masakapa.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.masakapa.R
import com.example.masakapa.adapter.MainAdapter
import com.example.masakapa.model.ModelMain
import com.example.masakapa.networking.Api
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity(), MainAdapter.onSelectDatas {

    var mainAdapter: MainAdapter? = null
    var progressDialog: ProgressDialog? = null
    var modelMain: MutableList<ModelMain> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setStatusBar()
        setInitLayout()
        setSearch()

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Mohon Tunggu")
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage("Sedang menampilkan data...")

        categories
    }

    private fun setSearch() {
        val searchPlateId = searcData.context.resources.getIdentifier("android:id/search_plate", null, null)
        val searchPlate = searcData.findViewById<View>(searchPlateId)
        searchPlate.setBackgroundColor(Color.TRANSPARENT)
        searcData.imeOptions = EditorInfo.IME_ACTION_DONE
        searcData.setOnQueryTextListener(object: android.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                mainAdapter?.filter?.filter(newText)
                return true
            }
        })
    }

    private fun setInitLayout() {
        val mLayoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        rvKategori.setLayoutManager(mLayoutManager)
        rvKategori.setHasFixedSize(true)
    }

    private fun setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    private val categories: Unit
        private get() {
            progressDialog!!.show()
            AndroidNetworking.get(Api.CategoriesMakanan)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        try {
                            progressDialog!!.dismiss()
                            val playerArray = response.getJSONArray("categories")
                            for (i in 0 until playerArray.length()) {

                                val temp = playerArray.getJSONObject(i)
                                val dataApi = ModelMain()
                                dataApi.strCategory = temp.getString("strCategory")
                                dataApi.strCategoryThumb = temp.getString("strCategoryThumb")
                                dataApi.strCategoryDescription = temp.getString("strCategoryDescription")
                                modelMain.add(dataApi)
                                showCategories()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            Toast.makeText(this@MainActivity,
                                "Gagal menampilkan data!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onError(anError: ANError) {
                        progressDialog!!.dismiss()
                        Toast.makeText(this@MainActivity,
                            "Tidak ada jaringan internet!", Toast.LENGTH_SHORT).show()
                    }
                })
        }

    private fun showCategories() {
        mainAdapter = MainAdapter(this@MainActivity, modelMain, this)
        rvKategori!!.adapter = mainAdapter
    }

    override fun onSelected(modelMain: ModelMain?) {
        val intent = Intent(this@MainActivity, FilterActivity::class.java)
        intent.putExtra("showFilter", modelMain)
        startActivity(intent)
    }

    companion object {
        //Set Transparent Status bar
        fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
            val win = activity.window
            val winParams = win.attributes
            if (on) {
                winParams.flags = winParams.flags or bits
            } else {
                winParams.flags = winParams.flags and bits.inv()
            }
            win.attributes = winParams
        }
    }
}