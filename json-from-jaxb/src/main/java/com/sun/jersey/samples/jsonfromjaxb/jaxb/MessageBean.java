package com.sun.jersey.samples.jsonfromjaxb.jaxb;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/* This object is available from resources.FlightList.  
 * Both CXF and Jersey, its XML output returns:
 * <messageBean><message>hello</message></messageBean>
 * 
 * For JSON/Jettison (default with this example)
 * CXF: {"messageBean":{"message":"hello"}}
 * Jersey: {"message":"hello"}
 * (Jersey uses an older Jettison dependency, 1.1 vs. 1.3.1 for CXF)
 * 
 * For JSON/Jackson (both CXF and Jersey):
 * {"message":"abcde"}
 * Note Jackson calls the accessor method to determine
 * the value of message while Jettison does not.
 * 
 * (For this CXF example, Jackson can be activated by uncommenting
 * relevant code in Main.java and the pom.xml)
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
