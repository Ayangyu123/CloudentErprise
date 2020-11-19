package com.ucas.cloudenterprise.ui


import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioGroup
import androidx.fragment.app.FragmentTransaction
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
import com.ucas.cloudenterprise.ui.fragment.*
import com.ucas.cloudenterprise.utils.Toastinfo
import com.ucas.cloudenterprise.utils.VerifyUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.common_head.*
import kotlinx.android.synthetic.main.dialog_updater.view.*
import org.json.JSONObject

class MainActivity : BaseActivity() {
    var TAG = "MainActivity"
    var mLastFgIndex = 0

    var lastBackPressedAt: Long = 0
    lateinit var mFragments: ArrayList<BaseFragment>
    lateinit var mMyFilesFragment: MyFilesFragment
    lateinit var mOthersShareFragment: OthersShareFragment
    lateinit var mTransferListFragment: TransferListFragment
    lateinit var mPersonalCenterFragment: PersonalCenterFragment
    var pid = "root"
    lateinit var ft: FragmentTransaction

    //lateinit var uploadFilenName: String
    override fun GetContentViewId() = R.layout.activity_main
    override fun InitView() {
        mFragments = ArrayList()
        initPager(true, 0)
        setSelected(0)
        rg_main_bottom.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: RadioGroup?, id: Int) {
                when (id) {
                    R.id.rb_my_files -> {
                        setSelected(0)
                    }
                    R.id.rb_others_share -> {
                        setSelected(1)
                    }
                    R.id.rb_transfer_list -> {
                        setSelected(2)
                    }
                    R.id.rb_personal_center -> {
                        setSelected(3)
                    }
                }
            }
        })
    }


    private fun initPager(isRecreate: Boolean, position: Int) {
        mMyFilesFragment = MyFilesFragment.getInstance(isRecreate, null)
        mFragments.add(mMyFilesFragment)
        rb_my_files.isChecked = true
        initFragments()  //进行显示隐藏碎片
    }

    fun switchFragment(position: Int) {
        if (position >= mFragments.size) {
            return
        }
        val ft = supportFragmentManager.beginTransaction()
        val targetFg = mFragments[position]
        val lastFg = mFragments[mLastFgIndex]
        mLastFgIndex = position
        if (!targetFg.isAdded) {
            supportFragmentManager.beginTransaction().remove(targetFg).commit()
            ft.add(R.id.fl_content, targetFg)
        }
        ft.hide(lastFg)
        ft.show(targetFg)
        ft.commitAllowingStateLoss()
    }

    fun setSelected(position: Int) {
        //需要将按钮变亮，且需要切换fragment的状体
        //获取事务
        ft = supportFragmentManager.beginTransaction()
        hideTransaction(ft);//自定义一个方法，来隐藏所有的fragment
        when (position) {
            0 -> {
                if (!mMyFilesFragment.isAdded) {
                    ft.add(R.id.fl_content, mMyFilesFragment)
                }
                ft.show(mMyFilesFragment)
            }
            1 -> {
                if (!mOthersShareFragment.isAdded) {
                    ft.add(R.id.fl_content, mOthersShareFragment)
                }

                ft.show(mOthersShareFragment)
            }
            2 -> {
                if (!mTransferListFragment.isAdded) {
                    ft.add(R.id.fl_content, mTransferListFragment)
                }

                ft.show(mTransferListFragment)
            }
            3 -> {
                if (!mPersonalCenterFragment.isAdded) {
                    ft.add(R.id.fl_content, mPersonalCenterFragment)
                }
                ft.show(mPersonalCenterFragment)
            }
        }
        ft.commit()
    }

    fun hideTransaction(ftr: FragmentTransaction) {
        ftr.hide(mMyFilesFragment)
        ftr.hide(mOthersShareFragment)
        ftr.hide(mTransferListFragment)
        ftr.hide(mPersonalCenterFragment)
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
        /*if (checkPermission()) {
            FenXiangTUpian() //单个图片分享
            Log.e("yy", "222+++++++++")
        }*/
    }

    //<editor-fold desc="检查新版本">
    fun CheckNewVersion() {
        CheckNewVersion(true)
    }

    //版本升级进行弹窗操作
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
                                //new AppUpdater(getContext(),url).start();
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
        /*   NetRequest("${URLS_GET_VERSION_CHECK}/saturn-edisk-android"
   //                + "${BuildConfig.VERSION_CODE}"
               , NET_GET,null,this,object:BaseActivity.OnNetCallback{
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
                          }else{
                              Toastinfo("目前已是最新版本,无需更新")
                          }
                       }
                   }
               }

           } )*/
    }

    //</editor-fold>
    //检查SD卡权限
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

//    override fun onRestart() {
//        super.onRestart()
//        if (checkPermission()) {
//            FenXiangTUpian() //单个图片分享
//            Log.e("yy", "222+++++++++")
//        }
//    }

//采用单例设计模式进项对OnNewIntent进行判断   在进行单个上传文件的时候只有一个文件进行上传不会出现重复
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (checkPermission()) {
            FenXiangTUpian(intent!!) //单个图片分享
            Log.e("yy", "222+++++++++")
        }else{
            Toastinfo("请检查权限是否开启")
        }
    }



    /*override fun onStart() {
        super.onStart()
        if (checkPermission()) {
            FenXiangTUpian() //单个图片分享
            Log.e("yy", "222+++++++++")
        }else{
            Toastinfo("请检查权限是否开启")
        }
    }*/
    //分享单张图片
    fun FenXiangTUpian(intent: Intent) {
        //-----进行获取分享过来的图片的uri
        //实现单个分享图片 分享到文件根目录下
        // 更改需求  需要实现多个图片分享并指定文件路径
       // val intent: Intent = intent
        val extras = intent.extras
        val action = intent.action
        if (Intent.ACTION_SEND == action) {
            if (extras.containsKey(Intent.EXTRA_STREAM)) {
                try {
                    val parcelable = extras.getParcelable<Uri>(Intent.EXTRA_STREAM)
                    // 返回路径getRealPathFromURI
                    getRealPathFromURI(this, parcelable)
                } catch (e: Exception) {
                    Log.e(this.javaClass.name, e.toString())
                    Log.e("异常MainActivity 317", e.toString())
                }
            }
        }
    }

    //单选里面的方法
    fun getRealPathFromURI(
        mainActivity: MainActivity,
        uri2: Uri
    ): String {
        Log.e("123456", "URI2: $uri2")
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor = managedQuery(uri2, proj, null, null, null)
        if (cursor == null) {
            val path = uri2.path
            Log.e("123456", "path:$path")
            return path
        }
        val columnIndexOrThrow = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()

        val cursorstr = cursor.getString(columnIndexOrThrow)
        //uploadFilenName = cursorstr
        Toastinfo("~~~~~" + cursorstr)
        mMyFilesFragment.CheckFileIsExists(cursorstr)
        return cursor.getString(columnIndexOrThrow)
    }
    /*  fun CheckFileIsExists(file_path: String) {
          val destfile = File(file_path)
          val destfile_length = destfile.length() * 1.0 / (1024 * 1024)
          Log.e("ok", "当前文件大小: " + (destfile_length))
          if (destfile_length >= (4 * 1024)) {
              Toastinfo("该文件超过4G，不支持app传输")
              return
          }
          if (!CheckFreeSpace(destfile.length() * 1.5.toLong())) {
              Toastinfo("存储空间不足，无法完成上传操作")
              return
          }
          NetRequest(URL_FILE_UPLOADABLE, NET_POST, HashMap<String, Any>().apply {
              put("user_id", USER_ID)
              put("file_size", destfile.length())
          }, this, object : OnNetCallback {
              override fun OnNetPostSucces(
                  request: Request<String, out Request<Any, Request<*, *>>>?,
                  data: String
              ) {
                  if (VerifyUtils.VerifyResponseData(data)) {

                      (myBinder as DaemonService.MyBinder)?.GetDaemonService()
                          ?.AddFile(file_path, pid)
                  } else {
                      Toastinfo(JSONObject(data).getString("message"))
                  }

              }
          })
          var Memory = getMemory()
          Log.e("ok", "当前内存: $Memory")
      }*/
}



