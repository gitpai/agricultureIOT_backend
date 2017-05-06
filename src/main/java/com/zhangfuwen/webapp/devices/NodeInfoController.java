package com.zhangfuwen.webapp.devices;

import com.zhangfuwen.repositories.GatewayRepository;
import com.zhangfuwen.models.NodeInfo;
import com.zhangfuwen.repositories.NodeInfoRepository;
import com.zhangfuwen.services.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Created by dean on 4/8/17.
 */
@Controller
public class NodeInfoController {
    Logger logger = LoggerFactory.getLogger(NodeInfoRepository.class);
    @Autowired
    GatewayRepository gatewayRepository;

    @Autowired
    NodeInfoRepository nodeInfoRepository;

    @Autowired
    StorageService storageService;

    @RequestMapping(value = "/webapp/nodeinfo", method = RequestMethod.GET)
    public String listByGateway(Model model) {
        return "nodeinfo";
    }

    @RequestMapping(value = "/webapp/delnodeinfo", method = RequestMethod.GET)
    public ResponseEntity<String> delNodeInfo(Model model, @ModelAttribute("gatewayid") Long gatewayid, @ModelAttribute("nodeaddr") Byte nodeaddr) {
        NodeInfo nodeInfo = nodeInfoRepository.findTop1ByGatewayIdAndNodeAddr(gatewayid,nodeaddr);
        nodeInfoRepository.delete(nodeInfo);
        //return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        return ResponseEntity.ok("");
    }

    @RequestMapping(value = "/webapp/nodeinfo/add", method = RequestMethod.GET)
    public String nodeInfoAdd(Model model) {
        model.addAttribute("gateways", gatewayRepository.findAll());
        return "nodeinfo-add";
    }

    /**
     * 点节信息添加接口
     * @param model
     * @param file
     * @return
     */
    @RequestMapping(value = "/webapp/nodeinfo/add", method = RequestMethod.POST)
    public String addNodeInfo(Model model,final RedirectAttributes redirectAttributes,
                                              @ModelAttribute(name = "gatewayId") Long gatewayId,
                                              @ModelAttribute(name="nodeAddr") Byte nodeAddr,
                                              @ModelAttribute(name = "nodeName") String nodeName,
                                              @ModelAttribute(name="X") Float X,
                                              @ModelAttribute(name="Y") Float Y,
                                              @ModelAttribute(name="desc") String desc,
                                              @RequestParam("pic") MultipartFile file) {
        NodeInfo nodeInfoFrom = new NodeInfo();
        nodeInfoFrom.setGatewayId(gatewayId);
        nodeInfoFrom.setNodeAddr(nodeAddr);
        nodeInfoFrom.setNodeName(nodeName);
        nodeInfoFrom.setX(X);
        nodeInfoFrom.setY(Y);
        nodeInfoFrom.setDesc(desc);
        logger.info(nodeInfoFrom.toString());
        if(!file.isEmpty())
        {
            nodeInfoFrom.setPic(storageService.store(file));
        }

        NodeInfo info = nodeInfoRepository.findTop1ByGatewayIdAndNodeAddr(nodeInfoFrom.getGatewayId(), nodeInfoFrom.getNodeAddr());
        if(info==null){
            nodeInfoRepository.save(nodeInfoFrom);
        }else{
            nodeInfoFrom.setId(info.getId());
            nodeInfoRepository.save(nodeInfoFrom);
        }

        String referer = "/webapp/nodeinfo";
        redirectAttributes.addFlashAttribute("message", "添加成功");
        redirectAttributes.addFlashAttribute("referer", referer);
        return "redirect:/flash";

    }

}
