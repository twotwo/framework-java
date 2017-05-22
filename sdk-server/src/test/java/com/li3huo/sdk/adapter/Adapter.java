package com.li3huo.sdk.adapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;

import com.li3huo.sdk.App;
import com.li3huo.service.FacadeContext;
import com.li3huo.service.NettyContext;

import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;

public class Adapter {
	static final Logger logger = LogManager.getLogger(Adapter.class.getName());

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String file = "conf/games.properties";
		App.initConfig(file);
	}
	
	FacadeContext getContext(final String uri, String data) {
		
		ByteArrayOutputStream input = new ByteArrayOutputStream();
		try {
			IOUtils.write(data, input, "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		HttpRequest request = new HttpRequest() {
			
			@Override
			public void setDecoderResult(DecoderResult arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public DecoderResult decoderResult() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public DecoderResult getDecoderResult() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public HttpVersion protocolVersion() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public HttpHeaders headers() {
				// TODO Auto-generated method stub
				return new DefaultHttpHeaders();
			}
			
			@Override
			public HttpVersion getProtocolVersion() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String uri() {
				// TODO Auto-generated method stub
				return uri;
			}
			
			@Override
			public HttpRequest setUri(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public HttpRequest setProtocolVersion(HttpVersion arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public HttpRequest setMethod(HttpMethod arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public HttpMethod method() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getUri() {
				// TODO Auto-generated method stub
				return uri;
			}
			
			@Override
			public HttpMethod getMethod() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		NettyContext ctx = new NettyContext(null, request, input);
		return ctx;
	}

}
