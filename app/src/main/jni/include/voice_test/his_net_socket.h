#ifndef his_net_socket
#define his_net_socket

#include <arpa/inet.h>
#include<string.h>
#include<stdio.h>
#include<pthread.h>
#include<stdlib.h>
#include<sys/socket.h>
#include<sys/time.h>
#include<string.h>
#include<fcntl.h>
#include <net/if.h>
#include<sys/types.h>
#include<sys/ioctl.h>
#include<netinet/in.h>
#include<netinet/tcp.h>
#include<signal.h>
#include<errno.h>
#include <semaphore.h>
#include<unistd.h>
#include <arpa/inet.h>
#include <netdb.h>

namespace hwvoice{
class his_net_do
{
public:
	static bool net_init();
	static void net_release();
	static bool connect_to_server(int s,const char *ip,short port,int timeout = 2);
	static void disconnect(int s);
	static bool send_packet(int s,char* buf,int len);
	static bool recv_packet(int s,char* buf,int len);
};
}

#endif
