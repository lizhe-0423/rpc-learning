package com.lizhe.bhrpccommon.scanner.service;

import com.alibaba.fastjson.JSON;
import com.lizhe.bhrpcannotation.RpcService;
import com.lizhe.bhrpccommon.helper.RpcServiceHelper;
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
     * 该方法主要用于发现项目中所有使用了@RpcService注解的类，并创建它们的实例，以便进行后续的RPC服务注册
     *
     * @param scanPackage 需要扫描的包名，用于查找包含@RpcService注解的类
     * @return 返回一个映射，键是服务的唯一标识（由服务名、版本和组构成），值是该服务的实例对象
     * @throws Exception 当类加载、实例化或其他异常发生时抛出
     */
    public static Map<String, Object> doScannerWithRpcServiceAnnotationFilterAndRegistryService(/*String host, int port, */ String scanPackage/*, RegistryService registryService*/) throws Exception {
        // 创建一个映射，用于存储服务实例
        Map<String, Object> handlerMap = new HashMap<>();
        // 获取指定包下所有类的名称列表
        List<String> classNameList = getClassNameList(scanPackage);
        // 如果类名列表为空，则直接返回空映射
        if (classNameList.isEmpty()) {
            return handlerMap;
        }
        // 遍历类名列表
        classNameList.forEach(className -> {
            try {
                // 加载类
                Class<?> clazz = Class.forName(className);
                // 检查类是否使用了@RpcService注解
                RpcService rpcService = clazz.getAnnotation(RpcService.class);
                // 如果类使用了@RpcService注解，则进行处理
                if (rpcService != null) {
                    //优先使用interfaceClass, interfaceClass的name为空，再使用interfaceClassName
                    // TODO 后续逻辑向注册中心注册服务元数据，同时向handlerMap中记录标注了RpcService注解的类实例
                    //handlerMap中的key先简单存储为serviceName+version+group，后续根据实际情况处理key
                    // 构建服务的唯一标识键
                    String serviceName = getServiceName(rpcService);
                    String key = RpcServiceHelper.buildServiceKey(serviceName, rpcService.version(), rpcService.group());
                    // 实例化类，并存储到映射中
                    handlerMap.put(key, clazz.newInstance());
                }
            } catch (Exception e) {
                // 日志记录异常信息
                LOGGER.error("scan classes throws exception: {}", e.getMessage(), e);
            }
        });

        LOGGER.info("scan classes size : {} class{}", handlerMap.size(), JSON.toJSON(handlerMap));
        // 返回存储了服务实例的映射
        return handlerMap;
    }

    /**
     * 获取serviceName
     */
    private static String getServiceName(RpcService rpcService) {
        //优先使用interfaceClass
        Class clazz = rpcService.interfaceClass();
        if (clazz == void.class) {
            return rpcService.interfaceClassName();
        }
        String serviceName = clazz.getName();
        if (serviceName.trim().isEmpty()) {
            serviceName = rpcService.interfaceClassName();
        }
        return serviceName;
    }

}
