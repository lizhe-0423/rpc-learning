package com.lizhe.test.scanner;

import com.lizhe.bhrpcannotation.RpcReference;
import com.lizhe.bhrpccommon.scanner.ClassScanner;
import com.lizhe.bhrpccommon.scanner.reference.RpcReferenceScanner;
import com.lizhe.bhrpccommon.scanner.service.RpcServiceScanner;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ScannerTest
 * {@code @description} 扫描器测试类
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/8 下午1:57
 * @version 1.0
 */
class ScannerTest {

    /**
     * 扫描com.lizhe.test.scanner包下所有的类
     */
    @Test
    void testScannerClassNameList() throws Exception {
        List<String> expectedClassNames = Arrays.asList(
                "com.lizhe.test.scanner.ScannerTest",
                "com.lizhe.test.scanner.consumer.ConsumerBusinessService",
                "com.lizhe.test.scanner.consumer.service.impl.ConsumerBusinessServiceImpl",
                "com.lizhe.test.scanner.provider.ProviderDemoServiceImpl",
                "com.lizhe.test.scanner.service.DemoService"
        );

        List<String> actualClassNames = ClassScanner.getClassNameList("com.lizhe.test.scanner");

        // 验证返回的类名列表大小是否与预期一致
        assertEquals(expectedClassNames.size(), actualClassNames.size(), "返回的类名列表大小与预期不一致");

        // 验证返回的类名列表中的每个元素是否都在预期列表中
        for (String className : actualClassNames) {
            assertTrue(expectedClassNames.contains(className), "返回的类名列表中包含意外的类名: " + className);
        }
    }

    @Test
    void testScannerClassNameListByRpcService() throws Exception {
        RpcServiceScanner.
                doScannerWithRpcServiceAnnotationFilterAndRegistryService("com.lizhe.test.scanner");
    }

    @Test
    void testScannerClassNameListByRpcReference() throws Exception {
        RpcReferenceScanner.
                doScannerWithRpcReferenceAnnotationFilter("com.lizhe.test.scanner");
    }

}
