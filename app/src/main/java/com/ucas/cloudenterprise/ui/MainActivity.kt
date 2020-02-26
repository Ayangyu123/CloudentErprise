package com.ucas.cloudenterprise.ui


import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import com.ucas.cloudenterprise.app.FILE_CHOOSER_RESULT_CODE
import com.ucas.cloudenterprise.core.DaemonService
import com.ucas.cloudenterprise.utils.FileCP
import com.ucas.cloudenterprise.utils.get
import com.ucas.cloudenterprise.utils.store
import io.ipfs.api.IPFS
import io.ipfs.api.NamedStreamable
import io.ipfs.multiaddr.MultiAddress
import io.ipfs.multihash.Multihash
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import android.content.pm.PackageManager
import android.view.KeyEvent
import android.widget.RadioGroup
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.CORE_CLIENT_ADDRESS
import com.ucas.cloudenterprise.app.ROOT_DIR_PATH
import com.ucas.cloudenterprise.app.TEST_DOWN_FiLE_HASH
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.base.BaseFragment
import com.ucas.cloudenterprise.ui.fragment.MyFilesFragment
import com.ucas.cloudenterprise.ui.fragment.OthersShareFragment
import com.ucas.cloudenterprise.ui.fragment.PersonalCenterFragment
import com.ucas.cloudenterprise.ui.fragment.TransferListFragment
import com.ucas.cloudenterprise.utils.Toastinfo
import me.rosuh.filepicker.config.FilePickerManager


class MainActivity : BaseActivity() {

    var  TAG ="MainActivity"


    var  mLastFgIndex = 0
    var  lastBackPressedAt :Long  = 0
    lateinit  var mFragments:ArrayList<BaseFragment>
    lateinit  var mMyFilesFragment: MyFilesFragment
    lateinit  var mOthersShareFragment: OthersShareFragment
    lateinit  var mTransferListFragment: TransferListFragment
    lateinit  var mPersonalCenterFragment: PersonalCenterFragment


    override fun GetContentViewId()=R.layout.activity_main

    override fun InitView() {

        rg_main_bottom.setOnCheckedChangeListener(object :RadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: RadioGroup?, id: Int) {
                when(id){
                    R.id.rb_my_files->{  switchFragment(0)}
                    R.id.rb_others_share->{  switchFragment(1)}
                    R.id.rb_transfer_list->{  switchFragment(2)}
                    R.id.rb_personal_center->{  switchFragment(3)}
                }
            }

        })
        mFragments = ArrayList()
        initPager(true, 0)

    }

    private fun initPager(isRecreate: Boolean, position: Int) {
        mMyFilesFragment = MyFilesFragment.getInstance(isRecreate,null)
        mFragments.add(mMyFilesFragment)
        initFragments()
        rb_my_files.isChecked =true
    }

     fun switchFragment(position: Int) {
         if (position >= mFragments.size) {
             return
         }
         val ft = supportFragmentManager.beginTransaction()
         val targetFg = mFragments[position]
         val lastFg = mFragments[mLastFgIndex]
         mLastFgIndex = position
         ft.hide(lastFg)
         if (!targetFg.isAdded) {
             supportFragmentManager.beginTransaction().remove(targetFg).commit()
             ft.add(R.id.fl_content, targetFg)
         }
         ft.show(targetFg)
         ft.commitAllowingStateLoss()
    }

    private fun initFragments() {
        mFragments.apply {
            mOthersShareFragment=  OthersShareFragment()
            mTransferListFragment=  TransferListFragment()
            mPersonalCenterFragment=  PersonalCenterFragment()
            add(mOthersShareFragment)
            add(mTransferListFragment)
            add(mPersonalCenterFragment)
        }

    }

    override fun InitData(){}


    fun checkPermission(): Boolean? {
        var isGranted = true
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //如果没有写sd卡权限
                isGranted = false
            }
            if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false
            }
            Log.e(TAG, "isGranted == $isGranted")
            if (!isGranted) {
                this.requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    102
                )
            }
        }
        return isGranted
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - lastBackPressedAt > 2000) {
                Toastinfo("再按一次退出程序")
                lastBackPressedAt = System.currentTimeMillis()
            } else {

//                MApplication.getInstance().finishActivity()
//                    TODO
                finish()
                System.exit(0)
            }
            return false
        }
        return super.onKeyDown(keyCode, event)

    }

}


