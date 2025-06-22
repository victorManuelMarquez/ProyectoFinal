package ar.com.elbaden.gui;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Settings {

    public static final String FOLDER_NAME = ".baden";
    public static final String LOG_FILE_NAME = "log.txt";
    public static final String FILE_BASENAME = "config";
    public static final String XSD_FILE_NAME = FILE_BASENAME + ".xsd";
    public static final String XML_FILE_NAME = FILE_BASENAME + ".xml";
    public static final String BASE_KEY = "settings";
    private final DocumentBuilderFactory builderFactory;
    private DocumentBuilder builder;
    private Validator validator;
    private Document xmlDocument;

    public Settings() throws ParserConfigurationException {
        builderFactory = DocumentBuilderFactory.newNSInstance();
        builder = builderFactory.newDocumentBuilder();
        validator = null;
        xmlDocument = null;
    }

    public void loadXSD(File xsd) throws SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(xsd);
        builderFactory.setSchema(schema);
        validator = schema.newValidator();
    }

    public void loadXML(File xml) throws ParserConfigurationException, IOException, SAXException {
        builder = builderFactory.newDocumentBuilder();
        xmlDocument = null;
        Document temp = builder.parse(xml);
        if (validator != null) {
            validator.validate(new DOMSource(temp));
            xmlDocument = temp;
        }
    }

    public Map<String, Object> collectAsMap() {
        if (xmlDocument != null) {
            Map<String, Object> map = new HashMap<>();
            Element rootNode = getRootNode();
            if (rootNode != null) {
                mapping(rootNode, map, rootNode.getTagName());
            }
            return map;
        } else {
            return Collections.emptyMap();
        }
    }

    protected void mapping(Element element, Map<String, Object> map, String key) {
        if (element.hasChildNodes()) {
            NodeList nodeList = element.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node child = nodeList.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    mapping((Element) child, map, key + "." + child.getNodeName());
                }
            }
        }
        if (element.hasAttributes()) {
            NamedNodeMap attrs = element.getAttributes();
            for (int i = 0; i < attrs.getLength(); i++) {
                Node attribute = attrs.item(i);
                String attrKey = key + "." + attribute.getNodeName();
                map.put(attrKey, attribute.getTextContent());
            }
        } else {
            map.put(key, element.getTextContent());
        }
    }

    protected Element getRootNode() {
        if (xmlDocument == null) {
            return null;
        }
        String targetNamespace = "http://www.elbaden.com.ar/settings";
        NodeList nodeList = xmlDocument.getElementsByTagNameNS(targetNamespace, BASE_KEY);
        List<Element> elements = retrieveElements(nodeList);
        if (elements.isEmpty()) {
            return null;
        } else {
            return elements.getFirst();
        }
    }

    protected List<Element> retrieveElements(NodeList nodeList) {
        if (nodeList == null) {
            return Collections.emptyList();
        }
        List<Element> elements = new ArrayList<>(nodeList.getLength());
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                elements.add((Element) node);
            }
        }
        return Collections.unmodifiableList(elements);
    }

    public static File getAppFolder() {
        return new File(System.getProperty("user.home"), FOLDER_NAME);
    }

    public static File getLogFile() {
        return new File(getAppFolder(), LOG_FILE_NAME);
    }

    public static File getXSDFile() {
        return new File(getAppFolder(), XSD_FILE_NAME);
    }

    public static File getXMLFile() {
        return new File(getAppFolder(), XML_FILE_NAME);
    }

}
