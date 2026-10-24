package io.github.zpiboo.mpkspeedrun;

import io.github.kurrycat.mpkmod.Main;
import io.github.kurrycat.mpkmod.compatibility.API;
import io.github.kurrycat.mpkmod.events.EventAPI;
import io.github.kurrycat.mpkmod.gui.infovars.InfoString;
import io.github.kurrycat.mpkmod.modules.MPKModule;
import io.github.kurrycat.mpkmod.util.ClassUtil;
import io.github.zpiboo.mpkspeedrun.pkmaps.core.PkMap;
import io.github.zpiboo.mpkspeedrun.pkmaps.core.TriggerZone;
import io.github.zpiboo.mpkspeedrun.pkmaps.gui.screen.PkMapsGUIScreen;
import io.github.zpiboo.mpkspeedrun.util.FileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;

public class MPKSpeedrun implements MPKModule {
    public static final String MODULE_NAME = "mpkspeedrun";
    public static final Logger LOGGER = LogManager.getLogger(MODULE_NAME);
    public static final String MODULE_VERSION = MPKSpeedrun.class.getPackage().getImplementationVersion();

    private static boolean upToDate = true;

    public void init() {
        checkForUpdate();

        addClassesToClassesTxt(new Class[] {
                Speedrunner.class,
                Timer.class,
                PkMap.class,
                TriggerZone.class,
                TriggerZone.TriggerData.class
        });

        FileUtil.registerConfigDir("maps");

        API.registerGUIScreen("maps_gui", new PkMapsGUIScreen());
    }

    public void loaded() {
        EventAPI.addListener(EventAPI.EventListener.onTickEnd(Speedrunner.INSTANCE::onTickEnd));
    }

    private void addClassesToClassesTxt(Class<?>[] classes) {
        try {
            Field classesField = ClassUtil.class.getDeclaredField("classes");
            classesField.setAccessible(true);

            @SuppressWarnings("unchecked")
            final Set<Class<?>> classesTxt = (Set<Class<?>>) classesField.get(null);
            classesTxt.addAll(Arrays.asList(classes));

            Main.infoTree = InfoString.createInfoTree();
        } catch (ReflectiveOperationException e) { e.printStackTrace(); }
    }

    public static boolean isUpToDate() {
        return upToDate;
    }

    private void checkForUpdate() {
        URL url;
        try {
            url = new URI("https://api.github.com/repos/Zpiboo/MPKSpeedrun/releases").toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        String json;
        try (InputStream in = url.openStream()) {
            json = new String(readAll(in), StandardCharsets.UTF_8);
        } catch (IOException ignore) { return; }

        String remoteVersion = new JSONArray(json).getJSONObject(0).getString("tag_name");
        int compareVersions = compareVersions(MODULE_VERSION, remoteVersion);

        if (compareVersions > 0) {
            LOGGER.info("you live in the future!!");
        } else if (compareVersions < 0) {
            upToDate = false;
        }
    }


    private static byte[] readAll(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[8192];
        int n;
        while ((n = in.read(data)) != -1) {
            buffer.write(data, 0, n);
        }
        return buffer.toByteArray();
    }

    private static int compareVersions(String a, String b) {
        int[] va = parseVersion(a);
        int[] vb = parseVersion(b);

        if (va == null || vb == null)
            return 0;

        int compareMajor = Integer.compare(va[0], vb[0]);
        if (compareMajor != 0) return compareMajor;

        int compareMinor = Integer.compare(va[1], vb[1]);
        if (compareMinor != 0) return compareMinor;

        return Integer.compare(va[2], vb[2]);
    }

    private static int[] parseVersion(String v) {
        if (v.startsWith("v")) v = v.substring(1);

        int[] version;

        try {
            version = Arrays.stream(v.split("-")[0].split("\\.")).mapToInt(Integer::parseInt).toArray();
        } catch (NumberFormatException e) {
            LOGGER.error("Failed parsing version: " + v + " - " + e.getMessage(), e);
            return null;
        }

        if (version.length != 3) return null;

        return version;
    }
}