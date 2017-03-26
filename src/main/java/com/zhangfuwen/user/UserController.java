package com.zhangfuwen.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Created by dean on 3/26/17.
 */
@Controller
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private SysRoleService roleService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserValidator userValidator;

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registration(Model model) {
        model.addAttribute("userForm", new SysUser());

        return "sign-up";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registration(@ModelAttribute("userForm") SysUser userForm, BindingResult bindingResult, Model model) {
        userValidator.validate(userForm, bindingResult);
        logger.info(bindingResult.getAllErrors().toString());

        if (bindingResult.hasErrors()) {
            return "sign-up";
        }

        userService.save(userForm);

        securityService.autologin(userForm.getUsername(), userForm.getPasswordConfirm());

        return "redirect:/welcome";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        return "login";
    }

    @RequestMapping(value = {"/", "/welcome"}, method = RequestMethod.GET)
    public String welcome(Model model) {
        return "index";
    }

    @RequestMapping(value = {"/users"}, method = RequestMethod.GET)
    public String users(Model model) {
        model.addAttribute("users", userService.listUsers());
        return "users";
    }

    @RequestMapping(value = {"/user/show"}, method = RequestMethod.GET)
    public String show_user(@RequestParam String username, Model model) {
        SysUser user = userService.findByUsername(username);
        model.addAttribute("user", user);
        return "user-edit";
    }

    @RequestMapping(value = {"/user/edit"}, method = RequestMethod.GET)
    public String edit_user(@RequestParam String username, Model model) {
        model.addAttribute("user", userService.findByUsername(username));
        return "user-edit";
    }

    @RequestMapping(value = {"/user/delete"}, method = RequestMethod.POST)
    public String delete_user(@RequestParam String username) {
        userService.deleteByUsername(username);
        return "redirect:/users";
    }

    @RequestMapping(value = {"/user/promote"}, method = RequestMethod.POST)
    public String promote_user(@RequestParam String username, @RequestParam String role, Model model) {
        Long role_id;
        if (role.equals("admin")) {
            role_id = new Long(2);
        } else if (role.equals("expert")) {
            role_id = new Long(3);
        } else {
            return "error";
        }
        SysUser user = userService.findByUsername(username);
        user.getRoles().clear();
        SysRole newRole = roleService.getRoleById(role_id);
        user.getRoles().add(newRole);

        userService.save(user);
        model.addAttribute("user", userService.findByUsername(username));
        return "redirect:/users";
    }

    @RequestMapping(value = {"/user/demote"}, method = RequestMethod.POST)
    public String demote_user(@RequestParam String username, Model model) {
        SysUser user = userService.findByUsername(username);
        user.getRoles().clear();
        SysRole newRole = roleService.getRoleById(new Long(4));
        user.getRoles().add(newRole);
        userService.save(user);
        model.addAttribute("user", userService.findByUsername(username));
        return "redirect:/users";
    }

//    @RequestMapping(value = "/registration", method = RequestMethod.POST)
//    public String update_user(@ModelAttribute("userForm") SysUser userForm, BindingResult bindingResult, Model model) {
//        userValidator.validate(userForm, bindingResult);
//        logger.info(bindingResult.getAllErrors().toString());
//
//        if (bindingResult.hasErrors()) {
//            return "sign-up";
//        }
//
//        userService.save(userForm);
//
//        securityService.autologin(userForm.getUsername(), userForm.getPasswordConfirm());
//
//        return "redirect:/welcome";
//    }
}
