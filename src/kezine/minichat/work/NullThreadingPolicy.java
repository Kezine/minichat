package kezine.minichat.work;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class NullThreadingPolicy extends ThreadingPolicy {

	@Override
	protected void runLaterImpl(Runnable r) {
		r.run();
	}

	@Override
	protected Future<?> startTaskImpl(Runnable task) {
		return null;
	}

	@Override
	protected <T> Future<T> startTaskImpl(Callable<T> task) {
		return null;
	}

	@Override
	protected boolean isEventThread() {
		return true;
	}

	@Override
	protected void runCustomEventImpl(Runnable r)
	{
		
	}
}
