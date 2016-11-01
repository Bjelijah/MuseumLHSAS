#ifndef voice_test_include_h
#define voice_test_include_h

#ifdef __cplusplus
#if	__cplusplus
extern "C"{
#endif
#endif

typedef int voice_fun(int voice_type,const char* buf,int len);
typedef int voice_res(int msgCommand,void * res,int len);

typedef int voice_data_json(const char *jsonStr,int len);

/**
 * 注册到服务器
 * id: 必须唯一
 * local_phone: 电话号码
 * name: 姓名(UTF-8格式)
 * ip: 服务器IP
 * port:服务器端口
 * fun:回调函数
 * resFun 服务器回送的状态  回调函数
 * return: 0-成功,-1-失败
 */
int voice_register2svr(const char* userName ,const char * password,const char * mobileId,int mobileType ,int frequencyChannel,int onOfflineNoticeEnabled,int encryptionEnabled
		,const char* ip,short port,voice_fun* fun,voice_res* resFun,voice_data_json* dataFun);


/*
 * 从服务器注销
 * return: 0-成功 -1-失败
 */
int voice_unregister2svr();


/*
 * 获取注册状态
 * state: 返回 0-异常  1-正常
 * return:0-成功 -1-失败
 */
int voice_get_register_state(int* state);


/*
 * 注册成功后,发送心跳,3秒发送一次
 */
int voice_send_register_heartbeat();


/*
 * 请求与服务器说话
 * return: 0-成功(因为服务器不是立刻回复，所以还需要调用voice_get_talk_state) -1-失败
 */
int voice_start_talk2svr();


/*
 * 获取与服务器的说话状态
 * state: 返回 1-正在等待服务器应答  2-服务器允许通话  3-服务器拒绝通话
 * return: 0-成功 -1-失败
 */
int voice_get_talk_state(int* state);

/**
 * 设置发送数据目标的 id 和 name 同时只支持一种
 *
 *return:0-成功 -1-失败
 */
int voice_set_target(char **id,int idLen,char ** name,int nameLen);

/*
 * 放入音频PCM数据
 * voice_type: 音频类型(当前只支持g711u)
 * buf:存放音频数据的buf
 * len:存放音频数据的长度
 * return: 0-成功 -1-失败
 */
int voice_input_voice_data(int voice_type,const char* buf,int len);

/**
 * 放入音频g711u  base64数据
 * jsonStr:整个json body
 * len: jsonStr len;
 * return: 0-成功 -1-失败
 */
int voice_input_voice_json(char *jsonStr,int len);

/**
 * g711转pcm 并回调
 * g711uBuf 数据
 * len 长度
 *  return: 0-成功 -1-失败
 */
int voice_g711u2Pcm_callFun(const char *g711uBuf,int len);


/**
 * g711转pcm
 * g711uBuf 数据
 * glen  g711长度
 * pcmBuf 回传pcm数据
 *  return: 0-成功 -1-失败
 */
int voice_g711u2Pcm(const char *g711uBuf,int gLen,char* pcmBuf);

/**
 * pcm转个g711u
 * src pcm数据
 * dst g711u数据
 * srcLen pcm数据长度
 *  return: 0-成功 -1-失败
 */
int voice_pcm2g711u(const char *src,char *dst,int srcLen);

/*
 * 停止与服务器说话
 */
int voice_stop_talk2svr();

/**
 * 获取当前频段内的用户会话信息
 * mobileType：设备类型 0-不区分 1:手机/PAD  2:PC
 * return: 0-成功 -1-失败
 */
int voice_get_channel_user_info(int mobileType);

/**
 * 客户端发送广播数据到服务器
 * ContentType: 0-Audio(语音数据),1-Text,2-Picture,
 * buf:存放音频数据的buf
 * len:存放音频数据的长度
 *  return: 0-成功 -1-失败
 */
int voice_input_boardcast_data(int contentType,const char *buf,int len);


/**
 * 屏蔽用户
 * userName:屏蔽用户名
 * return: 0-成功 -1-失败
 */
int voice_silent(const char* userName);



/**
 * 创建会话群组
 * groupName:群组名称
 * members:群组成员用户名列表
 * memberLen::群组成员个数
 * return: 0-成功 -1-失败
 */
int voice_create_group(const char* groupName,const char **members,int memberLen);

/**
 * 删除会话群组
 * groupId:群组唯一标识符
 * return: 0-成功 -1-失败
 */
int voice_delete_group(const char* groupId);

/**
 * 修改群组
 * groupId：  群组唯一标识符
 * groupName   群组名称  （无：NULL）
 * addMembers 新增成员的用户名 （无：NULL）
 * addLen：新增成员个数
 * removeMembers：移除成员的用户名（无：NULL）
 * removeLen：删除成员个数
 * return: 0-成功 -1-失败
 */
int voice_updata_group(const char* groupId,const char *groupName,const char **addMembers,int addLen,const char **removeMembers,int removeLen);


/**
 * 获取所属的群组列表
 * userName:当前用户的用户名
 * return: 0-成功 -1-失败
 */
int voice_get_groups(const char *userName);

/**
 * 获取所属的单个群组信息
 * groupId:群组唯一标识符
 *  return: 0-成功 -1-失败
 */
int voice_get_group(const char *groupId);

/**
 * 客户端发送数据到服务器指定的群组
 * GroupId群组唯一标识符
 * buf:数据
 * len 长度
 *  return: 0-成功 -1-失败
 */
int voice_input_group_data(const char *groupId,char *buf,int len);


/**
 * 屏蔽组群
 *  GroupId群组唯一标识符
 *   return: 0-成功 -1-失败
 */
int voice_silent_group(const char *groupId);


/**
 * 客户端上下线通知
 * dialogId 会话唯一标识符
 * userName 用户名
 * isOnline 上下线信息 false-下线 true-上线
 * return: 0-成功 -1-失败
 */
int voice_onOffline_notice(const char *dialogId,const char* userName,int isOnline);

/**
 * 获取所有的用户信息
 * userName  请求者的用户名称
 * isOnline  上下线条件过滤   (若缺省  -1)
 * isSilent  是否屏蔽条件过滤(若缺省  -1)
 * return: 0-成功 -1-失败
 */
int voice_get_users_info(const char *userName,int isOnline,int isSilent);


/**
 * 获取指定的用户信息    (指定的UserName|UserId必须出现其中之一)
 * userName :请求者的用户名称
 * userId:用户唯一标识符
 *  return: 0-成功 -1-失败
 */
int voice_get_user_info(const char *userName,const char *userId);

/**
 * 收到服务器数据后的回送结果
 *result 0-ok   201-be silent 202-no access
 *return: 0-成功 -1-失败
 */
int voice_set_receive_result(int result,int sequence);

/**
 * 收到服务器组数据后的回送结果
 * result 0-ok   201-be silent 202-no access
 * return: 0-成功 -1-失败
 */
int voice_set_group_receive_result(int result,int sequence);





#ifdef __cplusplus
#if	__cplusplus
}
#endif
#endif

#endif

