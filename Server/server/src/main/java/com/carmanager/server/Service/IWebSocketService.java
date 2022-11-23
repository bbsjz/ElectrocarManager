package com.carmanager.server.Service;

import com.carmanager.server.Entity.Point;
import org.springframework.stereotype.Service;

/**
 * 与实时通信相关的服务，提供向客户端更新数据的服务
 */
@Service
public interface IWebSocketService {

    void update(Point point);

}
