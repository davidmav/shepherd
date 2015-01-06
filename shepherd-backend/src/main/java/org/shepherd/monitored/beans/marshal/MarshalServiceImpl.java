package org.shepherd.monitored.beans.marshal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

@Service
public class MarshalServiceImpl implements MarshalService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MarshalServiceImpl.class);

	@Override
	public Element marshalBeanDefinition(BeanDefinition beanDefinition) throws ParserConfigurationException {
		LOGGER.debug("Creating Marshaled beanDefinition {}", beanDefinition.toString());
		Document doc = createNewDocument();
		Element beansElement = createBeansElement(doc);
		Element beanElement = createBeanElementFromBeanDefinition(beanDefinition, doc);
		beansElement.appendChild(beanElement);
		return beansElement;
	}

	private Element createBeanElementFromBeanDefinition(BeanDefinition beanDefinition, Document doc) {
		LOGGER.debug("Creating bean Element of give beanDefinition {}", beanDefinition.toString());
		Element beanElement = doc.createElement(MarshalServiceConstants.BEAN_ELEMENT_NAME);
		beanElement.setAttribute(MarshalServiceConstants.BEAN_CLASS_ATTRIBUTE, beanDefinition.getBeanClassName());
		beanElement.setAttribute(MarshalServiceConstants.BEAN_ID_ATTRIBUTE, beanDefinition.getAttribute(MarshalServiceConstants.BEAN_ID_ATTRIBUTE).toString());
		ConstructorArgumentValues constructorArgumentValues = beanDefinition.getConstructorArgumentValues();
		if (constructorArgumentValues != null && constructorArgumentValues.getArgumentCount() > 0) {
			for (int i = 0; i < constructorArgumentValues.getArgumentCount(); i++) {
				Element constructorArg = doc.createElement(MarshalServiceConstants.BEAN_CONSTRUCTOR_ARG_ELEMENT_NAME);
				constructorArg.setAttribute(MarshalServiceConstants.INDEX_ATTRIBUTE_NAME, i + MarshalServiceConstants.EMPTY_STRING);
				Object value = constructorArgumentValues.getArgumentValue(i, null).getValue();
				if (value != null) {
					String string = value.toString();
					constructorArg.setAttribute(MarshalServiceConstants.VALUE_ATTRIBUTE_NAME, string);
				} else {
					constructorArg.appendChild(doc.createElement(MarshalServiceConstants.NULL_ELEMENT_NAME));
				}
				beanElement.appendChild(constructorArg);
			}
		}
		return beanElement;
	}

	private Document createNewDocument() throws ParserConfigurationException {
		LOGGER.debug("Creating new Document");
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		return doc;
	}

	private Element createBeansElement(Document doc) {
		LOGGER.debug("Creating Empty Beans element with beans namespaces");
		Element beansElement = doc.createElement(MarshalServiceConstants.BEANS_ELEMENT_NAME);
		beansElement.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, XMLConstants.XMLNS_ATTRIBUTE, MarshalServiceConstants.SPRING_BEANS_NAMESPACE);
		beansElement.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, MarshalServiceConstants.XMLNS_XSI, XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
		beansElement.setAttribute(MarshalServiceConstants.XSI_SCHEMA_LOCATION, MarshalServiceConstants.XSI_BEANS_SCHEMA_LOCATION_VALUE);
		return beansElement;
	}

	@Override
	public void writeElement(Element element, OutputStream outputStream) throws UnsupportedEncodingException, TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, MarshalServiceConstants.OMIT_XML_DECLARATION_VALUE);
		transformer.setOutputProperty(OutputKeys.METHOD, XMLConstants.XML_NS_PREFIX);
		transformer.setOutputProperty(OutputKeys.INDENT, MarshalServiceConstants.INDENT);
		transformer.setOutputProperty(OutputKeys.ENCODING, MarshalServiceConstants.WRITE_CHARSET);
		transformer.setOutputProperty(MarshalServiceConstants.INDENT_PROPERTY, MarshalServiceConstants.INDENT_PROPERTY_VALUE);
		transformer.transform(new DOMSource(element), new StreamResult(new OutputStreamWriter(outputStream, MarshalServiceConstants.WRITE_CHARSET)));
	}
}
