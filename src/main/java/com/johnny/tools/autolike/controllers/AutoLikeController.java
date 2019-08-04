package com.johnny.tools.autolike.controllers;

import com.johnny.tools.autolike.beans.ResponseModel;
import com.johnny.tools.autolike.services.AutoLikeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
public class AutoLikeController extends BaseController {

    @Resource
    private AutoLikeService autoLikeService;

    @PostMapping("/like")
    public ResponseModel doLike(HttpServletRequest request,
                                @RequestParam(name = "cookie", required = true) String cookie) {
        this.autoLikeService.autoLike(cookie);
        return this.getOkResponseModel();
    }
}
