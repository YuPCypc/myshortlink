package com.yupc.myshortlink.admin.dto.req;

import lombok.Data;

@Data
public class RecycleBinRemoveReqDTO {
    private String gid;
    private String fullShortLink;
}
