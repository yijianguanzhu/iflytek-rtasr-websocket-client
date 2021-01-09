package com.yijianguanzhu.iflytek.rtasr.model;

import com.yijianguanzhu.iflytek.rtasr.exception.AsrException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author yijianguanzhu 2021年01月08日
 * @since 1.8
 */
public class FinishLatchImpl implements FinishLatch {
	private CountDownLatch latch = new CountDownLatch( 1 );

	@Override
	public boolean await() throws AsrException {
		return this.await( Long.MAX_VALUE, TimeUnit.SECONDS );
	}

	@Override
	public boolean await( long timeout, TimeUnit unit ) throws AsrException {
		try {
			return latch.await( timeout, unit );
		}
		catch ( Exception e ) {
			throw new AsrException( e );
		}
	}

	@Override
	public boolean finished() {
		return latch.getCount() == 0;
	}

	public void countDown() {
		this.latch.countDown();
	}
}
