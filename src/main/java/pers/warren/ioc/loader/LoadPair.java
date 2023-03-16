package pers.warren.ioc.loader;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
