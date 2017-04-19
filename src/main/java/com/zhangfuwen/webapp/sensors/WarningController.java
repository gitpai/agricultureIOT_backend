package com.zhangfuwen.webapp.sensors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhangfuwen.collector.CoilOrSensorRepository;
import com.zhangfuwen.info.NodeInfo;
import com.zhangfuwen.info.ThresholdInfoRepository;
import com.zhangfuwen.info.Warning;
import com.zhangfuwen.info.WarningRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by dean on 4/16/17.
 */
@Controller
public class WarningController {
    Logger logger = LoggerFactory.getLogger(WarningController.class);

    @Autowired
    WarningRepository warningRepository;

    @Autowired
    ThresholdInfoRepository thresholdInfoRepository;
    @Autowired
    CoilOrSensorRepository coilOrSensorRepository;

    @RequestMapping(value = "/api/warnings", method = RequestMethod.GET)
    @ResponseBody
    public String warningsAPI() {
        List<Warning> warningList = warningRepository.findAll();
        for(Warning w : warningList) {
            w.readout = coilOrSensorRepository.findOne(w.getReadoutId());
            w.thresholdInfo = thresholdInfoRepository.findOne(w.getThresholdId());
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        String warnings;
        try {
            warnings = mapper.writeValueAsString(warningList);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());

            return "error";
        }
        return warnings;
    }
    @RequestMapping(value = "/webapp/warnings", method = RequestMethod.GET)
    public String delNodeInfo(Model model) {
        List<Warning> warningList = warningRepository.findAll();
        for(Warning w : warningList) {
            w.readout = coilOrSensorRepository.findOne(w.getReadoutId());
            w.thresholdInfo = thresholdInfoRepository.findOne(w.getThresholdId());
        }
        model.addAttribute("warnings", warningList);
        return "warnings";
    }
}
