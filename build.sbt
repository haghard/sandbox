import sbt._

val scala2Version = "2.13.14"
val scala3Version = "3.4.2"

val AmmoniteVersion = "3.0.0-M2-9-88291dd8"

val scalac3_Options = Seq(
  "-deprecation",
  "-feature",
  "-language:implicitConversions",
  "-unchecked",
  "-Ykind-projector",
  "-Ysafe-init",
  "-Xfatal-warnings",
  "-rewrite",
  "-source",
  "future-migration"
)

/*val scala2_13_Options2 = Seq(
  // Feature options
  "-encoding", "utf-8",
  "-explaintypes",
  "-feature",
  "-language:existentials",
  "-language:experimental.macros",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-Ymacro-annotations",

  // Warnings as errors!
  "-Xfatal-warnings",

  // Linting options
  "-unchecked",
  "-Xcheckinit",
  "-Xlint:adapted-args",
  "-Xlint:constant",
  "-Xlint:delayedinit-select",
  "-Xlint:deprecation",
  "-Xlint:doc-detached",
  "-Xlint:inaccessible",
  "-Xlint:infer-any",
  "-Xlint:missing-interpolator",
  //"-Xlint:nullary-override",
  "-Xlint:nullary-unit",
  "-Xlint:option-implicit",
  "-Xlint:package-object-classes",
  "-Xlint:poly-implicit-overload",
  "-Xlint:private-shadow",
  "-Xlint:stars-align",
  "-Xlint:type-parameter-shadow",
  "-Wdead-code",
  "-Wextra-implicit",
  "-Wnumeric-widen",
  "-Wunused:implicits",
  "-Wunused:imports",
  "-Wunused:locals",
  "-Wunused:params",
  "-Wunused:patvars",
  "-Wunused:privates",
  "-Wvalue-discard",
)*/

val scala2_13_Options = Seq(
  //"-Xsource:3",

  //When migrating from Scala 2.13 to 3, the 2.13.13 release introduces the `-Xsource:3-cross` flag for users who want to
  // cross build their code on both Scala 2.13 and 3, allowing to adopt some Scala 3 semantics and skip warnings which
  // don't bring value in cross-built projects
  "-Xsource:3-cross",

  "-target:jvm-17",
  "-explaintypes", // Explain type errors in more detail.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
  "-language:experimental.macros", // Allow macro definition (besides implementation and application)
  "-language:higherKinds", // Allow higher-kinded types
  "-language:implicitConversions", // Allow definition of implicit functions called views
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.

  //"-Xfatal-warnings", // Fail the compilation if there are any warnings.

  /*
  "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Xlint:constant", // Evaluation of a constant arithmetic expression results in an error.
  "-Xlint:delayedinit-select", // Selecting member of DelayedInit.
  "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
  "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`.
  "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
  "-Xlint:option-implicit", // Option.apply used implicit view.
  "-Xlint:package-object-classes", // Class or object defined in package object.
  "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.

  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.

  "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
  "-Ywarn-unused:imports", // Warn if an import selector is not referenced.
  "-Ywarn-unused:locals", // Warn if a local definition is unused.
  "-Ywarn-unused:params", // Warn if a value parameter is unused.
  "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
  "-Ywarn-unused:privates", // Warn if a private member is unused.
  "-Ycache-plugin-class-loader:last-modified", // Enables caching of classloaders for compiler plugins
  "-Ycache-macro-class-loader:last-modified", // and macro definitions. This can lead to performance improvements.
  */

  //https://alexn.org/blog/2020/05/26/scala-fatal-warnings/
  /*
    You still want to keep the exhaustiveness checks as errors.
    What this does is to turn all warnings into errors, except for deprecation messages, which are still left as warnings.
    To break it down:
      cat=deprecation refers to deprecation messages (classes/methods marked with @deprecated being called)
      ws says that for these warnings a “warning summary” should be shown
      any:e says that for any other kind of warning, signal it via an error
  */
  "-Wconf:cat=deprecation:ws,any:e",
  //OR
  //"-Wconf:cat=other-match-analysis:error", //Make only some warnings fatal: Transform exhaustivity warnings into errors.
)

lazy val root = project
  .in(file("."))
  .settings(
    name := "sandbox",
    version := "0.1.0",

    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-collection-contrib" % "0.3.0",
      "dev.zio"   %% "izumi-reflect" % "2.3.8",
      "com.lihaoyi" % "ammonite" % AmmoniteVersion % "test" cross CrossVersion.full
    ),
    
    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test,

    Compile / scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 13)) =>
          //scala2_13_Options2
          scala2_13_Options
        case _ =>
          scalac3_Options
      }
    },

    // To make the default compiler and REPL use Dotty
    //scalaVersion := scala3Version,
    scalaVersion := scala2Version,

    // To cross compile with Scala 3 and Scala 2
    crossScalaVersions := Seq(scala3Version, scala2Version)
  )

scalafmtOnCompile := true

addCommandAlias("c", "compile")
addCommandAlias("r", "reload")


//for ammonite
//run / fork := true

// ammonite repl
Test / sourceGenerators += Def.task {
  val file = (Test / sourceManaged).value / "amm.scala"
  IO.write(file, """object amm extends App { ammonite.Main().run() }""")
  Seq(file)
}.taskValue


//++3.4.2
//++2.13.14
//show scalacOptions

