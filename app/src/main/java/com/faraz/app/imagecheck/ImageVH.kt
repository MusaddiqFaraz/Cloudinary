package com.faraz.app.imagecheck

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.animation.Transformation
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_image.view.*
import com.faraz.app.imagecheck.R
import com.cloudinary.android.ResponsiveUrl
import com.cloudinary.android.MediaManager
import android.R.attr.resource
import com.cloudinary.Url


/**
 * Created by root on 6/9/18.
 */
class ImageVH(itemView: View): RecyclerView.ViewHolder(itemView){

    fun bindImage(resource: Resource) {
        with(itemView) {
            Log.d("TAG","image size ${resource.height} ${resource.width}")

            val url = "http://res.cloudinary.com/dlpw4aumb/"+resource.publicId
            Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.default_placeholder)
                    .centerCrop()
                    .into(ivImage)
        }
    }

    fun bindImage(publicId:String) {
        with(itemView) {

            val url = MediaManager.get().url().publicId(publicId).format("webp")
            MediaManager.get().responsiveUrl(ResponsiveUrl.Preset.AUTO_FILL)
                    .generate(url, ivImage) { url ->
                        Glide.with(context)
                                .load(url.generate())
                                .placeholder(R.drawable.default_placeholder)
                                .centerCrop()
                                .into(ivImage)
                    }

//            val url = "http://res.cloudinary.com/dlpw4aumb/$url"


        }
    }
}