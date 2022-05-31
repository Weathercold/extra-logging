package example;

import arc.files.*;
import arc.util.*;
import clojure.java.api.*;
import clojure.lang.*;
import mindustry.mod.*;

public class CljLoader extends Mod{
    public static String ns = CljLoader.class.getPackageName() + ".core";

    static{
        Thread.currentThread().setContextClassLoader(CljLoader.class.getClassLoader());
        Clojure.var("clojure.core", "require").invoke(Clojure.read(ns));
    }

    public CljLoader(){
        IFn main = Clojure.var(ns, "main");
        if(!(main instanceof Var.Unbound)) main.invoke();
    }

    @Override
    public void init(){
        IFn init = Clojure.var(ns, "init");
        if(!(init instanceof Var.Unbound)) init.invoke();
    }

    @Override
    public void loadContent(){
        IFn loadContent = Clojure.var(ns, "loadContent");
        if(!(loadContent instanceof Var.Unbound)) loadContent.invoke();
    }

    @Override
    public void registerClientCommands(CommandHandler handler){
        IFn registerClientCommands = Clojure.var(ns, "registerClientCommands");
        if(!(registerClientCommands instanceof Var.Unbound)) registerClientCommands.invoke();
    }

    @Override
    public void registerServerCommands(CommandHandler handler){
        IFn registerServerCommands = Clojure.var(ns, "registerServerCommands");
        if(!(registerServerCommands instanceof Var.Unbound)) registerServerCommands.invoke();
    }

    @Override
    public Fi getConfig(){
        IFn getConfig = Clojure.var(ns, "getConfig");
        if(!(getConfig instanceof Var.Unbound)) return (Fi)getConfig.invoke();
        return super.getConfig();
    }
}