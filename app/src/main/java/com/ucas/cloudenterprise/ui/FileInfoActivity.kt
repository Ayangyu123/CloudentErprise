package com.ucas.cloudenterprise.ui

import android.text.format.Formatter
import android.view.View
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.IS_DIR
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.model.File_Bean
import com.ucas.cloudenterprise.utils.FormatFileSize
import kotlinx.android.synthetic.main.activity_fileinfo.*
import kotlinx.android.synthetic.main.common_head.*

class FileInfoActivity : BaseActivity() {
    override fun GetContentViewId() = R.layout.activity_fileinfo

    override fun InitView() {
        tv_title.text = "详细信息"
        tv_edit.visibility =View.GONE
        iv_back.setOnClickListener{finish()}


        val item =intent.getSerializableExtra("file") as File_Bean
        item?.apply {
            tv_file_create_time.text = created_at
            tv_file_last_update.text = updated_at
            tv_file_last_update_persional.text = compet_user
            tv_file_owners.text = compet_user
            if(size!=null){
//                tv_file_size.text = Formatter.formatFileSize(this@FileInfoActivity,size).toUpperCase()
                tv_file_size.text = FormatFileSize(size)
            }



            tv_file_name.text = file_name
            var file_type_text =""
            var file_type_icon =R.drawable.icon_list_folder
            if(item.is_dir==IS_DIR){
                file_type_text =  "文件夹"
                item.pshare?.apply {
                    if(this==-1){
                        file_type_text =  "共享文件夹"
                        file_type_icon =R.drawable.icon_list_share_folder
                    }
                }

            }else{
                file_type_text =  "文件"
                file_type_icon =  R.drawable.icon_list_unknown
            }
            tv_file_type.text = file_type_text
            iv_type.setImageResource(file_type_icon)
            tv_file_size_text.visibility = if(item.is_dir==IS_DIR) View.GONE else View.VISIBLE
            tv_file_size.visibility = if(item.is_dir==IS_DIR) View.GONE else View.VISIBLE

        }



    }

    override fun InitData() {


    }

}
