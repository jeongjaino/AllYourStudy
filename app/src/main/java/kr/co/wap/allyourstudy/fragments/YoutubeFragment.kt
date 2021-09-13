package kr.co.wap.allyourstudy.fragments

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.wap.allyourstudy.MainActivity
import kr.co.wap.allyourstudy.adapter.YoutubePlayListAdapter
import kr.co.wap.allyourstudy.api.YoutubeRetrofit
import retrofit2.Callback
import kr.co.wap.allyourstudy.databinding.FragmentYoutubeBinding
import kr.co.wap.allyourstudy.youtubedata.Item
import kr.co.wap.allyourstudy.youtubedata.YouTubePlayListResponse
import retrofit2.Call
import retrofit2.Response

class YoutubeFragment : Fragment(), YoutubePlayListAdapter.onItemClickListener {

    val binding by lazy{FragmentYoutubeBinding.inflate(layoutInflater)}

    var mainActivity: MainActivity?= null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainActivity) mainActivity = context
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View?{
        loadPlayList()
        return binding.root
    }
    private fun loadPlayList() {
        CoroutineScope(Dispatchers.IO).launch {
            YoutubeRetrofit.youtubeService.getYouTubePlayList()
                .enqueue(object : Callback<YouTubePlayListResponse> {
                    override fun onResponse(
                        call: Call<YouTubePlayListResponse>,
                        response: Response<YouTubePlayListResponse>
                    ) {
                        val responseCode = response.code()
                        if (responseCode == 200) {
                            Log.d("tag", "success")
                            CoroutineScope(Dispatchers.Main).launch {
                                val adapter = YoutubePlayListAdapter(this@YoutubeFragment)
                                binding.youtubePlayListRecyclerview.adapter = adapter
                                binding.youtubePlayListRecyclerview.layoutManager =
                                    LinearLayoutManager(context)
                                adapter.playList = response.body() as YouTubePlayListResponse
                                adapter.notifyDataSetChanged()
                            }
                        } else {
                            Log.d("tag", responseCode.toString())
                        }
                    }

                    override fun onFailure(call: Call<YouTubePlayListResponse>, t: Throwable) {
                        Log.d("Tag", t.message.toString())
                    }
                })
        }
    }
    override fun onClick(position: Int, playInit: Item) {
        val videoId = playInit.id.videoId
        val bundle = bundleOf("videoId" to videoId)
        setFragmentResult("request",bundle)
        mainActivity?.goYoutubePlayer()
    }
}