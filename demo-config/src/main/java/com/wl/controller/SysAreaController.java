package com.wl.controller;


import cn.hutool.json.JSONUtil;
import com.wl.config.MyProperties;
import com.wl.entity.SysArea;
import com.wl.service.ISysAreaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 区域表 前端控制器
 * </p>
 *
 * @author ptm
 * @since 2021-04-21
 */
@RestController
@RequestMapping("/sys-area")
@Slf4j
public class SysAreaController {

    @Autowired
    private ISysAreaService iSysAreaService;
    @Autowired
    private MyProperties myProperties;

    @GetMapping("info/{id}")
    public String areaInfo(@PathVariable("id") String id){
        try {
            if(log.isDebugEnabled()){
                log.debug("areaInfo->start,param,id:{}",id);
            }
            log.info("msg:{}",myProperties.getMsg());
            if(myProperties.getFlag()){
                SysArea area = iSysAreaService.getById(id);
                return JSONUtil.toJsonStr(area) ;
            }else {
                return "默认返回 ： 长沙";
            }
        }catch (Exception e){
            log.error("areaInfo->ex",e);
            return "未知错误";
        }

    }

}

