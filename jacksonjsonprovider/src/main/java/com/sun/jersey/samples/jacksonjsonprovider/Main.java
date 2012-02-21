package com.sun.jersey.samples.jacksonjsonprovider;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.ext.RuntimeDelegate;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.provider.jsonp.JsonpInInterceptor;
import org.apache.cxf.jaxrs.provider.jsonp.JsonpPreStreamInterceptor;
import org.apache.cxf.jaxrs.provider.jsonp.JsonpPostStreamInterceptor;

public class Main {

    protected Main() throws Exception {
    	RuntimeDelegate delegate = RuntimeDelegate.getInstance();   	
        JAXRSServerFactoryBean bean = delegate.createEndpoint(new MyApplication(), 
            JAXRSServerFactoryBean.class);
        
        // Activate JSONP functionality 
        JsonpInInterceptor jii = new JsonpInInterceptor();
        jii.setAcceptType("application/json");
        bean.getInInterceptors().add(jii);
        bean.getOutInterceptors().add(new JsonpPreStreamInterceptor());
        bean.getOutInterceptors().add(new JsonpPostStreamInterceptor());        

        bean.setAddress("http://localhost:9998/jacksonjsonprovider");
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
