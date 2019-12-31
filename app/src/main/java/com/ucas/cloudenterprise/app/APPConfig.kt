package com.ucas.cloudenterprise.app

/**
@author simpler
@create 2019年12月27日  14:51
 */
  //<editor-fold desc="core文件目录">
  val CORE_COMMAND_BIN = "core_bin" //core命令目录名称

  val CORE_WORK_DIR = "core_work" //core工作目录名称

  val CORE_WORK_CONFIG = "config" //core工作目录名称


  //</editor-fold>

    //<editor-fold desc="core 私钥">
    val CORE_WORK_PRIVATE_KEY ="swarm.key"
  //</editor-fold>

  //<editor-fold desc="core Service（后台服务id） ID">
    val  CORE_SERVICE_ID = 1

  //</editor-fold>

    //<editor-fold desc="core 配置文件名称 ">
    val    CORE_CONFIG_FILE_NAME = "config"
  //</editor-fold>

   //<editor-fold desc="core config文件 BOOTSTRAP 节点 数组">
   val BOOTSTRAPS_ARRAY = arrayOf<String>(
      "/ip4/39.106.216.189/tcp/4001/ipfs/QmabYFoBnCwUYHCx5wU75wtQ57YxqNdHRbLwpX8rVA2Bzo"
      ,"/ip4/47.95.145.45/tcp/4001/ipfs/QmVU7hgAfBEk7Z9W2nxy6GqLVpX77fREZL2h1D3NZo5Zr1"
      ,"/ip4/47.95.145.45/tcp/14001/ipfs/QmQ9gAiQFJ5waMjbBCgvvaP9FpJg7Rjpnc2tx8dJo128kZ"
      ,"/ip4/47.95.145.45/tcp/14002/ipfs/Qmb9pp9WoZDAvTDxvbdj4cpJ3iPkbZF5VopfHhWhUoY6GD"
      ,"/ip4/47.95.145.45/tcp/14003/ipfs/QmeniNKh2tgpSttiufC8dj14FsjeKpucwGZkX5XeRCcCF9"
      ,"/ip4/47.95.145.45/tcp/14004/ipfs/QmYrizCmCi7YMzMyEtYTBukNqYqP47134Yky7siXVUV17g"
      ,"/ip4/47.95.145.45/tcp/14005/ipfs/QmZoh59gZqAnDSsn72KcBh3inEkvNv55eRNzpiZjRq1oo5"
      ,"/ip4/47.95.145.45/tcp/14006/ipfs/QmUWhbxm4oRJZLaCzAqL8syx4HyLzNMp6MuLLFJsRBn2TB"
   )
    //</editor-fold>


    //<editor-fold desc=" FILE_CHOOSER_RESULT_CODE 文件选择">
    val FILE_CHOOSER_RESULT_CODE =1000
    //</editor-fold>
    //<editor-fold desc=" APP 设置">
    var IS_FIRSTRUN = true


    //</editor-fold>
    //<editor-fold desc=" CORE_PATH 环境变量">
      val CORE_PATH = "SVBGU19QQVRI" // 需要base64 解密
    //</editor-fold>
    //<editor-fold desc=" sharedpreference 中 对应的存储名称 ">
    val PREFERENCE__NAME__FOR_PREFERENCE = "AppSettings" //sharedpreference 名称
    val FIRSTRUN_NAME_FOR_PREFERENCE = "isfirstrun"   //第一次运行
   //</editor-fold>





