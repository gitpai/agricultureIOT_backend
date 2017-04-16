package com.zhangfuwen.webapp.sensors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhangfuwen.collector.Gateway;
import com.zhangfuwen.collector.GatewayRepository;
import com.zhangfuwen.info.NodeInfo;
import com.zhangfuwen.info.NodeInfoRepository;
import com.zhangfuwen.webapp.user.SysUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.Id;
import java.text.SimpleDateFormat;
import java.util.List;

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

    @RequestMapping(value = "/webapp/nodeinfo/add", method = RequestMethod.POST)
    public ResponseEntity<String> addNodeInfo(Model model, @ModelAttribute("nodeInfoFrom") NodeInfo nodeInfoFrom) {
        logger.info(nodeInfoFrom.toString());
        NodeInfo info = nodeInfoRepository.findTop1ByGatewayIdAndNodeAddr(nodeInfoFrom.getGatewayId(), nodeInfoFrom.getNodeAddr());
        if(info==null){
            nodeInfoRepository.save(nodeInfoFrom);
        }else{
            nodeInfoFrom.setId(info.getId());
            nodeInfoRepository.save(nodeInfoFrom);
        }

        return ResponseEntity.ok("");

    }

}
