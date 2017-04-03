package com.zhangfuwen.webapp.sensors;

import com.zhangfuwen.collector.*;
import com.zhangfuwen.webapp.user.SysUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dean on 4/2/17.
 */
@Controller
public class ZigbeeNodeController {
    Logger logger = LoggerFactory.getLogger(ZigbeeNodeController.class);
    @Autowired
    ZigbeeNodeRepository zigbeeNodeRepository;

    @Autowired
    CoilOrSensorRepository coilOrSensorRepository;

    @Autowired
    GatewayRepository gatewayRepository;

    @RequestMapping(value = "/webapp/nodes", method = RequestMethod.GET)
    public String list(Model model, @ModelAttribute("gatewayid") Long gatewayid) {
        List<ZigbeeNode> nodes = new ArrayList<ZigbeeNode>();
        Gateway gateway = gatewayRepository.findOne(gatewayid);
        for(Byte i = 1; i<64;i++) {
            List<ZigbeeNode> ns = zigbeeNodeRepository.findTop1ByNodeAddrAndGatewayOrderByIdDesc(i,gateway);
            if(!ns.isEmpty())
            {
                ZigbeeNode node = ns.get(0);
                List<CoilOrSensor> sensors = coilOrSensorRepository.findAllByNode(node);
                node.setCoilOrSensors(sensors);
                logger.info(sensors.toString());
                nodes.add(node);
                logger.info(node.toString());
                logger.info(nodes.toString());
            }
        }
        model.addAttribute("nodes", nodes);
        return "nodes";
    }
}
