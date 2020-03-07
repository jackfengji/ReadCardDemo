package com.ming.util.common;

public interface HttpListener {
	public void loading(long total, long current);
	public boolean isStop();
}
