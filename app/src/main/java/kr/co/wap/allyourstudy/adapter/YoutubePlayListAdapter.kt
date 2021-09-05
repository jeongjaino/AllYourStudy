package kr.co.wap.allyourstudy.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.co.wap.allyourstudy.R
import kr.co.wap.allyourstudy.databinding.YoutubeRecyclerItemBinding
import kr.co.wap.allyourstudy.youtubedata.Item
import kr.co.wap.allyourstudy.youtubedata.YouTubePlayListResponse

class YoutubePlayListAdapter(private val listener: onItemClickListener)
    : RecyclerView.Adapter<YoutubePlayListAdapter.Holder>() {
    lateinit var playList: YouTubePlayListResponse
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = YoutubeRecyclerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val playView = playList.items.get(position)
        holder.setPlayList(playView)
    }

    override fun getItemCount(): Int {
        return playList.pageInfo.resultsPerPage
    }
    inner class Holder(val binding: YoutubeRecyclerItemBinding):RecyclerView.ViewHolder(binding.root),
    View.OnClickListener{
        lateinit var playView: Item
        fun setPlayList(playList: Item) {
            playList.let {
                binding.youtubeChannel.text = it.snippet.channelTitle
                binding.youtubeTitle.text = it.snippet.title
                binding.youtubeDate.text = it.snippet.publishTime
                Glide.with(binding.imageView).load(it.snippet.thumbnails.medium.url)
                    .into(binding.imageView)
                this.playView = playList
            }
        }
        init{
            binding.cardView.setOnClickListener(this)
        }
        override fun onClick(view: View?) {
            val position: Int = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                if(view?.id == R.id.cardView){
                    listener.onClick(position, playView)
                }
            }
        }
    }
    interface onItemClickListener{
        fun onClick(position: Int, playInit: Item)
    }
}
