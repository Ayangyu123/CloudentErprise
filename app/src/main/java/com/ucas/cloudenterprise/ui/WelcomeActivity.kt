package com.ucas.cloudenterprise.ui

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.FIRSTRUN_NAME_FOR_PREFERENCE
import com.ucas.cloudenterprise.app.IS_FIRSTRUN
import com.ucas.cloudenterprise.app.PREFERENCE__NAME__FOR_PREFERENCE
import com.ucas.cloudenterprise.app.WELCOME_GUIDES
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.utils.StatusBarUtil
import com.ucas.cloudenterprise.utils.startActivity
import kotlinx.android.synthetic.main.activity_welcome.*
import java.lang.reflect.Array.get

/**
@author simpler
@create 2020年01月08日  11:18
 */
class WelcomeActivity :BaseActivity (){
    val TAG = "WelcomeActivity"
    override fun InitView() {
        StatusBarUtil.setStatusDarkColor(window)
        vp_welcome.apply {
            adapter = object :PagerAdapter(){
                override fun instantiateItem(container: ViewGroup, position: Int): Any {
                    return ImageView(this@WelcomeActivity).apply {
                        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
                        setBackgroundResource(WELCOME_GUIDES[position])
                        scaleType = ImageView.ScaleType.CENTER
                        container.addView(this)

                    }
                }

                override fun isViewFromObject(view: View, `object`: Any): Boolean {
                    return view == `object`
                }

                override fun getCount(): Int {
                    return  WELCOME_GUIDES.size}

                override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                    container.removeView(`object` as View)
                }
            }
            addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
                override fun onPageScrollStateChanged(state: Int) { }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) { }

                override fun onPageSelected(position: Int) {
                  if(position== WELCOME_GUIDES.size-1){
                      tv_to_login.visibility =View.VISIBLE
                  }  else {tv_to_login.visibility =View.GONE}
                }

            })

        }


    }

    override fun InitData() { }

    override fun GetContentViewId(): Int = R.layout.activity_welcome
    fun tologinactivity(view: View) {
        Log.e(TAG,"ToLoginActivity")
        IS_FIRSTRUN = false
        getSharedPreferences(PREFERENCE__NAME__FOR_PREFERENCE, Context.MODE_PRIVATE).edit().putBoolean(
            FIRSTRUN_NAME_FOR_PREFERENCE,false).commit()
        startActivity<LoginActivity>()
        finish()
    }
    override fun onBackPressed() {
        if (vp_welcome.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            vp_welcome.currentItem = vp_welcome.currentItem - 1
        }
    }
}