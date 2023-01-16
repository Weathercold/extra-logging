(ns logging.util.color
  "The only color utility you'll ever need!"
  (:require [clojure.set :as set]
            [clojure.string :as str])
  (:import (java.util.regex Pattern)))

;; region Data

(def ^:const color->escseq
  "Color keyword to terminal escape sequence."
  #::{:flush         "\033[H\033[2J"
      :reset         "\u001B[0m"
      :bold          "\u001B[1m"
      :italic        "\u001B[3m"
      :underline     "\u001B[4m"

      :black         "\u001B[30m"
      :red           "\u001B[31m"
      :green         "\u001B[32m"
      :yellow        "\u001B[33m"
      :blue          "\u001B[34m"
      :purple        "\u001B[35m"
      :cyan          "\u001B[36m"
      :white         "\u001B[37m"

      :light-black   "\u001b[90m"
      :light-red     "\u001B[91m"
      :light-green   "\u001B[92m"
      :light-yellow  "\u001B[93m"
      :light-blue    "\u001B[94m"
      :light-magenta "\u001B[95m" ; Yes it's magenta, not purple, in arc.util.ColorCodes
      :light-cyan    "\u001B[96m"
      :light-white   "\u001b[97m"

      :back-default  "\u001B[49m"
      :back-red      "\u001B[41m"
      :back-green    "\u001B[42m"
      :back-yellow   "\u001B[43m"
      :back-blue     "\u001B[44m"})

(def ^:const color->code
  "Color keyword to color code."
  #::{:flush         "&ff"
      :reset         "&fr"
      :bold          "&fb"
      :italic        "&fi"
      :underline     "&fu"

      :black         "&k"
      :red           "&r"
      :green         "&g"
      :yellow        "&y"
      :blue          "&b"
      :purple        "&p"
      :cyan          "&c"
      :white         "&w"

      :light-black   "&lk"
      :light-red     "&lr"
      :light-green   "&lg"
      :light-yellow  "&ly"
      :light-blue    "&lb"
      :light-magenta "&lm"
      :light-cyan    "&lc"
      :light-white   "&lw"

      :back-default  "&bd"
      :back-red      "&br"
      :back-green    "&bg"
      :back-yellow   "&by"
      :back-blue     "&bb"})

(def tag->color
  "Color tag to color keyword."
  (let [bracketless-map
        {""           ::reset
         "clear"      ::reset ; No corresponding color

         "black"      ::black
         "darkgray"   ::light-black
         "darkgrey"   ::light-black
         "gray"       ::light-black
         "grey"       ::light-black
         "lightgray"  ::light-white
         "lightgrey"  ::light-white
         "white"      ::white

         "maroon"     ::red
         "crimson"    ::red
         "scarlet"    ::red
         "negstat"    ::red #_"#e55454"
         "red"        ::red
         "coral"      ::light-red
         "salmon"     ::light-red
         "pink"       ::light-red

         "olive"      ::green
         "green"      ::green
         "teal"       ::green
         "forest"     ::green
         "acid"       ::green
         "lime"       ::light-green

         "brown"      ::yellow
         "brick"      ::yellow
         "goldenrod"  ::yellow
         "tan"        ::yellow
         "orange"     ::yellow
         "gold"       ::yellow
         "accent"     ::yellow #_"#ffd37f"
         "stat"       ::yellow #_"#ffd37f"
         "yellow"     ::yellow
         "highlight"  ::light-yellow

         "blue"       ::blue
         "navy"       ::blue
         "unlaunched" ::blue #_"#8982ed"
         "royal"      ::blue
         "slate"      ::blue
         "sky"        ::light-blue
         "cyan"       ::cyan

         "purple"     ::purple
         "magenta"    ::purple
         "violet"     ::light-magenta}]
    ;; I need to do this because cursive doesn't display color inside brackets
    (zipmap (map #(str "[" % "]") (keys bracketless-map)) (vals bracketless-map))))

(def colors
  "Color keywords as an intermediate."
  (keys color->escseq))
(def escseqs
  "Terminal escape sequences that affect color."
  (vals color->escseq))
(def codes
  "Color codes, i.e. shorthands for use in log messages. Does anyone actually use this..."
  (vals color->code))
(def tags
  "Color tags, i.e. special strings that affect in-game string color."
  (keys tag->color))

(let [truncated-tags
      ["" "[]" "" "" ""
       "[black]" "[red]" "[green]" "[yellow]" "[blue]" "[purple]" "[cyan]" "[white]"
       "[gray]" "[rose]" "[lime]" "[highlight]" "[sky]" "[violet]" "[cyan]" "[lightgray]"
       "" "" "" "" ""]]
  (def color->tag
    "Color keyword to color tag."
    (zipmap colors truncated-tags))
  (def escseq->tag
    "Terminal escape sequence to color tag."
    (zipmap escseqs truncated-tags))
  (def code->tag
    "Color code to color tag."
    (zipmap codes truncated-tags)))
(def escseq->color
  "Terminal escape sequence to color keyword."
  (set/map-invert color->escseq))
(def escseq->code
  "Terminal escape sequence to color code."
  (zipmap escseqs codes))
(def code->color
  "Color codesto color keyword."
  (set/map-invert color->code))
(def code->escseq
  "Color codes to terminal escape sequence."
  (set/map-invert escseq->code))
(def tag->escseq
  "Color tag to terminal escape sequence."
  (->> tag->color
       vals
       (map color->escseq)
       (zipmap tags)))
(def tag->code
  "Color tag to color code."
  (->> tag->color
       vals
       (map color->code)
       (zipmap tags)))

(def ^:private re-escseqs
  "Regex that matches terminal color escape sequences."
  (->> escseqs
       ;; https://stackoverflow.com/questions/35199808/clojure-unable-to-find-static-field
       (map #(Pattern/quote %))
       (str/join "|")
       re-pattern))
(def ^:private re-codes
  "Regex that matches color codes."
  (re-pattern (str/join "|" codes)))
(def ^:private re-tags
  "Regex that matches color tags."
  (->> tags
       (map #(Pattern/quote %))
       (str/join "|")
       (str "\\[#[0-9a-f]{6,8}\\]|") ; Match hex color tags as well
       re-pattern))

;; endregion
;; region Functions

(defn str-escseq->code
  "Replace terminal escape sequences with color codes."
  [^String text]
  (str/replace text re-escseqs escseq->code))
(defn str-escseq->tag
  "Replace terminal escape sequences with color tags."
  [^String text]
  (str/replace text re-escseqs escseq->tag))
(defn str-code->escseq
  "Replace color codes with terminal escape sequences."
  [^String text]
  (str/replace text re-codes code->escseq))
(defn str-code->tag
  "Replace color codes with color tags."
  [^String text]
  (str/replace text re-codes code->tag))
(defn str-tag->escseq
  "Replace color tags with terminal escape sequences."
  [^String text]
  (str/replace text re-tags #(tag->escseq % "")))
(defn str-tag->code
  "Replace color tags with color codes."
  [^String text]
  (str/replace text re-tags #(tag->code % "")))

(defn remove-escseqs
  "Remove color escape sequences."
  [^String text]
  (str/replace text re-escseqs ""))
(defn remove-codes
  "Remove color codes."
  [^String text]
  (str/replace text re-codes ""))
(defn remove-tags
  "Remove color tags."
  [^String text]
  (str/replace text re-tags ""))
(defn remove-colors
  "Remove all colors."
  [^String text]
  (-> text
      remove-escseqs
      remove-codes
      remove-tags))

;; endregion