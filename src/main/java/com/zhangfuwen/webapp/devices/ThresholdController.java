package com.zhangfuwen.webapp.devices;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhangfuwen.models.Gateway;
import com.zhangfuwen.repositories.GatewayRepository;
import com.zhangfuwen.validators.ThesholdInfoValidator;
import com.zhangfuwen.models.ThresholdInfo;
import com.zhangfuwen.repositories.ThresholdInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.SimpleDateFormat;

/**
 * Created by dean on 4/16/17.
 */
@Controller
public class ThresholdController {
    Logger logger = LoggerFactory.getLogger(ThresholdController.class);

    @Autowired
    ThresholdInfoRepository thresholdInfoRepository;

    @Autowired
    GatewayRepository gatewayRepository;

    @RequestMapping(value = "/webapp/threshold", method = RequestMethod.GET)
    String thresholdList(Model model) {
        Iterable<Gateway> gateways = gatewayRepository.findAll();
        model.addAttribute("gateways", gateways);
        return "threshold-list";
    }

    @RequestMapping(value = "/api/threshold", method = RequestMethod.GET)
    @ResponseBody
    String thresholdListAPI(
            Model model,
            @ModelAttribute(name = "gatewayid") Long gatewayid
    ) {
        Iterable<ThresholdInfo> thresholdInfos = thresholdInfoRepository.findByGatewayId(gatewayid);
        String thresholdInfoList;
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        try {
            thresholdInfoList = mapper.writeValueAsString(thresholdInfos);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());

            return "error";
        }
        return thresholdInfoList;
    }

    /**
     * 删除阈值
     * @param id 条目id
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "/webapp/threshold/del", method = RequestMethod.GET)
    String thresholdAddForm(
            @ModelAttribute("id") Long id,
            final RedirectAttributes redirectAttributes
    )
    {
        if (thresholdInfoRepository.findOne(id) != null) {
            thresholdInfoRepository.delete(id);
        }else {
            logger.info("not found");
        }
        String referer = "/webapp/threshold";
        redirectAttributes.addFlashAttribute("message", "删除成功");
        redirectAttributes.addFlashAttribute("referer", referer);
        return "redirect:/flash";
    }

    @RequestMapping(value = "/api/threshold/del", method = RequestMethod.GET)
    @ResponseBody
    String thresholdAddFormAPI(
            @ModelAttribute("id") Long id,
            final RedirectAttributes redirectAttributes
    )
    {
        if (thresholdInfoRepository.findOne(id) != null) {
            thresholdInfoRepository.delete(id);
        }else {
            logger.info("not found");
            return "not found";
        }
        return "ok";
    }

    @RequestMapping(value = "/webapp/threshold/add", method = RequestMethod.GET)
    String thresholdAddForm(Model model,
                            @ModelAttribute("thresholdInfoForm") ThresholdInfo thresholdInfoForm,
                            BindingResult bindingResult)
    {
        Iterable<Gateway> gateways = gatewayRepository.findAll();
        model.addAttribute("gateways", gateways);
        //model.addAttribute("thesholdInfoForm", thresholdInfoForm);
        return "threshold-add";
    }

    @RequestMapping(value = "/webapp/threshold/add", method = RequestMethod.POST)
    String thresholdAdd(
            Model model,
            @ModelAttribute("thresholdInfoForm") ThresholdInfo thresholdInfoForm,
            BindingResult bindingResult,
            final RedirectAttributes redirectAttributes
    )
    {
        logger.info(thresholdInfoForm.toString());
        //验证
        ThesholdInfoValidator validator = new ThesholdInfoValidator();
        validator.validate(thresholdInfoForm,bindingResult);
        if(bindingResult.hasErrors()) {
            Iterable<Gateway> gateways = gatewayRepository.findAll();
            model.addAttribute("gateways", gateways);
            model.addAttribute("thesholdInfoForm", thresholdInfoForm);
            return "threshold-add";
        }
        ThresholdInfo oldInfo = thresholdInfoRepository.findOneByGatewayIdAndNodeAddrAndChannel(thresholdInfoForm.getGatewayId(),
                thresholdInfoForm.getNodeAddr(), thresholdInfoForm.getChannel());
        if (oldInfo != null) {
            thresholdInfoForm.setId(oldInfo.getId());
        }
        thresholdInfoRepository.save(thresholdInfoForm);
        String referer = "/webapp/threshold";
        redirectAttributes.addFlashAttribute("message", "添加成功");
        redirectAttributes.addFlashAttribute("referer", referer);
        return "redirect:/flash";
    }
}
