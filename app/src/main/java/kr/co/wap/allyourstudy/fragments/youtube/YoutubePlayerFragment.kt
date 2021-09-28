package kr.co.wap.allyourstudy.fragments.youtube

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kr.co.wap.allyourstudy.databinding.FragmentYoutubePlayerBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import kr.co.wap.allyourstudy.MainActivity


class YoutubePlayerFragment : Fragment() {

    val binding by lazy{FragmentYoutubePlayerBinding.inflate(layoutInflater)}

    private lateinit var videoId: String

    private lateinit var mainActivity: MainActivity


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainActivity) mainActivity = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR

        setFragmentResultListener("request"){Key, bundle ->
            bundle.getString("videoId")?.let{
                videoId = it
            }
        }
        lifecycle.addObserver(binding.youtubePlayerView)
        loadVideo()
        return binding.root
    }
    private fun loadVideo(){
        binding.youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0f)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}