package ar.com.elbaden.gui;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

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
    public static final String XML_FILE_NAME = FILE_BASENAME + ".xml";
    public static final String BASE_KEY = "settings";
    private final String targetNamespace = "http://www.elbaden.com.ar/settings";
    private final String rootNodeName = BASE_KEY;
    private final String closingDialogTagName = "showClosingDialog";
    private final String closingDialogAttrName = "value";
    private final DocumentBuilderFactory builderFactory;
    private final Validator validator;
    private DocumentBuilder builder;
    private Document xmlDocument;

    public Settings() throws ParserConfigurationException, SAXException {
        builderFactory = DocumentBuilderFactory.newNSInstance();
        builder = builderFactory.newDocumentBuilder();
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(new DOMSource(generateXSD()));
        builderFactory.setSchema(schema);
        validator = schema.newValidator();
        xmlDocument = null;
    }

    public void loadXML(File xml) throws ParserConfigurationException, IOException, SAXException {
        builder = builderFactory.newDocumentBuilder();
        xmlDocument = null;
        Document temp = builder.parse(xml);
        validator.validate(new DOMSource(temp));
        xmlDocument = temp;
        xmlDocument.normalize();
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
        NodeList nodeList = xmlDocument.getElementsByTagNameNS(targetNamespace, rootNodeName);
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

    protected Document generateXSD() {
        // variables
        String namespace = XMLConstants.W3C_XML_SCHEMA_NS_URI;
        String closingDialogComplexTypeName = "closingDialogType";

        // definición del esquema
        Document xsd = builder.newDocument();

        // esquema
        Element schema = xsd.createElementNS(namespace, "xs:schema");
        schema.setAttribute("targetNamespace", targetNamespace);
        schema.setAttribute("xmlns", targetNamespace);
        schema.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:xs", namespace);
        schema.setAttribute("elementFormDefault", "qualified");
        xsd.appendChild(schema);

        // nodo raíz
        Element root = xsd.createElementNS(namespace, "xs:element");
        root.setAttribute("name", rootNodeName);
        Element rootComplexType = xsd.createElementNS(namespace, "xs:complexType");
        Element rootSequence = xsd.createElementNS(namespace, "xs:sequence");
        // elementos en el nodo raíz
        Element closingDialogElement = xsd.createElementNS(namespace, "xs:element");
        closingDialogElement.setAttribute("name", closingDialogTagName);
        closingDialogElement.setAttribute("type", closingDialogComplexTypeName);
        rootSequence.appendChild(closingDialogElement);
        // fin de los elementos en el nodo raíz
        rootComplexType.appendChild(rootSequence);
        root.appendChild(rootComplexType);
        schema.appendChild(root);

        // tipos
        Element closingDialogComplexType = xsd.createElementNS(namespace, "xs:complexType");
        closingDialogComplexType.setAttribute("name", closingDialogComplexTypeName);
        Element showDialogAttribute = xsd.createElementNS(namespace, "xs:attribute");
        showDialogAttribute.setAttribute("name", closingDialogAttrName);
        showDialogAttribute.setAttribute("type", "xs:boolean");
        closingDialogComplexType.appendChild(showDialogAttribute);
        schema.appendChild(closingDialogComplexType);

        return xsd;
    }

    protected Document generateXML() {
        // esquema
        Document xml = builder.newDocument();
        // definición del esquema
        Element root = xml.createElementNS(targetNamespace, rootNodeName);
        Element showClosingDialog = xml.createElementNS(targetNamespace, closingDialogTagName);
        showClosingDialog.setAttribute(closingDialogAttrName, Boolean.toString(true));
        root.appendChild(showClosingDialog);
        xml.appendChild(root);
        return xml;
    }

    protected void saveDocument(Document document, File outputFile, int indent) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        factory.setAttribute("indent-number", Integer.toString(indent));
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(outputFile);
        transformer.transform(source, result);
    }

    public void restoreXML(File xml, int indent) throws TransformerException {
        saveDocument(generateXML(), xml, indent);
    }

    public static File getAppFolder() {
        return new File(System.getProperty("user.home"), FOLDER_NAME);
    }

    public static File getLogFile() {
        return new File(getAppFolder(), LOG_FILE_NAME);
    }

    public static File getXMLFile() {
        return new File(getAppFolder(), XML_FILE_NAME);
    }

}
