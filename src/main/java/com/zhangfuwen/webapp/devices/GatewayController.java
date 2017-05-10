package com.zhangfuwen.webapp.devices;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhangfuwen.models.Gateway;
import com.zhangfuwen.repositories.GatewayRepository;
import com.zhangfuwen.CollectorTask;
import com.zhangfuwen.services.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * Created by dean on 4/3/17.
 */
@Controller
public class GatewayController {
    Logger logger = LoggerFactory.getLogger(GatewayController.class);
    @Autowired
    GatewayRepository gatewayRepository;

    @Autowired
    StorageService storageService;

    @RequestMapping(value = {"/webapp/gateways" ,"/","/index","/index.html"}, method = RequestMethod.GET)
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

    /**
     * 填加网关post controller
     * @param model
     * @param redirectAttributes
     * @param name
     * @param ip
     * @param port
     * @param max_nodes
     * @param max_channels
     * @param interval
     * @param X
     * @param Y
     * @param desc
     * @param file
     * @return
     */
    @RequestMapping(value = {"/webapp/gateways/add"}, method = RequestMethod.POST)
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
                      @RequestParam("pic") MultipartFile file)
    {
        CollectorTask.gatewayLock.lock();
        Gateway gateway = new Gateway(name,ip, port,max_nodes, max_channels, new HashMap<Byte,String>());
        gateway.setInterval(interval);
        gateway.setX(X);
        gateway.setY(Y);
        gateway.setDesc(desc);

        if(!file.isEmpty())
        {
            gateway.setPic(storageService.store(file));
        }

        gatewayRepository.save(gateway);
        CollectorTask.setGateways(null);
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
        CollectorTask.setGateways(null);
        CollectorTask.gatewayLock.unlock();
        String referer = "/webapp/gateways";
        redirectAttributes.addFlashAttribute("message", "删除成功");
        redirectAttributes.addFlashAttribute("referer", referer);
        return "redirect:/flash";
    }
}
