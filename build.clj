(ns build
  (:require
   [clojure.tools.build.api :as b]
   [deps-deploy.deps-deploy :as dd]))

(def lib 'io.github.clojure-quant/telegram)
(def version (format "0.2.%s" (b/git-count-revs nil)))
(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
(def jar-file (format "target/%s-%s.jar" (name lib) version))

(defn clean [_]
  (b/delete {:path "target"}))

(def pom-template
  [[:licenses
    [:license
     [:name "Eclipse Public License"]
     [:url "https://www.eclipse.org/legal/epl-v10.html"]]]
   [:developers
    [:developer
     [:name "clojure-quant"]]]
   [:scm
    [:url "https://github.com/clojure-quant/telegram"]
    [:connection "scm:git:git://github.com/clojure-quant/telegram.git"]
    [:developerConnection "scm:git:ssh://git@github.com/clojure-quant/telegram.git"]]])

(def opts {:class-dir class-dir
           :lib lib
           :version version
           :basis basis
           :pom-data pom-template
           :src-dirs ["src"]})

(defn jar [_]
  (b/write-pom opts)
  (b/copy-dir {:src-dirs ["src" 
                          ;"resources" 
                          ;"target/node_modules"
                          ]
               :target-dir class-dir})
  (b/jar {:class-dir class-dir
          :jar-file jar-file}))

(defn deploy "Deploy the JAR to Clojars." [_]
  (println "Deploying to Clojars.")
  (dd/deploy {:installer :remote
              ;:sign-releases? true
              :pom-file (b/pom-path (select-keys opts [:lib :class-dir]))
              ;:artifact "target/tech.ml.dataset.jar"
              :artifact (b/resolve-path jar-file)}))
