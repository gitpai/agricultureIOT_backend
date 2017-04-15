package com.zhangfuwen.webapp.sensors;

import com.zhangfuwen.info.NodeInfo;
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

import java.util.List;

/**
 * Created by dean on 4/16/17.
 */
@Controller
public class WarningController {
    Logger logger = LoggerFactory.getLogger(WarningController.class);

    @Autowired
    WarningRepository warningRepository;

    @RequestMapping(value = "/webapp/warnings", method = RequestMethod.GET)
    public String delNodeInfo(Model model, @ModelAttribute("gatewayid") Long gatewayid, @ModelAttribute("nodeaddr") Byte nodeaddr) {
        List<Warning> warningList = warningRepository.findAll();
        model.addAttribute("warnings", warningList);
        return "warnings";
    }
}
