name := "bizzy"

version := "1.0"

lazy val `bizzy` = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq( javaJdbc , javaJpa, cache , javaWs )

libraryDependencies += "org.hibernate" % "hibernate-entitymanager" % "4.3.6.Final"

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  