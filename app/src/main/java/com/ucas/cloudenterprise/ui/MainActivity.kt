package com.ucas.cloudenterprise.ui


import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.RadioGroup
import com.king.app.dialog.AppDialog
import com.king.app.dialog.AppDialogConfig
import com.king.app.updater.AppUpdater
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.BuildConfig
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.NET_GET
import com.ucas.cloudenterprise.app.URLS_GET_VERSION_CHECK
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.base.BaseFragment
import com.ucas.cloudenterprise.core.DaemonService
import com.ucas.cloudenterprise.ui.fragment.MyFilesFragment
import com.ucas.cloudenterprise.ui.fragment.OthersShareFragment
import com.ucas.cloudenterprise.ui.fragment.PersonalCenterFragment
import com.ucas.cloudenterprise.ui.fragment.TransferListFragment
import com.ucas.cloudenterprise.utils.Toastinfo
import com.ucas.cloudenterprise.utils.VerifyUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.common_head.*
import org.json.JSONObject


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

    override fun InitData(){

       CheckNewVersion()
    }

     fun CheckNewVersion() {
        NetRequest("${URLS_GET_VERSION_CHECK}${BuildConfig.VERSION_CODE}", NET_GET,null,this,object:BaseActivity.OnNetCallback{
            override fun OnNetPostSucces(
                request: Request<String, out Request<Any, Request<*, *>>>?,
                data: String
            ) {
                if(VerifyUtils.VerifyRequestData(data)){
                    JSONObject(data).getJSONObject("data").apply {
                       if (getBoolean("is_new")){
                           //需要更新 TODO
//一句代码，傻瓜式更新
//                                            new AppUpdater(getContext(),url).start();
//简单弹框升级
                           val config = AppDialogConfig()
                           config.setTitle("发现新版本")
                               .setOk("升级")
                               .setContent(getString("description") + "").onClickOk =
                               View.OnClickListener {
                                   AppUpdater.Builder()
                                       .serUrl(getString("url"))
                                       .setFilename("tuxingyun.apk")
                                       .build(this@MainActivity)
                                       .start()
                                   AppDialog.INSTANCE.dismissDialog()
                               }
                           AppDialog.INSTANCE.showDialog(this@MainActivity, config)
                       }
                    }
                }
            }

        } )
    }


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
        when(mLastFgIndex){
           0->{//我的文件
               if(!mMyFilesFragment.pid.equals("root")){
                   mMyFilesFragment.pid_stack.remove(mMyFilesFragment.pid_stack[0])
                   mMyFilesFragment.pid=mMyFilesFragment.pid_stack[0]
                   mMyFilesFragment.GetFileList()
                   if(mMyFilesFragment.pid.equals("root")&&mMyFilesFragment.iv_back.visibility==View.VISIBLE){
                       mMyFilesFragment.iv_back.visibility=View.GONE
                       mMyFilesFragment.tv_title.text="我的文件"
                   }
                   return  false
               }
           }
            1->{// 共享文件
                if(!mOthersShareFragment.pid.equals("root")){
                    mOthersShareFragment.pid_stack.remove(mOthersShareFragment.pid_stack[0])
                    mOthersShareFragment.pid=mOthersShareFragment.pid_stack[0]

                    mOthersShareFragment.GetFileList()
                    if(mOthersShareFragment.pid.equals("root")&&mOthersShareFragment.iv_back.visibility==View.VISIBLE){
                        mOthersShareFragment.iv_back.visibility=View.GONE
                        mOthersShareFragment.tv_title.text="内部共享"

                    }
                    return  false
                }
            }

        }


        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - lastBackPressedAt > 2000) {
                Toastinfo("再按一次退出程序")
                lastBackPressedAt = System.currentTimeMillis()
            } else {

//                MApplication.getInstance().finishActivity()
//                    TODO
                myBinder?.mDaemonService?.stop()
                finish()
//                System.exit(0)
            }
            return false
        }
        return super.onKeyDown(keyCode, event)

    }

}


