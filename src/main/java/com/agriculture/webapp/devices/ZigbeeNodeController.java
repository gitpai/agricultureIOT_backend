package com.agriculture.webapp.devices;

import com.agriculture.models.CoilOrSensor;
import com.agriculture.models.Gateway;
import com.agriculture.models.ZigbeeNode;
import com.agriculture.repositories.CoilOrSensorRepository;
import com.agriculture.repositories.GatewayRepository;
import com.agriculture.repositories.NodeInfoRepository;
import com.agriculture.repositories.ZigbeeNodeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by dean on 4/2/17.
 */
@Controller
public class ZigbeeNodeController {
    Logger logger = LoggerFactory.getLogger(ZigbeeNodeController.class);
    @Autowired
    ZigbeeNodeRepository zigbeeNodeRepository;

    @Autowired
    NodeInfoRepository nodeInfoRepository;

    @Autowired
    CoilOrSensorRepository coilOrSensorRepository;

    @Autowired
    GatewayRepository gatewayRepository;

    @RequestMapping(value = "/webapp/nodes", method = RequestMethod.GET)
    public String list(Model model, @ModelAttribute("gatewayid") Long gatewayid) {
        List<ZigbeeNode> nodes = new ArrayList<ZigbeeNode>();
        Gateway gateway = gatewayRepository.findOne(gatewayid);
        for (Byte i = 1; i < 64; i++) {
            List<ZigbeeNode> ns = zigbeeNodeRepository.findTop1ByNodeAddrAndGatewayOrderByIdDesc(i, gateway);
            if (!ns.isEmpty()) {
                ZigbeeNode node = ns.get(0);
                node.info = nodeInfoRepository.findTop1ByGatewayIdAndNodeAddr(gatewayid, node.getNodeAddr());
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
