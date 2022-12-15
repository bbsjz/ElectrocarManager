/* USER CODE BEGIN Header */
/**
 ******************************************************************************
 * File Name          : freertos.c
 * Description        : Code for freertos applications
 ******************************************************************************
 * @attention
 *
 * Copyright (c) 2022 STMicroelectronics.
 * All rights reserved.
 *
 * This software is licensed under terms that can be found in the LICENSE file
 * in the root directory of this software component.
 * If no LICENSE file comes with this software, it is provided AS-IS.
 *
 ******************************************************************************
 */
/* USER CODE END Header */

/* Includes ------------------------------------------------------------------*/
#include "FreeRTOS.h"
#include "task.h"
#include "main.h"
#include "cmsis_os.h"

/* Private includes ----------------------------------------------------------*/
/* USER CODE BEGIN Includes */
#include "stdio.h"
#include "usart.h"
#include "MPU6050.h"
#include "string.h"
#include "ML302.h"
/* USER CODE END Includes */

/* Private typedef -----------------------------------------------------------*/
/* USER CODE BEGIN PTD */

/* USER CODE END PTD */

/* Private define ------------------------------------------------------------*/
/* USER CODE BEGIN PD */

/* USER CODE END PD */

/* Private macro -------------------------------------------------------------*/
/* USER CODE BEGIN PM */

/* USER CODE END PM */

/* Private variables ---------------------------------------------------------*/
/* USER CODE BEGIN Variables */
extern double longitude;
extern double latidude;
extern SType lock_type;

uint8_t lock_cmd[7] = {0xFD, 0x8, 0x37, 0x44, S_NULL, 0x5F, 0xDF};
char json[512];
/* USER CODE END Variables */
osThreadId lockTaskHandle;
osThreadId reportTaskHandle;
osSemaphoreId gnssSemHandle;
osSemaphoreId lockSemHandle;

/* Private function prototypes -----------------------------------------------*/
/* USER CODE BEGIN FunctionPrototypes */

/* USER CODE END FunctionPrototypes */

void StartLockTask(void const * argument);
void StartReportTask(void const * argument);

void MX_FREERTOS_Init(void); /* (MISRA C 2004 rule 8.1) */

/* GetIdleTaskMemory prototype (linked to static allocation support) */
void vApplicationGetIdleTaskMemory( StaticTask_t **ppxIdleTaskTCBBuffer, StackType_t **ppxIdleTaskStackBuffer, uint32_t *pulIdleTaskStackSize );

/* USER CODE BEGIN GET_IDLE_TASK_MEMORY */
static StaticTask_t xIdleTaskTCBBuffer;
static StackType_t xIdleStack[configMINIMAL_STACK_SIZE];

void vApplicationGetIdleTaskMemory(StaticTask_t **ppxIdleTaskTCBBuffer, StackType_t **ppxIdleTaskStackBuffer, uint32_t *pulIdleTaskStackSize)
{
  *ppxIdleTaskTCBBuffer = &xIdleTaskTCBBuffer;
  *ppxIdleTaskStackBuffer = &xIdleStack[0];
  *pulIdleTaskStackSize = configMINIMAL_STACK_SIZE;
  /* place for user code */
}
/* USER CODE END GET_IDLE_TASK_MEMORY */

/**
  * @brief  FreeRTOS initialization
  * @param  None
  * @retval None
  */
void MX_FREERTOS_Init(void) {
  /* USER CODE BEGIN Init */
  MPU6050_Init();
  /* USER CODE END Init */

  /* USER CODE BEGIN RTOS_MUTEX */
  /* add mutexes, ... */
  /* USER CODE END RTOS_MUTEX */

  /* Create the semaphores(s) */
  /* definition and creation of gnssSem */
  osSemaphoreDef(gnssSem);
  gnssSemHandle = osSemaphoreCreate(osSemaphore(gnssSem), 1);

  /* definition and creation of lockSem */
  osSemaphoreDef(lockSem);
  lockSemHandle = osSemaphoreCreate(osSemaphore(lockSem), 1);

  /* USER CODE BEGIN RTOS_SEMAPHORES */
  /* add semaphores, ... */
  /* USER CODE END RTOS_SEMAPHORES */

  /* USER CODE BEGIN RTOS_TIMERS */
  /* start timers, add new ones, ... */
  /* USER CODE END RTOS_TIMERS */

  /* USER CODE BEGIN RTOS_QUEUES */
  /* add queues, ... */
  /* USER CODE END RTOS_QUEUES */

  /* Create the thread(s) */
  /* definition and creation of lockTask */
  osThreadDef(lockTask, StartLockTask, osPriorityHigh, 0, 1024);
  lockTaskHandle = osThreadCreate(osThread(lockTask), NULL);

  /* definition and creation of reportTask */
  osThreadDef(reportTask, StartReportTask, osPriorityNormal, 0, 1024);
  reportTaskHandle = osThreadCreate(osThread(reportTask), NULL);

  /* USER CODE BEGIN RTOS_THREADS */
  /* add threads, ... */
  /* USER CODE END RTOS_THREADS */

}

/* USER CODE BEGIN Header_StartLockTask */
/**
 * @brief  Function implementing the lockTask thread.
 * @param  argument: Not used
 * @retval None
 */
/* USER CODE END Header_StartLockTask */
void StartLockTask(void const * argument)
{
  /* USER CODE BEGIN StartLockTask */
  /* Infinite loop */
  for (;;)
  {
    if (xSemaphoreTake(lockSemHandle, 10) != pdTRUE)
      continue;
    // Control Lock
    lock_cmd[4] = lock_type;
    HAL_UART_Transmit(&huart2, lock_cmd, 7, 0x7fff);
    osDelay(1);
  }
  /* USER CODE END StartLockTask */
}

/* USER CODE BEGIN Header_StartReportTask */
/**
 * @brief Function implementing the reportTask thread.
 * @param argument: Not used
 * @retval None
 */
/* USER CODE END Header_StartReportTask */
void StartReportTask(void const * argument)
{
  /* USER CODE BEGIN StartReportTask */
  /* Infinite loop */
  for (;;)
  {
    vTaskDelay(3000);
    ML302_QueryGNSS();
    while (xSemaphoreTake(gnssSemHandle, 10) != pdTRUE)
      vTaskDelay(10);
    struct MPU6050_Data out;
    MPU6050_Read_Reg(&out);
    sprintf(
        (char *)json,
        "{\\\"latitude\\\":%f,\\\"longitude\\\":%f,\
\\\"accelerationX\\\":%f,\\\"accelerationY\\\":%f,\\\"accelerationZ\\\":%f,\
\\\"angularVelocityX\\\":%f,\\\"angularVelocityY\\\":%f,\\\"angularVelocityZ\\\":%f}",
        latidude, longitude,
        out.ACCEL_X, out.ACCEL_Y, out.ACCEL_Z, 
        out.GYRO_X, out.GYRO_Y, out.GYRO_Z);
    ML302_Publish(json);
  }
  /* USER CODE END StartReportTask */
}

/* Private application code --------------------------------------------------*/
/* USER CODE BEGIN Application */

/* USER CODE END Application */
