package co.yiiu.welink.controller.front;

import co.yiiu.welink.controller.api.BaseApiController;
import co.yiiu.welink.util.FileUtil;
import co.yiiu.welink.util.captcha.Captcha;
import co.yiiu.welink.util.captcha.GifCaptcha;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by We-Link.
 * Copyright (c) 2018, All Rights Reserved.
 * https://atjiu.github.io
 */
@Controller
@RequestMapping("/common")
public class CommonController extends BaseApiController {

    @Resource
    private FileUtil fileUtil;

    // gif 验证码
    @GetMapping("/captcha")
    public void captcha(HttpServletResponse response, HttpSession session) throws IOException {
        Captcha captcha = new GifCaptcha();
        captcha.out(response.getOutputStream());
        String text = captcha.text();
        session.setAttribute("_captcha", text);
    }

    @GetMapping("/show_img")
    public String showOssImg(String name) {
        return "redirect:" + fileUtil.generatorOssUrl(name);
    }

}
