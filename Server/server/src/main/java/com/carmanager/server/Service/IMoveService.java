package com.carmanager.server.Service;

import com.carmanager.server.Entity.Move;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * 与移动信息相关的数据库管理服务，提供移动信息查询，添加的服务
 */
@Service
public interface IMoveService {

    /**
     * 新增或者更新Move
     * @param move 新的Move
     * @return 添加到数据库后的Move，带有唯一的Id
     */
    Move saveMove(Move move);

    /**
     * 分页按开始移动时间倒序获取Move对象
     * @param pageNum 页数
     * @param pageSize 页大小
     * @return 分页获取结果
     */
    Page<Move> getAllMove(int pageNum, int pageSize);

    /**
     * 按Id获取对应的Move
     * @param id Move的Id
     * @return 找到的Move
     */
    Move getMove(long id);

}
