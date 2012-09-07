package com.sun.jersey.samples.exceptions;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.ext.RuntimeDelegate;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.impl.WebApplicationExceptionMapper;
import com.sun.jersey.samples.exceptions.resources.MyResource;

public class Main {

    protected Main() throws Exception {
        JAXRSServerFactoryBean bean = new JAXRSServerFactoryBean();

		List perRequestResourceList = new ArrayList<Object>();
        perRequestResourceList.add(MyResource.class);
        // add any additional per-request resources
        bean.setResourceClasses(perRequestResourceList);
    
        /* 
         *  Normally not necessary to explicitly add WebApplicationExceptionMapper to
         *  provider list.  Doing so here to change printStackTrace's value from default 
         *  so WAEM's will output to server console (as it does by default with Jersey)
         */
        WebApplicationExceptionMapper waem = new WebApplicationExceptionMapper();
        waem.setPrintStackTrace(true);

        List<Object> providerList = new ArrayList<Object>();
        providerList.add(new MyResource.MyMappedExceptionMapper());
        providerList.add(new MyResource.MyMappedRuntimeExceptionMapper());
        providerList.add(new MyResource.MyMappedThrowingExceptionMapper());
        providerList.add(waem);
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
