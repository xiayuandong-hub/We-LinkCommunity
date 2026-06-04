package co.yiiu.welink.controller.admin;

import co.yiiu.welink.config.service.SensitiveWordFilterService;
import co.yiiu.welink.model.SensitiveWord;
import co.yiiu.welink.service.ISensitiveWordService;
import co.yiiu.welink.util.Result;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by We-Link.
 * Copyright (c) 2018, All Rights Reserved.
 * https://atjiu.github.io
 */
@Controller
@RequestMapping("/admin/sensitive_word")
public class SensitiveWordAdminController extends BaseAdminController {

    private static final int SENSITIVE_WORD_MAX_LENGTH = 10;

    @Autowired
    @Resource
    private ISensitiveWordService sensitiveWordService;
    @Resource
    private SensitiveWordFilterService sensitiveWordFilterService;

    @RequiresPermissions("sensitive_word:list")
    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") Integer pageNo, String word, Model model) {
//        word= SecurityUtil.sanitizeInput(word);
        model.addAttribute("page", sensitiveWordService.page(pageNo, word));
        model.addAttribute("word", word);
        return "admin/sensitive_word/list";
    }

    @RequiresPermissions("sensitive_word:add")
    @PostMapping("/add")
    @ResponseBody
    public Result add(String word) {
        word = normalizeWord(word);
        Result validateResult = validateWord(word);
        if (validateResult != null) return validateResult;
        Result duplicateResult = validateDuplicateWord(null, word);
        if (duplicateResult != null) return duplicateResult;

        SensitiveWord sensitiveWord = new SensitiveWord();
        sensitiveWord.setWord(word);
        sensitiveWordService.save(sensitiveWord);
        sensitiveWordFilterService.refresh();
        return success();
    }

    @RequiresPermissions("sensitive_word:edit")
    @PostMapping("/edit")
    @ResponseBody
    public Result edit(Integer id, String word) {
        word = normalizeWord(word);
        Result validateResult = validateWord(word);
        if (validateResult != null) return validateResult;
        Result duplicateResult = validateDuplicateWord(id, word);
        if (duplicateResult != null) return duplicateResult;

        sensitiveWordService.updateWordById(id, word);
        sensitiveWordFilterService.refresh();
        return success();
    }

    @RequiresPermissions("sensitive_word:delete")
    @GetMapping("/delete")
    @ResponseBody
    public Result delete(Integer id) {
        sensitiveWordService.deleteById(id);
        sensitiveWordFilterService.refresh();
        return success();
    }

    private String normalizeWord(String word) {
        return word == null ? null : word.trim();
    }

    private Result validateWord(String word) {
        if (StringUtils.isEmpty(word)) return error("不能为空");
        if (word.length() > SENSITIVE_WORD_MAX_LENGTH) return error("敏感词不能超过10个字");
        return null;
    }

    private Result validateDuplicateWord(Integer id, String word) {
        SensitiveWord sensitiveWord = sensitiveWordService.selectByWord(word);
        if (sensitiveWord != null && (id == null || !sensitiveWord.getId().equals(id))) {
            return error("已存在");
        }
        return null;
    }
}
