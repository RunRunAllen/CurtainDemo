package com.example.curtaindemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TestActivity : AppCompatActivity() {
    val list = arrayListOf<String>()
    private var container: FrameLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_activity)
        val guideLayerHelper = GuideLayerHelper()
        container = findViewById<FrameLayout>(R.id.container)
        list.add("专车")
        list.add("津贴")

        //方案一：
//        val view = LayoutInflater.from(this)
//            .inflate(R.layout.houseajk_af_detail_view_bottom_bar_wb, container as ViewGroup?, true)
//        val tvPop = view.findViewById<TextView>(R.id.tvPop)


        //方案二：
        val tvPop = findViewById<TextView>(R.id.tvPop)
        tvPop.visibility = View.VISIBLE
        tvPop.setBackgroundResource(R.mipmap.houseajk_af_iconpop_small)
        guideLayerHelper.showPop(tvPop, list)
    }
}