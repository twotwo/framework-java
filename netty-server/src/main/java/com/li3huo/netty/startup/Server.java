package com.li3huo.netty.startup;

public interface Server {

	public abstract void init();

	public abstract void start();

	public abstract void stop();

	public abstract void status();

}