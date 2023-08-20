package dev.haqim.pitjarusapp.data.remote.retrofit

import dev.haqim.pitjarusapp.data.remote.response.LoginResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("login/LoginTest")
    suspend fun login(
        @Field("username") username: String, 
        @Field("password") password: String
    ): LoginResponse
}