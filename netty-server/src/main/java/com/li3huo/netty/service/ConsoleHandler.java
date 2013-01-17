/**
 * Create at Jan 16, 2013
 */
package com.li3huo.netty.service;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;

/**
 * @author liyan
 *
 */
public class ConsoleHandler extends HttpRequestHandler {
	
	private StringBuilder help = new StringBuilder();
	

	private Logger logger;

	public ConsoleHandler(SocketServerFactory server, SocketServerFactory console) {
		super(server);
		this.logger = server.getLogger(); 
		
		//create help hint
		help.append("Usage:\n");
		help.append("Actions\n");
		help.append("action=status\n");
	}
	
	/* (non-Javadoc)
	 * @see com.li3huo.netty.service.HttpRequestHandler#handleHttpRequest(org.jboss.netty.channel.MessageEvent, org.jboss.netty.handler.codec.http.HttpRequest)
	 */
	@Override
	public void handleHttpRequest(MessageEvent event, HttpRequest request) {

		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
		Map<String, List<String>> params = queryStringDecoder.getParameters();
		
		List<String> actions = params.get("action");
		if(null == actions) {
			this.writeResponse(event, help.toString());
			return;
		}
		
		for(String action : actions) {
		
			if("status".equalsIgnoreCase(action)) {
				this.writeResponse(event, "Access Count: "+getAccessCount());
				continue;
			}else if("stop".equalsIgnoreCase(action)) {
				this.writeResponse(event, "Access Count: "+getAccessCount());
				logger.info("stoped by console, from " + event.getRemoteAddress());
				System.exit(0);
			}else {
				this.writeResponse(event, help.toString());
				return;
			}
		}
	}
	
}
