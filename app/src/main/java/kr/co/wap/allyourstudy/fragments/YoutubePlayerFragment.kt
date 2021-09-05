package kr.co.wap.allyourstudy.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.setFragmentResultListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kr.co.wap.allyourstudy.databinding.FragmentYoutubePlayBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer



class YoutubePlayerFragment : Fragment() {

    val binding by lazy{FragmentYoutubePlayBinding.inflate(layoutInflater)}

    private lateinit var getContent: ActivityResultLauncher<Intent>

    private lateinit var videoId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setFragmentResultListener("request"){Key, bundle ->
            bundle.getString("videoId")?.let{
                videoId = it
            }
        }
        lifecycle.addObserver(binding.youtubePlayerView)

        binding.youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0f)
            }
        })
        return binding.root
    }
}