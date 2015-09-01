(ns spekl-package-manager.check
(:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [spekl-package-manager.util :as util]
            [spekl-package-manager.constants :as constants]
            [spekl-package-manager.download :as download]
            [spekl-package-manager.package :as package]
            [clojure.core.reducers :as r]
            [org.satta.glob :as glob]
            [clojure.string :as string]
            [clojure.java.shell :as shell]
            )
)


(defn get-classpath-sep []
  (if (.equals (util/get-my-platform) "windows")
    ";"
    ":"))

(defn configure-classpath [extra]
  (let [cp (*check-configuration* :classpath)]
    (if (= nil cp)
      (str  "."  (get-classpath-sep) extra)
      (string/join (get-classpath-sep) (cons extra (util/expand-glob cp))))))

(defn get-out-dir []
  (if (not (= nil (*check-configuration* :out)))
    (list "-d" (*check-configuration* :out))))

(defn get-classpath [extra]
  (if (= nil (*check-configuration* :out))
    (list  "-classpath" (str "\"" (configure-classpath extra)  "\""))
    (list  "-classpath" (str "\"" (configure-classpath extra) (get-classpath-sep) (*check-configuration* :out) "\""))))


(defn get-extra-args []
  (filter (fn [x] (not (= nil x))) (flatten (list (get-out-dir) (get-classpath ".")))))


(defcheck nullness
  (log/info  "Running Nullness Check...")
  (run "java" "-jar"  "${checker/dist/checker.jar}" (get-extra-args) "-processor" "org.checkerframework.checker.nullness.NullnessChecker" *project-files*)
  )

(defcheck interning
  (log/info  "Running Interning Check...")
  (run "java" "-jar"  "${checker/dist/checker.jar}" (get-extra-args) "-processor" "org.checkerframework.checker.interning.InterningChecker" *project-files*)
  )



(defcheck default (nullness))
