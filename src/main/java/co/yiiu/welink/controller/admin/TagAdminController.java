package co.yiiu.welink.controller.admin;

import co.yiiu.welink.model.Tag;
import co.yiiu.welink.service.ITagService;
import co.yiiu.welink.util.FileUtil;
import co.yiiu.welink.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by We-Link.
 * Copyright (c) 2018, All Rights Reserved.
 * https://atjiu.github.io
 */
@Controller
@RequestMapping("/admin/tag")
public class TagAdminController extends BaseAdminController {

    @Resource
    private ITagService tagService;
    @Resource
    private FileUtil fileUtil;

    @RequiresPermissions("tag:list")
    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") Integer pageNo, String name, Model model) {
        if (name != null) name = name.replace("\"", "").replace("'", "");
//        name= SecurityUtil.sanitizeInput(name);
        if (StringUtils.isEmpty(name)) name = null;
        IPage<Tag> page = tagService.selectAll(pageNo, null, name);
        model.addAttribute("page", page);
        model.addAttribute("name", name);
        return "admin/tag/list";
    }

    @RequiresPermissions("tag:edit")
    @GetMapping("/edit")
    public String edit(Integer id, String error, Model model) {
        Tag tag = tagService.selectById(id);
        if (tag == null) return redirect("/admin/tag/list");
        model.addAttribute("tag", tag);
        model.addAttribute("error", error);
        return "admin/tag/edit";
    }

    @RequiresPermissions("tag:edit")
    @PostMapping("/edit")
    public String update(Integer id, String name, String description, Integer topicCount, MultipartFile file,
                         RedirectAttributes redirectAttributes) {
        Tag tag = tagService.selectById(id);
        if (tag == null) return redirect("/admin/tag/list");
        name = name == null ? null : name.trim();
        if (StringUtils.isEmpty(name)) {
            redirectAttributes.addAttribute("error", "标签名称不能为空");
            redirectAttributes.addAttribute("id", id);
            return redirect("/admin/tag/edit");
        }
        Tag existedTag = tagService.selectByName(name);
        if (existedTag != null && !existedTag.getId().equals(id)) {
            redirectAttributes.addAttribute("error", "标签名称已存在");
            redirectAttributes.addAttribute("id", id);
            return redirect("/admin/tag/edit");
        }
        tag.setName(name);
        tag.setDescription(description);
        tag.setTopicCount(topicCount == null || topicCount < 0 ? 0 : topicCount);
        String path = fileUtil.upload(file, null, "tag");
        if (path != null) {
            tag.setIcon(path);
        }
        tagService.update(tag);
        return redirect("/admin/tag/list");
    }

    @RequiresPermissions("tag:edit")
    @GetMapping("/add")
    public String add(String error, Model model) {
        Tag tag = new Tag();
        tag.setTopicCount(0);
        model.addAttribute("tag", tag);
        model.addAttribute("isAdd", true);
        model.addAttribute("error", error);
        return "admin/tag/edit";
    }

    @RequiresPermissions("tag:edit")
    @PostMapping("/add")
    public String save(String name, String description, Integer topicCount, MultipartFile file,
                       RedirectAttributes redirectAttributes) {
        name = name == null ? null : name.trim();
        if (StringUtils.isEmpty(name)) {
            redirectAttributes.addAttribute("error", "标签名称不能为空");
            return redirect("/admin/tag/add");
        }
        if (tagService.selectByName(name) != null) {
            redirectAttributes.addAttribute("error", "标签名称已存在");
            return redirect("/admin/tag/add");
        }
        Tag tag = new Tag();
        tag.setName(name);
        tag.setDescription(description);
        tag.setTopicCount(topicCount == null || topicCount < 0 ? 0 : topicCount);
        tag.setIcon(fileUtil.upload(file, null, "tag"));
        tag.setInTime(new Date());
        tagService.insert(tag);
        return redirect("/admin/tag/list");
    }

    @RequiresPermissions("tag:delete")
    @GetMapping("/delete")
    @ResponseBody
    public Result delete(Integer id) {
        Tag tag = tagService.selectById(id);
        if (tag.getTopicCount() > 0) return error("标签还关联着话题，要先把相关联的话题都删了，这个标签才能删除");
        tagService.delete(id);
        return success();
    }

    // 同步标签的话题数
    @RequiresPermissions("tag:async")
    @GetMapping("/async")
    @ResponseBody
    public Result async() {
        tagService.async();
        return success();
    }
}
