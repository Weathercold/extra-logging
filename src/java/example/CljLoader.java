package example;

import arc.files.*;
import arc.util.*;
import clojure.java.api.*;
import mindustry.mod.*;

/**
 * Initializes Clojure with the correct classloader.
 * @author Weathercold
 */
public class CljLoader extends Mod{
    public static String ns = CljLoader.class.getPackageName() + ".core";

    static{
        Thread.currentThread().setContextClassLoader(CljLoader.class.getClassLoader());
        Clojure.var("clojure.core", "require").invoke(Clojure.read(ns));
    }

    public CljLoader(){
        try{
            Clojure.var(ns, "main").invoke();
        }catch(Throwable ignored){
        }
    }

    @Override
    public void init(){
        try{
            Clojure.var(ns, "init").invoke();
        }catch(Throwable ignored){
        }
    }

    @Override
    public void loadContent(){
        try{
            Clojure.var(ns, "load-content").invoke();
        }catch(Throwable ignored){
        }
    }

    @Override
    public void registerClientCommands(CommandHandler handler){
        try{
            Clojure.var(ns, "register-client-commands").invoke();
        }catch(Throwable ignored){
        }
    }

    @Override
    public void registerServerCommands(CommandHandler handler){
        try{
            Clojure.var(ns, "register-server-commands").invoke();
        }catch(Throwable ignored){
        }
    }

    @Override
    public Fi getConfig(){
        try{
            return (Fi)Clojure.var(ns, "get-config").invoke();
        }catch(IllegalStateException e){
            return super.getConfig();
        }
    }
}