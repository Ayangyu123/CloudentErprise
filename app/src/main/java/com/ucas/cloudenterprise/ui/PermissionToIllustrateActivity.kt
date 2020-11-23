package com.ucas.cloudenterprise.ui

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.PERMISSIONTOILLUSTRATE_GUIDES
import com.ucas.cloudenterprise.app.WELCOME_GUIDES
import com.ucas.cloudenterprise.base.BaseActivity
import kotlinx.android.synthetic.main.activity_permission_to_illustrate.*
import kotlinx.android.synthetic.main.activity_welcome.*

class PermissionToIllustrateActivity : BaseActivity() {


    override fun GetContentViewId() = R.layout.activity_permission_to_illustrate

    override fun InitView() {
        iv_back.setOnClickListener { finish() }


       viewpager_content.apply {
           adapter = object :PagerAdapter(){
               override fun instantiateItem(container: ViewGroup, position: Int): Any {
                   return ImageView(this@PermissionToIllustrateActivity).apply {
                       layoutParams = ViewGroup.LayoutParams(
                           ViewGroup.LayoutParams.MATCH_PARENT,
                           ViewGroup.LayoutParams.MATCH_PARENT)
                       setBackgroundResource(PERMISSIONTOILLUSTRATE_GUIDES[position])
                       scaleType = ImageView.ScaleType.CENTER
                       container.addView(this)

                   }
               }

               override fun isViewFromObject(view: View, `object`: Any): Boolean {
                   return view == `object`
               }

               override fun getCount(): Int {
                   return  WELCOME_GUIDES.size }

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
                   view_line.apply {
                       animate().translationX(position*this.width.toFloat())
                   }
               }

           })

       }
        tv_editable.setOnClickListener {
            viewpager_content.currentItem = 0

        }
        tv_uploadable.setOnClickListener {
            viewpager_content.currentItem = 1

        }
        tv_selectable.setOnClickListener {
            viewpager_content.currentItem = 2

        }

    }

    override fun InitData() {
    }
}