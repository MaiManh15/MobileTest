package com.example.mobiletest.Datas.API

import android.util.Log
import com.example.mobiletest.Datas.Model.APIResModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object CallApi {
    fun callApi(apiService: ApiService) {
        val apiKey = "HisEbNchhPdke-LKMeJToYhcUiP3SlLY-xlKylQdfds"
        val query = "Hanoi, VietNam"

        val call = apiService.getLocations(query, apiKey)
        call.enqueue(object : Callback<APIResModel> {
            override fun onResponse(call: Call<APIResModel>, response: Response<APIResModel>) {
                if (response.isSuccessful) {
                    val hereResponse = response.body()
                    val items = hereResponse?.items

                    if (items != null && items.isNotEmpty()) {
                        val item = items[0]
                        val position = item.position
                        val lat = position.lat
                        val lng = position.lng
                        val address = item.address

                        // Lấy địa chỉ đầy đủ
                        val fullAddress = address.label
                        val country = address.countryName
                        val state = address.state
                        val city = address.city
                        val postalCode = address.postalCode

                        // In thông tin ra log
                        Log.d("Location Info", "Latitude: $lat, Longitude: $lng, Address: $fullAddress, Country: $country, State: $state, City: $city, Postal Code: $postalCode")

                    } else {
                        // Xử lý trường hợp không tìm thấy vị trí
                        Log.d("Location Info", "Không tìm thấy vị trí")
                    }
                } else {
                    // Xử lý lỗi nếu phản hồi không thành công
                    Log.d("Location Info", "Phản hồi không thành công: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<APIResModel>, t: Throwable) {
                // Xử lý lỗi
                Log.d("Location Info", "Lỗi: ${t.message}")
            }
        })
    }


}