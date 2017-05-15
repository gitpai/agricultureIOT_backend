package com.agriculture.webapp.devices;

import com.agriculture.models.Warning;
import com.agriculture.repositories.CoilOrSensorRepository;
import com.agriculture.repositories.GatewayRepository;
import com.agriculture.repositories.ThresholdInfoRepository;
import com.agriculture.repositories.WarningRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    @Autowired
    GatewayRepository gatewayRepository;
    /**
     * close warning
     * @param id warning id
     * @return
     */
    @RequestMapping(value="/api/warnings/del", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> shut(
            @ModelAttribute(name="id") Long id
    )
    {
        Warning w = warningRepository.findOne(id);
        if(w==null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else {
            w.setClosed(new Timestamp(new Date().getTime()));
            warningRepository.save(w);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * get a list of all warnings, order by created, most recent items first
     * @return
     */
    @RequestMapping(value = "/api/warnings", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> warningsAPI() {
        List<Warning> warningList = warningRepository.findAll();
        for(Warning w : warningList) {
            w.readout = coilOrSensorRepository.findOne(w.getReadoutId());
            w.readout.gateway = gatewayRepository.findOne(w.readout.gatewayId);
            w.thresholdInfo = thresholdInfoRepository.findOne(w.getThresholdId());
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        String warnings;
        try {
            warnings = mapper.writeValueAsString(warningList);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());

            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(warnings, HttpStatus.OK);
    }

    /**
     * get warnings page
     * @param model
     * @return
     */
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
