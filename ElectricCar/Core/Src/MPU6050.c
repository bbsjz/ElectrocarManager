#include "MPU6050.h"
#include "i2c.h"

int16_t generate_data(uint16_t Data_H, uint16_t Data_L)
{
    uint16_t Data;
    Data = (Data_H << 8) | Data_L;
    return Data;
}

void MPU6050_Write_Reg(uint8_t RegAddr, uint8_t RegData)
{
    HAL_I2C_Mem_Write(&hi2c1, DEV_ADDR, RegAddr, I2C_MEMADD_SIZE_8BIT, &RegData, 1, 0xffff);
}

void MPU6050_Read_Reg(struct MPU6050_Data *data)
{
    uint8_t out[14];
    HAL_I2C_Mem_Read(&hi2c1, DEV_ADDR, DATA_BEGIN, I2C_MEMADD_SIZE_8BIT, (uint8_t *)out, 14, 0xffff);
    data->ACCEL_X = generate_data(out[0], out[1]) / 16384.0;
    data->ACCEL_Y = generate_data(out[2], out[3]) / 16384.0;
    data->ACCEL_Z = generate_data(out[4], out[5]) / 16384.0;
    data->TEMP = generate_data(out[6], out[7]) / 340.0 + 36.53;
    data->GYRO_X = generate_data(out[8], out[9]) / 2000.0;
    data->GYRO_Y = generate_data(out[10], out[11]) / 2000.0;
    data->GYRO_Z = generate_data(out[12], out[13]) / 2000.0;
}

void MPU6050_Init(void)
{
    MPU6050_Write_Reg(PWR_MGMT_1, 0x00);    //解除休眠状态     
    MPU6050_Write_Reg(SMPLRT_DIV, 0x07);    //陀螺仪采样率，典型值：0x07(125Hz)     
    MPU6050_Write_Reg(CONFIG, 0x06);        //低通滤波频率，典型值：0x06(5Hz)     
    MPU6050_Write_Reg(GYRO_CONFIG, 0x18);   //陀螺仪自检及测量范围，典型值：0x18(不自检，2000deg/s)     
    MPU6050_Write_Reg(ACCEL_CONFIG, 0x01);  //加速计自检、测量范围及高通滤波频率，典型值：0x01(不自检，2G，5Hz) 
}