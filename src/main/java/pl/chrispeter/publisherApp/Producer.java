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
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Producer {

	public static void main(String[] args) {

		String xmlMessage;
		try {
			xmlMessage = getXmlAsString("src/main/resources/excercise-1.xml");
		} catch (TransformerException e1) {
			System.out.println("Input data error.");
			return;
		} catch (ParserConfigurationException e2) {
			System.out.println("Input data error.");
			return;
		} catch (SAXException e3) {
			System.out.println("Input data error.");
			return;
		} catch (IOException e4) {
			System.out.println("Input data error.");
			return;
		}

		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
		Connection connection = null;

		try {
			connection = factory.createConnection();
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			Topic topic = session.createTopic("Example.Library.Publication");
			MessageProducer producer = session.createProducer(topic);
			producer.send(session.createTextMessage(xmlMessage));
		} catch (JMSException e) {
			e.printStackTrace();
		} finally {
			closeConnection(connection);
		}
	}

	private static void closeConnection(Connection connection) {
		if (connection == null)
			return;
		try {
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	private static String getXmlAsString(String path)
			throws TransformerException, ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(path);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(document), new StreamResult(writer));
		return writer.getBuffer().toString();
	}
}
