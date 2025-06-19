package ar.com.elbaden.gui;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Settings {

    public static final String BASE_FILE_NAME = "config";
    public static final String XSD_FILE_NAME = BASE_FILE_NAME + ".xsd";
    public static final String XSL_FILE_NAME = BASE_FILE_NAME + ".xsl";
    public static final String XML_FILE_NAME = BASE_FILE_NAME + ".xml";
    public static final String BASE_KEY = "settings";
    private final String targetNamespace = "http://www.example.com/settings";
    private final String rootNodeName = BASE_KEY;
    private final String confirmExitNodeName = "confirmExit";
    private final String themeNodeName = "theme";
    private final String classThemeNodeName = "className";
    private final String fontsNodeName = "fonts";
    private final String fontNodeName = "font";
    private final String familyNodeName = "family";
    private final String styleNodeName = "style";
    private final String sizeNodeName = "size";
    private final DocumentBuilder builder;
    private Document document;
    private Validator validator;
    private Source xslSource;

    public Settings() throws ParserConfigurationException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        builder = builderFactory.newDocumentBuilder();
        document = builder.newDocument();
    }

    private Document generateXSD() {
        String namespace = XMLConstants.W3C_XML_SCHEMA_NS_URI;
        String confirmType = "ConfirmExitType";
        String themeType = "LookAndFeelType";
        String fontsType = "FontsType";
        String fontType = "FontType";

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
        Element confirmElement = xsdDocument.createElementNS(namespace, "xs:element");
        confirmElement.setAttribute("name", confirmExitNodeName);
        confirmElement.setAttribute("type", confirmType);
        rootSequence.appendChild(confirmElement);
        Element themeElement = xsdDocument.createElementNS(namespace, "xs:element");
        themeElement.setAttribute("name", themeNodeName);
        themeElement.setAttribute("type", themeType);
        rootSequence.appendChild(themeElement);
        Element fontsElement = xsdDocument.createElementNS(namespace, "xs:element");
        fontsElement.setAttribute("name", fontsNodeName);
        fontsElement.setAttribute("type", fontsType);
        rootSequence.appendChild(fontsElement);
        rootComplexType.appendChild(rootSequence);
        rootElement.appendChild(rootComplexType);
        schemaElement.appendChild(rootElement);

        // nodo confirmar para salir: confirmExit complexType
        Element confirmComplexType = xsdDocument.createElementNS(namespace, "xs:complexType");
        confirmComplexType.setAttribute("name", confirmType);
        Element confirmAttribute = xsdDocument.createElementNS(namespace, "xs:attribute");
        confirmAttribute.setAttribute("name", "value");
        confirmAttribute.setAttribute("type", "xs:boolean");
        confirmComplexType.appendChild(confirmAttribute);
        schemaElement.appendChild(confirmComplexType);

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

        // atributo opcional
        Element boldMetalAttribute = xsdDocument.createElementNS(namespace, "xs:attribute");
        boldMetalAttribute.setAttribute("name", "swing.boldMetal");
        boldMetalAttribute.setAttribute("type", "xs:boolean");
        boldMetalAttribute.setAttribute("use", "optional");
        themeComplexType.appendChild(boldMetalAttribute);

        schemaElement.appendChild(themeComplexType);

        // nodo fuentes: fonts complexType
        Element fontsComplexType = xsdDocument.createElementNS(namespace, "xs:complexType");
        fontsComplexType.setAttribute("name", fontsType);
        Element fontsSequence = xsdDocument.createElementNS(namespace, "xs:sequence");
        Element fontElement = xsdDocument.createElementNS(namespace, "xs:element");
        fontElement.setAttribute("name", fontNodeName);
        fontElement.setAttribute("type", fontType);
        fontElement.setAttribute("maxOccurs", "unbounded");
        fontsSequence.appendChild(fontElement);
        fontsComplexType.appendChild(fontsSequence);

        schemaElement.appendChild(fontsComplexType);

        // nodo fuente: font complexType
        Element fontComplexType = xsdDocument.createElementNS(namespace, "xs:complexType");
        fontComplexType.setAttribute("name", fontType);
        Element fontSequence = xsdDocument.createElementNS(namespace, "xs:sequence");
        Element familyElement = xsdDocument.createElementNS(namespace, "xs:element");
        familyElement.setAttribute("name", familyNodeName);
        familyElement.setAttribute("type", "xs:string");
        fontSequence.appendChild(familyElement);
        Element styleElement = xsdDocument.createElementNS(namespace, "xs:element");
        styleElement.setAttribute("name", styleNodeName);
        styleElement.setAttribute("type", "xs:integer");
        fontSequence.appendChild(styleElement);
        Element sizeElement = xsdDocument.createElementNS(namespace, "xs:element");
        sizeElement.setAttribute("name", sizeNodeName);
        sizeElement.setAttribute("type", "xs:integer");
        fontSequence.appendChild(sizeElement);
        fontComplexType.appendChild(fontSequence);

        // id para la fuente
        Element idFontAttribute = xsdDocument.createElementNS(namespace, "xs:attribute");
        idFontAttribute.setAttribute("name", "id");
        idFontAttribute.setAttribute("type", "xs:ID");
        idFontAttribute.setAttribute("use", "required");
        fontComplexType.appendChild(idFontAttribute);

        schemaElement.appendChild(fontComplexType);

        return xsdDocument;
    }

    private Document generateXSL() {
        String namespace = "http://www.w3.org/1999/XSL/Transform";

        Document xslDocument = builder.newDocument();
        xslDocument.setXmlStandalone(true);

        Element styleSheet = xslDocument.createElementNS(namespace, "xsl:stylesheet");
        styleSheet.setAttribute("version", "1.0");
        xslDocument.appendChild(styleSheet);

        Element output = xslDocument.createElementNS(namespace, "xsl:output");
        output.setAttribute("method", "xml");
        output.setAttribute("indent", "yes");
        output.setAttribute("encoding", "UTF-8");
        styleSheet.appendChild(output);

        Element stripSpace = xslDocument.createElementNS(namespace, "xsl:strip-space");
        stripSpace.setAttribute("elements", "*");
        styleSheet.appendChild(stripSpace);

        Element identityTemplate = xslDocument.createElementNS(namespace, "xsl:template");
        identityTemplate.setAttribute("match", "@*|node()");
        styleSheet.appendChild(identityTemplate);

        Element xslCopy = xslDocument.createElementNS(namespace, "xsl:copy");
        identityTemplate.appendChild(xslCopy);

        Element applyTemplates = xslDocument.createElementNS(namespace, "xsl:apply-templates");
        applyTemplates.setAttribute("select", "@*|node()");
        xslCopy.appendChild(applyTemplates);

        return xslDocument;
    }

    private void rebuildXML() {
        document = builder.newDocument(); // sobreescribo cualquier estructura anterior
        // nodos
        Element rootNode = document.createElementNS(targetNamespace, rootNodeName);
        Element confirmNode = document.createElementNS(targetNamespace, confirmExitNodeName);
        confirmNode.setAttribute("value", "true");
        Element themeNode = document.createElementNS(targetNamespace, themeNodeName);
        Element classThemeNode = document.createElementNS(targetNamespace, classThemeNodeName);
        Element fontsNode = document.createElementNS(targetNamespace, fontsNodeName);
        // recuperando datos
        LookAndFeel theme = UIManager.getLookAndFeel();
        UIDefaults defaults = UIManager.getDefaults();
        classThemeNode.setTextContent(theme.getClass().getName());
        themeNode.setAttribute("id", theme.getID());
        if (theme.getName().equals("Metal")) {
            themeNode.setAttribute("swing.boldMetal", "true");
        }
        Enumeration<Object> keys = defaults.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = defaults.get(key);
            if (value instanceof Font font) {
                Element fontNode = document.createElementNS(targetNamespace, fontNodeName);
                fontNode.setAttribute("id", key.toString());
                Element family = document.createElementNS(targetNamespace, familyNodeName);
                family.setTextContent(font.getFamily());
                fontNode.appendChild(family);
                Element style = document.createElementNS(targetNamespace, styleNodeName);
                style.setTextContent(Integer.toString(font.getStyle()));
                fontNode.appendChild(style);
                Element size = document.createElementNS(targetNamespace, sizeNodeName);
                size.setTextContent(Integer.toString(font.getSize()));
                fontNode.appendChild(size);
                fontsNode.appendChild(fontNode);
            }
        }
        // estableciendo jerarquía entre los nodos
        rootNode.appendChild(confirmNode);
        themeNode.appendChild(classThemeNode);
        rootNode.appendChild(themeNode);
        rootNode.appendChild(fontsNode);
        document.appendChild(rootNode);
    }

    public void restoreXSD(File xsd, int indent) throws TransformerException {
        Document xsdDocument = generateXSD();
        saveDocument(xsdDocument, xsd, indent);
    }

    public void restoreXSL(File xsl, int indent) throws TransformerException {
        Document xslDocument = generateXSL();
        saveDocument(xslDocument, xsl, indent);
    }

    public void restoreXML(File xml, int indent) throws TransformerException {
        rebuildXML();
        saveDocument(document, xml, indent);
    }

    public void saveDocument(Document document, File outputFile, int indent) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        factory.setAttribute("indent-number", Integer.toString(indent));
        Transformer transformer = factory.newTransformer();
        if (xslSource != null) {
            transformer = factory.newTransformer(xslSource);
        }
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(outputFile);
        transformer.transform(source, result);
    }

    public void loadXSD(File xsd) throws SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(xsd);
        validator = schema.newValidator();
    }

    public void loadXSL(File xsl) {
        xslSource = new StreamSource(xsl);
    }

    public void loadXML(File xml) throws IOException, SAXException {
        Document temp = builder.parse(xml);
        if (validator != null) {
            validator.validate(new DOMSource(temp));
            document = temp;
        }
    }

    public void loadDocument(File inputFile) throws SAXException, IOException {
        switch (inputFile.getName()) {
            case XSD_FILE_NAME -> loadXSD(inputFile);
            case XSL_FILE_NAME -> loadXSL(inputFile);
            case XML_FILE_NAME -> loadXML(inputFile);
        }
    }

    public String getTheme() {
        if (document == null) {
            throw new IllegalStateException("xmlDocument.isNull");
        }
        NodeList results = document.getElementsByTagNameNS(targetNamespace, classThemeNodeName);
        if (results.getLength() > 0) {
            return results.item(0).getTextContent();
        }
        return null;
    }

    public String getConfirmValue() {
        if (document == null) {
            throw new IllegalStateException("xmlDocument.isNull");
        }
        NodeList results = document.getElementsByTagNameNS(targetNamespace, confirmExitNodeName);
        if (results.getLength() > 0) {
            Node confirmNode = results.item(0);
            if (confirmNode.getNodeType() == Node.ELEMENT_NODE) {
                Element node = (Element) confirmNode;
                return node.getAttribute("value");
            }
        }
        return null;
    }

    public Map<String, Font> getFonts() {
        if (document == null) {
            throw new IllegalStateException("xmlDocument.isNull");
        }
        Map<String, Font> map = new HashMap<>();
        NodeList results = document.getElementsByTagNameNS(targetNamespace, fontsNodeName);
        if (results.getLength() > 0) {
            if (results.item(0).getNodeType() == Node.ELEMENT_NODE) {
                Element fontsNode = (Element) results.item(0);
                NodeList nodes = fontsNode.getChildNodes();
                for (int i = 0; i < nodes.getLength(); i++) {
                    if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        Element fontNode = (Element) nodes.item(i);
                        String id = fontNode.getAttribute("id");
                        Font font = UIManager.getFont(id);
                        if (font != null) {
                            font = alterFont(font, fontNode);
                            map.put(createKey(id), font);
                        }
                    }
                }
            }
        }
        return map;
    }

    private Font alterFont(Font font, Element fontNode) {
        NodeList nodes = fontNode.getChildNodes();
        Font temp = font;
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            switch (node.getNodeName()) {
                case familyNodeName -> temp = new Font(node.getTextContent(), temp.getStyle(), temp.getSize());
                case styleNodeName -> {
                    switch (Integer.parseInt(node.getTextContent())) {
                        case Font.PLAIN -> temp = temp.deriveFont(Font.PLAIN);
                        case Font.ITALIC -> temp = temp.deriveFont(Font.ITALIC);
                        case Font.BOLD -> temp = temp.deriveFont(Font.BOLD);
                        case Font.BOLD|Font.ITALIC -> temp = temp.deriveFont(Font.BOLD|Font.ITALIC);
                    }
                }
                case sizeNodeName -> temp = temp.deriveFont(Float.parseFloat(node.getTextContent()));
            }
        }
        return temp;
    }

    private String createKey(String nodeName) {
        return BASE_KEY + "." + nodeName;
    }

    public Map<String, Object> mapAll() {
        Map<String, Object> map = new HashMap<>(getFonts());
        map.put(createKey(themeNodeName), getTheme());
        map.put(createKey(confirmExitNodeName), Boolean.parseBoolean(getConfirmValue()));
        return map;
    }

    public static int applyFont(Map<String, Object> fonts, Component origin, int counter) {
        if (origin instanceof JMenuItem item) {
            for (MenuElement element : item.getSubElements()) {
                counter = applyFont(fonts, (Component) element, counter);
            }
        }
        if (origin instanceof Container container) {
            for (Component component : container.getComponents()) {
                counter = applyFont(fonts, component, counter);
            }
        }
        String key = getValue(origin);
        if (key != null) {
            key = BASE_KEY + "." + key + ".font";
            if (fonts.containsKey(key)) {
                Object value = fonts.get(key);
                if (value instanceof Font font) {
                    SwingUtilities.invokeLater(() -> origin.setFont(font));
                    counter = counter + 1;
                }
            }
        }
        return counter;
    }

    public static String getValue(Component component) {
        if (component instanceof JComponent) {
            String className = component.getClass().getName();
            Pattern pattern = Pattern.compile("\\.J[^.]*");
            Matcher matcher = pattern.matcher(className);
            if (matcher.find()) {
                String result = matcher.group();
                if (result.length() > 1) {
                    return result.substring(2);
                }
            }
        }
        return null;
    }

    public static String applyTheme(Window source, String className) {
        String name = null;
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if (info.getClassName().equals(className)) {
                name = info.getName();
                break;
            }
        }
        if (UIManager.getLookAndFeel().getClass().getName().equals(className)) {
            return name;
        }
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(className);
                SwingUtilities.updateComponentTreeUI(source);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return name;
    }

}
