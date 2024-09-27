package com.example.mobiletest.UI.Home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobiletest.Datas.API.ApiService
import com.example.mobiletest.Datas.Model.APIResModel
import com.example.mobiletest.Datas.Model.ItemModel
import com.example.mobiletest.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var apiService: ApiService

    private lateinit var adapter: HomeAdapter

    // Handler được sử dụng để trì hoãn việc gọi API
    private val handler = Handler(Looper.getMainLooper())

    // Runnable để trì hoãn việc tìm kiếm
    private var searchRunnable: Runnable? = null

    // Const
    private val apiKey = "HisEbNchhPdke-LKMeJToYhcUiP3SlLY-xlKylQdfds"
    private val lang = "vi"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //adapter
        adapter = HomeAdapter(listOf(), "")
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter

        adapter.setOnItemClickListener(object : HomeAdapter.onItemClickListener {
            override fun onItemClicked(item: ItemModel) {
                showOnGoogleMap(item.position.lat, item.position.lng)
            }
        })

        // Khởi tạo Retrofit để gọi API
        val retrofit = Retrofit.Builder()
            .baseUrl("https://geocode.search.hereapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        //searchView
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(submitInput: String?): Boolean {
                // Ẩn bàn phím
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.searchView.windowToken, 0)

                binding.searchView.clearFocus()
                adapter.updateData(listOf(), "")

                return true
            }

            override fun onQueryTextChange(dataInput: String): Boolean {
//                Log.d("checkInPut123", dataInput)
                // Xóa bỏ Runnable hiện tại nếu người dùng tiếp tục nhập từ khóa mới
                searchRunnable?.let { handler.removeCallbacks(it) }

                if (dataInput.isBlank()) {
                    adapter.updateData(listOf(), "")
                } else {
                    // Tạo một Runnable mới để gọi API sau 1 giây
                    searchRunnable = Runnable {
                        callApi(apiService, dataInput, adapter)
                    }
                    // Đưa Runnable vào Handler với độ trễ 1 giây
                    handler.postDelayed(searchRunnable!!, 1000)
                }
                return true
            }
        })
    }

    // Hủy bỏ Runnable khi hủy Activity
    override fun onDestroy() {
        super.onDestroy()
        searchRunnable?.let { handler.removeCallbacks(it) }
    }

    private fun showOnGoogleMap(lat: Double, lng: Double) {
        val uri = "https://www.google.com/maps/dir/?api=1&destination=$lat,$lng"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.setPackage("com.google.android.apps.maps")
        startActivity(intent)
    }

    private fun callApi(apiService: ApiService, query: String, adapter: HomeAdapter) {
        val call = apiService.getLocations(query, apiKey, lang)

        // Gọi API và xử lý phản hồi
        call.enqueue(object : Callback<APIResModel> {
            override fun onResponse(call: Call<APIResModel>, response: Response<APIResModel>) {
                if (response.isSuccessful) {
                    // Lấy dữ liệu từ phản hồi của API
                    val hereResponse = response.body()
                    val items = hereResponse?.items

                    if (!items.isNullOrEmpty()) {
                        adapter.updateData(items, query)
                    } else {
                        Log.d("Location Info", "Không tìm thấy vị trí")
                    }
                } else {
                    Log.d("Location Info", "Phản hồi không thành công: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<APIResModel>, t: Throwable) {
                Log.d("Location Info", "Lỗi: ${t.message}")
            }
        })
    }
}