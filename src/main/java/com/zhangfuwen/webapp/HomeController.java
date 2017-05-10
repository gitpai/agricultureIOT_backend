package com.zhangfuwen.webapp;

import org.h2.util.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * HomeController
 */
@Controller
public class HomeController {
//    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
//    public String welcome(Model model) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String username = auth.getName();
//        model.addAttribute("username", username);
//        return "index";
//    }

    @RequestMapping(value = {"/uploaded/{filename}.{fileext}"}, method = RequestMethod.GET)
    public ResponseEntity<byte[]> upload(Model model, @PathVariable String filename, @PathVariable String fileext) {
        String filePath = "./upload-dir/" + filename+"."+fileext;
        try (RandomAccessFile f = new RandomAccessFile(filePath, "r")) {
            byte[] b = new byte[(int) f.length()];
            f.readFully(b);
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<byte[]>(b, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getLocalizedMessage());

            return new ResponseEntity<byte[]>(HttpStatus.BAD_GATEWAY);
        }
    }

}