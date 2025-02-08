package com.lizhe.bhrpccommon.scanner.service;

import com.lizhe.bhrpcannotation.RpcService;
import com.lizhe.bhrpccommon.scanner.ClassScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RpcServiceScanner
 * {@code @description} 实现@RpcService注解扫描器
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/8 上午11:24
 * @version 1.0
 */
public class RpcServiceScanner extends ClassScanner {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServiceScanner.class);

    /**
     * 扫描指定包下的类，并筛选使用@RpcService注解标注的类
     */
    public static Map<String, Object> doScannerWithRpcServiceAnnotationFilterAndRegistryService(/*String host, int port, */ String scanPackage/*, RegistryService registryService*/) throws Exception {
        Map<String, Object> handlerMap = new HashMap<>();
        List<String> classNameList = getClassNameList(scanPackage);
        if (classNameList.isEmpty()) {
            return handlerMap;
        }
        classNameList.forEach(className -> {
            try {
                Class<?> clazz = Class.forName(className);
                RpcService rpcService = clazz.getAnnotation(RpcService.class);
                if (rpcService != null) {
                    //优先使用interfaceClass, interfaceClass的name为空，再使用interfaceClassName
                    // TODO 后续逻辑向注册中心注册服务元数据，同时向handlerMap中记录标注了RpcService注解的类实例
                    LOGGER.info("当前标注了@RpcService注解的类实例名称===>>> {}", clazz.getName());
                    LOGGER.info("@RpcService注解上标注的属性信息如下：");
                    LOGGER.info("interfaceClass===>>> {}", rpcService.interfaceClass().getName());
                    LOGGER.info("interfaceClassName===>>> {}", rpcService.interfaceClassName());
                    LOGGER.info("version===>>> {}", rpcService.version());
                    LOGGER.info("group===>>> {}", rpcService.group());
                }
            } catch (Exception e) {
                LOGGER.error("scan classes throws exception: {}", e.getMessage(), e);
            }
        });
        return handlerMap;
    }

}
