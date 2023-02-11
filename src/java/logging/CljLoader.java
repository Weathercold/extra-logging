package logging;

import arc.files.Fi;
import arc.util.CommandHandler;
import clojure.java.api.Clojure;
import mindustry.mod.Mod;

/**
 * Initializes Clojure with the correct classloader.
 * @author Weathercold
 */
public class CljLoader extends Mod {
    public static final String ns = CljLoader.class.getPackageName() + ".core";

    static {
        Thread.currentThread().setContextClassLoader(CljLoader.class.getClassLoader());
        Clojure.var("clojure.core", "require").invoke(Clojure.read(ns));
    }

    public CljLoader() {
        try {
            Clojure.var(ns, "-main").invoke();
        } catch (IllegalStateException ignored) {}
    }

    @Override
    public void init() {
        try {
            Clojure.var(ns, "-init").invoke();
        } catch (IllegalStateException ignored) {}
    }

    @Override
    public void loadContent() {
        try {
            Clojure.var(ns, "-load-content").invoke();
        } catch (IllegalStateException ignored) {}
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        try {
            Clojure.var(ns, "-register-client-commands").invoke(handler);
        } catch (IllegalStateException ignored) {}
    }

    @Override
    public void registerServerCommands(CommandHandler handler) {
        try {
            Clojure.var(ns, "-register-server-commands").invoke(handler);
        } catch (IllegalStateException ignored) {}
    }

    @Override
    public Fi getConfig() {
        try {
            return (Fi) Clojure.var(ns, "-get-config").invoke();
        } catch (IllegalStateException e) {
            return super.getConfig();
        }
    }
}
