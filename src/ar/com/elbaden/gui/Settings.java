package ar.com.elbaden.gui;

import ar.com.elbaden.main.App;
import org.w3c.dom.*;
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
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Settings {

    public static final String FOLDER_NAME = ".baden";
    public static final String LOG_FILE_NAME = "log.txt";
    public static final String FILE_BASENAME = "config";
    public static final String XML_FILE_NAME = FILE_BASENAME + ".xml";
    public static final String BASE_KEY = "settings";
    public static final String BOLD_METAL = "swing.boldMetal";
    private final String targetNamespace = "http://www.elbaden.com.ar/app/settings";
    private final String rootNodeName = BASE_KEY;
    private final String closingDialogTagName = "showClosingDialog";
    private final String closingDialogAttrName = "value";
    private final String plafTagName = "lookAndFeel";
    private final String plafChildNodeName = "className";
    private final String fontsTagName = "fonts";
    private final String fontTagName = "font";
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

    public Map<String, Object> collectNodes() {
        if (xmlDocument == null) {
            return Collections.emptyMap();
        }
        Map<String, Object> map = new HashMap<>();
        Element rootNode = getElementsByTagName(rootNodeName).getFirst();
        if (rootNode != null) {
            // mapeo la confirmación de cierre
            Element closingDialogNode = getElementsByTagName(closingDialogTagName).getFirst();
            mapping(closingDialogNode, map, rootNode.getTagName());
            // mapeo el tema
            Element lookAndFeelNode = getElementsByTagName(plafTagName).getFirst();
            mapLookAndFeelNode(lookAndFeelNode, map, rootNode.getTagName());
        }
        return map;
    }

    protected void mapping(Element element, Map<String, Object> map, String key) {
        key += "." + element.getTagName();
        if (element.hasAttribute("id")) {
            key += "." + element.getAttribute("id");
        }
        if (element.hasChildNodes()) {
            NodeList nodeList = element.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node child = nodeList.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    mapping((Element) child, map, key);
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

    protected void mapLookAndFeelNode(Element lookAndFeelNode, Map<String, Object> map, String baseName) {
        String id = lookAndFeelNode.getAttribute("id");
        if (id.isBlank()) {
            return;
        }
        String key = baseName + "." + lookAndFeelNode.getTagName();
        String idKey = key + ".id";
        map.put(idKey, id);
        String boldValue = lookAndFeelNode.getAttribute(BOLD_METAL);
        if (!boldValue.isBlank()) {
            String boldKey = key + "." + BOLD_METAL;
            map.put(boldKey, boldValue);
        }
        NodeList childNodes = lookAndFeelNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                mapping((Element) child, map, key);
            }
        }
    }

    protected Map<String, Font> collectFonts() {
        if (xmlDocument == null) {
            return Collections.emptyMap();
        }
        Map<String, Font> map = new HashMap<>();
        List<Element> fontsNode = getElementsByTagName(fontTagName);
        for (Element fontNode : fontsNode) {
            String id = fontNode.getAttribute("id");
            if (!id.isBlank()) {
                Font font = UIManager.getFont(id);
                if (font != null) {
                    List<Element> childElements = retrieveElements(fontNode.getChildNodes());
                    for (Element child : childElements) {
                        font = deriveFont(child, font);
                    }
                    map.put(id, font);
                }
            }
        }
        return map;
    }

    protected Font deriveFont(Element element, Font font) {
        switch (element.getTagName()) {
            case "family" -> font = new Font(element.getTextContent(), font.getStyle(), font.getSize());
            case "style" -> {
                switch (Integer.parseInt(element.getTextContent())) {
                    case Font.PLAIN -> font = font.deriveFont(Font.PLAIN);
                    case Font.BOLD -> font = font.deriveFont(Font.BOLD);
                    case Font.ITALIC -> font = font.deriveFont(Font.ITALIC);
                    case Font.BOLD|Font.ITALIC -> font = font.deriveFont(Font.BOLD|Font.ITALIC);
                }
            }
            case "size" -> font = font.deriveFont(Float.parseFloat(element.getTextContent()));
        }
        return font;
    }

    protected List<Element> getElementsByTagName(String tagName) {
        if (xmlDocument == null) {
            return Collections.emptyList();
        }
        NodeList nodeList = xmlDocument.getElementsByTagNameNS(targetNamespace, tagName);
        return retrieveElements(nodeList);
    }

    protected List<Element> retrieveElements(NodeList nodeList) {
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
        String closingDialogTypeName = "closingDialogType";
        String plafTypeName = "plafType";
        String fontsTypeName = "fontsType";
        String fontTypeName = "fontType";

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
        closingDialogElement.setAttribute("type", closingDialogTypeName);
        rootSequence.appendChild(closingDialogElement);

        Element plafElement = xsd.createElementNS(namespace, "xs:element");
        plafElement.setAttribute("name", plafTagName);
        plafElement.setAttribute("type", plafTypeName);
        rootSequence.appendChild(plafElement);

        Element fontsElement = xsd.createElementNS(namespace, "xs:element");
        fontsElement.setAttribute("name", fontsTagName);
        fontsElement.setAttribute("type", fontsTypeName);
        rootSequence.appendChild(fontsElement);
        // fin de los elementos en el nodo raíz

        rootComplexType.appendChild(rootSequence);
        root.appendChild(rootComplexType);
        schema.appendChild(root);

        // closingDialog
        Element closingDialogComplexType = xsd.createElementNS(namespace, "xs:complexType");
        closingDialogComplexType.setAttribute("name", closingDialogTypeName);
        Element showDialogAttribute = xsd.createElementNS(namespace, "xs:attribute");
        showDialogAttribute.setAttribute("name", closingDialogAttrName);
        showDialogAttribute.setAttribute("type", "xs:boolean");
        closingDialogComplexType.appendChild(showDialogAttribute);
        schema.appendChild(closingDialogComplexType);

        // lookAndFeel
        Element plafComplexType = xsd.createElementNS(namespace, "xs:complexType");
        plafComplexType.setAttribute("name", plafTypeName);

        Element plafSequence = xsd.createElementNS(namespace, "xs:sequence");
        Element classNameElement = xsd.createElementNS(namespace, "xs:element");
        classNameElement.setAttribute("name", plafChildNodeName);
        classNameElement.setAttribute("type", "xs:string");
        plafSequence.appendChild(classNameElement);
        plafComplexType.appendChild(plafSequence);

        Element plafIdAttribute = xsd.createElementNS(namespace, "xs:attribute");
        plafIdAttribute.setAttribute("name", "id");
        plafIdAttribute.setAttribute("type", "xs:ID");
        plafIdAttribute.setAttribute("use", "required");
        plafComplexType.appendChild(plafIdAttribute);

        Element boldMetalAttribute = xsd.createElementNS(namespace, "xs:attribute");
        boldMetalAttribute.setAttribute("name", BOLD_METAL);
        boldMetalAttribute.setAttribute("type", "xs:boolean");
        boldMetalAttribute.setAttribute("use", "optional");
        plafComplexType.appendChild(boldMetalAttribute);

        schema.appendChild(plafComplexType);

        // fonts
        Element fontsComplexType = xsd.createElementNS(namespace, "xs:complexType");
        fontsComplexType.setAttribute("name", fontsTypeName);
        Element fontsSequence = xsd.createElementNS(namespace, "xs:sequence");
        Element fontElement = xsd.createElementNS(namespace, "xs:element");
        fontElement.setAttribute("name", fontTagName);
        fontElement.setAttribute("type", fontTypeName);
        fontElement.setAttribute("maxOccurs", "unbounded");
        fontsSequence.appendChild(fontElement);
        fontsComplexType.appendChild(fontsSequence);
        schema.appendChild(fontsComplexType);

        // font
        Element fontComplexType = xsd.createElementNS(namespace, "xs:complexType");
        fontComplexType.setAttribute("name", fontTypeName);
        Element fontSequence = xsd.createElementNS(namespace, "xs:sequence");
        Element familyElement = xsd.createElementNS(namespace, "xs:element");
        familyElement.setAttribute("name", "family");
        familyElement.setAttribute("type", "xs:string");
        fontSequence.appendChild(familyElement);
        Element styleElement = xsd.createElementNS(namespace, "xs:element");
        styleElement.setAttribute("name", "style");
        styleElement.setAttribute("type", "xs:integer");
        fontSequence.appendChild(styleElement);
        Element sizeElement = xsd.createElementNS(namespace, "xs:element");
        sizeElement.setAttribute("name", "size");
        sizeElement.setAttribute("type", "xs:integer");
        fontSequence.appendChild(sizeElement);
        fontComplexType.appendChild(fontSequence);

        Element fontIdAttribute = xsd.createElementNS(namespace, "xs:attribute");
        fontIdAttribute.setAttribute("name", "id");
        fontIdAttribute.setAttribute("type", "xs:ID");
        fontIdAttribute.setAttribute("use", "required");
        fontComplexType.appendChild(fontIdAttribute);

        schema.appendChild(fontComplexType);

        return xsd;
    }

    protected Document generateXML() {
        // esquema
        Document xml = builder.newDocument();

        // definición del esquema
        Element root = xml.createElementNS(targetNamespace, rootNodeName);
        Element showClosingDialog = xml.createElementNS(targetNamespace, closingDialogTagName);
        showClosingDialog.setAttribute(closingDialogAttrName, Boolean.toString(true));
        Element lookAndFeel = xml.createElementNS(targetNamespace, plafTagName);
        Element className = xml.createElementNS(targetNamespace, plafChildNodeName);
        Element fonts = xml.createElementNS(targetNamespace, fontsTagName);

        // recolección de datos
        // tema
        LookAndFeel laf = UIManager.getLookAndFeel();
        className.setTextContent(laf.getClass().getName());
        lookAndFeel.setAttribute("id", laf.getID());
        if (laf.getName().equals("Metal")) {
            // esta característica solo es aplicable en este tema
            lookAndFeel.setAttribute(BOLD_METAL, Boolean.toString(true));
        }
        // fuentes
        UIDefaults defaults = UIManager.getDefaults();
        Enumeration<Object> keys = defaults.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            if (defaults.get(key) instanceof Font font) {
                fonts.appendChild(createFontNode(xml, key.toString(), font));
            }
        }
        // fin de la recolección

        // estableciendo la jerarquía
        root.appendChild(showClosingDialog);
        lookAndFeel.appendChild(className);
        root.appendChild(lookAndFeel);
        root.appendChild(fonts);
        xml.appendChild(root);

        return xml;
    }

    protected Element createFontNode(Document xml, String id, Font font) {
        Element fontNode = xml.createElementNS(targetNamespace, fontTagName);
        fontNode.setAttribute("id", id);
        Element family = xml.createElementNS(targetNamespace, "family");
        family.setTextContent(font.getFamily());
        Element style = xml.createElementNS(targetNamespace, "style");
        style.setTextContent(Integer.toString(font.getStyle()));
        Element size = xml.createElementNS(targetNamespace, "size");
        size.setTextContent(Integer.toString(font.getSize()));
        fontNode.appendChild(family);
        fontNode.appendChild(style);
        fontNode.appendChild(size);
        return fontNode;
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

    protected static String swingComponentClassName(Class<?> clazz) {
        if (clazz.getName().startsWith("javax.swing.")) {
            return clazz.getName();
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass == null) {
            return null;
        } else if (superClass.getName().startsWith("javax.swing.")) {
            return superClass.getName();
        } else {
            return swingComponentClassName(superClass);
        }
    }

    protected static int applyFont(String swingComponentClassName, Component component) {
        String simpleName = swingComponentClassName.replace("javax.swing.J", "");
        Set<String> keySet = App.fontMap.keySet();
        Predicate<String> fontProperty = k -> k.startsWith(simpleName) && k.endsWith(".font");
        Stream<String> fontPropertyResult = keySet.stream().filter(fontProperty);
        Optional<String> optional = fontPropertyResult.findFirst();
        if (optional.isPresent()) {
            String key = optional.get();
            Font font = App.fontMap.get(key);
            SwingUtilities.invokeLater(() -> component.setFont(font));
            return 1;
        }
        return 0;
    }

    public static int updateFont(Component origin) {
        int totalUpdated = 0; // valor inicial
        if (origin instanceof JMenuItem item) {
            for (MenuElement element : item.getSubElements()) {
                totalUpdated += updateFont((Component) element); // acumulo el resultado de la recursión
            }
        } else if (origin instanceof Container container) {
            for (Component component : container.getComponents()) {
                totalUpdated += updateFont(component); // acumulo el resultado de la recursión
            }
        }
        String className = swingComponentClassName(origin.getClass());
        if (className != null) {
            totalUpdated += applyFont(className, origin); // acumulo el resultado
        }
        return totalUpdated;
    }

    public static int updateExclusiveFonts(LookAndFeel lookAndFeel) {
        Set<String> keySet = App.fontMap.keySet();
        Stream<String> filteredKeys = keySet.stream().filter(k -> k.endsWith("Font"));
        List<String> keys = filteredKeys.toList();
        UIDefaults defaults = lookAndFeel.getDefaults();
        int total = 0;
        for (String key : keys) {
            if (defaults.containsKey(key)) {
                UIManager.put(key, App.fontMap.get(key));
                total++;
            }
        }
        return total;
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
