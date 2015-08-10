/**
 * 
 */
package io.pivotal.demo.websocket.proxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * An example to illustrate how to use mock objects for remote dependencies.
 * 
 * @author cdelashmutt-pivotal
 */
@Service
public class ProxyService {

	RemoteService service;

	@Autowired
	public ProxyService(RemoteService service)
	{
		this.service = service;
	}
	
	public String doProxyMethod()
	{
		return "Proxy: " + service.getData();
	}
}
