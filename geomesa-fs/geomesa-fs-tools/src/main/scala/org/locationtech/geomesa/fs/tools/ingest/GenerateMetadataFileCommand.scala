package org.locationtech.geomesa.fs.tools.ingest

import com.beust.jcommander.Parameters
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.locationtech.geomesa.fs.tools.PathParam
import org.locationtech.geomesa.tools.{Command, RequiredTypeNameParam}

import scala.collection.mutable

/**
  * Created by ahulbert on 7/3/17.
  */
class GenerateMetadataFileCommand extends Command {
  override def params: GenMetaCommands = new GenMetaCommands

  override val name: String = "gen-metadata"

  override def execute(): Unit = {
    val featureRoot = new Path(new Path(params.path), params.featureName)
    val fs = featureRoot.getFileSystem(new Configuration)
    val files = fs.listFiles(featureRoot, true)

    val parentPath = featureRoot.toString
    val res = mutable.ListBuffer.empty[String]
    while (files.hasNext) {
      val f = files.next()
      val path = f.getPath.toString

      // TODO don't use this
      if (path.endsWith(".parquet")) {
        val a = path.replaceAllLiterally(".parquet", "").replaceAllLiterally(parentPath, "")
        val b = if (a.startsWith("/")) {
          a.drop(1)
        } else a
        res += b
      }
    }




  }
}

@Parameters(commandDescription = "Generate the metadata file")
class GenMetaCommands extends PathParam with RequiredTypeNameParam
