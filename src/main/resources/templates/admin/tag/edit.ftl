<#include "../layout/layout.ftl">
<#assign isAdd = isAdd!false>
<@html page_title=isAdd?string("添加标签", "标签编辑") page_tab="tag">
    <section class="content-header">
        <h1>
            标签
            <small>${isAdd?string("添加", "编辑")}</small>
        </h1>
        <ol class="breadcrumb">
            <li><a href="/admin/index"><i class="fa fa-dashboard"></i> 首页</a></li>
            <li><a href="/admin/tag/list">标签</a></li>
            <li class="active">${isAdd?string("添加", "编辑")}</li>
        </ol>
    </section>
    <section class="content">
        <div class="box box-info">
            <div class="box-header with-border">
                <h3 class="box-title">${isAdd?string("添加标签", "标签编辑")}</h3>
            </div>
            <!-- /.box-header -->
            <div class="box-body">
                <#if error??>
                    <div class="alert alert-danger">${error}</div>
                </#if>
                <form id="form" action="${isAdd?string('/admin/tag/add', '/admin/tag/edit')}" method="post"
                      enctype="multipart/form-data">
                    <input type="hidden" value="${tag.id!}" name="id">
                    <div class="form-group">
                        <label>名称</label>
                        <input type="text" name="name" value="${tag.name!}" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label>话题数</label>
                        <input type="number" min="0" pattern="\d" name="topicCount" value="${tag.topicCount!0}"
                               class="form-control">
                    </div>
                    <div class="form-group">
                        <label>Icon</label>
                        <input type="file" name="file" class="form-control"><br>
                        <#if tag.icon??>
                            <img src="${tag.icon!}" width="50" alt="">
                        </#if>
                    </div>
                    <div class="form-group">
                        <label for="">描述</label>
                        <textarea name="description" rows="7" class="form-control">${tag.description!}</textarea>
                    </div>
                    <button type="submit" id="btn" class="btn btn-primary">${isAdd?string("添加标签", "提交")}</button>
                </form>
            </div>
        </div>
    </section>
</@html>
