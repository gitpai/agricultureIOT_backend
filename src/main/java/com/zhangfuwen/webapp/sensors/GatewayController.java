package com.zhangfuwen.webapp.sensors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhangfuwen.Application;
import com.zhangfuwen.collector.Gateway;
import com.zhangfuwen.collector.GatewayRepository;
import com.zhangfuwen.webapp.CollectorTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dean on 4/3/17.
 */
@Controller
public class GatewayController {
    Logger logger = LoggerFactory.getLogger(GatewayController.class);
    @Autowired
    GatewayRepository gatewayRepository;

    @RequestMapping(value = "/webapp/gateways", method = RequestMethod.GET)
    public String list(Model model) {
        Iterable<Gateway> gatewayList = gatewayRepository.findAll();
        model.addAttribute("gateways", gatewayList);
        return "gateway-list";
    }

    @RequestMapping(value = "/api/gateways", method = RequestMethod.GET)
    @ResponseBody
    public String listJson(Model model) {
        Iterable<Gateway> gatewayList = gatewayRepository.findAll();
        String gateways;
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        try {
            gateways = mapper.writeValueAsString(gatewayList);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());

            return "error";
        }
        return gateways;
    }

    @RequestMapping(value = "/webapp/gateways/add", method = RequestMethod.GET)
    public String addpage(Model model) {
        return "gateways-add";
    }
    @RequestMapping(value = "/webapp/gateways/add", method = RequestMethod.POST)
    public String add(Model model, final RedirectAttributes redirectAttributes,
                      @ModelAttribute(name = "name") String name,
                      @ModelAttribute(name="ip") String ip,
                      @ModelAttribute(name = "port") int port,
                      @ModelAttribute(name="max_nodes") int max_nodes,
                      @ModelAttribute(name="max_channels") int max_channels,
                      @ModelAttribute(name="interval") int interval,
                      @ModelAttribute(name="X") Float X,
                      @ModelAttribute(name="Y") Float Y,
                      @ModelAttribute(name="desc") String desc,
                      @ModelAttribute(name="pic") String  pic)
    {
        CollectorTask.gatewayLock.lock();
        Gateway gateway = new Gateway(name,ip, port,max_nodes, max_channels, new HashMap<Byte,String>());
        gateway.setInterval(interval);
        gateway.setX(X);
        gateway.setY(Y);
        gateway.setDesc(desc);
        gateway.setPic(pic);
        gatewayRepository.save(gateway);
        CollectorTask.gateways = null;
        CollectorTask.gatewayLock.unlock();
        String referer = "/webapp/gateways";
        redirectAttributes.addFlashAttribute("message", "添加成功");
        redirectAttributes.addFlashAttribute("referer", referer);
        return "redirect:/flash";
    }

    @RequestMapping(value = "/webapp/gateways/delete", method = RequestMethod.GET)
    public String delete(
            Model model,
            @ModelAttribute(name="gatewayid") Long gatewayid,
            HttpServletRequest request,
            final RedirectAttributes redirectAttributes)
    {
        Gateway gateway = gatewayRepository.findOne(gatewayid);
        if(gateway==null) {
            String referer = "/webapp/gateways";
            redirectAttributes.addFlashAttribute("message", "未找到该网关");
            redirectAttributes.addFlashAttribute("referer", referer);
            return "redirect:/flash";
        }

        CollectorTask.gatewayLock.lock();
        gatewayRepository.delete(gatewayid);
        CollectorTask.gateways = null;
        CollectorTask.gatewayLock.unlock();
        String referer = "/webapp/gateways";
        redirectAttributes.addFlashAttribute("message", "删除成功");
        redirectAttributes.addFlashAttribute("referer", referer);
        return "redirect:/flash";
    }
}
