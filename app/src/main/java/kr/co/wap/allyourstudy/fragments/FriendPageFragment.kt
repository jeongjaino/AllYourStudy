package kr.co.wap.allyourstudy.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.wap.allyourstudy.MainActivity
import kr.co.wap.allyourstudy.adapter.SearchAdapter
import kr.co.wap.allyourstudy.api.RetrofitBuilder
import retrofit2.Callback
import kr.co.wap.allyourstudy.databinding.FragmentPageBinding
import kr.co.wap.allyourstudy.frienddata.Account
import kr.co.wap.allyourstudy.frienddata.SearchData
import kr.co.wap.allyourstudy.utils.ADDRESS
import kr.co.wap.allyourstudy.utils.REQUEST_CODE
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception

class FriendPageFragment : Fragment(), SearchAdapter.onSearchItemClickListener {


    private val cropActivityResultContract = object: ActivityResultContract<Any?, Uri?>(){

        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .getIntent(mainActivity)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent).uri
        }
    }

    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

    val binding by lazy{ FragmentPageBinding.inflate(layoutInflater)}

    private lateinit var mainActivity: MainActivity

    // registerForActivity assignment inside onAttach or onCreate, i.e, before the activity is displayed
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainActivity) mainActivity = context
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract){
            it?.let{
                Glide.with(binding.userImage).load(it).into(binding.userImage)
            }
        }
        binding.searchNameText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                if(s != null && binding.searchNameText.text.toString() != "") {
                    binding.searchRecyclerView.visibility = View.VISIBLE
                    loadSearchData()
                }
                else{
                    binding.searchRecyclerView.visibility = View.GONE //대안을 찾아봅시다.
                }
            }
        })
        binding.profileButton.setOnClickListener {
            checkPermission()
        }
        return binding.root
    }
    private fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        intent.type = "image/*"
        intent.putExtra("crop", true)
        intent.action = Intent.ACTION_GET_CONTENT
        try {
            cropActivityResultLauncher.launch(null)
        }
        catch(e:Exception){e.printStackTrace()}
    }
    private fun checkPermission(){

        val galleryPermission = ContextCompat.checkSelfPermission(mainActivity,
            Manifest.permission.READ_EXTERNAL_STORAGE)

        if(galleryPermission == PackageManager.PERMISSION_GRANTED){
            openGallery()
        }
        else{
            requestPermissions()
        }
    }
    private fun requestPermissions(){
        ActivityCompat.requestPermissions(mainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),99)
    }
    private fun loadSearchData(){
        val searchUserName = binding.searchNameText.text.toString()
        CoroutineScope(Dispatchers.IO).launch{
            RetrofitBuilder.userService.getSearchUser(searchUserName).enqueue(object: Callback<SearchData>{
                override fun onResponse(call: Call<SearchData>, response: Response<SearchData>) {
                    val adapter = SearchAdapter(this@FriendPageFragment)
                    binding.searchRecyclerView.adapter = adapter
                    binding.searchRecyclerView.layoutManager = LinearLayoutManager(mainActivity)
                    adapter.searchItems = response.body() as SearchData
                    adapter.notifyDataSetChanged()
                    Log.d("Tag",response.toString())
                }
                override fun onFailure(call: Call<SearchData>, t: Throwable) {
                    Log.d("Tag","32131")
                }
            })
        }
    }
    override fun onClick(position: Int, searchItem: Account) {
        binding.userNameText.text = searchItem.username
        val profile = ADDRESS + searchItem.profile
        Glide.with(mainActivity).load(profile).into(binding.userImage)
        when(searchItem.status){
            1 -> {
                binding.profileButton.text = "친구"
                binding.profileButton.isEnabled
            }
            -1 -> {binding.profileButton.text = "친구 신청"}
            0 -> {binding.profileButton.text = "친구 요청 보냄"}
        }
        binding.searchRecyclerView.visibility = View.GONE
    }
}
