/**
 * 
 */
package io.pivotal.demo.websocket.tests.proxy;

import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import io.pivotal.demo.websocket.proxy.ProxyService;
import io.pivotal.demo.websocket.proxy.RemoteService;

import org.junit.Test;

/**
 * @author grog
 */
public class ProxyTests {

	@Test
	public void testProxyWithMock()
	{
		String sampleData = "Mocked Method!";
		RemoteService service = mock(RemoteService.class);
		when(service.getData()).thenReturn(sampleData);
		
		ProxyService proxy = new ProxyService(service);
		assertThat(proxy.doProxyMethod(), equalTo("Proxy: " + sampleData));
	}
}
