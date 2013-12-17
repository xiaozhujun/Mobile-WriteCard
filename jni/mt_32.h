
typedef int HANDLE;

HANDLE   open_device(unsigned int nPort,unsigned int ulBaud);
	int  close_device(HANDLE icdev);
	int get_version(unsigned char *nVerLen,unsigned char *sVerData);
	int hex_asc(unsigned char *sHex,unsigned char *sAsc,unsigned long ulLength);
	int asc_hex(unsigned char *sAsc,unsigned char *sHex,unsigned long ulLength);
	int rf_desele(HANDLE icdev);
	int rf_halt(HANDLE icdev);
	//******************************* Contactless CPU card *****************************************//
	int OpenCard(unsigned char *rLen, unsigned char *receive) ;
	int Open_Card(unsigned char *sSnr) ;
	int Reset_Card(unsigned char *sCardInfo, unsigned char *nCardInfoLen) ;
	int ExchangePro(unsigned char *sCmd, unsigned char nCmdLen, unsigned char *sResp, unsigned char *nRespLen);
	int CloseCard(unsigned char *rlen, unsigned char *receive_buffer);
	//******************************* Contact CPU card and PSAM card ********************************//
	int ICC_Reset( unsigned char *nCardSet, unsigned char *sAtr, unsigned char *nAtrLen);
	int ICC_PowerOn(HANDLE icdev, unsigned char nCardSet, unsigned char *sAtr, unsigned char *nAtrLen);
	int ICC_CommandExchange(unsigned char *sCmd, unsigned char nCmdLen, unsigned char *sResp, unsigned char *nRespLen);
	int ICC_PowerOff(unsigned char *nCardSet, unsigned char *resCmd, unsigned char *resLen);

	//******************************* M1 card ********************************************************//
	int rf_reset(HANDLE icdev);
	int rf_card (unsigned char nMode,unsigned char *sSnr);
	int rf_authentication_key (unsigned char *sNkey, unsigned char* len, unsigned char* receiveCMD);
	int rf_read (unsigned char* nAdr, unsigned char *sReadData);
	int rf_read_hex (HANDLE icdev,unsigned char nAdr,unsigned char *sReadData);
	int rf_write (unsigned char *sWriteData,int *rlen, unsigned char *receive);
	int rf_write_hex (HANDLE icdev,unsigned char nAdr,unsigned char *sWriteData);
	int rf_initval(HANDLE icdev,unsigned char nAdr,unsigned long ulValue);
	int rf_readval(HANDLE icdev,unsigned char nAdr,unsigned long *ulValue);
	int rf_increment(HANDLE icdev,unsigned char nAdr,unsigned long ulValue);
	int rf_decrement(HANDLE icdev,unsigned char nAdr,unsigned long ulValue);
	int rf_transfer(HANDLE icdev,unsigned char nAdr);
	int rf_terminal(HANDLE icdev);
	int rf_restore(HANDLE icdev,unsigned char nAdr); 

	//******************************* 15693 **********************************************************//
	//寻卡，下发标志，
	int ISO15693_rf_Inventory(unsigned char *resplen,unsigned char *resp);
	//选卡
	int ISO15693_Select(unsigned char *uid, unsigned char *resplen, unsigned char *resp);
	//块读
	int ISO15693_Read_sm (unsigned char *blnr, unsigned char *nbl, unsigned char *resplen, unsigned char *resp);
	//块写
	int ISO15693_Write_sm(unsigned char *blnr,unsigned char *nbl,unsigned char *data, unsigned char *resplen, unsigned char *resp);
	//复位到装备态
	int ISO15693_Reset_To_Ready(HANDLE icdev,unsigned char flags,unsigned char *uid,unsigned short *resplen,unsigned char *resp);
	//静默设置
	int ISO15693_Stay_Quiet(HANDLE icdev,unsigned char	flags,unsigned char *uid,unsigned short *resplen,unsigned char *resp);
	//取卡片系统信息
	int ISO15693_Get_System_Information(HANDLE icdev,unsigned char	flags,unsigned char *uid,unsigned short *resplen,unsigned char *resp);
