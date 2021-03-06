import Dependencies._

name := "geotrellis-raster"
libraryDependencies ++= Seq(
  typesafeConfig,
  jts,
  spire,
  monocleCore,
  monocleMacro,
  openCSV)
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
scalacOptions ++= Seq("-optimize", "-language:experimental.macros")
sourceGenerators in Compile <+= (sourceManaged in Compile).map(Boilerplate.genRaster)

initialCommands in console :=
  """
  import geotrellis.raster._
  import geotrellis.raster.resample._
  import geotrellis.vector._
  """
