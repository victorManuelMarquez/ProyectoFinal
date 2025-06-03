package ar.com.elbaden;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
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
import java.io.StringWriter;

public class Settings {

    private final String targetNamespace = "http://www.example.com/settings";
    private final String rootNodeName = "settings";
    private final String themeNodeName = "theme";
    private final String classThemeNodeName = "className";
    private final String fontsNodeName = "fonts";
    private final DocumentBuilder builder;
    private Document document;

    public Settings() throws ParserConfigurationException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        builder = builderFactory.newDocumentBuilder();
        document = builder.newDocument();
    }

    @Override
    public String toString() {
        try {
            return convertToString(document);
        } catch (Exception e) {
            return super.toString();
        }
    }

    private Document generateXSD() {
        String namespace = XMLConstants.W3C_XML_SCHEMA_NS_URI;
        String themeType = "LookAndFeelType";
        String fontsType = "FontsType";
        Document xsdDocument = builder.newDocument();

        // esquema
        Element schemaElement = xsdDocument.createElementNS(namespace, "xs:schema");
        schemaElement.setAttribute("targetNamespace", targetNamespace);
        schemaElement.setAttribute("xmlns", targetNamespace);
        schemaElement.setAttribute("xmlns:xs", namespace);
        schemaElement.setAttribute("elementFormDefault", "qualified");
        xsdDocument.appendChild(schemaElement);

        // nodo raíz: settings
        Element rootElement = xsdDocument.createElementNS(namespace, "xs:element");
        rootElement.setAttribute("name", rootNodeName);
        Element rootComplexType = xsdDocument.createElementNS(namespace, "xs:complexType");
        Element rootSequence = xsdDocument.createElementNS(namespace, "xs:sequence");
        Element themeElement = xsdDocument.createElementNS(namespace, "xs:element");
        themeElement.setAttribute("name", themeNodeName);
        themeElement.setAttribute("type", themeType);
        Element fontsElement = xsdDocument.createElementNS(namespace, "xs:element");
        fontsElement.setAttribute("name", fontsNodeName);
        fontsElement.setAttribute("type", fontsType);
        rootSequence.appendChild(themeElement);
        rootSequence.appendChild(fontsElement);
        rootComplexType.appendChild(rootSequence);
        rootElement.appendChild(rootComplexType);
        schemaElement.appendChild(rootElement);

        // nodo tema: LookAndFeel complexType
        Element themeComplexType = xsdDocument.createElementNS(namespace, "xs:complexType");
        themeComplexType.setAttribute("name", themeType);
        Element themeSequence = xsdDocument.createElementNS(namespace, "xs:sequence");
        Element classThemeElement = xsdDocument.createElementNS(namespace, "xs:element");
        classThemeElement.setAttribute("name", classThemeNodeName);
        classThemeElement.setAttribute("type", "xs:string");
        themeSequence.appendChild(classThemeElement);
        themeComplexType.appendChild(themeSequence);

        // id para el tema
        Element idThemeAttribute = xsdDocument.createElementNS(namespace, "xs:attribute");
        idThemeAttribute.setAttribute("name", "id");
        idThemeAttribute.setAttribute("type", "xs:ID");
        idThemeAttribute.setAttribute("use", "required");
        themeComplexType.appendChild(idThemeAttribute);

        schemaElement.appendChild(themeComplexType);

        // nodo fuentes: fonts complexType
        Element fontsComplexType = xsdDocument.createElementNS(namespace, "xs:complexType");
        fontsComplexType.setAttribute("name", fontsType);
        Element fontsSequence = xsdDocument.createElementNS(namespace, "xs:sequence");
        fontsComplexType.appendChild(fontsSequence);

        schemaElement.appendChild(fontsComplexType);

        return xsdDocument;
    }

    private void rebuildXML() {
        document = builder.newDocument(); // sobreescribo cualquier estructura anterior
        // nodos
        Element rootNode = document.createElementNS(targetNamespace, rootNodeName);
        Element themeNode = document.createElementNS(targetNamespace, themeNodeName);
        Element classThemeNode = document.createElementNS(targetNamespace, classThemeNodeName);
        Element fontsNode = document.createElementNS(targetNamespace, fontsNodeName);
        // recuperando datos
        LookAndFeel theme = UIManager.getLookAndFeel();
        classThemeNode.setTextContent(theme.getClass().getName());
        themeNode.setAttribute("id", theme.getID());
        // estableciendo jerarquía entre los nodos
        themeNode.appendChild(classThemeNode);
        rootNode.appendChild(themeNode);
        rootNode.appendChild(fontsNode);
        document.appendChild(rootNode);
    }

    private String convertToString(Document document) throws TransformerException {
        StringWriter writer = new StringWriter();
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        return writer.toString();
    }

    public void saveDocument(Document document, File outputFile) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(outputFile);
        transformer.transform(source, result);
    }

    public void loadDocument(File xsdFile, File xmlFile) throws IOException, SAXException {
        Document xmlDocument = builder.parse(xmlFile);
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(xsdFile);
        Validator validator = schema.newValidator();
        validator.validate(new DOMSource(xmlDocument));
        document = xmlDocument;
    }

    public String getTheme() {
        NodeList elements = document.getElementsByTagNameNS(targetNamespace, classThemeNodeName);
        if (elements.getLength() > 0) {
            return elements.item(0).getTextContent();
        }
        return null;
    }

    public void restoreDefaults(File xsdFile, File xmlFile) throws TransformerException {
        Document xsdDocument = generateXSD();
        rebuildXML();
        saveDocument(xsdDocument, xsdFile);
        saveDocument(document, xmlFile);
    }

}
