package com.zhangfuwen.repositories;
import com.zhangfuwen.models.ThresholdInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by dean on 4/8/17.
 */
public interface ThresholdInfoRepository extends JpaRepository<ThresholdInfo, Long> {
    ThresholdInfo findOneByGatewayIdAndNodeAddrAndChannel(Long gatewayId, Byte nodeAddr, Byte channel);
    List<ThresholdInfo> findByGatewayId(Long gatewayId);
}
