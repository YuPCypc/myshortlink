package com.yupc.myshortlink.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupc.myshortlink.project.dao.entity.ShortLinkDO;
import com.yupc.myshortlink.project.dao.mapper.ShortLinkMapper;
import com.yupc.myshortlink.project.service.ShortLinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

}
