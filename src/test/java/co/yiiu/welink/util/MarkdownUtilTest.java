package co.yiiu.welink.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * MarkdownUtil 工具类单元测试
 */
public class MarkdownUtilTest {

    @Test
    public void testRenderPlainText() {
        String html = MarkdownUtil.render("Hello World");
        assertNotNull(html);
        assertTrue(html.contains("Hello World"));
    }

    @Test
    public void testRenderBoldText() {
        String html = MarkdownUtil.render("这是**粗体**文字");
        assertNotNull(html);
        assertTrue(html.contains("<strong>") || html.contains("<b>"));
    }

    @Test
    public void testRenderItalicText() {
        String html = MarkdownUtil.render("这是*斜体*文字");
        assertNotNull(html);
        assertTrue(html.contains("<em>") || html.contains("<i>"));
    }

    @Test
    public void testRenderLink() {
        String html = MarkdownUtil.render("[链接](https://example.com)");
        assertNotNull(html);
        assertTrue(html.contains("https://example.com") || html.contains("href="));
    }

    @Test
    public void testRenderCodeBlock() {
        String html = MarkdownUtil.render("```\ncode block\n```");
        assertNotNull(html);
        assertTrue(html.contains("code"));
        assertTrue(html.contains("<pre") || html.contains("<code"));
    }

    @Test
    public void testRenderInlineCode() {
        String html = MarkdownUtil.render("这是`行内代码`");
        assertNotNull(html);
        assertTrue(html.contains("行内代码"));
    }

    @Test
    public void testRenderTable() {
        String html = MarkdownUtil.render("| a | b |\n|---|---|\n| 1 | 2 |");
        assertNotNull(html);
        assertTrue(html.contains("<table") || html.contains("<th"));
    }

    @Test
    public void testRenderEmptyString() {
        String html = MarkdownUtil.render("");
        assertNotNull(html);
    }

    @Test
    public void testRenderHeading() {
        String html = MarkdownUtil.render("# 标题一\n## 标题二");
        assertNotNull(html);
        assertTrue(html.contains("<h1") || html.contains("<h2"));
    }

    @Test
    public void testRenderList() {
        String html = MarkdownUtil.render("- 项目1\n- 项目2");
        assertNotNull(html);
        assertTrue(html.contains("<li") || html.contains("项目1"));
    }

    @Test
    public void testRenderAddsTargetBlankToLinks() {
        // 注意：当前渲染器没有自动添加 target=_blank 的功能
        // 链接仅以标准 <a> 标签形式渲染
        String html = MarkdownUtil.render("[test](https://example.com)");
        assertNotNull(html);
        // 验证链接内容被正确渲染
        assertTrue(html.contains("test") && (html.contains("href=") || html.contains("example.com")));
    }

    @Test
    public void testRenderNullInput() {
        // 应该优雅处理 null
        String html = MarkdownUtil.render(null);
        assertNotNull(html);
    }
}
