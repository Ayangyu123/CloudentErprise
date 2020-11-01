package com.ucas.cloudenterprise.ui


import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioGroup
import android.widget.Toast
import com.king.app.dialog.AppDialog
import com.king.app.dialog.AppDialogConfig
import com.king.app.updater.AppUpdater
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.ucas.cloudenterprise.BuildConfig
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.URLS_GET_VERSION_CHECK
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.base.BaseFragment
import com.ucas.cloudenterprise.ui.fragment.MyFilesFragment
import com.ucas.cloudenterprise.ui.fragment.OthersShareFragment
import com.ucas.cloudenterprise.ui.fragment.PersonalCenterFragment
import com.ucas.cloudenterprise.ui.fragment.TransferListFragment
import com.ucas.cloudenterprise.utils.Toastinfo
import com.ucas.cloudenterprise.utils.Utls
import com.ucas.cloudenterprise.utils.VerifyUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.common_head.*
import kotlinx.android.synthetic.main.dialog_updater.view.*
import org.json.JSONObject
import java.io.File


class MainActivity : BaseActivity() {
    var TAG = "MainActivity"
    var mLastFgIndex = 0
    var lastBackPressedAt: Long = 0
    lateinit var mFragments: ArrayList<BaseFragment>
    lateinit var mMyFilesFragment: MyFilesFragment
    lateinit var mOthersShareFragment: OthersShareFragment
    lateinit var mTransferListFragment: TransferListFragment
    lateinit var mPersonalCenterFragment: PersonalCenterFragment


    override fun GetContentViewId() = R.layout.activity_main

    override fun InitView() {
        rg_main_bottom.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: RadioGroup?, id: Int) {
                when (id) {
                    R.id.rb_my_files -> {
                        switchFragment(0)
                    }
                    R.id.rb_others_share -> {
                        switchFragment(1)
                    }
                    R.id.rb_transfer_list -> {
                        switchFragment(2)
                    }
                    R.id.rb_personal_center -> {
                        switchFragment(3)
                    }
                }
            }
        })
        mFragments = ArrayList()
        initPager(true, 0)

    }


    private fun initPager(isRecreate: Boolean, position: Int) {
        mMyFilesFragment = MyFilesFragment.getInstance(isRecreate, null)
        mFragments.add(mMyFilesFragment)
        initFragments()  //进行显示隐藏碎片
        rb_my_files.isChecked = true
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
            mOthersShareFragment = OthersShareFragment()
            mTransferListFragment = TransferListFragment()
            mPersonalCenterFragment = PersonalCenterFragment()
            add(mOthersShareFragment)
            add(mTransferListFragment)
            add(mPersonalCenterFragment)
        }

    }

    override fun InitData() {
        //TODO 暂时取消版本检查
        CheckNewVersion(false)
        //权限
        //设置分享图片
        if (checkPermission()) {
            FenXiangTUpian() //图片分享
        }
    }

    //<editor-fold desc="检查新版本">
    fun CheckNewVersion() {
        CheckNewVersion(true)
    }

    fun CheckNewVersion(needtoast: Boolean) {
        OkGo.get<String>("${URLS_GET_VERSION_CHECK}/saturn-edisk-android")
            .tag(this)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    var data = response?.body().toString()
                    if (VerifyUtils.VerifyResponseData(data)) {
                        JSONObject(data).getJSONObject("data").apply {
                            if (getInt("code") > (BuildConfig.VERSION_CODE)) {
                                //需要更新 TODO
//一句代码，傻瓜式更新
//                                            new AppUpdater(getContext(),url).start();
//简单弹框升级
                                if (getInt("force_update") == 0) {
                                    val config = AppDialogConfig()
                                    config.setTitle("发现新版本${getString("version")}")
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
                                } else {
                                    var UpdateDialog = Dialog(this@MainActivity)
                                    var contentview = LayoutInflater.from(this@MainActivity)
                                        .inflate(R.layout.dialog_updater, null)
                                    contentview.apply {
                                        this.tv_title.text = "发现新版本${getString("version")}"
                                        this.tv_content.text = getString("description") + ""
                                        this.tv_OK.setOnClickListener {
                                            AppUpdater.Builder()
                                                .serUrl(getString("url"))
                                                .setFilename("tuxingyun.apk")
                                                .build(this@MainActivity)
                                                .start()
                                            UpdateDialog.dismiss()
                                        }
                                    }
                                    UpdateDialog.setCancelable(false)
                                    UpdateDialog.setContentView(contentview)
                                    UpdateDialog.show()
                                }

                            } else {
                                if (needtoast)
                                    Toastinfo("目前已是最新版本,无需更新")
                            }
                        }
                    }
                }

            })
//        NetRequest("${URLS_GET_VERSION_CHECK}/saturn-edisk-android"
////                + "${BuildConfig.VERSION_CODE}"
//            , NET_GET,null,this,object:BaseActivity.OnNetCallback{
//            override fun OnNetPostSucces(
//                request: Request<String, out Request<Any, Request<*, *>>>?,
//                data: String
//            ) {
//                if(VerifyUtils.VerifyRequestData(data)){
//                    JSONObject(data).getJSONObject("data").apply {
//                       if (getBoolean("is_new")){
//                           //需要更新 TODO
////一句代码，傻瓜式更新
////                                            new AppUpdater(getContext(),url).start();
////简单弹框升级
//                           val config = AppDialogConfig()
//                           config.setTitle("发现新版本")
//                               .setOk("升级")
//                               .setContent(getString("description") + "").onClickOk =
//                               View.OnClickListener {
//                                   AppUpdater.Builder()
//                                       .serUrl(getString("url"))
//                                       .setFilename("tuxingyun.apk")
//                                       .build(this@MainActivity)
//                                       .start()
//                                   AppDialog.INSTANCE.dismissDialog()
//                               }
//                           AppDialog.INSTANCE.showDialog(this@MainActivity, config)
//                       }else{
//                           Toastinfo("目前已是最新版本,无需更新")
//                       }
//                    }
//                }
//            }
//
//        } )
    }
    //</editor-fold>

    fun checkPermission(): Boolean {
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
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
                    ),
                    102
                )
            }
        }
        return isGranted
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (mLastFgIndex) {
            0 -> {//我的文件
                if (!mMyFilesFragment.pid.equals("root")) {
                    mMyFilesFragment.pid_stack.remove(mMyFilesFragment.pid_stack[0])
                    mMyFilesFragment.pid = mMyFilesFragment.pid_stack[0]
                    mMyFilesFragment.GetFileList()
                    if (mMyFilesFragment.pid.equals("root") && mMyFilesFragment.iv_back.visibility == View.VISIBLE) {
                        mMyFilesFragment.iv_back.visibility = View.GONE
                        mMyFilesFragment.tv_title.text = "我的文件"
                    }
                    return false
                }
            }
            1 -> {// 共享文件
                if (!mOthersShareFragment.pid.equals("root")) {
                    mOthersShareFragment.pid_stack.remove(mOthersShareFragment.pid_stack[0])
                    mOthersShareFragment.pid = mOthersShareFragment.pid_stack[0]

                    mOthersShareFragment.GetFileList()
                    if (mOthersShareFragment.pid.equals("root") && mOthersShareFragment.iv_back.visibility == View.VISIBLE) {
                        mOthersShareFragment.iv_back.visibility = View.GONE
                        mOthersShareFragment.tv_title.text = "内部共享"
                    }
                    return false
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
//                myBinder?.mDaemonService?.stop()
                finish()
//                System.exit(0)
            }
            return false
        }
        return super.onKeyDown(keyCode, event)

    }

    fun FenXiangTUpian() {
        //-----进行获取分享过来的图片的uri
        //实现单个分享图片 分享到文件根目录下
        val intent: Intent = intent
        val extras = intent.extras
        val action = intent.action
        if (Intent.ACTION_SEND == action) {
            if (extras.containsKey(Intent.EXTRA_STREAM)) {
                try {
                    val parcelable =
                        extras.getParcelable<Uri>(
                            Intent.EXTRA_STREAM
                        )
                    // 返回路径getRealPathFromURI
                    val path: String =
                        getRealPathFromURI(this, parcelable)
                } catch (e: Exception) {
                    Log.e(this.javaClass.name, e.toString())
                }
            }
        }
    }


    fun getRealPathFromURI(
        mainActivity: MainActivity,
        uri2: Uri
    ): String {
        Log.e("123456", "URI2: $uri2")
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor = managedQuery(uri2, proj, null, null, null)

        if (cursor == null) {
            return uri2.path
        }
        val columnIndexOrThrow: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()


        //  mAba.setText(cursor.getString(columnIndexOrThrow));
        val string: String = cursor.getString(columnIndexOrThrow)
        //File file_string = new File(string);
        Log.e("123456", "返回图片路径1: " + string)
        val file = File(string)
        val b = Utls.copyFile(
            string,
            Environment.getExternalStorageDirectory().path + "/" + file.getName()
        )
        Log.e("123456", "返回状态: " + b)
        val aaaa = Uri.fromFile(File(string))
        if (aaaa != null) {
            Log.e("123456", "返回图片路径2: $aaaa")
            Toast.makeText(mainActivity, "URL不为空", Toast.LENGTH_SHORT).show()
            Log.e("123456", "图片展示成功！")
        } else {
            Toast.makeText(mainActivity, "URL为空", Toast.LENGTH_SHORT).show()
        }
        return cursor.getString(columnIndexOrThrow)
    }

}


