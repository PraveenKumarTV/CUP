package com.example.cup2.network
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import com.example.cup2.model.SheetsResponse

interface SheetsApi{
    @GET("v4/spreadsheets/{sheetId}/values/Sheet1!A2:B")
    suspend fun getQuestions(
        @Path("sheetId") sheetId:String,
        @Query("key") apiKey: String
    ):SheetsResponse
}