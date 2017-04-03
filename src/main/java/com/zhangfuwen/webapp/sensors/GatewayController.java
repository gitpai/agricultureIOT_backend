package com.zhangfuwen.webapp.sensors;

import com.zhangfuwen.collector.Gateway;
import com.zhangfuwen.collector.GatewayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dean on 4/3/17.
 */
@Controller
public class GatewayController {
    @Autowired
    GatewayRepository gatewayRepository;

    @RequestMapping(value = "/webapp/gateways", method = RequestMethod.GET)
    public String list(Model model) {
        Iterable<Gateway> gatewayList = gatewayRepository.findAll();
        model.addAttribute("gateways", gatewayList);
        return "gateway-list";
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
                      @ModelAttribute(name="max_channels") int max_channels)
    {
        Gateway gateway = new Gateway(name,ip, port,max_nodes, max_channels, new HashMap<Byte,String>());
        gatewayRepository.save(gateway);
        String referer = "/webapp/gateways";
        redirectAttributes.addFlashAttribute("message", "添加成功");
        redirectAttributes.addFlashAttribute("referer", referer);
        return "redirect:/flash";
    }

    @RequestMapping(value = "/webapp/gateways/delete", method = RequestMethod.GET)
    public String delete(Model model, @ModelAttribute(name="gatewayid") Long gatewayid, HttpServletRequest request,
                         final RedirectAttributes redirectAttributes) {
        Gateway gateway = gatewayRepository.findOne(gatewayid);
        if(gateway==null) {
            String referer = "/webapp/gateways";
            redirectAttributes.addFlashAttribute("message", "未找到该网关");
            redirectAttributes.addFlashAttribute("referer", referer);
            return "redirect:/flash";
        }
        gatewayRepository.delete(gatewayid);
        String referer = "/webapp/gateways";
        redirectAttributes.addFlashAttribute("message", "删除成功");
        redirectAttributes.addFlashAttribute("referer", referer);
        return "redirect:/flash";
    }
}
