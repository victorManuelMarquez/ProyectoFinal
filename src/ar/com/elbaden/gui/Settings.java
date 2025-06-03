package ar.com.elbaden.gui;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Settings {

    private final String targetNamespace = "http://www.example.com/settings";
    private final String rootNodeName = "settings";
    private final String themeNodeName = "theme";
    private final String classThemeNodeName = "className";
    private final DocumentBuilderFactory builderFactory;
    private DocumentBuilder builder;
    private Document document;

    public Settings() throws ParserConfigurationException {
        builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setValidating(true);
        builderFactory.setNamespaceAware(true);
        builder = builderFactory.newDocumentBuilder();
        document = builder.newDocument();
    }

    public Document createSchema() {
        Document doc = builder.newDocument();
        String themeType = "LookAndFeelType";
        String xsdNamespace = "http://www.w3.org/2001/XMLSchema";
        Element schemaElement = doc.createElementNS(xsdNamespace, "xs:schema");
        schemaElement.setAttribute("targetNamespace", targetNamespace);
        schemaElement.setAttribute("xmlns", targetNamespace);
        schemaElement.setAttribute("elementFormDefault", "qualified");
        doc.appendChild(schemaElement);
        // nodo raíz
        Element rootElement = doc.createElementNS(xsdNamespace, "xs:element");
        rootElement.setAttribute("name", rootNodeName);
        Element rootComplexType = doc.createElementNS(xsdNamespace, "xs:complexType");
        Element rootSequence = doc.createElementNS(xsdNamespace, "xs:sequence");
        Element themeElement = doc.createElementNS(xsdNamespace, "xs:element");
        themeElement.setAttribute("name", themeNodeName);
        themeElement.setAttribute("type", themeType);
        rootSequence.appendChild(themeElement);
        rootComplexType.appendChild(rootSequence);
        rootElement.appendChild(rootComplexType);
        schemaElement.appendChild(rootElement);
        // nodo tema
        Element themeComplexType = doc.createElementNS(xsdNamespace, "xs:complexType");
        themeComplexType.setAttribute("name", themeType);
        Element themeSequence = doc.createElementNS(xsdNamespace, "xs:sequence");
        Element classThemeElement = doc.createElementNS(xsdNamespace, "xs:element");
        classThemeElement.setAttribute("name", "className");
        classThemeElement.setAttribute("type", "xs:string");
        Element idThemeElement = doc.createElementNS(xsdNamespace, "xs:attribute");
        idThemeElement.setAttribute("name", "id");
        idThemeElement.setAttribute("type", "xs:ID");
        idThemeElement.setAttribute("use", "required");
        themeSequence.appendChild(classThemeElement);
        themeComplexType.appendChild(themeSequence);
        themeComplexType.appendChild(idThemeElement);
        schemaElement.appendChild(themeComplexType);
        return doc;
    }

    public File restoreSchema(File outputFile, Document schema) throws TransformerException, FileNotFoundException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        DOMSource source = new DOMSource(schema);
        StreamResult result = new StreamResult(new FileOutputStream(outputFile.getPath()));
        transformer.transform(source, result);
        return outputFile;
    }

    public File restoreDefaults(File outputFile) throws TransformerException {
        Element rootNode = document.createElementNS(targetNamespace, rootNodeName);
        Element themeNode = document.createElement(themeNodeName);
        Element classTheme = document.createElement(classThemeNodeName);
        LookAndFeel theme = UIManager.getLookAndFeel();
        classTheme.setTextContent(theme.getClass().getName());
        themeNode.appendChild(classTheme);
        themeNode.setAttribute("id", theme.getID());
        rootNode.appendChild(themeNode);
        document.appendChild(rootNode);
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(outputFile);
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(source, result);
        return outputFile;
    }

    public ErrorHandler load(File xsdFile, File xmlFile) throws IOException, SAXException, ParserConfigurationException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(xsdFile);
        builderFactory.setSchema(schema);
        builder = builderFactory.newDocumentBuilder();
        XSDErrorHandler errorHandler = new XSDErrorHandler();
        Validator validator = schema.newValidator();
        validator.setErrorHandler(errorHandler);
        document = builder.parse(xmlFile);
        validator.validate(new DOMSource(document));
        return errorHandler;
    }

    public String getClassTheme() {
        NodeList nodeList = document.getElementsByTagNameNS(targetNamespace, themeNodeName);
        if (nodeList.getLength() > 0) {
            if (nodeList.item(0) instanceof Element themeNode && themeNode.hasChildNodes()) {
                NodeList nodes = themeNode.getChildNodes();
                String className = null;
                for (int i = 0; i < nodes.getLength(); i++) {
                    if (nodes.item(i) instanceof Element node && node.getNodeName().equals(classThemeNodeName)) {
                        className = node.getTextContent();
                        break;
                    }
                }
                return className;
            }
        }
        return null;
    }

    static class XSDErrorHandler implements ErrorHandler {

        @Override
        public void warning(SAXParseException exception) throws SAXException {
            System.out.println(exception.getMessage());
            throw exception;
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            System.out.println(exception.getMessage());
            throw exception;
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            System.out.println(exception.getMessage());
            throw exception;
        }

    }

}
