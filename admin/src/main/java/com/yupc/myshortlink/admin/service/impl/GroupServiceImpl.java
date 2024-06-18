package com.yupc.myshortlink.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupc.myshortlink.admin.dao.entity.GroupDO;
import com.yupc.myshortlink.admin.dao.mapper.GroupMapper;
import com.yupc.myshortlink.admin.service.GroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {


}
