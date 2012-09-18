package com.skymobi.monitor.util;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;

/**
 * @author Hill.Hu
 */
public class VelocityFactoryBean implements FactoryBean{
    private VelocityConfigurer velocityConfigurer;
    @Override
    public Object getObject() throws Exception {
        return velocityConfigurer.getVelocityEngine();
    }

    @Override
    public Class<?> getObjectType() {
        return VelocityEngine.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setVelocityConfigurer(VelocityConfigurer velocityConfigurer) {
        this.velocityConfigurer = velocityConfigurer;
    }
}
