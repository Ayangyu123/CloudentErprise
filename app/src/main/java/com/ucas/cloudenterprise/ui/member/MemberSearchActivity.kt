package com.ucas.cloudenterprise.ui.member

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.`interface`.OnRecyclerItemClickListener
import com.ucas.cloudenterprise.adapter.MemberSearchAdapter
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.model.File_Bean
import com.ucas.cloudenterprise.model.MemberInfo
import com.ucas.cloudenterprise.utils.SetEt_Text
import com.ucas.cloudenterprise.utils.Toastinfo
import com.ucas.cloudenterprise.utils.intent
import kotlinx.android.synthetic.main.activity_member_invite.*
import kotlinx.android.synthetic.main.activity_member_search.*
import kotlinx.android.synthetic.main.activity_member_search.et_search_key_word
import kotlinx.android.synthetic.main.swiperefreshlayout.*
import org.json.JSONObject

class MemberSearchActivity : BaseActivity(), BaseActivity.OnNetCallback {

    var  mMemberlist= ArrayList<MemberInfo>()
    lateinit var mContext :Context
    lateinit var adapter :MemberSearchAdapter
    override fun GetContentViewId() = R.layout.activity_member_search

    override fun InitView() {
        mContext  = this@MemberSearchActivity
        iv_back.setOnClickListener { finish() }

        rc_myfiles.apply {
            layoutManager=LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false)
            addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))
        }
        adapter = MemberSearchAdapter(mContext,mMemberlist)
        rc_myfiles.adapter =adapter
        adapter.SetOnRecyclerItemClickListener(object: OnRecyclerItemClickListener {
            override fun onItemClick(holder: RecyclerView.ViewHolder, position: Int) {
                var item = mMemberlist[position]
                (holder as MemberSearchAdapter.ViewHolder).apply {
                    item.apply {
                        tv_acc_name.text = acc_name
                        tv_email.text = email
                        ll_root.setOnClickListener {
                            startActivityForResult(intent<MemberInfoActivity>().apply {
                                putExtra("item",item)
                            },1)
                        }
                    }



                }
            }

        })

        iv_clear.setOnClickListener {
            et_search_key_word.text=SetEt_Text("")
        }
        iv_search.setOnClickListener {
            if(!TextUtils.isEmpty(et_search_key_word.text)){
                membersearch()
            }else{
                Toastinfo("搜索内容不能为空")
            }
        }
                // 监听软键盘的按键
        et_search_key_word.addTextChangedListener {
            if(!TextUtils.isEmpty(et_search_key_word.text)){
                iv_clear.visibility =View.VISIBLE
            }else{
                iv_clear.visibility =View.INVISIBLE
            }

        }
        et_search_key_word.setOnEditorActionListener(object : TextView.OnEditorActionListener{
            override fun onEditorAction(textView: TextView?, actionId: Int, keyEvent: KeyEvent?): Boolean {
                //回车等操作
                if (actionId == EditorInfo.IME_ACTION_SEND
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_GO
                    || (keyEvent != null && KeyEvent.KEYCODE_ENTER == keyEvent.getKeyCode()
                            && KeyEvent.ACTION_DOWN == keyEvent.getAction())) {
                    // 搜索
                    membersearch()
                }
                return true;
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK){
            membersearch()
        }
    }

    private fun membersearch() {
        if(TextUtils.isEmpty(et_search_key_word.text)){
            Toastinfo("搜索内容不能为空")
            return
        }
        var imm: InputMethodManager =   getSystemService(INPUT_METHOD_SERVICE)  as InputMethodManager
        if (imm.isActive()){
            imm.hideSoftInputFromWindow(et_search_key_word.getWindowToken(), 0); //隐藏软键盘
        }
        var paramsjson=HashMap<String,Any>().apply{
//            ${et_search_key_word.text}/comp/${COMP_ID}
            put("acc_name",et_search_key_word.text.toString())
            put("comp_id","$COMP_ID")
            put("telephone","")
        }
        NetRequest("$URL_SEARCH_MEMBER", NET_POST,paramsjson,this,this)
    }

    override fun InitData() {

    }

    override fun OnNetPostSucces(
        request: Request<String, out Request<Any, Request<*, *>>>?,
        data: String
    ) {
        JSONObject(data).apply {
            if(!isNull("data")&&getInt("code")== REQUEST_SUCCESS_CODE){
                mMemberlist.addAll(Gson().fromJson<List<MemberInfo>>(getJSONArray("data").toString(),object : TypeToken<List<MemberInfo>>(){}.type) as ArrayList<MemberInfo>)
                adapter?.notifyDataSetChanged()

            }
        }

    }
}