package com.faraz.app.imagecheck

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.util.Log.d
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.Toast
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.faraz.app.imagecheck.extensions.KotlinRVAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import java.io.File


class MainActivity : AppCompatActivity() {

    private val mainVm by lazy { component.mainVm }
    private val PERMISSION_WRITE_REQUEST = 1
    private val RC_CHOOSE_PHOTO = 1
    private val RC_TAKE_PHOTO = 2

    private var imageList  = ArrayList<Resource>()
    var PICK_IMAGE_MULTIPLE = 1

    private lateinit var progressDialog: ProgressDialog

    private lateinit var imagesAdapter: KotlinRVAdapter<String,ImageVH>

    private var uploadedImages = ArrayList<String>()


    private enum class Resolution (val width:Int,val height:Int) {
        SMALL(250,200),
        MEDIUM(500,400),
        LARGE(1000,800)
    }
    private var selectedResolution = Resolution.SMALL
    private lateinit var sharedPreferences: SharedPreferences

    private val resolutionList = Resolution.values()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        getAllImages()

        rvImages.setHasFixedSize(true)
        sharedPreferences = getSharedPreferences("ImageCheck", Context.MODE_PRIVATE)

        if(sharedPreferences.contains("UploadedList")) {
            val list = sharedPreferences.getStringSet("UploadedList",null)
            list?.let {
                uploadedImages.addAll(it)
                displayImages()
            }
        }

        setupPermissions()
        fabAddImage.setOnClickListener {
            EasyImage.openChooserWithDocuments(this, "Choose from", PICK_IMAGE_MULTIPLE)
        }

        val adapter =ArrayAdapter.createFromResource(this,R.array.resolution,android.R.layout.simple_spinner_item)
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        acSpinner.adapter = adapter
        adapter.notifyDataSetChanged()

        acSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position) {
                    0 -> {
                        selectedResolution = Resolution.SMALL
                    }
                    1 -> {
                        selectedResolution = Resolution.MEDIUM
                    }
                    2 -> {
                        selectedResolution = Resolution.LARGE
                    }
                }
                displayImages()
            }

        }

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading. Please wait..")

    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {

            makeRequest()
        } else {
        }

    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                PERMISSION_WRITE_REQUEST)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_WRITE_REQUEST -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    d("Main", "Permission has been denied by user")
                } else {
                    d("Main", "Permission has been granted by user")
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
    }



    private fun displayImages() {

        val dividerSize = 3
        val spanCount = if(selectedResolution.width > rvImages.width)
            1
        else
            rvImages.width/selectedResolution.width
//        val spanCount = 3
        rvImages.addItemDecoration(GridDividerItemDecoration(spanCount,dividerSize))

        rvImages.layoutManager = GridLayoutManager(this,spanCount)



        imagesAdapter = KotlinRVAdapter(this,R.layout.layout_image,
                {
                    it.layoutParams.height = selectedResolution.height
                    it.layoutParams.width = selectedResolution.width
                    ImageVH(it) },{
            holder, item, position ->
            holder.bindImage(item)
        } ,uploadedImages)

        rvImages.adapter = imagesAdapter
        imagesAdapter.notifyDataSetChanged()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, object : DefaultCallback() {
            override fun onImagePicked(imageFile: File?, source: EasyImage.ImageSource?, type: Int) {
                imageFile?.let {
//                    progressDialog.show()
                    Log.d("mainAct","Image selected")
                    val uri = Uri.fromFile(imageFile)
                    uploadImage(uri)
                }
            }
        })
        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun uploadImage(uri: Uri) {
        MediaManager.get().upload(uri).callback(object : UploadCallback {
            override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                Log.d("mainAct","upload success")
                cvProgress.visibility = View.GONE
                progressDialog.dismiss()
                resultData?.let {
                    val publicId = resultData["public_id"] as String
                    uploadedImages.add(publicId)
                    saveImages()
                    displayImages()
                }

                Toast.makeText(this@MainActivity,"Image Uploaded",Toast.LENGTH_SHORT).show()
            }

            override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                val progress = bytes / totalBytes
//                pbProgress.progress = progress.toInt()
                Log.d("mainAct","upload progress $progress")
            }

            override fun onReschedule(requestId: String?, error: ErrorInfo?) {

            }

            override fun onError(requestId: String?, error: ErrorInfo?) {
                progressDialog.dismiss()
                cvProgress.visibility = View.GONE
                Log.d("mainAct","upload failed ${error?.description}")
                Toast.makeText(this@MainActivity,"Image Upload failed ${error?.description}",Toast.LENGTH_SHORT).show()
            }

            override fun onStart(requestId: String?) {
                cvProgress.visibility = View.VISIBLE
                Log.d("mainAct","upload started")
            }

        }).unsigned("ih7xko2b")
                .option("tag","people")
                .option("folder", "samples")
                .dispatch()

    }


    private fun saveImages() {
        val images = HashSet<String>()
        images.addAll(uploadedImages)
        sharedPreferences.edit().putStringSet("UploadedList",images).apply()
    }



}
