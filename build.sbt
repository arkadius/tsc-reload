import com.banno.license.Licenses._
import com.banno.license.Plugin.LicenseKeys._
import net.virtualvoid.sbt.graph.Plugin._
import sbt.Keys._
import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

val scalaV = "2.11.12"

graphSettings

licenseSettings

organization  := "pl.touk"
name := "tsc-reload"
scalaVersion  := scalaV
scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")
crossPaths := false
autoScalaLibrary := false
license := apache2("Copyright 2015 the original author or authors.")
licenses :=  Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))
homepage := Some(url("https://github.com/touk/tsc-reload"))
removeExistingHeaderBlock := true
resolvers ++= Seq(
  "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository",
  Resolver.jcenterRepo
)

libraryDependencies ++= {
  Seq(
    "com.typesafe"      % "config"            % "1.3.3"       % "optional",
    "io.vavr"           % "vavr"              % "0.9.2",
    "org.slf4j"         % "slf4j-api"         % "1.7.25",
    "org.projectlombok" % "lombok"            % "1.16.22"     % "provided",
    "org.scalatest"    %% "scalatest"         % "3.0.5"       % "test",
    "ch.qos.logback"    % "logback-classic"   % "1.2.3"       % "test",
    "com.iheart"       %% "ficus"             % "1.4.3"       % "test"
  )
}

publishMavenStyle := true
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}
publishArtifact in Test := false
pomExtra in Global := {
  <scm>
    <connection>scm:git:github.com/touk/tsc-reload.git</connection>
    <developerConnection>scm:git:git@github.com:touk/tsc-reload.git</developerConnection>
    <url>github.com/touk/tsc-reload</url>
  </scm>
  <developers>
    <developer>
      <id>ark_adius</id>
      <name>Arek Burdach</name>
      <url>https://github.com/arkadius</url>
    </developer>
  </developers>
}

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  ReleaseStep(action = Command.process("publishSigned", _)),
  setNextVersion,
  commitNextVersion,
  ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
  pushChanges
)
