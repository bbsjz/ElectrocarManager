package com.carmanager.server.Service;

import com.carmanager.server.Entity.DateMove;
import com.carmanager.server.Entity.Move;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 与移动信息相关的数据库管理服务，提供移动信息查询，添加的服务
 */
@Service
public interface IMoveService {

    Move addMove(Move move);

    Page<Move> getMove(long pageNum, long pageSize);

}
