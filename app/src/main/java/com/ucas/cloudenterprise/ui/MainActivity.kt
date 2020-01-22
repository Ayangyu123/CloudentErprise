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
            add(OthersShareFragment())
            add(TransferListFragment())
            add(PersonalCenterFragment())
        }

    }

    override fun InitData(){}




    fun GetStats(view: View) {
      if(DaemonService.daemon!=null){
           Thread(object:Runnable{
               override fun run() {
                   Log.e(TAG,"准备 ipf client")
                   val ipfs = IPFS( CORE_CLIENT_ADDRESS)
                   Log.e(TAG,"准备 ipf client")
                   val stats =ipfs.stats.bw().toString()
                   Log.e("ipfs.stats.bw()","ipfs.stats.bw()="+ stats)
                   runOnUiThread(){
//                       tv_text.text = stats
                   }

               }

           }).start()
       }else{
//          tv_text.text =tv_text.text.toString()+"daemon is null"
      }


    }



    fun GetFile(view: View) {
        if(checkPermission()==false){
//            tv_text.text =  tv_text.text.toString()+"\n"+"没有写sd卡权限"
            return
        }

        DaemonService.daemon?.let {
        Thread(object:Runnable{
            override fun run() {
                var ipfs = IPFS( MultiAddress(CORE_CLIENT_ADDRESS))

                 var filePointer = Multihash.fromBase58(TEST_DOWN_FiLE_HASH)
                var fileContents = ipfs.cat(filePointer)



                   val root =  File(ROOT_DIR_PATH)
                    if(!root.exists()){
                     root.mkdirs()
                      }
                val dest  = File(ROOT_DIR_PATH+System.currentTimeMillis())

//                if(!dest.exists()){
//                    dest.createNewFile()
//                }

//                val dest  = store["lj.png"]
                val output_swarm_key = dest.outputStream()
                try {
                    output_swarm_key.write(fileContents)
                }finally {
                    output_swarm_key.close()
                }

                Log.e("ok","文件写入完毕")
                runOnUiThread(){
//                    tv_text.text =  tv_text.text.toString()+"\n 文件名称：${dest.name}"+"\n"+"filesize=${dest.length()}\nfilepath=${dest.absolutePath}"
                }

            }

        }).start()
        }
    }
    //上传文件
    fun UploadFile(view: View) {
//        val i4 = Intent(applicationContext, FileBrowser::class.java)
//        startActivity(i4)
//        val i2 = Intent(applicationContext, FileChooser::class.java)
//        i2.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal())
//        startActivityForResult(i2, FILE_CHOOSER_RESULT_CODE)
        FilePickerManager
            .from(this)
            .forResult(FilePickerManager.REQUEST_CODE)

//            var i =  Intent(Intent.ACTION_GET_CONTENT);
//            i.addCategory(Intent.CATEGORY_OPENABLE);
//            i.setType("*/*");
//            startActivityForResult(Intent.createChooser(i, "File Chooser"), FILE_CHOOSER_RESULT_CODE);




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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//        when (requestCode) {
//            FilePickerManager.REQUEST_CODE -> {
//                if (resultCode == Activity.RESULT_OK) {
////                    val list = FilePickerManager.obtainData()
////                    list.forEach {
////                        Log.e("",""+it)
////                    }
//
//
//
//                    // do your work
//                } else {
//                    Toast.makeText(this, "没有选择任何东西~", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }


        if (requestCode == FILE_CHOOSER_RESULT_CODE && data != null) {

            val uri = Uri.parse(data.dataString) // 获取用户选择文件的URI
            Log.e("uri)", "data.getData()=" + uri)
            Log.e("uri)", "uri.getScheme()=" + uri.getScheme())
            Log.e("uri)", "uri.authority=" + uri.authority)


            val cursor = contentResolver.query(uri, null, null, null, null, null)
            var displayName: String? = null
            cursor?.use {
                if (it.moveToFirst()) {
                    displayName =
                        it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
//                    tv_text.text  =  tv_text.text.toString()+"\n 文件名称：$displayName"}
                    val size: Int =
                        it.getInt(it.getColumnIndex(OpenableColumns.SIZE))
//                    tv_text.text  =  tv_text.text.toString()+"\n 文件大小：$size"}

                    DaemonService.daemon?.let {
                        Thread(Runnable {
                            runOnUiThread() {
                                //                        tv_text.text =  tv_text.text.toString()+"\n"+"开始复制文件"
                            }
                            FileCP(
                                contentResolver.openInputStream(uri)!!,
                                store[displayName!!].outputStream()
                            )
                            runOnUiThread() {
                                //                        tv_text.text =  tv_text.text.toString()+"\n"+"复制文件完毕，准备上传"
                            }
                            val ipfs = IPFS(CORE_CLIENT_ADDRESS)
                            val file = NamedStreamable.FileWrapper(store[displayName!!])
                            val addResult = ipfs.add(file)[0]
                            runOnUiThread() {
                                //                        tv_text.text =  tv_text.text.toString()+"\n"+"上传文件完毕"
                            }
                            Log.e("ifileContents", "addResult=" + addResult)

                            runOnUiThread() {
                                //                        tv_text.text =  tv_text.text.toString()+"\n"+"addResult="+addResult
                            }
                            store[displayName!!].delete()
                            runOnUiThread() {
                                //                        tv_text.text =  tv_text.text.toString()+"\n"+"临时文件已删除"
                            }
                        }).start()
                    }
                }


            }


        }
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


