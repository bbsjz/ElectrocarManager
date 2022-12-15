# MQTT 指令流程

## 循环发送等待响应

```c
=> AT

<= AT OK
```

## 关闭回显

```c
=> ATE0

<= ATE0 OK
```

## 查询信号

```c
=> AT+CFUN?

<= +CFUN: 1 OK
```

## 启用GNSS

```c
=> AT+MGNSS=1

<= OK
```

## 设置卫星

```c
=> AT+MGNSSTYPE=4

<= OK
```

## 查询位置

```c
=> AT+MGNSSINFO

<= +MGNSSINFO: E114.24071183,N30.62761816,49.1,0.603,4 OK
```

查询失败后返回

```c
<= +MGNSSINFO: GNSS NO SINGAL OK
```

## 配置MQTT

```c
=> AT+MQTTCFG="jp.safengine.xyz",8883,"iot-steve",60,"iot","1Ce0W2DtGsFcUwd0",0

<= OK
```

## 开启MQTT

```c
=> AT+MQTTOPEN=1,1,0,0,0,"",""

<= +MQTTOPEN:OK
```

开启后会不断产生心跳包

```c
<= +MQTTPINGRSP: OK
```

## 订阅lock主题

```c
=> AT+MQTTSUB="car-manager/lock",0

<= OK +MQTTSUBACK:1,0,car-manager/lock
```

接收到消息

```c
<= +MQTTPUBLISH: 0,0,0,0,"car-manager/lock",8,"UNLOCK"
```

"UNLOCK"为消息
8为消息长度

## 向sensor主题发布内容

```c
=> AT+MQTTPUB="car-manager/sensor",0,0,0,"nihao"

<= OK
```

"nihao"为待发送的字符串
