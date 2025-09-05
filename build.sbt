import org.openurp.parent.Settings.*

ThisBuild / organization := "org.openurp.std.fee"
ThisBuild / version := "0.0.28"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/openurp/std-fee"),
    "scm:git@github.com:openurp/std-fee.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id = "chaostone",
    name = "Tihua Duan",
    email = "duantihua@gmail.com",
    url = url("http://github.com/duantihua")
  )
)

ThisBuild / description := "OpenURP Std Fee"
ThisBuild / homepage := Some(url("http://openurp.github.io/std-fee/index.html"))

val apiVer = "0.46.0"
val starterVer = "0.4.0"
val baseVer = "0.4.55"
val openurp_base_api = "org.openurp.base" % "openurp-base-api" % apiVer
val openurp_std_api = "org.openurp.std" % "openurp-std-api" % apiVer
val openurp_stater_web = "org.openurp.starter" % "openurp-starter-web" % starterVer
val openurp_base_tag = "org.openurp.base" % "openurp-base-tag" % baseVer


lazy val webapp = (project in file("."))
  .enablePlugins(WarPlugin, TomcatPlugin)
  .settings(
    name := "openurp-std-fee-webapp",
    common,
    libraryDependencies ++= Seq(openurp_base_api, openurp_std_api),
    libraryDependencies ++= Seq(openurp_stater_web, openurp_base_tag)
  )
