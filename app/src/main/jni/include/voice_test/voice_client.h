#ifndef voice_include_h
#define voice_include_h

#include <string>
#include <hwvoice_msg.h>
#include <boost/thread.hpp>

typedef int voice_fun(int voice_type,const char* buf,int len);

namespace hwvoice{
    class voice_client
    {
        public:
            voice_client(const char* local_phone,const char* name,const char* svr_ip,short svr_port);

            ~voice_client();

            bool connect();

            void disconnect();

            bool is_connected();

            bool start_voice();

            void stop_voice();

            bool input_voice(const char* data,int len);

            bool send_heartbeat();

            int get_heartbeat_cnt();

            void set_voice_fun(voice_fun* fun);

        private:
            boost::mutex m_send_mu;
            bool send_msg(msg_t* msg,const char* buf,int len);

        private:
            unsigned int m_msg_id;
            boost::mutex m_msg_mu;
            unsigned int get_msg_id();

        private:
            boost::thread m_recv_thread;
            void on_recv();

            bool process_msg(msg_t* msg,const char* buf,int len);

        private:
            int m_s;
            bool m_is_connected;
            bool m_is_voice_start;
            std::string m_phone;
            std::string m_name;
            std::string m_svr_ip;
            short m_svr_port;
            int m_heartbeat_tm;
            voice_fun* m_voice_fun;
    };
}
#endif

