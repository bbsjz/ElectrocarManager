#ifndef __ML302_H
#define __ML302_H

#include "stm32f4xx_hal.h"

#define ML302_Rx_SIZE 256

/* AT Commands */
#define CMD_AT ((uint8_t *)"AT\r\n")
#define CMD_AT_LEN 4

#define CMD_ATE0 ((uint8_t *)"ATE0\r\n")
#define CMD_ATE0_LEN 6

#define CMD_CFUN ((uint8_t *)"AT+CFUN?\r\n")
#define CMD_CFUN_LEN 10

#define CMD_MGNSS ((uint8_t *)"AT+MGNSS=1\r\n")
#define CMD_MGNSS_LEN 12

#define CMD_MGNSSTYPE ((uint8_t *)"AT+MGNSSTYPE=4\r\n")
#define CMD_MGNSSTYPE_LEN 16

#define CMD_MGNSSINFO ((uint8_t *)"AT+MGNSSINFO\r\n")
#define CMD_MGNSSINFO_LEN 14

#define CMD_MQTTCFG ((uint8_t *)"AT+MQTTCFG=\"jp.safengine.xyz\",8883,\"iot-steve\",60,\"iot\",\"****************\",0\r\n")
#define CMD_MQTTCFG_LEN 78

#define CMD_MQTTOPEN ((uint8_t *)"AT+MQTTOPEN=1,1,0,0,0,\"\",\"\"\r\n")
#define CMD_MQTTOPEN_LEN 29

#define CMD_MQTTSUB ((uint8_t *)"AT+MQTTSUB=\"car-manager/lock\",0\r\n")
#define CMD_MQTTSUB_LEN 33

#define CMD_MQTTPUB1 ((uint8_t *)"AT+MQTTPUB=\"car-manager/sensor\",0,0,0,\"")
#define CMD_MQTTPUB1_LEN 39

#define CMD_MQTTPUB2 ((uint8_t *)"\"\r\n")
#define CMD_MQTTPUB2_LEN 3

#define CMD_MQTTLOG1 ((uint8_t *)"AT+MQTTPUB=\"car-manager/logger\",0,0,0,\"")
#define CMD_MQTTLOG1_LEN 39

#define CMD_MQTTLOG2 ((uint8_t *)"\"\r\n")
#define CMD_MQTTLOG2_LEN 3

/* AT Response */
#define R_MGNSSINFO ("+MGNSSINFO: ")
#define R_MGNSSINFO_LEN 12

#define R_MQTTPUBLISH ("+MQTTPUBLISH: ")
#define R_MQTTPUBLISH_LEN 14

/* Handler */
#define HType uint8_t
#define H_NULL 0x0
#define H_GNSS 0x1
#define H_LOCK 0x2

/* LOCK STATUS */
#define SType uint8_t
#define S_NULL 0x60
#define S_UNLOCK 0x61
#define S_LOCK 0x62
#define S_START 0x64

void ML302_Init();

void ML302_QueryGNSS();

void ML302_Publish(char *json);

void ML302_Log(char *json);

void ML302_IRQHandler(uint16_t len);

#endif /* __ML302_H */