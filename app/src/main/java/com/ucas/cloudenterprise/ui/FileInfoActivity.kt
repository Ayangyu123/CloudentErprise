package com.ucas.cloudenterprise.ui

import android.text.format.Formatter
import android.view.View
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.IS_DIR
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.model.File_Bean
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
                tv_file_size.text = Formatter.formatFileSize(this@FileInfoActivity,size).toUpperCase()
            }



            tv_file_name.text = file_name
            iv_type.setImageResource(if(item.is_dir==IS_DIR) R.drawable.icon_list_folder else  R.drawable.icon_list_unknown)
            tv_file_type.text = if(item.is_dir==IS_DIR)  "文件夹" else   "文件"


//            when(item.file_name.substringAfterLast(".")){
//
//                else->{
//                    iv_type.setImageResource(R.drawable.icon_list_folder)
//                    tv_file_type.text = "文件夹"
//                }
//            }
        }



    }

    override fun InitData() {


    }

}
