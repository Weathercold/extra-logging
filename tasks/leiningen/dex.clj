;;; Credits to iarkn

(ns leiningen.dex
  (:require [clojure.java.io :as io]
            [clojure.java.shell :as shell]
            [clojure.set :as set]
            [leiningen.core.classpath :as cp]
            [leiningen.core.main :as main]
            [leiningen.core.project :as project]
            [leiningen.jar :as jar]))

(defn- find-android-jar []
       (if-let [plat-dir
                (if-let [sdk-root (or (System/getenv "ANDROID_SDK_ROOT")
                                      (System/getenv "ANDROID_HOME"))]
                        (->> (.listFiles (io/file sdk-root "platforms"))
                             sort
                             reverse
                             (filter #(.exists (io/file % "android.jar")))
                             first)
                        nil)]
               (str (.getPath plat-dir) "/android.jar")
               (main/abort "Could not find android.jar file, make sure that an Android "
                           "SDK platform is installed.")))

(defn dex
      "Converts Java bytecode to DEX bytecode using d8.
      The ANDROID_SDK_ROOT (or ANDROID_HOME as fallback) environment variable
      must be set and an Android SDK platform must be installed."
      [project]
      (let [scoped-profiles (set (project/pom-scope-profiles project :provided))
            default-profiles (set (project/expand-profile project :default))
            provided-profiles (remove
                                (set/difference default-profiles scoped-profiles)
                                (-> project meta :included-profiles))
            project (->> (into [:uberjar] provided-profiles)
                         (project/merge-profiles project))
            classpath (for [cp (cp/get-classpath project)
                            :when (.exists (io/file cp))]
                           ["--classpath" cp])
            out-name (jar/get-classified-jar-filename project :android)
            standalone-name (jar/get-jar-filename project :standalone)]
           ;; --min-api is 26 because d8 says that MethodHandle.invoke and
           ;; MethodHandle.invokeExact are only supported
           ;; starting with Android 8.
           (apply shell/sh (flatten ["d8" "--min-api" "26" "--lib" (find-android-jar)
                                     classpath standalone-name]))
           (io/copy (io/file standalone-name) (io/file out-name))
           (shell/sh "jar" "-uf" out-name "classes.dex")
           (io/delete-file "classes.dex" true)))