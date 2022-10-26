package com.example.iotvoicecommands

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface APIInterface {
    @POST("/command")
    fun post_command(@Body() command : String) : Call<String>
}