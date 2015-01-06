package org.shepherd.monitored.beans.marshal;

import org.springframework.beans.factory.config.BeanDefinition;
import org.w3c.dom.Element;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

/**
 * 
 * 
 * @author davidm
 * @since Jan 3, 2015
 * @version 0.1.0
 */
public interface MarshalService {

	/**
	 * 
	 * @since Jan 4, 2015
	 * @author davidm
	 * @param beanDefinition
	 * @return
	 * @throws ParserConfigurationException
	 */
	public Element marshalBeanDefinition(BeanDefinition beanDefinition) throws ParserConfigurationException;

	/**
	 * 
	 * @since Jan 4, 2015
	 * @author davidm
	 * @param element
	 * @param outputStream
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 * @throws UnsupportedEncodingException
	 */
	public void writeElement(Element element, OutputStream outputStream) throws TransformerConfigurationException, UnsupportedEncodingException, TransformerException;

}
