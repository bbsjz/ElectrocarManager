package com.carmanager.server.Service;

import com.carmanager.server.Entity.Command;
import org.springframework.stereotype.Service;

@Service
public interface IMqttService {

    void controlCar(Command command);

}
