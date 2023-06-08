package pers.warren.ioc.event;

import lombok.Data;
import lombok.NoArgsConstructor;

 /**
  * 抽象事件
  *
  * @since 1.0.2
  */
@Data
@NoArgsConstructor
public abstract class AbstractSignal implements Signal {

    /**
     * 事件类型
     *
     * @since 1.0.2
     */
    protected ISignalType type;

     /**
      * 事件处理类型
      *
      * @since 1.0.2
      */
     protected SignalProcessorType processorType;


    public AbstractSignal(ISignalType type, SignalProcessorType processorType) {
        this.type = type;
        this.processorType = processorType;
    }

}
