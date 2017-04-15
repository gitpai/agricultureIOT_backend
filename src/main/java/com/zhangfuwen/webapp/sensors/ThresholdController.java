package com.zhangfuwen.webapp.sensors;

import com.zhangfuwen.info.ThresholdInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by dean on 4/16/17.
 */
@Controller
public class ThresholdController {
    Logger logger = LoggerFactory.getLogger(ThresholdController.class);

    @Autowired
    ThresholdInfoRepository thresholdInfoRepository;

    @RequestMapping(value = "/webapp/threshold", method = RequestMethod.GET)
    String thresholdList()
    {
        return "threshold-list";
    }
}
