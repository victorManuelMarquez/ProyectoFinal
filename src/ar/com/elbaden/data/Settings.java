package ar.com.elbaden.data;

import ar.com.elbaden.gui.modal.ErrorDialog;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class Settings implements PropertyChangeListener {

    public static final String APP_DIR = ".baden";
    public static final String INI_FILE = "app.ini";

    public static final String KEY_URL_CONNECT     = "database.url";
    public static final String KEY_USERNAME_DB     = "database.user";
    public static final String KEY_PASSWORD_DB     = "database.pass";
    public static final String KEY_ASK_FOR_CLOSING = "app.confirm_to_exit";

    private final Properties properties;
    private final PropertyChangeSupport changeSupport;
    private final ConcurrentHashMap<String, String> backup;

    public Settings() {
        changeSupport = new PropertyChangeSupport(this);
        properties = new KeyProperties(changeSupport);
        backup = new ConcurrentHashMap<>();
        addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt == null) throw new IllegalArgumentException("PropertyChangeEvent == null");
        if (evt.getPropertyName() == null) return;
        if (evt.getOldValue() == null) return;
        if (evt.getNewValue() == null) return;
        backup.put(evt.getPropertyName(), evt.getOldValue().toString());
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    public void applyChanges(Window origin, String comments) {
        if (!backup.isEmpty()) {
            try {
                saveIni(comments);
                backup.clear();
            } catch (IOException ioException) {
                ErrorDialog.createAndShow(origin, ioException);
                discardChanges();
            }
        }
    }

    public void discardChanges() {
        removePropertyChangeListener(this);
        for (Map.Entry<String, String> entry : backup.entrySet()) {
            getProperties().setProperty(entry.getKey(), entry.getValue());
        }
        backup.clear();
        addPropertyChangeListener(this);
    }

    static Optional<String> getWorkingDir() {
        String path = System.getProperty("user.home");
        if (path == null) {
            URL location = Settings.class.getProtectionDomain().getCodeSource().getLocation();
            try {
                File sourceFile = new File(location.toURI());
                path = sourceFile.getParentFile().getPath();
            } catch (URISyntaxException e) {
                return Optional.empty();
            }
        }
        return Optional.of(path);
    }

    public static File getAppFolder() throws FileNotFoundException {
        if (getWorkingDir().isPresent()) {
            return new File(getWorkingDir().get(), APP_DIR);
        } else {
            throw new FileNotFoundException(APP_DIR + " == null");
        }
    }

    public boolean loadIni() throws IOException {
        File parentDir = getAppFolder();
        File iniFile = new File(parentDir, INI_FILE);
        try (
                FileInputStream inputStream = new FileInputStream(iniFile);
                InputStreamReader reader = new InputStreamReader(inputStream)
        ) {
            ((KeyProperties) getProperties()).loadStream(reader);
        }
        return true;
    }

    public boolean saveIni(String comments) throws IOException {
        File parentDir = getAppFolder();
        File iniFile = new File(parentDir, INI_FILE);
        if (!iniFile.getParentFile().exists()) {
            boolean ignore = iniFile.getParentFile().mkdir();
        }
        try (
                FileOutputStream outputStream = new FileOutputStream(iniFile);
                OutputStreamWriter writer = new OutputStreamWriter(outputStream)
        ) {
            getProperties().store(writer, comments);
        }
        return true;
    }

    static final class KeyProperties extends Properties {

        private final PropertyChangeSupport changeSupport;

        private KeyProperties(PropertyChangeSupport changeSupport) {
            this.changeSupport = changeSupport;
            super.putAll(getDefaults());
        }

        // setProperty(String k, String v) invoca a esta función
        @Override
        public synchronized Object put(Object key, Object value) {
            if (key == null) throw new IllegalArgumentException("Properties->key == null");
            if (value == null) throw new IllegalArgumentException("Properties->value == null");
            Object previous = super.put(key, value);
            changeSupport.firePropertyChange(key.toString(), previous, value);
            return previous;
        }

        @Override
        public synchronized void load(Reader reader) {}

        @Override
        public synchronized void load(InputStream inStream) {}

        @Override
        public synchronized void loadFromXML(InputStream in) {}

        @Override
        public synchronized Object remove(Object key) {
            return null;
        }

        @Override
        public synchronized void putAll(Map<?, ?> t) {}

        @Override
        public synchronized void replaceAll(BiFunction<? super Object, ? super Object, ?> function) {}

        @Override
        public synchronized Object putIfAbsent(Object key, Object value) {
            return null;
        }

        @Override
        public synchronized boolean remove(Object key, Object value) {
            return false;
        }

        @Override
        public synchronized boolean replace(Object key, Object oldValue, Object newValue) {
            return false;
        }

        @Override
        public synchronized Object replace(Object key, Object value) {
            return null;
        }

        @Override
        public synchronized Object computeIfAbsent(Object key, Function<? super Object, ?> mappingFunction) {
            return null;
        }

        @Override
        public synchronized Object computeIfPresent(Object key, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
            return null;
        }

        @Override
        public synchronized Object compute(Object key, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
            return null;
        }

        @Override
        public synchronized Object merge(Object key, Object value, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
            return null;
        }

        void loadStream(Reader reader) throws IOException {
            super.load(reader);
        }

        public Map<String, String> getDefaults() {
            return Map.ofEntries(
                    Map.entry(KEY_URL_CONNECT, "jdbc:mysql://localhost:3306"),
                    Map.entry(KEY_USERNAME_DB, "root"),
                    Map.entry(KEY_PASSWORD_DB, ""),
                    Map.entry(KEY_ASK_FOR_CLOSING, Boolean.toString(true))
            );
        }

    }

    public Properties getProperties() {
        return properties;
    }

}
