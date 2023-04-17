## sbt project cross-compiled with Scala 3 and Scala 2

### Usage

This is a normal sbt project, you can compile code with `sbt compile` and run it
with `sbt run`, `sbt console` will start a Dotty REPL. For more information on
cross-compilation in sbt, see <https://www.scala-sbt.org/1.x/docs/Cross-Build.html>.




```

sbt new scala/scala3.g8
sbt new scala/scala3-cross.g8. //2_3
sbt new scala/scala-seed.g8

```
https://yobriefca.se/g8ling/

https://www.scala-sbt.org/1.x/docs/sbt-new-and-Templates.html
https://github.com/foundweekends/giter8/wiki/giter8-templates