package io.pivotal.demo.websocket.proxy;

import org.springframework.stereotype.Service;

@Service
public class DefaultRemoteService 
implements RemoteService {

	@Override
	public String getData() {
		return "This is the default implementation";
	}

}
