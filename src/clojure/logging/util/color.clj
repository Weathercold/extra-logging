(ns logging.util.color
  "The only color utility you'll ever need!"
  (:require [clojure.set :as set]
            [clojure.string :as str])
  (:import (java.util.regex Pattern)))

;; TODO: Add true color support
;; https://stackoverflow.com/a/4842446

;; region Data

(def ^:const color->escseq
  "Color keyword to terminal escape sequence."
  {::flush         "\033[H\033[2J"
   ::reset         "\033[0m"
   ::bold          "\033[1m"
   ::italic        "\033[3m"
   ::underline     "\033[4m"

   ::black         "\033[30m"
   ::red           "\033[31m"
   ::green         "\033[32m"
   ::yellow        "\033[33m"
   ::blue          "\033[34m"
   ::purple        "\033[35m"
   ::cyan          "\033[36m"
   ::white         "\033[37m"

   ::light-black   "\033[90m"
   ::light-red     "\033[91m"
   ::light-green   "\033[92m"
   ::light-yellow  "\033[93m"
   ::light-blue    "\033[94m"
   ::light-magenta "\033[95m" ; Yes it's magenta, not purple, in arc.util.ColorCodes
   ::light-cyan    "\033[96m"
   ::light-white   "\033[97m"

   ::back-default  "\033[49m"
   ::back-red      "\033[41m"
   ::back-green    "\033[42m"
   ::back-yellow   "\033[43m"
   ::back-blue     "\033[44m"})

(def ^:const color->code
  "Color keyword to color code."
  {::flush         "&ff"
   ::reset         "&fr"
   ::bold          "&fb"
   ::italic        "&fi"
   ::underline     "&fu"

   ::black         "&k"
   ::red           "&r"
   ::green         "&g"
   ::yellow        "&y"
   ::blue          "&b"
   ::purple        "&p"
   ::cyan          "&c"
   ::white         "&w"

   ::light-black   "&lk"
   ::light-red     "&lr"
   ::light-green   "&lg"
   ::light-yellow  "&ly"
   ::light-blue    "&lb"
   ::light-magenta "&lm"
   ::light-cyan    "&lc"
   ::light-white   "&lw"

   ::back-default  "&bd"
   ::back-red      "&br"
   ::back-green    "&bg"
   ::back-yellow   "&by"
   ::back-blue     "&bb"})

(def ^:const color->tag
  "Color keyword to color tag."
  {::flush         ""
   ::reset         "[]"
   ::bold          ""
   ::italic        ""
   ::underline     ""

   ::black         "[black]"
   ::red           "[red]"
   ::green         "[green]"
   ::yellow        "[yellow]"
   ::blue          "[blue]"
   ::purple        "[purple]"
   ::cyan          "[cyan]"
   ::white         "[white]"

   ::light-black   "[gray]"
   ::light-red     "[rose]"
   ::light-green   "[lime]"
   ::light-yellow  "[highlight]"
   ::light-blue    "[sky]"
   ::light-magenta "[violet]"
   ::light-cyan    "[cyan]"
   ::light-white   "[lightgray]"

   ::back-default  "[]"
   ::back-red      ""
   ::back-green    ""
   ::back-yellow   ""
   ::back-blue     ""})

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

(let [truncated-tags (vals color->tag)]
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
