package com.example.mapsfriends

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ServerService {

    private val api: ServerAPI

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://...")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(ServerAPI::class.java)
    }

    fun sendPosition(userId: Int, latitude: Double, longitude: Double) {
        val data = PositionData(userId, latitude, longitude)
        api.sendPosition(data).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (!response.isSuccessful) {
                    println("Ошибка отправки: ${response.code()}")
                } else {
                    println("Данные успешно отправлены")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("Ошибка сети: ${t.message}")
            }
        })
    }

    interface ServerAPI {
        @retrofit2.http.POST("/api/position")
        fun sendPosition(@retrofit2.http.Body data: PositionData): Call<Void>
    }

    data class PositionData(val userId: Int, val latitude: Double, val longitude: Double)
}