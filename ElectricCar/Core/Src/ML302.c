#include "ML302.h"
#include "usart.h"
#include "FreeRTOS.h"
#include "string.h"
#include "task.h"
#include "cmsis_os.h"
#include "stdlib.h"

/* Signals */
extern osSemaphoreId gnssSemHandle;
extern osSemaphoreId lockSemHandle;

uint8_t rx1_data[ML302_Rx_SIZE];
uint8_t holl[256];

double longitude = 0;
double latidude = 0;
SType lock_type = S_NULL;

void ML302_Init()
{
    HAL_StatusTypeDef atResponse = HAL_TIMEOUT;
    while (atResponse != HAL_OK)
    {
        HAL_UART_Transmit(&huart1, CMD_AT, CMD_AT_LEN, 0x7fff);
        atResponse = HAL_UART_Receive(&huart1, holl, 6, 1000);
        HAL_GPIO_TogglePin(GPIOC, GPIO_PIN_13);
    }
    HAL_UART_Transmit(&huart1, CMD_ATE0, CMD_ATE0_LEN, 0x7fff);
    HAL_UART_Receive(&huart1, holl, 256, 200);
    holl[9] = '0';
    while (!(holl[9] - '0')) // CFUN: 1
    {
        HAL_UART_Transmit(&huart1, CMD_CFUN, CMD_CFUN_LEN, 0x7fff);
        HAL_UART_Receive(&huart1, holl, 18, 200);
    }
    HAL_UART_Transmit(&huart1, CMD_MGNSS, CMD_MGNSS_LEN, 0x7fff);
    HAL_UART_Receive(&huart1, holl, 256, 200);
    HAL_UART_Transmit(&huart1, CMD_MGNSSTYPE, CMD_MGNSSTYPE_LEN, 0x7fff);
    HAL_UART_Receive(&huart1, holl, 256, 200);
    for (uint8_t i = 0; i < 3; ++i)
    {
        HAL_UART_Transmit(&huart1, CMD_MQTTCFG, CMD_MQTTCFG_LEN, 0x7fff);
        HAL_UART_Receive(&huart1, holl, 256, 200);
    }
    HAL_UART_Transmit(&huart1, CMD_MQTTOPEN, CMD_MQTTOPEN_LEN, 0x7fff);
    HAL_UART_Receive(&huart1, holl, 14, 0x7fff); // wait +MQTTOPEN:OK
    HAL_UART_Transmit(&huart1, CMD_MQTTSUB, CMD_MQTTSUB_LEN, 0x7fff);
    HAL_UART_Receive(&huart1, holl, 256, 500);

    // ML302初始化完成
    HAL_GPIO_WritePin(GPIOC, GPIO_PIN_13, GPIO_PIN_RESET);

    HAL_UART_Receive_DMA(&huart1, rx1_data, ML302_Rx_SIZE);
}

void ML302_QueryGNSS()
{
    HAL_UART_Transmit(&huart1, CMD_MGNSSINFO, CMD_MGNSSINFO_LEN, 0x7fff);
}

void ML302_Publish(char *msg)
{
    HAL_UART_Transmit(&huart1, CMD_MQTTPUB1, CMD_MQTTPUB1_LEN, 0x7fff);
    HAL_UART_Transmit(&huart1, (uint8_t *)msg, strlen(msg), 0x7fff);
    HAL_UART_Transmit(&huart1, CMD_MQTTPUB2, CMD_MQTTPUB2_LEN, 0x7fff);
}

void ML302_Log(char *msg)
{
    HAL_UART_Transmit(&huart1, CMD_MQTTLOG1, CMD_MQTTLOG1_LEN, 0x7fff);
    HAL_UART_Transmit(&huart1, (uint8_t *)msg, strlen(msg), 0x7fff);
    HAL_UART_Transmit(&huart1, CMD_MQTTLOG2, CMD_MQTTLOG2_LEN, 0x7fff);
}

uint8_t cmp_CMD(char *rx, char *cmd, uint8_t cmd_len)
{
    for (uint8_t i = 0; i < cmd_len; ++i)
        if (rx[i] != cmd[i])
            return 0;
    return 1;
}

HType identify_CMD(uint16_t len)
{
    char *rx = (char *)rx1_data;
    uint16_t bias = 0;
    rx[len] = 0;
    // trim
    while (rx[bias] != '+')
        bias++;
    // response OK
    if (bias == len)
        return H_NULL;
    rx += bias;

    // MGNSSINFO Response
    if (bias + R_MGNSSINFO_LEN + 1 < len &&
        cmp_CMD(rx, R_MGNSSINFO, R_MGNSSINFO_LEN))
    {
        rx += R_MGNSSINFO_LEN;
        // GNSS NO SINGAL
        if (rx[0] == 'G')
        {
            longitude = 0;
            latidude = 0;
            return H_GNSS;
        }
        char *p = strtok(rx, ",");
        // no longitude
        if (p == NULL)
            return H_NULL;
        double longitude_temp = atof(p + 1);
        p = strtok(NULL, ",");
        // no latidude
        if (p == NULL)
            return H_NULL;
        longitude = longitude_temp;
        latidude = atof(p + 1);
        return H_GNSS;
    }

    // MQTTPUBLISH Response
    if (bias + R_MQTTPUBLISH_LEN < len &&
        cmp_CMD(rx, R_MQTTPUBLISH, R_MQTTPUBLISH_LEN))
    {
        rx += R_MQTTPUBLISH_LEN;
        while (rx[0])
        {
            switch (rx[0])
            {
            case 'U':
                lock_type = S_UNLOCK;
                return H_LOCK;
                break;

            case 'L':
                lock_type = S_LOCK;
                return H_LOCK;
                break;

            case 'S':
                lock_type = S_START;
                return H_LOCK;
                break;

            default:
                rx++;
                break;
            }
        }
    }

    // fallback
    return H_NULL;
}

void ML302_IRQHandler(uint16_t len)
{
    HType cmd = identify_CMD(len);

    HAL_UART_Receive_DMA(&huart1, rx1_data, ML302_Rx_SIZE);

    // handle USART1 result
    switch (cmd)
    {
    case H_LOCK:
        // blink after USART1 receive complete
        HAL_GPIO_TogglePin(GPIOC, GPIO_PIN_13);
        xSemaphoreGiveFromISR(lockSemHandle, NULL);
        break;

    case H_GNSS:
        // blink after USART1 receive complete
        HAL_GPIO_TogglePin(GPIOC, GPIO_PIN_13);
        xSemaphoreGiveFromISR(gnssSemHandle, NULL);
        break;

    default:
        break;
    }
}