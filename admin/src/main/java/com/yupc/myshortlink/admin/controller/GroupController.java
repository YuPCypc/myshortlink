package com.yupc.myshortlink.admin.controller;

import com.yupc.myshortlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短链接分组控制层
 */
@RequiredArgsConstructor
@RestController
public class GroupController {
    private final GroupService groupService;
}
