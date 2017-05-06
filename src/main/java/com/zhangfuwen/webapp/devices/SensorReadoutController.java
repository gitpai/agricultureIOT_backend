package com.zhangfuwen.webapp.devices;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhangfuwen.models.CoilOrSensor;
import com.zhangfuwen.repositories.CoilOrSensorRepository;
import com.zhangfuwen.repositories.GatewayRepository;
import com.zhangfuwen.repositories.NodeInfoRepository;
import com.zhangfuwen.repositories.ZigbeeNodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by dean on 5/6/17.
 */
@Controller
public class SensorReadoutController {
    Logger logger = LoggerFactory.getLogger(ZigbeeNodeController.class);
    @Autowired
    ZigbeeNodeRepository zigbeeNodeRepository;

    @Autowired
    NodeInfoRepository nodeInfoRepository;

    @Autowired
    CoilOrSensorRepository coilOrSensorRepository;

    @Autowired
    GatewayRepository gatewayRepository;

    @RequestMapping(value = "/webapp/realtime", method = RequestMethod.GET)
    public String realtimePage(Model model) {
        model.addAttribute("gateways", gatewayRepository.findAll());
        return "realtime";
    }

    @RequestMapping(value = "/api/realtime", method = RequestMethod.GET)
    @ResponseBody
    public String realtime(Model model,
                           @ModelAttribute(name = "gatewayid") Long gatewayid,
                           @ModelAttribute(name = "nodeaddr") Byte nodeaddr,
                           @ModelAttribute(name = "channel") Byte channel
                           )
    {
        Pageable limit = new PageRequest(0,10);
        List<CoilOrSensor> coilOrSensors = coilOrSensorRepository.
                findTop30ByGatewayIdAndNodeAddrAndChannelOrderByCreatedDesc(gatewayid,nodeaddr,channel);
        String sensorsString;
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        try {
            sensorsString = mapper.writeValueAsString(coilOrSensors);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            return "redirect:/";
        }
        return sensorsString;
    }

    @RequestMapping(value = "/webapp/history", method = RequestMethod.GET)
    public String history(Model model) {
        model.addAttribute("gateways", gatewayRepository.findAll());
        return "history";
    }


    @RequestMapping(value = "/webapp/trend", method = RequestMethod.GET)
    @ResponseBody
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
        //logger.validators(since.toString()+"  " + until.toString());
        List<CoilOrSensor> sensors = coilOrSensorRepository.findByGatewayIdAndNodeAddrAndChannelAndCreatedBetween(
                gatewayid, nodeaddr, channel,
                since, until
        );
        String sensorsString;
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        try {
            sensorsString = mapper.writeValueAsString(sensors);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            return "redirect:/";
        }

        return sensorsString;
    }
}
