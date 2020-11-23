package com.ucas.cloudenterprise.ui.fragment

import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.base.BaseFragment
import com.ucas.cloudenterprise.ui.MainActivity
import com.ucas.cloudenterprise.utils.Utls
import kotlinx.android.synthetic.main.activity_choose_dest_dir.*
import kotlinx.android.synthetic.main.common_head.*
import kotlinx.android.synthetic.main.transfer_list_fragment.*
import kotlinx.android.synthetic.main.transfer_list_fragment.view_select_bar
import kotlinx.android.synthetic.main.transfer_list_fragment.viewpager_content
import java.io.File

/**
@author simpler
@create 2020年01月10日  14:31
 */
class TransferListFragment : BaseFragment() {
    var fragmentlist = ArrayList<BaseFragment>()

    override fun initView() {
        iv_back.visibility = View.GONE
        tv_title.text = "传输列表"
        tv_edit.visibility = View.GONE
        fragmentlist.add(TransferlistItemFragment(TransferlistItemFragment.DOWNLOAD, mContext!!))
        fragmentlist.add(TransferlistItemFragment(TransferlistItemFragment.UPLOAD, mContext!!))

        viewpager_content.apply {
            adapter = object : FragmentPagerAdapter(activity!!.supportFragmentManager) {
                //            adapter = object : FragmentPagerAdapter(activity!!.){
                override fun getItem(position: Int): Fragment {
                    return fragmentlist[position]
                }

                override fun getCount(): Int {
                    return fragmentlist.size
                }

            }
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    when (position) {
                        0 -> { //下载
                            tv_download.setTextColor(Color.parseColor("#FFFFFF"))
                           // tv_upload.setTextColor(Color.argb(204, 255, 255, 255))
                            view_select_bar.animate()
                                .translationX(view_select_bar.width.toFloat() * 0f)
                        }
                        1 -> { //上传
                            tv_upload.setTextColor(Color.parseColor("#FFFFFF"))
                           // tv_download.setTextColor(Color.argb(204, 255, 255, 255))
                            view_select_bar.animate()
                                .translationX(view_select_bar.width.toFloat() * 2f)
                        }
                    }
                }

            })

        }

        tv_download.setOnClickListener {

            viewpager_content.currentItem = 0
            view_select_bar.animate().translationX(view_select_bar.width.toFloat() * 0f)
        }
        tv_upload.setOnClickListener {
            viewpager_content.currentItem = 1
            view_select_bar.animate().translationX(view_select_bar.width.toFloat() * 2f)
        }

    }

    override fun initData() {}

    override fun GetRootViewID() = R.layout.transfer_list_fragment

    companion object {
        private var instance: TransferListFragment? = null

        fun getInstance(param1: Boolean, param2: String?): TransferListFragment {
            if (instance == null) {
                synchronized(TransferListFragment::class.java) {
                    if (instance == null) {
                        instance = TransferListFragment().apply {
                            this.arguments = Bundle().apply {
                                putBoolean("param1", param1)
                                putString("param2", param2)
                            }
                        }
                    }
                }
            }
            return instance!!
        }
    }

}