package com.github.gelald.batch.strategy;

import com.github.gelald.batch.entity.Maintain;
import com.github.gelald.batch.enums.ImportStrategyEnum;
import com.github.gelald.batch.factory.ImportStrategyFactory;
import com.github.gelald.batch.util.HikariCPUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.util.List;

/**
 * for循环+jdbc的insert方法+批量+事务提交
 *
 * @author WuYingBin
 * date: 2022/12/26
 */
@Component
public class JDBCBatchStrategy extends AbstractImportStrategy {
    @Override
    void doRegistry() {
        ImportStrategyFactory instance = ImportStrategyFactory.getInstance();
        instance.registry(ImportStrategyEnum.JDBC_BATCH_TX_STRATEGY.getStrategyName(), this);
    }

    /*
    5w:3240,3428,3153
    10w:6606,6281,7160
    20w:12014,14157,14655
    40w:23317,25490,22860
     */
    @Override
    public void doImport(List<Maintain> maintains) {
        StopWatch stopWatch = new StopWatch();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        stopWatch.start();
        try {
            connection = HikariCPUtils.getConnection();
            // 开启事务手动提交
            connection.setAutoCommit(false);
            String sql = "insert into batch_maintain (maintain_num, maintain_name, equipment_num, maintain_type, functionary, maintain_duration, start_time, end_time, maintain_status) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(sql);
            int count = 0;
            for (Maintain maintain : maintains) {
                preparedStatement.setString(1, maintain.getMaintainNum());
                preparedStatement.setString(2, maintain.getMaintainName());
                preparedStatement.setLong(3, maintain.getEquipmentNum());
                preparedStatement.setInt(4, maintain.getMaintainType());
                preparedStatement.setString(5, maintain.getFunctionary());
                preparedStatement.setString(6, maintain.getMaintainDuration());
                preparedStatement.setTimestamp(7, Timestamp.from(maintain.getStartTime().toInstant(ZoneOffset.ofHours(8))));
                preparedStatement.setTimestamp(8, Timestamp.from(maintain.getEndTime().toInstant(ZoneOffset.ofHours(8))));
                preparedStatement.setBoolean(9, maintain.getMaintainStatus());
                preparedStatement.addBatch();
                count++;
                if (count % BATCH_SIZE == 0) {
                    // 一次性提交缓存区中的SQL语句
                    preparedStatement.executeBatch();
                    count = 0;
                }
            }
            if (count != 0) {
                preparedStatement.executeBatch();
            }
            // 提交事务
            connection.commit();
        } catch (SQLException exception) {
            exception.printStackTrace();
        } finally {
            stopWatch.stop();
            HikariCPUtils.close(preparedStatement, connection);
            System.out.println("jdbc批处理事务插入方式花费时间 ==> " + stopWatch.getLastTaskTimeMillis());
        }
    }

}