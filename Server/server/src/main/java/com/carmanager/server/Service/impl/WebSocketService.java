package com.carmanager.server.Service.impl;

import com.carmanager.server.Entity.MovingDto;
import com.carmanager.server.Entity.Point;
import com.carmanager.server.Service.IMoveService;
import com.carmanager.server.Service.IPointService;
import com.carmanager.server.Service.IWebSocketService;
import com.carmanager.server.Utils.MoveUtils;
import com.carmanager.server.webSocket.ChannelSupervise;
import com.google.gson.Gson;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService implements IWebSocketService {

    final IPointService pointService;

    final IMoveService moveService;

    final MoveUtils moveUtils = new MoveUtils();

    public WebSocketService(IPointService pointService, IMoveService moveService) {
        this.pointService = pointService;
        this.moveService = moveService;
    }

    /**
     * 每次收到更新的位置之后，服务器将位置更新给所有在线的用户，并将点存入数据库内
     * 当有用户开启位移提醒时，服务器判断是否发生移动，并将移动信息返回给开启移动提醒的用户
     * @param point 更新的位置
     */
    @Override
    public void update(Point point) {
        pointService.addPoint(point);
        moveUtils.addPoint(point);

        // 向所有未开启非位移提醒的APP发送位置消息
        MovingDto dto = new MovingDto();
        dto.setId(null); // 非开启提醒或移动情况下不返回Id
        dto.setAlert(false);
        dto.setEndTime(point.getCreateTime());
        dto.setToLatitude(point.getLatitude());
        dto.setToLongitude(point.getLongitude());
        String pointMessage = new Gson().toJson(dto);
        TextWebSocketFrame closeFrame = new TextWebSocketFrame(pointMessage);
        ChannelSupervise.send2CloseLocationRemoveAlert(closeFrame);

        // 有人开启了移动提醒
        if (ChannelSupervise.ifAlertOn()) {
            moveUtils.updateRecordStatus(true);
        }

        // 发生移动时发送移动消息
        if (!moveUtils.isMoving()) {
            ChannelSupervise.send2OpenLocationRemoveAlert(closeFrame);
        } else {
            Point startPoint = moveUtils.getBeginMovingPoint();
            dto.setAlert(true);
            dto.setBeginTime(startPoint.getCreateTime());
            dto.setFromLatitude(startPoint.getLatitude());
            dto.setFromLongitude(startPoint.getLongitude());
            dto.setDistance(moveUtils.getDistance());
            // 在一段移动记录中，假如出现一个人在某一个时刻开启了移动提醒，则整段记录都应该可见
            dto.setVisibility(moveUtils.isNeedToRecord());
            dto.setId(moveService.saveMove(dto).getId()); // 保存并获得Move的Id
            String moveMessage = new Gson().toJson(dto);
            TextWebSocketFrame openFrame = new TextWebSocketFrame(moveMessage);
            ChannelSupervise.send2OpenLocationRemoveAlert(openFrame);
        }

    }
}
