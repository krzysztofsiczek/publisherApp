package pl.chrispeter.publisherApp;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringWriter;

import javax.jms.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Producer {
	public static void main(String[] args) {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
		Connection connection = null;

		try {
			connection = factory.createConnection();
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			Topic topic = session.createTopic("Example.Library.Publication");
			MessageProducer producer = session.createProducer(topic);

			Document document = parseXml();
			String xmlMessage = getXmlAsString(document);

			TextMessage message = session.createTextMessage(xmlMessage);
			producer.send(message);

		} catch (JMSException e) {

			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {

					e.printStackTrace();
				}
			}
		}
	}

	private static Document parseXml() {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {

			e.printStackTrace();
		}
		try {
			return documentBuilder.parse(Producer.class.getResourceAsStream("excercise-1.xml"));
		} catch (SAXException e) {

			e.printStackTrace();
			return null;
		} catch (IOException e) {

			e.printStackTrace();
			return null;
		}
		
	}

	private static String getXmlAsString(Document document) {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			
			e.printStackTrace();
		}
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StringWriter writer = new StringWriter();
		try {
			transformer.transform(new DOMSource(document), new StreamResult(writer));
		} catch (TransformerException e) {
			
			e.printStackTrace();
		}
		return writer.getBuffer().toString();
	}
}
