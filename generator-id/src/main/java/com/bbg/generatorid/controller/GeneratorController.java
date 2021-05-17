package com.bbg.generatorid.controller;

import cn.hutool.core.util.StrUtil;
import com.bbg.generatorid.constant.BizConstant;
import com.bbg.generatorid.entity.SegmentRequestEntity;
import com.bbg.generatorid.enums.AlgorithmEnum;
import com.bbg.generatorid.enums.TypeEnum;
import com.bbg.generatorid.service.GeneratorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
@Api(tags = "分布式id生成器集成接口")
@RequestMapping(value = "/generator")
@Slf4j
public class GeneratorController {



    @Autowired
    private GeneratorService generatorService;

    /**
     * 1、算法  1 gene(基因法)   2 说明是用的分段算法 3 使用前缀 + 分段算法
     *       递增序列 当机器停机时 会产生序号不连续，但整体保证递增
     * 2、当采用 使用前缀 + 分段算法 时 如果没有传bigTag 和 prefix 参数 则默认返回 分段算法生成的递增序列
     *
     * 获取id、序列 入口
     * @param entity
     * @return
     */
    @RequestMapping(value="/getId", method = {RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    @ApiOperation(value="获取id",notes="获取id")
    public Object getId(@RequestBody SegmentRequestEntity entity) {

        String bizTag = entity.getBizTag();
        String prefix = entity.getPrefix();
        String algorithm = entity.getAlgorithm();
        if(StrUtil.isBlank(algorithm)){
            //默认分段序列
            entity.setAlgorithm(AlgorithmEnum.LEAF.getCode());
            entity.setBizTag(BizConstant.DEFAULT_TAG);
            entity.setPrefix(BizConstant.DEFAULT_TAG);
        }
        //当采用 使用前缀 + 分段算法 时 如果没有传bigTag 和 prefix 参数 则默认返回 分段算法生成的递增序列
        if(AlgorithmEnum.PREFIX_LEAF.getCode().equals(algorithm) && StrUtil.hasBlank(bizTag,prefix)){
            entity.setAlgorithm(AlgorithmEnum.LEAF.getCode());
            entity.setBizTag(BizConstant.DEFAULT_TAG);
            entity.setPrefix(BizConstant.DEFAULT_TAG);
        }

        if(AlgorithmEnum.LEAF.getCode().equals(algorithm)){
            entity.setBizTag(BizConstant.DEFAULT_TAG);
            entity.setPrefix(BizConstant.DEFAULT_TAG);
        }

        if(StrUtil.isBlank(entity.getType())){
            //如果没有指定返回类型默认返回为Long
            entity.setType(TypeEnum.LONG.getCode());
        }
        return generatorService.getId(entity);
    }

}
