package com.github.gelald.batch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.gelald.batch.entity.Maintain;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author WuYingBin
 * date: 2022/12/26
 */
@Repository
public interface MaintainMapper extends BaseMapper<Maintain> {
    void clearData();

    void resetAutoIncrement();

    void insertMaintain(@Param("maintain") Maintain maintain);

    void insertBatchMaintain(@Param("maintainList") List<Maintain> maintains);
}
