package com.ucas.cloudenterprise.app


/**
@author simpler
@create 2020年01月06日  13:33
 */

//<editor-fold desc=" 网络请求方法  ">
val NET_GET = 0
val NET_POST = 1
val NET_PUT = 2
//</editor-fold>

//<editor-fold desc=" 网络请求返回状态码  ">
val REQUEST_SUCCESS_CODE = 200 //请求成功
val REQUEST_NOT_FOUND_CODE = 404 //服务端找不到相应数据


val REQUEST_NO_TOKEN_CODE = 401 //没有携带Token
val REQUEST_GET_TOKEN_FAIL_CODE = 5001 //获取Token失败
val REQUEST_ACCESS_TOKEN_FAIL_CODE = 5002 //ACCESS_Token失效
val REQUEST_REFRESH_TOKEN_FAIL_CODE = 5000 //刷新token失效
val REQUEST_SUCCESS_CODE_NODATA = 4004 //请求成功没有数据
//</editor-fold>
//<editor-fold desc=" 网络请求参数  ">
//   文件类型
var IS_DIR = 1 // -1文件 1  文件夹 0 所有文件
var ALL_FILE =  0// 0  所有文件
var IS_FILE = -1 //-1 文件
//文件状态 是否是共享
var IS_COMMON_DIR = -1// 共享文件夹
var IS_UNCOMMON_DIR= 0 // 普通文件夹
//</editor-fold>
//<editor-fol desc="成员审核状态"> -1:未审核 1:拒绝 0:已通过 2所有
val All_STATE = 2
val UNAUDIT_STATE = -1
val NOT_PASS_STATE = 1
val PASS_STATE = 0
//</editor-fold>




//<editor-fold desc=" 网络请求link  ">
val  ACCESS_PORT = 6016
val  Get_Token_Port = 6019
val LOCAL_HOST="http://10.0.130.172:"

val BASE_HOST ="http://39.106.216.189:"

//val  BASE_URL="${BASE_HOST+ACCESS_PORT}/api/cloud/v1/"
val  BASE_URL="${BASE_HOST+ACCESS_PORT}/api/cloud/v1/"

val ENCODE="http://47.95.145.45:6020/api/v0/rs/encode"
val DECODE="http://47.95.145.45:6020/api/v0/rs/decode"
//<editor-fold desc=" 请求 REQUEST_CODE   ">
val REQUEST_SUCESS_CODE = 200 //请求成功 code


//</editor-fold>




val URL_REGISTER_COMPANY = "${BASE_URL}register_company" //企业注册
//{"code":200,"message":"Sucess","data":{"comp_id":"220874840104505344","comp_name":"test","comp_con_email":"916094751@qq.com","comp_con_tel":"test","vip_type":0,"vip_exp":0,"total_cap":0,"used_cap":0,"limit_user":0,"discounts":0,"creat
//{"code":200,"message":"Sucess","data":{"comp_id":"220546049285033984","comp_name":"test","comp_con_email":"916094751@qq.com","comp_con_tel":"test","vip_type":0,"vip_exp":0,"total_cap":0,"used_cap":0,"limit_user":0,"discounts":0,"create_time":"2020-01-06","update_time":"2020-01-06","status":0}}

val URL_TEAM = "${BASE_URL}team" //团队
val URL_GET_TEAM_LIST = "${BASE_URL}team_list/company" //团队
val URL_TEAM_INVITE = "${BASE_URL}team_invite" //团队

val URL_ADD_MEMBER = "${BASE_URL}member" //post 添加成员 put 删除
val URL_MEMBER_INFO_UPDATE = "${BASE_URL}member_info/update" //成员信息修改
//http://10.0.130.172:20000/api/cloud/v1/member_list/company/220874840104505344/mem/1578371132474935000-7696043959137995161
val URL_LIST_MEMBER = "${BASE_URL}member_list/" //添加成员
val URL_SEARCH_MEMBER = "${BASE_URL}member_search/" //成员搜索

val URL_MEMBER_AUDIT_LIST = "${BASE_URL}member_audit/company/"// :pid/status/:sts/" //get 成员审核列表  put 修改邀请信息
val URL_MEMBER_AUDIT_UPDATE_INFO = "${BASE_URL}member_audit/company"    // :pid/status/:sts/" //成员审核列表

val URL_ADD_File = "${BASE_URL}file_add" //添加文件
val URL_ADD_File_CHECK = "${BASE_URL}file_check_hash/" //添加文件 检查
val URL_DELETE_FILE = "${BASE_URL}file" //删除文件
val URL_LIST_FILES = "${BASE_URL}user_file/list/" //文件列表

val URL_LOGIN="http://39.106.216.189:${Get_Token_Port}/api/cloud/v1/login"
val GET_REFRESH_ACCESS_TOKEN="${BASE_HOST+Get_Token_Port}refresh/"// :id id为refresh_token + user_id"
val URL_FILE_SEARCH="${BASE_URL}file_search"
val URL_FILE_RENAME="${BASE_URL}file_rename"
val URL_FILE_MOV="${BASE_URL}file_mov"
val URL_FILE_COPY="${BASE_URL}file_copy"
val URL_LINK_SHARE_CREATE="${BASE_URL}enterprise/share/created"
val URL_LINK_SHARE_UPDATE="${BASE_URL}enterprise/share/update"
val URL_GET_COMPANY_INFO="${BASE_URL}company/"
val URL_POST_FILE_JURIS="${BASE_URL}file_juris" //文件收藏 共享接口
val URL_GET_FILE_JURIS="${BASE_URL}get_file_juris" //获取文件收藏
val URL_PUT_FILE_JURIS="${BASE_URL}del_file_juris" //取消文件收藏
val URL_SET_FILE_JURIS="${BASE_URL}set_file_juris" //取消文件收藏



val URL_RESERT_PASS_MESSAGE="${BASE_URL}reset_pass/message"//发送重置密码短信
val URL_RESERT_PASS_VERIFY="${BASE_URL}reset_pass/verify"//验证重置密码短信
val URL_RESERT_PASSWORD="${BASE_URL}reset_pass"//重置密码

//</editor-fold>

