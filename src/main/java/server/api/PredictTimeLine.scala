package server.api

import org.apache.spark.mllib.recommendation.{ALS, MatrixFactorizationModel, Rating}
import org.apache.spark.sql._
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{SparkConf, SparkContext}


object PredictTimeLine {

  case class TimeLine(rowKey:String,user: Int, text: String,type1:String,type2:String)
  case class User(userId: Int, gender: String, age: Int, school: String)
  case class AAA(userId: Int, rowKey: String,score:Int)

  var resertMaps:Map[Int, Iterable[String]] = null

  var model:MatrixFactorizationModel = null

  def getModel():MatrixFactorizationModel = {
    //    val spark = SparkSession.builder.master("local[2]").appName("Predict").getOrCreate()
    val localClusterURL = "local[2]"
    val clusterMasterURL = "spark://master:7077"
    val conf = new SparkConf().setAppName("ALS").setMaster(localClusterURL)
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    val hc = new HiveContext(sc)
    //    import spark.implicits._

    //
    val rating = hc.sql("select * from rating")
    val user_info = hc.sql("select * from user_info").rdd.map(x => User(x.getInt(0), x.getString(1), x.getInt(2),x.getString(3)))
    val time_line = hc.sql("select * from time_line").rdd.map(x => TimeLine(x.getString(0), x.getInt(1), x.getString(2),x.getString(3),x.getString(4)))


    val trainingData = rating.rdd.map(x => AAA(x.getInt(0), x.getString(1), x.getInt(2)))
    //因为rating的第二个参数必须是int，这里把rowkey找一个int一一对应
    val record = trainingData.map(_.rowKey).zipWithUniqueId().map(changeToInt _).collect().toMap
    val resertMap = record.groupBy(_._2).mapValues(_.map(_._1))

    //最终训练的数据
    val result = rating.rdd.map(x => Rating(x.getInt(0), record.get(x.getString(1)).get, x.getInt(2)))
    resertMaps = resertMap

    //    result.foreach(println(_))
    // training model
    val recomModel = new ALS().setRank(20).setIterations(5).run(result)
//    recomModel.save(sc, s"hdfs://master:9000/result/")
    model = recomModel
//    sc.stop()
    recomModel
  }

  def changeToInt(data: (String,Long)): (String,Int) = {
    (data._1,data._2.toInt)
  }

  def recommend(uId :Int,sinceId :Int)= {
    val recomResult = model.recommendProducts(uId, sinceId*10)
    println(s"Recommend Movie to User ID 1000")
    println(recomResult.mkString("\n"))
    recomResult.map(_.product)

    val resultRowKey = recomResult.map(x => {
      resertMaps.get(x.product)
    })
    resultRowKey.foreach(println(_))
    resultRowKey.flatMap(_.toList).flatMap(_.toList).drop((sinceId-1)*10)
  }


}