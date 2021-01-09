package com.yijianguanzhu.iflytek.rtasr.model;

import com.yijianguanzhu.iflytek.rtasr.exception.AsrException;

import java.util.concurrent.TimeUnit;

/**
 * @author yijianguanzhu 2021年01月08日
 * @since 1.8
 */
public interface FinishLatch {

    /**
     * 同步等待，直到会话结束, 或者发生了异常情况
     *
     * @return true 结束； false 不应该发生
     * @throws AsrException 异常中断
     */
	boolean await() throws AsrException;

    /**
     * 同步等待，直到会话结束 或者超时发生
     *
     * @return true 结束； false 在指定的超时时间内， 会话还没有结束
     * @throws AsrException 异常中断
     */
	boolean await( long timeout, TimeUnit unit ) throws AsrException;

    /**
     * 非阻塞测试会话是否已经结束, 该调用会立即返回;
     *
     * @return true 结束； false 没有结束
     */
	boolean finished();
}
