package com.ucas.cloudenterprise.ui

import android.view.View
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.base.BaseActivity
import kotlinx.android.synthetic.main.activity_permission_to_illustrate.*

class PermissionToIllustrateActivity : BaseActivity() {
    override fun GetContentViewId() = R.layout.activity_permission_to_illustrate

    override fun InitView() {
       viewpager_content.adapter = object :PagerAdapter(){
           override fun isViewFromObject(view: View, `object`: Any): Boolean {
               TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
           }

           override fun getCount(): Int {
               TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
           }
       }
        viewpager_content.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onPageSelected(position: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

    }

    override fun InitData() {
    }
}