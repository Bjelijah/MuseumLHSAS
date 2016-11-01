#ifndef hwvoice_msg_include_h
#define hwvoice_msg_include_h

namespace hwvoice{

	typedef struct  //3.1
	{
		int tag;
		int version;
		int id;//sequence
		int command;
		char type;//contentType     json
		char reserve[3];
		int len;
		//int id;
		//char type;
	}msg_t;
	
	typedef struct//3.1
	{
		int tag;
		int version;
		int sequence;
		int command;
		char contentType;
		char reserve[3];
		int length;
	}msg_head_t;


	typedef struct  
	{

		char name[64];
		char region[64];
		char phone[32];
		char id[64];
		char reserve[64];
	}msg_detail_t;

	typedef struct//3.2
	{
		char name[64];
		char password[64];
		char mobileId[64];
		int mobileType;
		int frequencyChannel;
		bool dialogOnOffNotified;
		bool encryptionEnable;
	}msg_register_t;

	typedef struct
	{
		int result;
		int interval;
		char dialogId[256];
	}msg_register_res_t;


	typedef struct  //3.3
	{
		char dialogId[256];
	}msg_heartbeat_t;

	typedef struct //3.4
	{
		char* dialogID [64];//dialog id 数组  最多64个
		char* userName[64];
		int contentType;
		char * content;
		int contentLen;
	}msg_send_data_t;

	typedef struct
	{
		int result;
	}msg_result_t;

	typedef struct //3.5
	{
		int mobileType;
	}msg_get_dialogList_t;

	typedef struct
	{
		char dialogId[256];
		char userName[64];
		char mobileId[64];
		int mobileType;
		int frequencyChannel;
	}dialog_t;


	typedef struct
	{
		int result;
		dialog_t * dialog;//dialog 数组
		int len;//数组长度;
	}msg_get_dialogList_res_t;

	typedef struct //3.6
	{
		char senderDialogId[256];
		char Sender[64];
		int contentType;
		char *content;
		int len;
	}msg_receive_data_t;

	typedef struct//3.7
	{
		int contentType;
		char *data;
		int len;
	}msg_boardcast_send_data_t;

	typedef struct//3.8
	{
		char userName[64];
	}msg_silent_t;

	typedef struct//3.9
	{
		char groupName[64];
		char *members[64];//最多64个成员
	}msg_create_group_t;

	typedef struct
	{
		int result;
		char id[64];
	}msg_create_group_res_t;

	typedef struct//3.10
	{
		char groupId[64];
	}msg_delete_group_t;

	typedef struct//3.11
	{
		char groupId[64];
		char groupName[64];
		char *addMembers[64];
		char *removeMembers[64];
	}msg_set_group_t;

	typedef struct//3.12
	{
		char userName[64];
	}msg_get_groups_t;

	typedef struct
	{
		char groupId[64];
		char groupName[64];
		char *members[64];//最多64成员
		int membersLen;
		char owner[64];//群主;
		char creatTime[16];
		bool isSilent;
	}group_t;

	typedef struct
	{
		int result;
		group_t * group;
		int groupLen;
	}msg_get_group_res_t;

	typedef struct//3.13
	{
		char groupId[64];
	}msg_get_group_t;

	typedef struct//3.14
	{
		char groupId[64];
		int contentType;
		char *content;
		int len;
	}msg_group_send_data_t;

	typedef struct//3.15
	{
		char groupId[64];
		char senderId[64];
		char sender[64];
		int contentType;
		char *content;
		int len;
	}msg_group_receive_data_t;

	typedef struct//3.16
	{
		char groupId[64];
	}msg_silent_group_t;

	typedef struct//3.17
	{
		char dialogId[64];
		char userName[64];
		bool isOnLine;
	}msg_onOffLine_notice_t;

	typedef struct//3.18
	{
		char userName[64];
		bool useOnlie;
		bool isOnline;
		bool useSilent;
		bool isSilent;
	}msg_get_users_t;

	typedef struct
	{
		char userId[64];
		char userName[64];
		char nickName[64];
		bool isOnline;
		bool isSilent;
	}user_t;

	typedef struct
	{
		int result;
		user_t * user;
		int user_len;
		dialog_t * dialog;
		int dialog_len;
	}msg_get_users_res_t;

	typedef struct//3.19
	{
		char userName[64];
		char userId[64];
	}msg_get_user_t;


    typedef struct
    {
        int process;//0:请求说话  1:停止说话
        char reserve[256];
    }msg_voice_request_t;

    typedef struct
    {
        int process;
        int result;//0:成功 1:等待回复
        char reserve[256];
    }msg_voice_response_t;

#define VOICE_TAG														0x12345678
#define PROTOCOL_VERSION   									0x00000001
#define HWVOICE_MSG_VOICE_DATA							0x0
#define HWVOICE_MSG_VIOCE_HEARTBEAT				        0x1
#define HWVOICE_MSG_VOICE_DETAIL						0x2
#define HWVOICE_MSG_VOICE_REQUEST                       0x3
#define HWVOICE_MSG_VOICE_RESPONSE                      0x4


#define HWVOICE_PROTOCOL_ACK									0x80000000
#define HWVOICE_PROTOCOL_REGISTER							0x00000001
#define HWVOICE_PROTOCOL_ALIVE								0x00000002
#define HWVOICE_PROTOCOL_SENDING							0x00000003
#define HWVOICE_PROTOCOL_DIALOG_LIST					0x00000004
#define HWVOICE_PROTOCOL_RECEINING						0x00000005
#define HWVOICE_PROTOCOL_BOARDCAST_SENDING	0x00000006
#define HWVOICE_PROTOCOL_SILENT							0x00000007
#define HWVOICE_PROTOCOL_CREATE_GROUP				0x00000008
#define HWVOICE_PROTOCOL_DELETE_GROUP   			0x00000009
#define HWVOICE_PROTOCOL_SET_GROUP						0x0000000a
#define HWVOICE_PROTOCOL_GET_GROUPS					0x0000000b
#define HWVOICE_PROTOCOL_GET_GROUP						0x0000000c
#define HWVOICE_PROTOCOL_GROUP_SENDING			0x0000000d
#define HWVOICE_PROTOCOL_GROUP_RECEIVING   		0x0000000e
#define HWVOICE_PROTOCOL_SILENT_GROUP				0x0000000f
#define HWVOICE_PROTOCOL_ONOFFLINE_NOTICE		0x00000010
#define HWVOICE_PROTOCOL_GET_USERS						0x00000011
#define HWVOICE_PROTOCOL_GET_USER						0x00000012


#define HWVOICE_FAULT_NUM_OK									0      //success
#define HWVOICE_FAULT_NUM_SILENT							201 //send success but silent by service
#define HWVOICE_FAULT_NUM_PRIORITY						202 //no priority
#define HWVOICE_FAULT_NUM_NOTFOUND					404





}

#endif
