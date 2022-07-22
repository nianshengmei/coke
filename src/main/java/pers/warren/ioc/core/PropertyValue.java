package pers.warren.ioc.core;

import lombok.Data;

/**
 * 描述 K/V键值对的一种方式 ， 用于 K可重复场景
 * @author warren
 *
 * @since JDK1.8
 */
@Data
public class PropertyValue {

    /**
     * 键
     */
    private String k;

    /**
     * 值
     */
    private String v;
}
