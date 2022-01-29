Global / onChangedBuildSource := IgnoreSourceChanges // not working well with webpack devserver

name                     := "$name;format="Camel"$"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.8"

val versions = new {
  val outwatch          = "1.0.0-RC5"
  val funPack           = "0.1.4"
  val scalaTest         = "3.2.11"
}

lazy val scalaJsMacrotaskExecutor = Seq(
  // https://github.com/scala-js/scala-js-macrotask-executor
  libraryDependencies       += "org.scala-js" %%% "scala-js-macrotask-executor" % "1.0.0",
  Compile / npmDependencies += "setimmediate"  -> "1.0.5", // polyfill
)

lazy val webapp = project
  .enablePlugins(
    ScalaJSPlugin,
    ScalaJSBundlerPlugin,
  )
  .settings(scalaJsMacrotaskExecutor)
  .settings(
    libraryDependencies          ++= Seq(
      "io.github.outwatch" %%% "outwatch"      % versions.outwatch,
      "io.github.outwatch" %%% "outwatch-util" % versions.outwatch,
      "org.scalatest"      %%% "scalatest"     % versions.scalaTest % Test,
    ),
    Compile / npmDevDependencies ++= Seq(
      "@fun-stack/fun-pack" -> versions.funPack, // sane defaults for webpack development and production, see webpack.config.*.js
      "autoprefixer"        -> "10.2.5",
      "postcss"             -> "8.2.9",
      "postcss-loader"      -> "4.2.0",
      "tailwindcss"         -> "3.0.10",
    ),
    useYarn                       := true, // Makes scalajs-bundler use yarn instead of npm

    scalaJSLinkerConfig ~= (_.withModuleKind(
      ModuleKind.CommonJSModule,
    )), // configure Scala.js to emit a JavaScript module instead of a top-level script
    scalaJSUseMainModuleInitializer   := true, // On Startup, call the main function
    webpackDevServerPort              := 12345,
    webpack / version                 := "4.46.0",
    startWebpackDevServer / version   := "3.11.3",
    webpackDevServerExtraArgs         := Seq("--color"),
    fullOptJS / webpackEmitSourceMaps := true,
    fastOptJS / webpackBundlingMode   := BundlingMode
      .LibraryOnly(), // https://scalacenter.github.io/scalajs-bundler/cookbook.html#performance
    fastOptJS / webpackConfigFile := Some(baseDirectory.value / "webpack.config.dev.js"),
    fullOptJS / webpackConfigFile := Some(baseDirectory.value / "webpack.config.prod.js"),
    Test / requireJsDomEnv        := true,
  )

addCommandAlias("prod", "fullOptJS/webpack")
addCommandAlias("dev", "devInit; devWatchAll; devDestroy")
addCommandAlias("devInit", "; webapp/fastOptJS/startWebpackDevServer")
addCommandAlias("devWatchAll", "~; webapp/fastOptJS/webpack")
addCommandAlias("devDestroy", "webapp/fastOptJS/stopWebpackDevServer")
