package com.lizhe.bhrpcserializationjdk;

import com.lizhe.bhrpccommon.exception.SerializerException;
import com.lizhe.bhrpcserialzationapi.Serialization;

import java.io.*;

/**
 * JdkSerialization
 * {@code @description} JDK序列化实现类
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/12 下午5:09
 * @version 1.0
 */
public class JdkSerialization implements Serialization {
    /**
     * 将给定的对象序列化为字节数组
     * 该方法覆盖了父类的serialize方法，以实现自定义的序列化逻辑
     *
     * @param obj 待序列化的对象，可以是任何类型
     * @return 序列化后的字节数组
     * @throws SerializerException 如果序列化过程中发生错误或对象为null，则抛出此异常
     */
    @Override
    public <T> byte[] serialize(T obj) {
        // 检查对象是否为null，如果是，则抛出异常
        if (obj == null) {
            throw new SerializerException("serialize object is null");
        }
        try {
            // 创建字节流对象，用于存储序列化后的数据
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            // 创建对象输出流，用于将对象写入到字节流中
            ObjectOutputStream out = new ObjectOutputStream(os);
            // 将对象写入到字节流中，完成序列化过程
            out.writeObject(obj);
            // 返回序列化后的字节数组
            return os.toByteArray();
        } catch (IOException e) {
            // 捕获IOException，并包装为自定义异常抛出
            throw new SerializerException(e.getMessage(), e);
        }
    }

    /**
     * 反序列化指定字节数据为指定类的对象
     *
     * @param data 字节数据，用于反序列化
     * @param cls  指定的类，决定反序列化为何种类型的对象
     * @param <T>  泛型参数，表示反序列化后的对象类型
     * @return 反序列化后的对象，类型为指定的类
     * @throws SerializerException 如果反序列化过程中发生异常，抛出此异常
     */
    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) {
        // 检查输入的字节数据是否为null，如果为null，则抛出异常
        if (data == null) {
            throw new SerializerException("deserialize data is null");
        }
        try {
            // 创建一个字节输入流，用于读取输入的字节数据
            ByteArrayInputStream is = new ByteArrayInputStream(data);
            // 创建一个对象输入流，用于从字节输入流中读取对象
            ObjectInputStream in = new ObjectInputStream(is);
            // 读取对象并将其强制转换为指定的类的实例
            return (T) in.readObject();
        } catch (Exception e) {
            // 捕获任何在反序列化过程中可能发生的异常，并包装为自定义异常抛出
            throw new SerializerException(e.getMessage(), e);
        }
    }
}
