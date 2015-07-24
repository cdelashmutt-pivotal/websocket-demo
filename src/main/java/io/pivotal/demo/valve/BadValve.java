/**
 * 
 */
package io.pivotal.demo.valve;

import org.apache.catalina.valves.AccessLogValve;

/**
 * @author grog
 *
 */
public class BadValve extends AccessLogValve {
	
	@Override
	public boolean isAsyncSupported() {
		// TODO Auto-generated method stub
		return false;
	}

}
