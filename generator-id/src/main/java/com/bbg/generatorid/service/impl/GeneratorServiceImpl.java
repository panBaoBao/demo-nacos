package com.bbg.generatorid.service.impl;    /**
 * @Title:
 * @Package
 * @Description:
 * @author xwq
 * @date 2020/4/30 003015:52
 */

import cn.hutool.core.collection.CollectionUtil;
import com.bbg.generatorid.annotation.Algorithm;
import com.bbg.generatorid.entity.SegmentRequestEntity;
import com.bbg.generatorid.service.GeneService;
import com.bbg.generatorid.service.GeneratorService;
import com.bbg.generatorid.service.IdWorkerService;
import com.bbg.generatorid.service.LeafService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
public class GeneratorServiceImpl implements GeneratorService, ApplicationListener<ContextRefreshedEvent> {


    private Map<String, IdWorkerService> idWorkerServiceMap ;


    @Autowired
    private GeneService geneService;

    @Autowired
    private LeafService leafService;



    /**
     *
     * @param entity
     * @return
     */
    @Override
    public Object getId(SegmentRequestEntity entity) {
        IdWorkerService idWorkerService = idWorkerServiceMap.get(entity.getAlgorithm());
        if(null == idWorkerService){
            return null;
        }
        return idWorkerService.getId(entity);
    }


    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        idWorkerServiceMap = new HashMap<>();
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(Algorithm.class);
        if(CollectionUtil.isNotEmpty(beansWithAnnotation)){
            beansWithAnnotation.forEach((k,v)->{
                Class<IdWorkerService> workerServiceClass = (Class<IdWorkerService>) v.getClass();
                String type = workerServiceClass.getAnnotation(Algorithm.class).type();
                //将class加入map中,type作为key
                idWorkerServiceMap.put(type,applicationContext.getBean(workerServiceClass));
            });
        }
    }
}
