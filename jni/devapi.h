
#ifndef DEVAPI_H
#define DEVAPI_H

int h900_uart_open(int index, int baudrate);//UART OPEN
int h900_uart_close(int index, int fd);

int h900_scaner_power_on(void);
int h900_scaner_power_off(void);
int h900_scaner_trig_on(void);
int h900_scaner_trig_off(void);

int h900_uart1_switch2channel(int channel);

int h900_rfid_power_on(void);
int h900_rfid_power_off(void);

int h900_psam_power_on(void);
int h900_psam_power_off(void);

int h900_fingerprint_power_on(void);
int h900_fingerprint_power_off(void);

int h900_ex3v3_power_on(void);
int h900_ex3v3_power_off(void);

int h900_ex5v_power_on(void);
int h900_ex5v_power_off(void);

///////////////////////////////////////////////////////////////// C5050 ///////////////////////////////////////////
int c5050_uart_open(int index, int baudrate);//UART OPEN
int c5050_uart_close(int index, int fd);
int c5050_uart_sethwfc(int fd);
int c5050_uart_setnofc(int fd);
unsigned char c5050_uart_gethwfc(int fd);

int c5050_scaner_power_on(void);
int c5050_scaner_power_off(void);
int c5050_scaner_trig_on(void);
int c5050_scaner_trig_off(void);

int c5050_uart1_switch2channel(int channel);

int c5050_printer_power_on(void);
int c5050_printer_power_off(void);

int c5050_rfid_power_on(void);
int c5050_rfid_power_off(void);

int c5050_psam_power_on(void);
int c5050_psam_power_off(void);

int c5050_keypadled_power_on(void);
int c5050_keypadled_power_off(void);

int c5050_ex3v3_power_on(void);
int c5050_ex3v3_power_off(void);

int c5050_ex5v_power_on(void);
int c5050_ex5v_power_off(void);
//////////////////////////////////////////////////////////////  C5050 END ////////////////////////////////////////

/////////////////////////////////////////////////////////////ONTECH START ///////////////////////////////////////
int ontech_uart_open(int index, int baudrate);//UART OPEN
int ontech_uart_close(int index, int fd);
int ontech_led1_on(void);
int ontech_led1_off(void);
int ontech_led2_on(void);
int ontech_led2_off(void);
/////////////////////////////////////////////////////////////ONTECK END   ///////////////////////////////////////

/////////////////////////////////////////////////////////////W200 START ///////////////////////////////////////
int w200_uart_open(int index, int baudrate);//UART OPEN
int w200_uart_close(int index, int fd);

int w200_zigbee_power_on(void);
int w200_zigbee_power_off(void);


/////////////////////////////////////////////////////////////W200 END   ///////////////////////////////////////

/////////////////////////////////////////////////////////////AN3500 START ///////////////////////////////////////
int an3500_uart_open(int index, int baudrate);//UART OPEN
int an3500_uart_close(int index, int fd);

int an3500_scaner_power_on(void);
int an3500_scaner_power_off(void);
int an3500_scaner_trig_on(void);
int an3500_scaner_trig_off(void);

/////////////////////////////////////////////////////////////AN3500 END   ///////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////////////////////////////////////

int mc90_iic_gpio_init(int scl, int sda, unsigned char SlaveAddr, int delay);
int mc90_iic_gpio_close();
int mc90_iic_gpio_read(unsigned char RegAddr,unsigned char *Data, unsigned char NBytes);
int mc90_iic_gpio_write(unsigned char RegAddr,unsigned char *Data, unsigned char NBytes);
int mc90_iic_gpio_read_reg(unsigned char RegAddr);
int mc90_iic_gpio_write_reg(unsigned char RegAddr, unsigned short RegData);

int mc90_smbus_gpio_init(int scl, int sda, int udelay);
int mc90_smbus_gpio_close(int fd);
int mc90_smbus_gpio_read_reg(int fd, unsigned char SlaveAddr, unsigned char RegAddr);

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#endif
