package io.vitamin

import java.io.File
import java.nio.file.{Files, StandardCopyOption}
import java.util.regex.Pattern

import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

/**
  * Vitamin Software Consulting Ltd
  */
case class Config(
                   jarDirectory: File = new File("."),
                   group: String = "",
                   destination: File = new File(".")
                 )

object MavenCopier {

  val logger = LoggerFactory.getLogger(getClass)

  lazy val pattern = Pattern.compile("(.*?)-(\\d+.*)")

  def getListOfFiles(dir: File, extensions: Seq[String]): Seq[File] = {
    dir.listFiles
      .filter(_.isFile)
      .filter(file => extensions.exists(file.getName.endsWith(_)))
      .toSeq
  }

  def toNameAndVersion(artifactJarName:String):Seq[String] = {
      val matcher = pattern.matcher(artifactJarName)
      var lb = ListBuffer[String]()
      while(matcher.find()){
         lb += matcher.group(1)
         lb += matcher.group(2)
      }
      lb.toList
  }

  def mapToArtifactDir(artifactJar: String, group: String, dest: File): File = {
    val fileNameAndVersion = toNameAndVersion(artifactJar)
    val artifact = fileNameAndVersion.head
    val version = fileNameAndVersion(1).split('.').init mkString "."
    val groupDir = group.split('.') mkString File.separator

    val finalDestination = Array(dest.getAbsolutePath, groupDir, artifact, version).mkString(File.separator)
    new File(finalDestination)
  }

  val parser = new scopt.OptionParser[Config]("mavenCopier") {
    head("mavenCopier", "0.0.1")

    opt[File]('j', "jarDir").required().valueName("<file>").
      action((dir, c) => c.copy(jarDirectory = dir)).
      text("jarDir is a required file property")

    opt[File]('d', "destinationDir").required().valueName("<file>").
      action((dir, c) => c.copy(destination = dir)).
      text("destinationDir is a required file property")

    opt[String]('g', "group").required().
      action((g, c) => c.copy(group = g)).
      text("artifact's group should be provided")

  }

  def moveTo(file:File, dir:File) : Unit = {
     logger.info(s"Moving file:$file to the directory:$dir")
     val destPath = new File(dir.getAbsolutePath + File.separator + file.getName).toPath
     Files.copy(file.toPath, destPath, StandardCopyOption.ATOMIC_MOVE)
  }

  def execute(config:Config):Unit = {
      val files = getListOfFiles(config.jarDirectory, Array("jar"))

    val artifactToDirMap = files.map(file => file -> mapToArtifactDir(file.getName,
        config.group, config.destination)).toMap

    artifactToDirMap.values.map(_.mkdirs)
    artifactToDirMap.foreach(info => moveTo(info._1, info._2))
  }

  def main(args: Array[String]): Unit = {

    val params = parser.parse(args, Config())
    params.foreach(execute)
  }
}
