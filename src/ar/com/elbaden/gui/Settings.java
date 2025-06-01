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

    public Settings() throws ParserConfigurationException {
        builderFactory = DocumentBuilderFactory.newInstance();
        builder = builderFactory.newDocumentBuilder();
        document = builder.newDocument();
    }

    @Override
    public String toString() {
        return document.getDocumentURI();
    }

    private Element createNode(String tagName, String attrName, String attrValue) {
        Attr attr = (attrName == null) ? null : document.createAttribute(attrName);
        if (attr != null) {
            attr.setValue((attrValue == null) ? "" : attrValue);
        }
        return createNode(tagName, attr);
    }

    private Element createNode(String tagName, Attr attribute) {
        if (tagName == null) {
            throw new IllegalArgumentException("tagName = null");
        }
        Element node = document.createElement(tagName);
        if (attribute != null) {
            node.setAttributeNode(attribute);
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
    }

    public static Settings getDefaults() throws ParserConfigurationException {
        UIDefaults defaults = UIManager.getDefaults();
        Enumeration<Object> keys = defaults.keys();
        Settings settings = new Settings();
        LookAndFeel theme = UIManager.getLookAndFeel();

        Element root = settings.createNode("settings", null);
        settings.document.appendChild(root);

        Element themeNode = settings.createNode("theme", "id", theme.getID());
        settings.append(root, themeNode);
        Element classThemeNode = settings.createNode("class", null);
        settings.setTextNode(classThemeNode, theme.getClass().getName());
        settings.append(themeNode, classThemeNode);

        Element fontsNode = settings.createNode("fonts", null);
        settings.append(root, fontsNode);

        if (theme.getID().equals("Metal")) {
            fontsNode.setAttribute("swing.boldMetal", "true");
        }

        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            if (defaults.get(key) instanceof Font font) {
                Element fontNode = settings.createNode("font", "key", key.toString());
                settings.append(fontsNode, fontNode);
                Element familyNode = settings.createNode("family", null);
                settings.setTextNode(familyNode, font.getFamily());
                settings.append(fontNode, familyNode);
                Element styleNode = settings.createNode("style", null);
                settings.setTextNode(styleNode, font.getStyle());
                settings.append(fontNode, styleNode);
                Element sizeNode = settings.createNode("size", null);
                settings.setTextNode(sizeNode, font.getSize());
                settings.append(fontNode, sizeNode);
            }
        }
        return settings;
    }

}
