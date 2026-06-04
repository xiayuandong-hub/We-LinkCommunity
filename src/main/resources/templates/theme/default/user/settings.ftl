<#include "../layout/layout.ftl"/>
<@html page_title="设置" page_tab="settings">
    <style>
        @import url("https://cdnjs.cloudflare.com/ajax/libs/cropperjs/1.6.2/cropper.min.css");

        .avatar-upload-link {
            color: #4fa7c7;
            font-weight: 600;
        }

        .avatar-upload-link:hover {
            color: #355668;
            text-decoration: none;
        }

        .avatar-cropper-image-wrapper {
            min-height: 320px;
            max-height: 420px;
            display: flex;
            align-items: center;
            justify-content: center;
            overflow: hidden;
            border-radius: 18px;
            background: #f7fcfe;
            border: 1px solid #d7eaf1;
        }

        .avatar-cropper-image-wrapper img {
            display: block;
            max-width: 100%;
        }

        .avatar-crop-preview {
            width: 112px;
            height: 112px;
            margin: 0 auto;
            overflow: hidden;
            border-radius: 24px;
            border: 1px solid #d7eaf1;
            background: #ffffff;
            box-shadow: 0 18px 36px -30px rgba(79, 167, 199, .22);
        }

        .avatar-crop-tip {
            margin-bottom: 0;
            color: #6d8794;
            line-height: 1.75;
        }

        .avatar-crop-actions {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 12px;
            margin-top: 16px;
            flex-wrap: wrap;
        }

        #avatarCropModal .modal-content {
            border: 0;
            border-radius: 24px;
            overflow: hidden;
            box-shadow: 0 24px 60px -36px rgba(79, 167, 199, .24);
        }

        #avatarCropModal .modal-header {
            border-bottom: 1px solid #d7eaf1;
            background: #eaf7fb;
        }

        #avatarCropModal .modal-footer {
            border-top: 1px solid #d7eaf1;
        }

        @media (max-width: 767px) {
            .avatar-cropper-image-wrapper {
                min-height: 240px;
            }

            .avatar-crop-preview {
                width: 88px;
                height: 88px;
                border-radius: 20px;
            }
        }
    </style>
    <div class="row">
        <div class="col-md-9">
            <#if !user.active>
                <div class="alert alert-danger">
                    <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span
                                aria-hidden="true">&times;</span></button>
                    <strong>你的帐号还没有激活，请进入邮箱点击激活邮箱中的链接进行激活 或者 <a href="javascript:;" id="sendActiveEmail">重新发送</a>
                        激活链接</strong>
                </div>
            </#if>
            <div class="card">
                <div class="card-header">设置</div>
                <div class="card-body">
                    <br>
                    <form class="form-horizontal" onsubmit="return;">
                        <div class="form-group row">
                            <label for="username" class="col-sm-2 control-label">用户名</label>
                            <div class="col-sm-10">
                                <input type="text" class="form-control" id="username" name="username" disabled
                                       value="${user.username}"
                                       placeholder="用户名">
                            </div>
                        </div>
                        <div class="form-group row">
                            <label for="telegramName" class="col-sm-2 control-label">Telegram用户名</label>
                            <div class="col-sm-10">
                                <input type="text" class="form-control" id="telegramName" name="telegramName"
                                       value="${user.telegramName!}"
                                       placeholder="Telegram用户名">
                            </div>
                        </div>
                        <div class="form-group row">
                            <label for="website" class="col-sm-2 control-label">个人主页</label>
                            <div class="col-sm-10">
                                <input type="text" class="form-control" id="website" name="website"
                                       value="${user.website!}"
                                       placeholder="个人主页">
                            </div>
                        </div>
                        <div class="form-group row">
                            <label for="bio" class="col-sm-2 control-label">个人简介</label>
                            <div class="col-sm-10">
                                <textarea name="bio" id="bio" rows="3" class="form-control" placeholder="个人简介">${user.bio!?html}</textarea>
                            </div>
                        </div>
                        <div class="form-group row">
                            <div class="offset-sm-2 col-sm-10">
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" id="emailNotification"
                                               <#if user.emailNotification>checked</#if>>
                                        有新消息发送邮件
                                    </label>
                                </div>
                                <#--<div class="checkbox">
                                  <label>
                                    <input type="checkbox" id="telegramNotification"> 有新消息发送Telegram通知
                                  </label>
                                </div>-->
                            </div>
                        </div>
                        <div class="form-group row">
                            <div class="offset-sm-2 col-sm-10">
                                <button type="button" id="settings_btn" class="btn btn-info">提交</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
            <div class="card">
                <div class="card-header">修改邮箱</div>
                <div class="card-body">
                    <form onsubmit="return;" class="form-horizontal">
                        <div class="form-group row">
                            <label for="email" class="col-sm-2 control-label">邮箱</label>
                            <div class="col-sm-10">
                                <div class="input-group">
                                    <input type="email" name="email" id="email" class="form-control" value="${user.email!}" placeholder="邮箱"/>
                                    <span class="input-group-append">
                                        <button type="button" id="sendEmailCode" class="btn btn-info" autocomplete="off" data-loading-text="发送中...">发送验证码</button>
                                    </span>
                                </div>
                            </div>
                        </div>
                        <div class="form-group row">
                            <label for="code" class="col-sm-2 control-label">验证码</label>
                            <div class="col-sm-10">
                                <input type="text" name="code" id="code" class="form-control" placeholder="验证码"/>
                            </div>
                        </div>
                        <div class="form-group row">
                            <div class="offset-sm-2 col-sm-10">
                                <button type="button" id="settings_email_btn" class="btn btn-info">更改邮箱</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
            <div class="card">
                <div class="card-header">修改头像</div>
                <div class="card-body">
                    <form onsubmit="return;" class="form-horizontal">
                        <div class="form-group row">
                            <label for="" class="col-sm-2 control-label" style="vertical-align: middle">当前头像</label>
                            <div class="col-sm-10">
                                <img src="${user.avatar!}" class="avatar avatar-lg" alt="avatar"/>&nbsp;&nbsp;
                                <img src="${user.avatar!}" class="avatar" style="vertical-align: bottom" alt="avatar"/>&nbsp;&nbsp;
                                <img src="${user.avatar!}" class="avatar avatar-sm" style="vertical-align: bottom" alt="avatar"/>
                            </div>
                            <div class="offset-sm-2 col-sm-10" style="margin-top: 10px;">
                                <a href="javascript:;" id="selectAvatar" class="avatar-upload-link">上传新头像并裁切</a>
                                <input type="file" class="d-none" name="file" id="file" accept="image/*"/>
                                <div class="text-muted mt-2">上传后可手动裁切为 1:1 的方形头像。</div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
            <div class="card">
                <div class="card-header">修改密码</div>
                <div class="card-body">
                    <form onsubmit="return;" class="form-horizontal">
                        <div class="form-group row">
                            <label for="oldPassword" class="col-sm-2 control-label">旧密码</label>
                            <div class="col-sm-10">
                                <input type="password" name="oldPassword" id="oldPassword" class="form-control" placeholder="旧密码"/>
                            </div>
                        </div>
                        <div class="form-group row">
                            <label for="newPassword" class="col-sm-2 control-label">新密码</label>
                            <div class="col-sm-10">
                                <input type="password" name="newPassword" id="newPassword" class="form-control" placeholder="新密码"/>
                            </div>
                        </div>
                        <div class="form-group row">
                            <div class="offset-sm-2 col-sm-10">
                                <button type="button" id="settings_pwd_btn" class="btn btn-info">更改密码</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        <div class="col-md-3 hidden-xs">
            <#include "../components/user_info.ftl"/>
            <#include "../components/token.ftl"/>
        </div>
    </div>
    <div class="modal fade" id="avatarCropModal" tabindex="-1" role="dialog" aria-labelledby="avatarCropModalLabel"
         aria-hidden="true">
        <div class="modal-dialog modal-lg modal-dialog-centered" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="avatarCropModalLabel">裁切头像</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="row">
                        <div class="col-md-8 mb-3 mb-md-0">
                            <div class="avatar-cropper-image-wrapper">
                                <img id="avatarCropperImage" alt="待裁切头像"/>
                            </div>
                        </div>
                        <div class="col-md-4 d-flex flex-column justify-content-between">
                            <div>
                                <div class="avatar-crop-preview mb-3"></div>
                                <p class="avatar-crop-tip">拖动图片并调整范围，保存后头像会按 1:1 比例更新。</p>
                            </div>
                            <div class="avatar-crop-actions">
                                <button type="button" class="btn btn-light" data-dismiss="modal">取消</button>
                                <button type="button" class="btn btn-info" id="confirmAvatarCrop">保存头像</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/cropperjs/1.6.2/cropper.min.js"></script>
    <script>
        $(function () {
            var cropper = null;
            var avatarObjectUrl = null;
            var avatarMimeType = "image/png";
            var avatarFileName = "avatar.png";
            var avatarCropImage = document.getElementById("avatarCropperImage");
            var avatarUrlCreator = window.URL || window.webkitURL;
            var $avatarCropModal = $("#avatarCropModal");
            var $confirmAvatarCrop = $("#confirmAvatarCrop");

            function resetAvatarCropper() {
                if (cropper) {
                    cropper.destroy();
                    cropper = null;
                }
                if (avatarObjectUrl && avatarUrlCreator) {
                    avatarUrlCreator.revokeObjectURL(avatarObjectUrl);
                    avatarObjectUrl = null;
                }
                avatarCropImage.removeAttribute("src");
                $("#file").val("");
                $confirmAvatarCrop.prop("disabled", false).text("保存头像");
            }

            function uploadAvatar(blob) {
                var fd = new FormData();
                fd.append("file", blob, avatarFileName);
                fd.append("type", "avatar");
                fd.append("token", "${_user.token!}");
                $.ajax({
                    url: "/api/upload",
                    type: "POST",
                    data: fd,
                    dataType: "json",
                    headers: {
                        token: "${_user.token!}"
                    },
                    processData: false,
                    contentType: false,
                    success: function (data) {
                        if (data.code === 200) {
                            if (data.detail.errors.length === 0) {
                                suc("修改头像成功");
                                $.each($(".avatar"), function (i, v) {
                                    $(v).attr("src", data.detail.urls[0]);
                                });
                                $avatarCropModal.modal("hide");
                            } else {
                                err(data.detail.errors[0]);
                            }
                        } else {
                            err(data.description);
                        }
                    },
                    error: function () {
                        err("头像上传失败，请稍后重试");
                    },
                    complete: function () {
                        $confirmAvatarCrop.prop("disabled", false).text("保存头像");
                    }
                });
            }

            $("#settings_btn").click(function () {
                var telegramName = $("#telegramName").val();
                var website = $("#website").val();
                var bio = $("#bio").val();
                var emailNotification = $("#emailNotification").is(":checked");
                req("put", "/api/settings", {
                    telegramName: telegramName,
                    website: website,
                    bio: bio,
                    emailNotification: emailNotification,
                }, "${_user.token!}", function (data) {
                    if (data.code === 200) {
                        suc("更新个人资料成功");
                        setTimeout(function () {
                            window.location.reload();
                        }, 700);
                    } else {
                        err(data.description);
                    }
                });
            });

            // 上传头像
            $("#selectAvatar").click(function () {
                $("#file").click();
            });
            $("#file").change(function () {
                var file = this.files[0];
                if (!file) {
                    return;
                }
                if (typeof Cropper === "undefined") {
                    err("头像裁切组件加载失败，请刷新页面后重试");
                    $(this).val("");
                    return;
                }
                if (!file.type || file.type.indexOf("image/") !== 0) {
                    err("请选择图片文件");
                    $(this).val("");
                    return;
                }
                avatarMimeType = file.type === "image/jpeg" || file.type === "image/jpg" ? "image/jpeg" : "image/png";
                avatarFileName = avatarMimeType === "image/jpeg" ? "avatar.jpg" : "avatar.png";
                if (avatarObjectUrl && avatarUrlCreator) {
                    avatarUrlCreator.revokeObjectURL(avatarObjectUrl);
                }
                avatarObjectUrl = avatarUrlCreator.createObjectURL(file);
                avatarCropImage.src = avatarObjectUrl;
                $avatarCropModal.modal("show");
            });
            $avatarCropModal.on("shown.bs.modal", function () {
                if (cropper) {
                    cropper.destroy();
                }
                cropper = new Cropper(avatarCropImage, {
                    aspectRatio: 1,
                    viewMode: 1,
                    dragMode: "move",
                    autoCropArea: 1,
                    background: false,
                    preview: ".avatar-crop-preview",
                    responsive: true,
                    restore: false,
                    checkCrossOrigin: false
                });
            });
            $avatarCropModal.on("hidden.bs.modal", function () {
                resetAvatarCropper();
            });
            $confirmAvatarCrop.on("click", function () {
                if (!cropper) {
                    return;
                }
                $confirmAvatarCrop.prop("disabled", true).text("保存中...");
                cropper.getCroppedCanvas({
                    width: 400,
                    height: 400,
                    fillColor: "#FFFFFF",
                    imageSmoothingQuality: "high"
                }).toBlob(function (blob) {
                    if (!blob) {
                        err("头像裁切失败，请重试");
                        $confirmAvatarCrop.prop("disabled", false).text("保存头像");
                        return;
                    }
                    uploadAvatar(blob);
                }, avatarMimeType, .92);
            });

            // 发送激活邮件
            $("#sendActiveEmail").on("click", function () {
                req("get", "/api/settings/sendActiveEmail", "${_user.token!}", function (data) {
                    if (data.code === 200) {
                        suc("发送成功");
                    } else {
                        err(data.description);
                    }
                });
            })

            // 更改邮箱
            $("#sendEmailCode").on("click", function () {
                var loadingBtn = $(this).button("loading");
                var email = $("#email").val();
                req("get", "/api/sendEmailCode", {
                    email: email,
                }, function (data) {
                    if (data.code === 200) {
                        suc("发送成功");
                    } else {
                        err(data.description);
                    }
                    loadingBtn.button("reset");
                });
            })
            $("#settings_email_btn").click(function () {
                var email = $("#email").val();
                var code = $("#code").val();
                req("put", "/api/settings/updateEmail", {email, code}, "${_user.token!}", function (data) {
                    if (data.code === 200) {
                        suc("更改成功");
                        setTimeout(function () {
                            window.location.reload();
                        }, 700);
                    } else {
                        err(data.description);
                    }
                });
            })

            // 更改密码
            $("#settings_pwd_btn").click(function () {
                var oldPassword = $("#oldPassword").val();
                var newPassword = $("#newPassword").val();
                if (!oldPassword) {
                    err("请输入旧密码");
                    return;
                }
                if (!newPassword) {
                    err("请输入新密码");
                    return;
                }
                req("put", "/api/settings/updatePassword", {oldPassword, newPassword}, "${_user.token!}", function (data) {
                    if (data.code === 200) {
                        suc("修改密码成功");
                        setTimeout(function () {
                            window.location.reload();
                        }, 700);
                    } else {
                        err(data.description);
                    }
                });
            });
        })
    </script>
</@html>
