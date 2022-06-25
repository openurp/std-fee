import org.openurp.parent.Settings._
import org.openurp.parent.Dependencies._

ThisBuild / organization := "org.openurp.std.fee"
ThisBuild / version := "0.0.21"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/openurp/std-fee"),
    "scm:git@github.com:openurp/std-fee.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id    = "chaostone",
    name  = "Tihua Duan",
    email = "duantihua@gmail.com",
    url   = url("http://github.com/duantihua")
  )
)

ThisBuild / description := "OpenURP Std Fee"
ThisBuild / homepage := Some(url("http://openurp.github.io/std-fee/index.html"))

val apiVer = "0.26.0"
val starterVer = "0.0.21"
val baseVer = "0.1.30"
val openurp_base_api = "org.openurp.base" % "openurp-base-api" % apiVer
val openurp_std_api = "org.openurp.std" % "openurp-std-api" % apiVer
val openurp_stater_web = "org.openurp.starter" % "openurp-starter-web" % starterVer
val openurp_base_tag = "org.openurp.base" % "openurp-base-tag" % baseVer

lazy val root = (project in file("."))
  .settings()
  .aggregate(core,web,webapp)

lazy val core = (project in file("core"))
  .settings(
    name := "openurp-std-fee-core",
    common,
    libraryDependencies ++= Seq(openurp_base_api,openurp_std_api,beangle_ems_app,gson)
  )

lazy val web = (project in file("web"))
  .settings(
    name := "openurp-std-fee-web",
    common,
    libraryDependencies ++= Seq(openurp_stater_web,openurp_base_tag)
  ).dependsOn(core)

lazy val webapp = (project in file("webapp"))
  .enablePlugins(WarPlugin,TomcatPlugin)
  .settings(
    name := "openurp-std-fee-webapp",
    common
  ).dependsOn(web)

publish / skip := true
