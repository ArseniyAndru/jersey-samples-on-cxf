package com.sun.jersey.samples.jsonfromjaxb;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.ext.RuntimeDelegate;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import com.sun.jersey.samples.jsonfromjaxb.resources.AircraftTypeList;
import com.sun.jersey.samples.jsonfromjaxb.resources.FlightList;

public class Main {

    protected Main() throws Exception {
        JAXRSServerFactoryBean bean = new JAXRSServerFactoryBean();

		List perRequestResourceList = new ArrayList<Object>();
        perRequestResourceList.add(AircraftTypeList.class);
        bean.setResourceClasses(perRequestResourceList);
        
        // Choose either Jackson or Jettison provider (see README)
        // & update pom.xml with correct dependency.
        List<Object> providerList = new ArrayList<Object>();
        providerList.add(new org.codehaus.jackson.jaxrs.JacksonJsonProvider());
//      providerList.add(new org.apache.cxf.jaxrs.provider.json.JSONProvider());
        bean.setProviders(providerList);
 
        List singletonRequestList = new ArrayList(); 
        singletonRequestList.add(new SingletonResourceProvider(new FlightList()));
        bean.setResourceProviders(singletonRequestList);

        bean.setAddress("http://localhost:9998/jsonfromjaxb/");
        bean.create();
    }

    public static void main(String args[]) throws Exception {
        new Main();
        System.out.println("Server ready at http://127.0.0.1:9998/jsonfromjaxb/...");

        Thread.sleep(125 * 60 * 1000);
        System.out.println("Server exiting");
        System.exit(0);
    }
}
