package org.apache.spark.examples

import edu.hku.cs.dp.dpread
import org.apache.spark.sql.SparkSession

/**
  * Created by lionon on 11/28/18.
  */
object TpchQuery3 {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName("TpchQuery3")
      .getOrCreate()
    val OverallStartTime = System.nanoTime()
    val count = new dpread(spark.read.textFile(args(0)).rdd)

//    WITH A AS ( SELECT count (∗) FROM T1 ),
//    B AS ( SELECT count (∗) FROM T2 )
//    SELECT count (∗) FROM A JOIN B ON A . count = B. count

    val countresult1 = count.mapDP(line => {
      val part = line.split(',').map(_.toInt)
      (part(0),part(1))
    },args(1).toDouble).mapDPKV(q => q)
     val countresult = countresult1.joinDP[Int](countresult1).original.groupByKey.map(qq => qq._2.size)
    //**************Add noise**********************
    val original = countresult.collect()
    val elapsedTime = (System.nanoTime() - OverallStartTime)
    println("elapsedTime: " + elapsedTime)
//    original.foreach(println )
//    val broadcastValue = spark.sparkContext.broadcast(original.toMap)
//
//    val newPointsSample = countresult.sample.map(p => {
//      var OriginalValue = broadcastValue.value.getOrElse(p._1,p._2)//broadcast orignal result to each sample
//      val diff = math.abs(OriginalValue - p._2)//First need to find the difference
//      (p._1,diff)
//    })
//
//    val newPointsSampleMaxLocalSensitivity = newPointsSample.reduceByKey((a,b) => {
//      math.max(a,b)
//    })
//
//    val localSensitivity = countresult.sample
//    //********End of add noise**********************
//    println("=====================Original : " + original)
//    println("=====================Max local Sensitivity: ")
//    newPointsSampleMaxLocalSensitivity.collect().foreach(n => {
//      print(n._1 + "," + n._2)
//    })
    spark.stop()
  }
}
