package ar.com.elbaden.utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class FontLoader {

    public static final String FONTS_FOLDER = "fonts";

    public void registerFontsOf(Font[] fontFamily) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (Font font : fontFamily) {
            ge.registerFont(font);
        }
    }

    public Font[] createFontFamilyFrom(List<InputStream> sourceList, int fontSize) throws IOException, FontFormatException {
        List<Font> fontList = new ArrayList<>(sourceList.size());
        for (InputStream inputStream : sourceList) {
            try (InputStream fontStream = inputStream) {
                Font newFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
                String boldRegex = "(?i).*Bold.*";
                String italicRegex = boldRegex.replace("Bold", "Italic");
                if (newFont.getFontName().matches(boldRegex) && newFont.getFontName().matches(italicRegex)) {
                    newFont = newFont.deriveFont(Font.BOLD | Font.ITALIC, (float) fontSize);
                } else if (newFont.getFontName().matches(boldRegex)) {
                    newFont = newFont.deriveFont(Font.BOLD, (float) fontSize);
                } else if (newFont.getFontName().matches(italicRegex)) {
                    newFont = newFont.deriveFont(Font.ITALIC, (float) fontSize);
                }
                fontList.add(newFont);
            }
        }
        return fontList.toArray(Font[]::new);
    }

    public List<InputStream> getStreamsOf(String family) throws URISyntaxException, IOException {
        File fontsDir = new File(FONTS_FOLDER, family);
        ClassLoader classLoader = FontLoader.class.getClassLoader();
        URL resourceUrl = classLoader.getResource(fontsDir.getPath());
        List<InputStream> fontFamilyStream = Collections.emptyList();
        if (resourceUrl == null) {
            throw new IOException(fontsDir.getPath());
        }
        if (resourceUrl.getProtocol().equals("file")) {
            URI resourceUri = resourceUrl.toURI();
            Path resourcePath = Path.of(resourceUri);
            try (Stream<Path> pathStream = Files.walk(resourcePath, 1)) {
                Stream<Path> onlyFiles = pathStream.filter(Files::isRegularFile);
                Stream<Path> trueTypeFiles;
                trueTypeFiles = onlyFiles.filter(path -> path.getFileName().toString().endsWith(".ttf"));
                fontFamilyStream = trueTypeFiles.map(path -> {
                    try {
                        return Files.newInputStream(path);
                    } catch (IOException e) {
                        return null;
                    }
                }).filter(Objects::nonNull).toList();
            }
        }
        return fontFamilyStream;
    }

}
