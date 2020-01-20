package com.ucas.cloudenterprise.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.MyApplication
import com.ucas.cloudenterprise.app.MyApplication.Companion.context
import kotlinx.android.synthetic.main.dialog_create_new_dir.*
import kotlinx.android.synthetic.main.dialog_create_new_dir.view.*
import kotlinx.android.synthetic.main.dialog_up_file_type.view.*
import kotlinx.android.synthetic.main.popwiond_sort.view.*

/**
@author simpler
@create 2020年01月15日  17:31
 */
/*设置editText 文字
* */
fun SetEt_Text(s: String): Editable? {
    return Editable.Factory.getInstance().newEditable(s)
}
/* 新建文件夹Dialog
* */
fun GetCreateNewDirDialog(mContext:Context,
                          canclecallback: View.OnClickListener,
                          commitcallback:View.OnClickListener
):Dialog{
    return  Dialog(mContext!!).apply {
        var contentview =
            LayoutInflater.from(mContext).inflate(R.layout.dialog_create_new_dir,null)
        contentview.apply {
            tv_cancle.setOnClickListener(canclecallback)
            tv_commit.setOnClickListener (commitcallback)

        }
        setContentView(contentview)
        setCancelable(true)
    }
}
/* 上传文件类型Dialog
* */
fun GetUploadFileTypeDialog(mContext:Context,canclecallback: View.OnClickListener,commitcallback:View.OnClickListener):Dialog{
    return  Dialog(mContext!!, R.style.full_screen_dialog).apply {
        var contentview =
            LayoutInflater.from(mContext).inflate(R.layout.dialog_up_file_type,null)
        contentview.apply {
            iv_close.setOnClickListener(canclecallback)
            tv_image.setOnClickListener (commitcallback)
            tv_doc.setOnClickListener (commitcallback)
            tv_video.setOnClickListener (commitcallback)
            tv_all.setOnClickListener (commitcallback)

        }
        setContentView(contentview)
        setCancelable(true)
    }
}
/* 排序popwiond
* */
fun GetSortPOPwiond(
    mContext: Context,
    OnCheckedChangeListener: RadioGroup.OnCheckedChangeListener
):PopupWindow{
    return PopupWindow(mContext).apply {
        var contentview =
            LayoutInflater.from(mContext).inflate(R.layout.popwiond_sort,null)
        contentview.rg_sort.setOnCheckedChangeListener(OnCheckedChangeListener)
            width = WindowManager.LayoutParams.MATCH_PARENT
        height=WindowManager.LayoutParams.WRAP_CONTENT
        this.setBackgroundDrawable( ColorDrawable(Color.parseColor("#FFFFFF")))
        contentView = contentview
        isOutsideTouchable =true
    }}


    //文件删除dialog
    fun GetFileDeleteTipsDialog(
        mContext:Context,
        canclecallback: View.OnClickListener,
        commitcallback:View.OnClickListener
    ):Dialog{
        return  Dialog(mContext!!).apply {
            var contentview =
                LayoutInflater.from(mContext).inflate(R.layout.dialog_file_delete_tips,null)
            contentview.apply {
                tv_cancle.setOnClickListener(canclecallback)
                tv_commit.setOnClickListener (commitcallback)

            }
            setContentView(contentview)
            setCancelable(true)
        }
    }


    fun RightDrawable(RadioButton: TextView, ResourcesID:Int) {

        //加下划线
        val nav_up = MyApplication.context.resources.getDrawable(ResourcesID)
        nav_up.setBounds(0, 0, nav_up.minimumWidth, nav_up.minimumHeight)
        RadioButton.setCompoundDrawables(null, null, nav_up, null)
    }



