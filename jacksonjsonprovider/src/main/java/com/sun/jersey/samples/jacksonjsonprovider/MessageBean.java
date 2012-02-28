package com.sun.jersey.samples.jacksonjsonprovider;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/* For this class (called from NonJAXBBeanResource) XML output returns:
 * <messageBean><message>hello</message></messageBean>
 * but Jackson JSON output returns:
 * {"message":"abcde"}
 */
@XmlRootElement
public class MessageBean {
  
    @XmlElement
	protected String message;

	public MessageBean() {
    	message = "hello";
    }

	public String getMessage() {
		return "abcde";
	}
}
