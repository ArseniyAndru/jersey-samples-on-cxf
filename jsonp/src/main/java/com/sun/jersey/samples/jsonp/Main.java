package com.sun.jersey.samples.jsonp;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.ext.RuntimeDelegate;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import com.sun.jersey.samples.jsonp.resources.ChangeList;
import org.apache.cxf.jaxrs.provider.jsonp.JsonpInInterceptor;
import org.apache.cxf.jaxrs.provider.jsonp.JsonpPreStreamInterceptor;
import org.apache.cxf.jaxrs.provider.jsonp.JsonpPostStreamInterceptor;

public class Main {

    protected Main() throws Exception {
        JAXRSServerFactoryBean bean = new JAXRSServerFactoryBean();
        JsonpInInterceptor jii = new JsonpInInterceptor();
        jii.setAcceptType("application/json");
        bean.getInInterceptors().add(jii);
        bean.getOutInterceptors().add(new JsonpPreStreamInterceptor());
        bean.getOutInterceptors().add(new JsonpPostStreamInterceptor());
        

        List perRequestResourceList = new ArrayList();
        perRequestResourceList.add(ChangeList.class);
        bean.setResourceClasses(perRequestResourceList);
    
        List<Object> providerList = new ArrayList<Object>();
        providerList.add(new org.codehaus.jackson.jaxrs.JacksonJsonProvider());
        bean.setProviders(providerList);

        bean.setAddress("http://localhost:9998/jsonp");
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
