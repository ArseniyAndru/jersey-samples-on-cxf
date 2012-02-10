package com.sun.jersey.samples.entityprovider;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.ext.RuntimeDelegate;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import com.sun.jersey.samples.entityprovider.resources.MapResource;
import com.sun.jersey.samples.entityprovider.resources.PropertiesResource;

public class Main {

    protected Main() throws Exception {
        JAXRSServerFactoryBean bean = new JAXRSServerFactoryBean();

		List perRequestResourceList = new ArrayList<Object>();
        perRequestResourceList.add(PropertiesResource.class);
        // add any additional per-request resources
        bean.setResourceClasses(perRequestResourceList);
    
        List singletonRequestList = new ArrayList(); 
        singletonRequestList.add(new SingletonResourceProvider(new MapResource()));
        // add any more singleton root resources
        bean.setResourceProviders(singletonRequestList);

        List<Object> providerList = new ArrayList<Object>();
        providerList.add(new FormReader());
        providerList.add(new FormWriter());
        providerList.add(new PropertiesProvider());
        bean.setProviders(providerList);

        bean.setAddress("http://localhost:9998");
        bean.create();
    }

    public static void main(String args[]) throws Exception {
        new Main();
        System.out.println("Server ready...");

        Thread.sleep(125 * 60 * 1000);
        System.out.println("Server exiting");
        System.exit(0);
    }
}
