name := "retrieval-system"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.scalanlp" %% "breeze" % "0.12",
  "com.novocode" % "junit-interface" % "0.8" % "test->default"
)

resolvers += "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
