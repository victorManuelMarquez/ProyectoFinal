package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;
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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Settings {

    public static final String XSD_FILE_NAME = "settings.xsd";
    public static final String XML_FILE_NAME = "settings.xml";
    public static final String THEME_KEY = "Theme";
    public static final String CONFIRM_EXIT_KEY = "ConfirmExit";

    private static final Logger LOGGER = Logger.getLogger(Settings.class.getName());

    private final String targetNamespace = "http://www.example.com/settings";
    private final String rootNodeName = "settings";
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

    public Map<String, Object> mapAll() {
        Map<String, Object> map = new HashMap<>();
        map.put(CONFIRM_EXIT_KEY, getConfirmValue());
        map.put(THEME_KEY, getTheme());
        map.putAll(getFontsMap());
        return map;
    }

    public String getTheme() {
        NodeList elements = document.getElementsByTagNameNS(targetNamespace, classThemeNodeName);
        if (elements.getLength() > 0) {
            return elements.item(0).getTextContent();
        }
        return null;
    }

    public String getConfirmValue() {
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

    private void setConfirmValue(boolean value) {
        NodeList results = document.getElementsByTagNameNS(targetNamespace, confirmExitNodeName);
        if (results.getLength() > 0) {
            Node node = results.item(0);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element confirmNode = (Element) node;
                confirmNode.setAttribute("value", Boolean.toString(value));
            }
        }
    }

    private Font createFont(Node fontNode) {
        if (fontNode != null) {
            String family = null;
            String style = null;
            String size = null;
            NodeList children = fontNode.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) children.item(i);
                    switch (element.getNodeName()) {
                        case familyNodeName -> family = element.getTextContent();
                        case styleNodeName -> style = element.getTextContent();
                        case sizeNodeName -> size = element.getTextContent();
                    }
                }
            }
            int styleInt = Font.PLAIN;
            if (style != null) {
                switch (Integer.parseInt(style)) {
                    case Font.BOLD | Font.ITALIC -> styleInt = Font.BOLD | Font.ITALIC;
                    case Font.BOLD -> styleInt = Font.BOLD;
                    case Font.ITALIC -> styleInt = Font.ITALIC;
                }
            }
            int sizeInt = 12;
            if (size != null) {
                sizeInt = Integer.parseInt(size);
            }
            return new Font(family, styleInt, sizeInt);
        }
        return null;
    }

    public Map<String, Font> getFontsMap() {
        Map<String, Font> fontMap = new HashMap<>();
        NodeList elements = document.getElementsByTagNameNS(targetNamespace, fontNodeName);
        for (int i = 0; i < elements.getLength(); i++) {
            Node node = elements.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String id = element.getAttribute("id");
                Font font = createFont(element);
                fontMap.put(id, font);
            }
        }
        return fontMap;
    }

    public void restoreXSD(File outputFile) throws TransformerException {
        Document xsdDocument = generateXSD();
        saveDocument(xsdDocument, outputFile);
    }

    public void restoreXML(File outputFile) throws TransformerException {
        rebuildXML();
        saveDocument(document, outputFile);
    }

    public static String findKey(Component component) {
        String className = component.getClass().getSimpleName().substring(1);
        Map<String, Object> defaults = App.defaults();
        Stream<String> stream = defaults.keySet().stream().filter(k -> k.contains(className));
        Optional<String> key = stream.findFirst();
        return key.orElse(null);
    }

    public static void applyFont(Component component) {
        String key = findKey(component);
        Map<String, Object> defaults = App.defaults();
        if (key != null) {
            Font font = (Font) defaults.get(key);
            component.setFont(font);
        }
    }

    public static void updateAllFonts(Component component) {
        if (component instanceof JMenu menu) {
            for (Component c : menu.getMenuComponents()) {
                updateAllFonts(c);
            }
        } else if (component instanceof Container container) {
            for (Component c : container.getComponents()) {
                updateAllFonts(c);
            }
        }
        applyFont(component);
    }

    public static void confirmExit(boolean confirm) {
        try {
            Settings settings = new Settings();
            File outputDir = new File(System.getProperty("user.home"), App.FOLDER);
            File xsdFile = new File(outputDir, XSD_FILE_NAME);
            File xmlFile = new File(outputDir, XML_FILE_NAME);
            settings.loadDocument(xsdFile, xmlFile);
            settings.setConfirmValue(confirm);
            settings.saveDocument(settings.document, xmlFile);
            App.putDefault(CONFIRM_EXIT_KEY, confirm);
            LOGGER.fine(String.format("%s = %b", CONFIRM_EXIT_KEY, confirm));
        } catch (ParserConfigurationException | IOException | SAXException | TransformerException e) {
            LOGGER.severe(e.getMessage());
        }
    }

}
