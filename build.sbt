lazy val commonSettings = Seq(
  organization := "cc.akkaha",
  version := "0.2.0-SNAPSHOT",
  scalaVersion := "2.12.8"
)

lazy val pea = Project("pea", file("."))
  .enablePlugins(PlayScala)
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .dependsOn(
    peaDubbo % "compile->compile;test->test",
  ).aggregate(peaDubbo)

// pea-app dependencies
val gatling = "io.gatling.highcharts" % "gatling-charts-highcharts" % "3.1.2" exclude("io.gatling", "gatling-app")
val gatlingCompiler = "io.gatling" % "gatling-compiler" % "3.1.2"
val curator = "org.apache.curator" % "curator-recipes" % "2.12.0"
val asuraPlay = "cc.akkaha" %% "asura-play" % "0.6.0"

libraryDependencies ++= Seq(gatling, gatlingCompiler, curator, asuraPlay)
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test

// pea-dubbo dependencies, specify javassist and jbossnetty deps because of coursier dep resolve problems
val dubbo = "com.alibaba" % "dubbo" % "2.6.5" excludeAll(ExclusionRule(organization = "org.springframework"), ExclusionRule(organization = "org.javassist"), ExclusionRule(organization = "org.jboss.netty"))
val dubboJavassist = "org.javassist" % "javassist" % "3.21.0-GA"
val dubboJbossNetty = "org.jboss.netty" % "netty" % "3.2.5.Final"
val dubboSpring = "org.springframework" % "spring-context" % "4.3.10.RELEASE" % Test
lazy val peaDubbo = Project("pea-dubbo", file("pea-dubbo"))
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(libraryDependencies ++= Seq(
    gatling, dubbo, curator, dubboJavassist, dubboJbossNetty, dubboSpring
  ))

// release and publish settings
val username = "asura-pro"
val repo = "pea"

import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

lazy val releaseSettings = Seq(
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    //runClean,
    // runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    releaseStepCommand("publishSigned"),
    setNextVersion,
    commitNextVersion,
    releaseStepCommand("sonatypeReleaseAll"),
    //pushChanges
  )
)
lazy val publishSettings = Seq(
  homepage := Some(url(s"https://github.com/$username/$repo")),
  licenses += "MIT" -> url(s"https://github.com/$username/$repo/blob/master/LICENSE"),
  scmInfo := Some(ScmInfo(url(s"https://github.com/$username/$repo"), s"git@github.com:$username/$repo.git")),
  apiURL := Some(url(s"https://$username.github.io/$repo/latest/api/")),
  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  developers := List(
    Developer(
      id = username,
      name = "zhengshaodong",
      email = "ngxcorpio@gmail.com",
      url = new URL(s"http://github.com/${username}")
    )
  ),
  useGpg := true,
  usePgpKeyHex("200BB242B4BE47DD"),
  publishMavenStyle := true,
  publishArtifact in Test := false,
  publishTo := Some(if (isSnapshot.value) Opts.resolver.sonatypeSnapshots else Opts.resolver.sonatypeStaging),
  credentials ++= (for {
    username <- sys.env.get("SONATYPE_USERNAME")
    password <- sys.env.get("SONATYPE_PASSWORD")
  } yield Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", username, password)).toSeq,
  // Following 2 lines need to get around https://github.com/sbt/sbt/issues/4275
  publishConfiguration := publishConfiguration.value.withOverwrite(true),
  publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)
)

coverageEnabled := false
