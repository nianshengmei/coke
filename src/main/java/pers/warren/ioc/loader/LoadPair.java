package pers.warren.ioc.loader;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * 该方法用于描述一个bean加载的相互依赖关系
 * <p>
 * 例如：A依赖B，那么就是A -> B
 *
 * @author wareen
 * @since 1.0.1
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode
public class LoadPair {

    /**
     * 因为谁
     */
    private String a;

    /**
     * 加载谁
     */
    private String b;

    @Override
    public String toString() {
        return a + " -> " + b;
    }
}
