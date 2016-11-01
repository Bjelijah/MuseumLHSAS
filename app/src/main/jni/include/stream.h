#ifndef stream_include_h
#define stream_include_h
#include <boost/noncopyable.hpp>
#include <boost/shared_ptr.hpp>
#include <boost/enable_shared_from_this.hpp>
#include "frame_filt.h"
#include <string>

//��ȡ���ݳ�����
class stream_capture
	:public boost::noncopyable
{
public:
	stream_capture(frame_filt_ref frame_filt)
		:m_capturing(false),m_pausing(false) ,m_frame_filt(frame_filt)
	{		

	}

	virtual ~stream_capture()
	{		

	}

	//��ʼ����
	virtual bool start_capture() = 0	;

	//ֹͣ����
	virtual void stop_capture() = 0;

	virtual bool is_start_capture()
	{
		return m_capturing;
	}

	//��ͣ/��������
	virtual bool pause_capture(bool bpause) 
	{
		return false;
	}

	//�Ƿ���ͣ����
	virtual bool is_pausing()
	{
		return m_pausing;
	}

	//��λ��ָ��λ�ò���
	virtual bool locate_to(unsigned int pos )
	{
		return false;
	}

	/*��ȡһ֡stream,�ϲ���벻�ϵ���,����stream buf�п������*/
	virtual bool get_one_frame_stream(char* out,unsigned int* out_len,stream_head* out_head)
	{
			if(is_start_capture())
			{
				return m_frame_filt->get_one_frame_stream(out,out_len,out_head);
			}
			return false;
	}

	/*�������ݣ������ڲ����ã�Ҳ�����ⲿ����*/
	virtual bool input_stream(const char* buf,int len)
	{
		if(is_start_capture())
		{
			return m_frame_filt->input_stream(buf,len);
		}
		return false;
	}	

	/*��ȡ��ǰ���ݳ���*/
	virtual bool get_stream_len(unsigned int& len)
	{
		if(is_start_capture())
		{
			len =  m_frame_filt->get_stream_len();
			return true;
		}		
		return false;
	}

	/*��ȡ���ÿռ�*/
	virtual bool get_stream_free(unsigned int& len)
	{
		if(is_start_capture())
		{
			len = m_frame_filt->get_free_len();
			return true;
		}
		return false;
	}

	/*�������*/
	virtual void clear_stream()
	{
		m_frame_filt->clear_stream();
	}

	/*��ȡfilt*/
	virtual bool get_frame_filt(frame_filt_ref& frame_filt)
	{
		frame_filt = m_frame_filt;
		return true;
	}

	/*��ȡ·��*/
	virtual bool get_stream_path(std::string& path)
	{
		return false;
	}

protected:	
	frame_filt_ref m_frame_filt;
	bool m_capturing;
	bool m_pausing;
};

typedef boost::shared_ptr<stream_capture> stream_ref;

#endif