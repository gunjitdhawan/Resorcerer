package com.grappes.fdsample

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.grappes.resourcedownloader.ReSorcerer
import com.grappes.resourcedownloader.setImageRes
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ReSorcerer.Builder()
            .setContext(this)
            .addLink("http://icons.iconarchive.com/icons/thesquid.ink/free-flat-sample/1024/bell-icon.png")
            .addLink("http://icons.iconarchive.com/icons/thesquid.ink/free-flat-sample/1024/cap-icon.png")
            .addLink("http://icons.iconarchive.com/icons/thesquid.ink/free-flat-sample/1024/football-icon.png")
            .addLink("https://gradeup.co/liveData/f/2019/5/weekly-oneliner-8th-to-14th-may-eng-50.pdf")
            .addLink("https://gradeup.co/liveData/f/2019/5/my_custom_font-75.ttf")
            .setStorage(ReSorcerer.Storage.INTERNAL)
            .buildWithListener(object : ReSorcerer.OnResultInterface {
                override fun onComplete(failedList: ArrayList<String>) {
                    image.setImageRes("http://icons.iconarchive.com/icons/thesquid.ink/free-flat-sample/1024/cap-icon.png")
                }
            })



    }


}
