package com.zhangfuwen.webapp.sensors;

import com.fasterxml.jackson.core.JsonProcessingException;
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

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.RestController;

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
        for (Byte i = 1; i < 64; i++) {
            List<ZigbeeNode> ns = zigbeeNodeRepository.findTop1ByNodeAddrAndGatewayOrderByIdDesc(i, gateway);
            if (!ns.isEmpty()) {
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

    @RequestMapping(value = "/webapp/history", method = RequestMethod.GET)
    public String history(Model model) {
        model.addAttribute("gateways", gatewayRepository.findAll());
        return "history";
    }


    @RequestMapping(value = "/webapp/trend", method = RequestMethod.GET)
    public String trend(Model model,
                        @ModelAttribute(name = "gatewayid") Long gatewayid,
                        @ModelAttribute(name = "nodeaddr") Byte nodeaddr,
                        @ModelAttribute(name = "channel") Byte channel,
                        @ModelAttribute(name = "historyType") String historyType,
                        @ModelAttribute(name = "date") String date,
                        @ModelAttribute(name = "start") String start,
                        @ModelAttribute(name = "end") String end
    ) {
        Timestamp since;
        Timestamp until;
        long DAY_IN_MS = 1000 * 60 * 60 * 24;

        Date sevenDayAgo = new Date();
        Date date2, start2, end2;
        try {
            if ("week".equals(historyType)) {
                sevenDayAgo = new Date(System.currentTimeMillis() - (30 * DAY_IN_MS));
                since = new Timestamp(sevenDayAgo.getTime());
                until = new Timestamp(new Date().getTime());
            } else if ("day".equals(historyType)) {
                date2 = new SimpleDateFormat("yyyy-MM-dd").parse(date);
                since = new Timestamp(date2.getTime());
                until = new Timestamp(since.getTime() + DAY_IN_MS);
            } else if ("hour".equals(historyType)) {
                start2 = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(start);
                end2 = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(end);
                since = new Timestamp(start2.getTime());
                until = new Timestamp(end2.getTime());
            }else{
                return "error";
            }
        } catch (ParseException e) {
            logger.error(e.getMessage());
            return "error";
        }
        logger.info(since.toString()+"  " + until.toString());
        List<CoilOrSensor> sensors = coilOrSensorRepository.findByGatewayIdAndNodeAddrAndChannelAndCreatedBetween(
                gatewayid, nodeaddr, channel,
                since, until
        );
//        List<CoilOrSensor> sensors = coilOrSensorRepository.findByGatewayIdAndNodeAddrAndChannelAndCreatedAfter(
//                gatewayid,nodeaddr,channel,
//                since
//        );
        logger.info(sensors.toString());
        String sensorsString;
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        try {
            sensorsString = mapper.writeValueAsString(sensors);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            return "redirect:/";
        }

        model.addAttribute("sensors", sensorsString);
        return "trend";
    }
}
