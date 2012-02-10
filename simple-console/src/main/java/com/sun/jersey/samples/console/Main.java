package com.sun.jersey.samples.console;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.ext.RuntimeDelegate;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import com.sun.jersey.samples.console.resources.FormResource;

public class Main {

    protected Main() throws Exception {
        JAXRSServerFactoryBean bean = new JAXRSServerFactoryBean();

		List perRequestResourceList = new ArrayList<Object>();
        perRequestResourceList.add(FormResource.class);
        // add any additional per-request resources
        bean.setResourceClasses(perRequestResourceList);
    
        List<Object> providerList = new ArrayList<Object>();
        providerList.add(new org.codehaus.jackson.jaxrs.JacksonJsonProvider());
        bean.setProviders(providerList);
        
        bean.setAddress("http://localhost:9998/resources");
        bean.create();
    }

    public static void main(String args[]) throws Exception {
        new Main();
        System.out.println("Server ready at http://127.0.0.1:9998/resources/form...");

        Thread.sleep(125 * 60 * 1000);
        System.out.println("Server exiting");
        System.exit(0);
    }
}
