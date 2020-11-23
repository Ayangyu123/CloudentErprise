package com.ucas.cloudenterprise.ui

import android.app.DatePickerDialog
import android.util.Log
import android.view.View
import android.widget.DatePicker
import com.lzy.okgo.model.HttpMethod
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.model.File_Bean
import com.ucas.cloudenterprise.utils.AppUtils
import com.ucas.cloudenterprise.utils.AppUtils.Stringtotimestamp
import com.ucas.cloudenterprise.utils.AppUtils.timestamptoString
import com.ucas.cloudenterprise.utils.SetEt_Text
import com.ucas.cloudenterprise.utils.ShareUtils
import com.ucas.cloudenterprise.utils.Toastinfo
import kotlinx.android.synthetic.main.activity_link_shared.*
import kotlinx.android.synthetic.main.common_head.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class LinkSharedActivity : BaseActivity(), BaseActivity.OnNetCallback {


       var  shared_url:String ="" //文件分享链接
       var  shared_sid:String ="" //文件分享链接
    var expire_time  =0L
       var  shared_password:String ="" //文件分享链接密码
    var  shareable =false
    var mDatePickerDialog:DatePickerDialog ?=null
     lateinit var  item : File_Bean

    override fun GetContentViewId()= R.layout.activity_link_shared
    override fun InitView() {
        intent.apply {
           item = getSerializableExtra("file") as  File_Bean
        }
        //<editor-fold desc="生成分享链接">
        var params = HashMap<String,Any>()
        params["user_id"] = "${USER_ID}"
        params["file_id"] = "${item.file_id}"
        NetRequest(URL_LINK_SHARE_CREATE, NET_POST,params,this,this)
        //</editor-fold>

        tv_title.text="链接分享"
        iv_back.setOnClickListener { finish() }
        tv_edit.apply {
            text ="取消"
            setOnClickListener {
                finish()
            }
        }

        checkbox_setting_password.setOnCheckedChangeListener { buttonView, isChecked ->
            ll_random.visibility = if (isChecked) View.VISIBLE else  View.GONE
        }
        checkbox_setting_indate.setOnCheckedChangeListener { buttonView, isChecked ->
            textview_setting_indate.visibility = if (isChecked) View.VISIBLE else  View.GONE
        }

        textview_setting_indate.setOnClickListener {
            //TODO
            if(mDatePickerDialog==null){
                var  ca = Calendar.getInstance();
      var   mYear = ca.get(Calendar.YEAR);
      var   mMonth = ca.get(Calendar.MONTH);
      var   mDay = ca.get(Calendar.DAY_OF_MONTH);
                mDatePickerDialog =   DatePickerDialog(this,object :DatePickerDialog.OnDateSetListener{
                    override fun onDateSet(
                        view: DatePicker?,
                        year: Int,
                        month: Int,
                        dayOfMonth: Int
                    ) {
                        mYear = year;
                        mMonth = month;
                        mDay = dayOfMonth;
                        textview_setting_indate.text =SimpleDateFormat("yyyy/MM/dd").format(Date(year-1900,month+1,dayOfMonth))
                    }
                },mYear,mMonth,mDay)
            }
            mDatePickerDialog?.show()

        }
        textview_ipassword_generator.setOnClickListener {
            edittext_password.text = SetEt_Text(AppUtils.getRandomString(4))

        }
        tv_link_share.setOnClickListener {

            if(!shareable){//不可分享，没有生成分享链接
                Toastinfo("尚未生成分享链接，请稍后尝试")
                return@setOnClickListener
            }
            Log.e("ok","shared_password.equals( edittext_password.text ) = ${shared_password.equals( edittext_password.text.toString() )}")
            Log.e("ok","textview_setting_indate.text.equals(DatetoString(expire_time)) = ${textview_setting_indate.text.equals(timestamptoString(expire_time))}")

            //<editor-fold desc="修改分享链接">
            if(!shared_password.equals( edittext_password.text.toString() )||!textview_setting_indate.text.toString().equals(
                    timestamptoString(expire_time))){// 用户修改密码，与原始密码不一致。执行修改链接接口

                var params = HashMap<String,Any>()
                params["sid"] = "${shared_sid}"
                params["password"] = "${edittext_password.text}"
                params["expire_time"] = Stringtotimestamp(textview_setting_indate.text.toString())
                NetRequest(URL_LINK_SHARE_UPDATE, NET_POST,params,this,this)

                return@setOnClickListener
            }
            //</editor-fold>
            Log.e("ok","执行分享" )
            ShareUtils.sharebyandroidSheet("来自土星云的分享 文件地址：${shared_url} 密码：${shared_password}",this )
        }
    }

    override fun InitData() {
//        checkbox_setting_password.isChecked =true
//        edittext_password.text = SetEt_Text(AppUtils.getRandomString(4))
    }



    override fun OnNetPostSucces(
        request: Request<String, out Request<Any, Request<*, *>>>?,
        data: String
    ) {
        when(request?.url){

            URL_LINK_SHARE_CREATE ->{
                when(request?.method.name){
                    HttpMethod.POST.name ->{

                        if(!JSONObject(data).isNull("data")&&JSONObject(data).getInt("code")==REQUEST_SUCCESS_CODE){
                            Toastinfo("创建分享链接成功")
                            shareable = true
                            shared_password = JSONObject(data).getJSONObject("data").getString("password")
                            expire_time = JSONObject(data).getJSONObject("data").getLong("expire_time")
                            shared_url = JSONObject(data).getJSONObject("data").getString("short_url")
                            shared_sid = JSONObject(data).getJSONObject("data").getString("sid")
                            edittext_password.setText(SetEt_Text(shared_password))
                            textview_setting_indate.text = timestamptoString(expire_time)
                        }else{
                            Toastinfo("创建分享链接失败")
                            return
                        }
                    }

                }

            }
            URL_LINK_SHARE_UPDATE->{

            }

        }
        //TODO

    }
}