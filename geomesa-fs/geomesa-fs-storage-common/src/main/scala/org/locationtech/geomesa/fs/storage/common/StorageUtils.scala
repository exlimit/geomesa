package org.locationtech.geomesa.fs.storage.common

import org.apache.hadoop.fs.{FileSystem, Path}
import org.locationtech.geomesa.fs.storage.api.PartitionScheme

object StorageUtils {

  def buildPartitionList(root: Path,
                         fs: FileSystem,
                         typeName: String,
                         partitionScheme: PartitionScheme,
                         fileExtension: String): List[String] = {

    def recurse(path: Path, prefix: String, curDepth: Int, maxDepth: Int): List[String] = {
      if (curDepth > maxDepth) return List.empty[String]
      val status = fs.listStatus(path)
      status.flatMap { f =>
        if (f.isDirectory) {
          recurse(f.getPath, s"$prefix${f.getPath.getName}/", curDepth + 1, maxDepth)
        } else if (f.getPath.getName.endsWith(s".$fileExtension")) {
          val name = f.getPath.getName.dropRight(fileExtension.length + 1)
          List(s"$prefix$name")
        } else List()
      }
    }.toList

    recurse(new Path(root, typeName), "", 0, partitionScheme.maxDepth())
  }

}
