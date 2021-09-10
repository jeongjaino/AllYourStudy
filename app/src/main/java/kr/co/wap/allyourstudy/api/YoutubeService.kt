package kr.co.wap.allyourstudy.api

import kr.co.wap.allyourstudy.youtubedata.YouTubePlayListResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface YoutubeService {
    @GET("/youtube/v3/search?part=snippet&q=스터디+윗미&maxResults=10&order=viewCount&key=AIzaSyAed0QA0dyENPzjUddzR7xzdl7gESe_F7w")
    fun getYouTubePlayList()
    :Call<YouTubePlayListResponse>
}
object YoutubeRetrofit{
    val youtubeService: YoutubeService

    init{
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        youtubeService = retrofit.create(YoutubeService::class.java)
    }
}