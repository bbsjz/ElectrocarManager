package com.carmanager.server.Service;

import com.carmanager.server.Entity.Point;
import org.springframework.stereotype.Service;

@Service
public interface IWebSocketService {

    void update(Point point);

}
