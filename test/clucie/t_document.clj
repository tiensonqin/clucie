(ns clucie.t-document
  (:require [midje.sweet :refer :all]
            [clucie.document :as doc])
  (:import [org.apache.lucene.document Document Field Field$Store StringField
                                       TextField]
           [org.apache.lucene.index IndexOptions]))

(facts "field-type"
  (tabular
   (fact ":indexed?"
     (.indexOptions (doc/field-type ?map)) => ?expected)
   ?map                                    ?expected
   {:indexed? true}                        IndexOptions/DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
   {:indexed? false}                       IndexOptions/NONE
   {:indexed? IndexOptions/DOCS_AND_FREQS} IndexOptions/DOCS_AND_FREQS
   {}                                      IndexOptions/NONE)
  (tabular
   (fact ":stored?"
     (.stored (doc/field-type ?map)) => ?expected)
   ?map             ?expected
   {:stored? true}  truthy
   {:stored? false} falsey
   {}               truthy)
  (tabular
   (fact ":tokenized?"
     (.tokenized (doc/field-type ?map)) => ?expected)
   ?map                ?expected
   {:tokenized? true}  truthy
   {:tokenized? false} falsey
   {}                  falsey))

(facts "field"
  (tabular
   (fact "returns org.apache.lucene.document.Field"
     (doc/field ?k ?v) => #(instance? Field %))
   ?k   ?v
   :key "123"
   "key" 123
   :key :123)
  (fact "throws exception"
    (doc/field nil "123") => (throws Exception)))

(facts "text-field"
  (tabular
   (fact "returns org.apache.lucene.document.TextField"
     (doc/text-field ?k ?v ?s) => #(instance? TextField %))
   ?k    ?v    ?s
   :key  "123" true
   "key" ""    false
   :key  "123" Field$Store/NO)
  (tabular
   (fact "throws exception"
     (doc/text-field ?k ?v ?s) => (throws Exception))
   ?k   ?v    ?s
   nil  "123" true
   :key 123   true
   :key "123" "true"))

(facts "string-field"
  (tabular
   (fact "returns org.apache.lucene.document.StringField"
     (doc/string-field ?k ?v ?s) => #(instance? StringField %))
   ?k    ?v    ?s
   :key  "123" true
   "key" ""    false
   :key  "123" Field$Store/NO)
  (tabular
   (fact "throws exception"
     (doc/string-field ?k ?v ?s) => (throws Exception))
   ?k   ?v    ?s
   nil  "123" true
   :key 123   true
   :key "123" "true"))

(facts "document"
  (tabular
   (fact "returns org.apache.lucene.document.Document"
     (doc/document ?m ?ks) => #(instance? Document %))
   ?m ?ks
   {:key "123", :doc "abc"} [:key :doc]
   {:key 123, :doc "abc"} [:key :doc]
   {:key :123, :doc "abc"} [:key :doc]
   {:key "123", :clucie.core/raw-fields [(StringField. "doc" "abc" Field$Store/YES)]} [:key])
  (fact "throws exception"
    (doc/document {:key "123", :clucie.core/raw-fields [{:doc "abc"}]} [:key]) => (throws Exception)))

(fact "document->map"
  (doc/document->map (doc/document {:key "123", :doc "abc"} [:key :doc])) => map?)
