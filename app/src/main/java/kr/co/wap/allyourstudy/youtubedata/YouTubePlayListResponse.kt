package kr.co.wap.allyourstudy.youtubedata

data class YouTubePlayListResponse(
    val etag: String,
    val items: List<Item>,
    val kind: String,
    val nextPageToken: String,
    val pageInfo: PageInfo,
    val regionCode: String
)
data class Item(
    val etag: String,
    val id: Id,
    val kind: String,
    val snippet: Snippet
)
