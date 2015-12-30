name := "olapdemo"

version := "1.0"

lazy val `olapdemo` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq( jdbc , cache , ws exclude("xml-apis","xml-apis")   , specs2 % Test )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "pentaho-releases" at "http://repository.pentaho.org/artifactory/repo/"


libraryDependencies += ("org.olap4j" % "olap4j" % "1.2.0")

libraryDependencies += ("org.olap4j" % "olap4j-xmla" % "1.2.0")

libraryDependencies += "mdx4j" % "mdx4j" % "1.0"

libraryDependencies += "pentaho" % "mondrian" % "3.11.1.0-386" exclude("xml-apis","xml-apis")

libraryDependencies += ("hsqldb" % "hsqldb" % "1.8.0.7")

libraryDependencies += "xml-apis" % "xml-apis" % "1.4.01"

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.38"

libraryDependencies += "junit" % "junit" % "4.12" % Test

libraryDependencies += "io.spray" %%  "spray-json" % "1.3.2"

