typedef int HANDLE

	HANDLE  open_device_hid(unsigned long ulTotalTimeOuts, unsigned long ulMultiTimeOuts,unsigned long ulVID,unsigned long ulPID);
	HANDLE  open_device_com(unsigned char nPort,unsigned int uBaud);
	
	int send_cmd_mt3x(HANDLE icdev,int slen,unsigned char* sendcmd,int* rlen,unsigned char* receivedata);
	int send_cmd_mt3(HANDLE icdev,int slen,unsigned char* sendcmd,int* rlen,unsigned char* receivedata);

	void set_timeouts(unsigned long msTotal,unsigned long msMulti);
