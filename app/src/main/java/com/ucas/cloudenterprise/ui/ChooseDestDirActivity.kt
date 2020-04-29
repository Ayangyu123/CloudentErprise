package com.ucas.cloudenterprise.ui

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.IS_COMMON_DIR
import com.ucas.cloudenterprise.app.IS_UNCOMMON_DIR
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.base.BaseFragment
import com.ucas.cloudenterprise.model.File_Bean
import com.ucas.cloudenterprise.ui.fragment.MyDirsFragment
import com.ucas.cloudenterprise.ui.fragment.MyFilesFragment
import com.ucas.cloudenterprise.ui.fragment.ShareDirsFragment
import com.ucas.cloudenterprise.utils.GetCreateNewDirDialog
import com.ucas.cloudenterprise.utils.SetEt_Text
import com.ucas.cloudenterprise.utils.Toastinfo
import kotlinx.android.synthetic.main.activity_choose_dest_dir.*
import kotlinx.android.synthetic.main.activity_choose_dest_dir.view_select_bar
import kotlinx.android.synthetic.main.activity_choose_dest_dir.viewpager_content
import kotlinx.android.synthetic.main.common_head.*
import kotlinx.android.synthetic.main.dialog_create_new_dir.*
import org.json.JSONObject
import java.util.concurrent.TimeoutException

/**
@author simpler
@create 2020年01月22日  15:18
 */
class ChooseDestDirActivity : BaseActivity(),BaseActivity.OnNetCallback {
    companion object{
          val  MOVE= 1
           val COPY =2
        val SHOW_MYFILES =0
        val SHOW_OTHERSHARED =1
    }

     var CreateNewDirDialog: Dialog? =null
    var pid ="root"
    var file_item: File_Bean ? = null
    var   operatortype :Int = 0
    lateinit var  tv_dest_dir_commit:TextView
    lateinit var  mMyFilesDirFragment:MyDirsFragment
    lateinit var  mOthersShareDirFragment:ShareDirsFragment
    var  fragmentlist =ArrayList<BaseFragment>()

    override fun GetContentViewId() = R.layout.activity_choose_dest_dir

    override fun InitView() {
         intent.apply {
             file_item = getSerializableExtra("file") as File_Bean
             operatortype = getIntExtra("type",0)
//             pid =file_item!!.pid
         }


        tv_title.text ="选择目标文件夹"
        tv_edit.visibility =View.GONE
        iv_back.setOnClickListener { finish() }
        iv_create_dir.setOnClickListener {
            ShowCreateNewDirDialog()
        }
        tv_dest_dir_commit =findViewById<TextView>(R.id.tv_dest_dir_commit)
        tv_dest_dir_commit.setOnClickListener {

            setResult(Activity.RESULT_OK, intent.apply {
                when(viewpager_content.currentItem){
                    0->{ //我的文件
                        putExtra("pid","${mMyFilesDirFragment.pid}")
                    }
                    1->{ //共享文件
                         putExtra("pid","${mOthersShareDirFragment.pid}")
                    }
                }

                putExtra("file_id","${file_item?.file_id}")
            })
            finish()
        }
        tv_dest_dir_commit.isEnabled =false
        fragmentlist.clear()
        mMyFilesDirFragment =MyDirsFragment(pid)
        mOthersShareDirFragment = ShareDirsFragment(pid)
        fragmentlist.add(mMyFilesDirFragment)
        fragmentlist.add(mOthersShareDirFragment)
        viewpager_content.apply {
            adapter = object :FragmentPagerAdapter(supportFragmentManager,1){
                override fun getItem(position: Int): Fragment {
                    return  fragmentlist.get(position)
                }

                override fun getCount(): Int {
                    return  fragmentlist.size
                }

            }
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
                override fun onPageScrollStateChanged(state: Int) { }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) { }

                override fun onPageSelected(position: Int) {
                   when(position){
                       0->{ //我的文件
                           tv_path.text = "已选 ：我的文件"
                           view_select_bar.animate().translationX(view_select_bar.width.toFloat()*0f)
                           tv_myfiles.setTextColor( Color.parseColor("#4F73DF"))
                           tv_othercommom.setTextColor( Color.parseColor("#AAAFC0"))
                           iv_create_dir.isEnabled =true
                           iv_create_dir.visibility =View.VISIBLE
                          tv_dest_dir_commit.isEnabled = mMyFilesDirFragment.fileslist.isEmpty()
                       }
                       1->{ //共享文件
                           tv_path.text = "已选 ：共享文件"
                           iv_create_dir.visibility =View.INVISIBLE
                           view_select_bar.animate().translationX(view_select_bar.width.toFloat()*2f)
                           iv_create_dir.isEnabled =false
                           tv_othercommom.setTextColor( Color.parseColor("#4F73DF"))
                           tv_myfiles.setTextColor( Color.parseColor("#AAAFC0"))
                           tv_dest_dir_commit.isEnabled = mOthersShareDirFragment.fileslist.isEmpty()
                       }

                   }
                }

            })
        }

        tv_myfiles.setOnClickListener {
            view_select_bar.animate().translationX(view_select_bar.width.toFloat()*0f)
            viewpager_content.currentItem =SHOW_MYFILES
        }
        tv_othercommom.setOnClickListener {
            view_select_bar.animate().translationX(view_select_bar.width.toFloat()*2f)
            viewpager_content.currentItem = SHOW_OTHERSHARED
        }


        //<editor-fold  desc ="返回按钮 设置" >
        iv_back.setOnClickListener {
            var destfargment =  fragmentlist[viewpager_content.currentItem]
          when(destfargment){
              is MyDirsFragment->{
                  if( !destfargment.pid.equals(destfargment.pid_src)){ //选择文件夹显示返回
                      destfargment. pid_stack.remove(destfargment.pid_stack[0])
                      destfargment.pid = destfargment.pid_stack[0]
                      destfargment.GetFileList()

                  }else{ //相等 返回
                      finish()
                  }
              }
              is ShareDirsFragment->{
                  if( !destfargment.pid.equals(destfargment.pid_src)){ //选择文件夹显示返回
                      destfargment. pid_stack.remove(destfargment.pid_stack[0])
                      destfargment.pid = destfargment.pid_stack[0]
                      destfargment.GetFileList()

                  }else{ //相等 返回
                      finish()
                  }
              }
          }






        }
        //</editor-fold >

    }

    fun ShowCreateNewDirDialog() {
        if(CreateNewDirDialog == null){

            CreateNewDirDialog = GetCreateNewDirDialog(this,View.OnClickListener{
                CreateNewDirDialog!!.et_dir_name.text  = SetEt_Text("")
                CreateNewDirDialog!!.checkbox_is_common.isChecked = false
                CreateNewDirDialog!!.checkbox_is_common.visibility = View.GONE
                CreateNewDirDialog?.dismiss()
            }
                ,View.OnClickListener{
                    if(CreateNewDirDialog!!.et_dir_name?.text==null|| TextUtils.isEmpty(CreateNewDirDialog!!.et_dir_name?.text)){
                        Toastinfo("文件名不能为空")
                        return@OnClickListener
                    }
                    CreateNewDir( CreateNewDirDialog!!.et_dir_name?.text.toString(),
                        CreateNewDirDialog!!.checkbox_is_common.isChecked,
                        pid,
                        this,
                        this
                    )
                    CreateNewDirDialog?.dismiss()
                })
        }

        CreateNewDirDialog?.show()
    }
    override fun InitData(){

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
            when(viewpager_content.currentItem){
                SHOW_MYFILES->{
                   if( !mMyFilesDirFragment.pid.equals("root")){
//
                       mMyFilesDirFragment.pid_stack.removeAt(0)
                       if( !mMyFilesDirFragment.pid_stack.isEmpty()){
                       mMyFilesDirFragment.pid =mMyFilesDirFragment.pid_stack[0]
                       mMyFilesDirFragment.GetFileList()
                       return false
                       }
                   }
                }
                SHOW_OTHERSHARED->{
                    if( !mOthersShareDirFragment.pid.equals("root")){
                        mOthersShareDirFragment.pid_stack.removeAt(0)
                        if( !mOthersShareDirFragment.pid_stack.isEmpty()){
                        mOthersShareDirFragment.pid =mOthersShareDirFragment.pid_stack[0]
                        mOthersShareDirFragment.GetFileList()
                        return false
                    }}

                }
            }


        return super.onKeyDown(keyCode, event)
    }

    override fun OnNetPostSucces(
        request: Request<String, out Request<Any, Request<*, *>>>?,
        data: String
    ) {
           if(!JSONObject(data).isNull("data")){
               Toastinfo("新建文件夹成功")
               mMyFilesDirFragment.GetFileList()
           }

     }
}