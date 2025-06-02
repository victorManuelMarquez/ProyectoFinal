package ar.com.elbaden.gui;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

public class Settings {

    private DocumentBuilderFactory builderFactory;
    private DocumentBuilder builder;
    private Document document;
    private Element root;
    private Element themeNode;
    private Element fontsNode;
    private String source;

    public Settings() throws ParserConfigurationException {
        builderFactory = DocumentBuilderFactory.newInstance();
        builder = builderFactory.newDocumentBuilder();
        document = builder.newDocument();
        source = Settings.class.getSimpleName();
        configureDOM();
    }

    @Override
    public String toString() {
        return source;
    }

    private void configureDOM() {
        builderFactory.setValidating(true);
        builderFactory.setNamespaceAware(true);
    }

    private Element createNodeWithId(String tagName, String idValue) {
        if (idValue == null) {
            throw new IllegalArgumentException("id = null");
        }
        Attr attr = document.createAttribute("id");
        attr.setValue(idValue);
        return createNode(tagName, attr);
    }

    private Element createNode(String tagName, Attr attribute) {
        if (tagName == null) {
            throw new IllegalArgumentException("tagName = null");
        }
        Element node = document.createElement(tagName);
        if (attribute != null) {
            if (attribute.isId()) {
                node.setIdAttributeNode(attribute, true);
            } else {
                node.setAttributeNode(attribute);
            }
        }
        return node;
    }

    private void append(Element parent, Element child) {
        parent.appendChild(child);
    }

    private void setTextNode(Element node, Object value) {
        if (node == null) {
            throw new IllegalArgumentException("node = null");
        }
        Text textContent = document.createTextNode((value == null) ? "" : value.toString());
        node.appendChild(textContent);
    }

    private void installRoot() {
        root = createNode("settings", null);
        document.appendChild(root);
    }

    private void installTheme(LookAndFeel theme) {
        themeNode = createNodeWithId("theme", theme.getID());
        Element classNode = document.createElement("className");
        setTextNode(classNode, theme.getClass().getName());
        append(themeNode, classNode);
        append(root, themeNode);
    }

    private void installFontsNode() {
        fontsNode = createNode("fonts", null);
        append(root, fontsNode);
    }

    private void mapping() {
        NodeList nodeList = document.getElementsByTagName("settings");
        if (nodeList.getLength() > 0) {
            root = (Element) nodeList.item(0);
        }
        nodeList = document.getElementsByTagName("theme");
        if (nodeList.getLength() > 0) {
            themeNode = (Element) nodeList.item(0);
        }
        nodeList = document.getElementsByTagName("fonts");
        if (nodeList.getLength() > 0) {
            fontsNode = (Element) nodeList.item(0);
            NodeList nodes = fontsNode.getElementsByTagName("font");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element fontNode = (Element) nodes.item(i);
                if (fontNode.hasAttributes()) {
                    NamedNodeMap attributes = fontNode.getAttributes();
                    for (int j = 0; j < attributes.getLength(); j++) {
                        // solución "bruta" para establecer los ID para el document
                        if (attributes.item(j).getNodeName().equals("id")) {
                            fontNode.setIdAttributeNode((Attr) attributes.item(j), true);
                        }
                    }
                }
            }
        }
    }

    private void addFont(Object key, Font font) {
        Element fontNode = createNodeWithId("font", key.toString());
        append(fontsNode, fontNode);
        Element familyNode = createNode("family", null);
        setTextNode(familyNode, font.getFamily());
        append(fontNode, familyNode);
        Element styleNode = createNode("style", null);
        setTextNode(styleNode, font.getStyle());
        append(fontNode, styleNode);
        Element sizeNode = createNode("size", null);
        setTextNode(sizeNode, font.getSize());
        append(fontNode, sizeNode);
    }

    public void dump(File outputFile, int indentSpaces) throws TransformerException {
        if (indentSpaces < 1) {
            throw new IllegalArgumentException(indentSpaces + " tab spaces");
        }
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(outputFile);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(
                "{http://xml.apache.org/xslt}indent-amount", Integer.toString(indentSpaces));
        transformer.transform(source, result);
    }

    public void load(File inputFile) throws ParserConfigurationException, IOException, SAXException {
        builderFactory = DocumentBuilderFactory.newInstance();
        builder = builderFactory.newDocumentBuilder();
        document = builder.parse(inputFile);
        configureDOM();
        mapping();
        source = inputFile.getPath();
    }

    public String getThemeID() {
        return themeNode.getAttributeNode("id").getValue();
    }

    public String getThemeClass() {
        NodeList elements = themeNode.getElementsByTagName("className");
        if (elements.getLength() > 0) {
            Node classNode = elements.item(0);
            return classNode.getTextContent();
        }
        return null;
    }

    public static Settings getDefaults() throws ParserConfigurationException {
        Settings settings = new Settings();

        UIDefaults defaults = UIManager.getDefaults();
        Enumeration<Object> keys = defaults.keys();
        LookAndFeel theme = UIManager.getLookAndFeel();

        settings.installRoot();
        settings.installTheme(theme);
        settings.installFontsNode();

        if (theme.getID().equals("Metal")) {
            settings.fontsNode.setAttribute("swing.boldMetal", "true");
        }

        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            if (defaults.get(key) instanceof Font font) {
                settings.addFont(key, font);
            }
        }
        return settings;
    }

}
