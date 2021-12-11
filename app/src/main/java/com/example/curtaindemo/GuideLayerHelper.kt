package com.example.curtaindemo

import android.animation.*
import android.graphics.Color
import android.os.Handler
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.TextView

/**
 *  蒙层引导动画&角标动画
 */
class GuideLayerHelper {
    private var count: Int = 0
    private var animatorUpSet: AnimatorSet? = null
    private var handler: Handler? = null
    private var runnable: Runnable? = null

    /**
     *  显示角标
     */
    fun showPop(tvPop: TextView?, tips: MutableList<String>?) {
        if (tips != null && tips.size > 1) {
            //轮播展示
            handler = Handler()
            runnable = object : Runnable {
                override fun run() {
                    if (tvPop != null) {
                        val index = count % tips.size
//                        tvPop.setBackgroundResource(R.mipmap.houseajk_af_iconpop_small)
                        tvPop.text = tips[index]
                        startAnimation(tvPop, 350, 0.7f, 1.0f)
                        if (count == tips.size) {
                            release()
                            return
                        }
                        count++
                        handler?.postDelayed(this, 3000)
                    }
                }
            }
            handler?.post(runnable as Runnable)
        } else if (tips != null && tips.size > 0) {
            //单独展示
            tvPop?.text = tips[0]
        }
    }

    /**
     *  启动角标动画
     */
    private fun startAnimation(popView: TextView?, durationTime: Long, start: Float, end: Float) {
        Log.i("haha", "======111")
        popView?.pivotX = 0f
        popView?.pivotY = popView?.height?.toFloat()!!
        val scaleXAnimation: ObjectAnimator = ObjectAnimator.ofFloat(popView, "scaleX", start, end)
        val scaleYAnimation: ObjectAnimator = ObjectAnimator.ofFloat(popView, "scaleY", start, end)
        val colorAnimation: ObjectAnimator =
            ObjectAnimator.ofInt(popView, "textColor", Color.TRANSPARENT, Color.WHITE)
        colorAnimation.setEvaluator(ArgbEvaluator())
        animatorUpSet?.interpolator = LinearInterpolator()
        animatorUpSet?.playTogether(
            scaleXAnimation,
            scaleYAnimation,
            colorAnimation
        )
        animatorUpSet?.duration = durationTime
        animatorUpSet?.start()
    }

    fun release() {
        if (runnable != null) {
            handler?.removeCallbacks(runnable!!)
        }
        handler?.removeCallbacksAndMessages(null)
        handler = null
    }
}