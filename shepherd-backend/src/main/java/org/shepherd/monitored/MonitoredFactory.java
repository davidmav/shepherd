package org.shepherd.monitored;

import org.springframework.beans.factory.FactoryBean;

/**
 * 
 * @author DavidM
 *
 * @param <T>
 */
public abstract class MonitoredFactory<T extends Monitored> implements FactoryBean<T> {


}
