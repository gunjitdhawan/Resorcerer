package com.grappes.resourcedownloader

import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.io.File

fun ImageView.setImageRes(link : String) {

    var localPath = ReSorcerer.getLocalPath(context, link);

    if(localPath.isNullOrBlank()) {
        Glide.with(this)
            .load(link)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true).into(this)
        return
    }

    Glide.with(this).load(localPath)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true).into(this)

}