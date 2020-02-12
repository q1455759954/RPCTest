package log

import java.io.ByteArrayInputStream
import java.net.URI

import kafka.serializer.StringDecoder
import org.apache.commons.lang3.time.FastDateFormat
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Milliseconds, StreamingContext}
import org.apache.spark.streaming.kafka.KafkaUtils
import com.alibaba.fastjson.JSON
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.hive.HiveContext
import server.entity.{StatusContent, UserInfo}

/**
  * 消费kafka里的日志信息，处理保存到hive，供分析模型
  */
object LogToHive {

  val service:PictureServiceImpl = new PictureServiceImpl

  def main(args: Array[String]): Unit = {
    val Array(zkQuorum, group, topics, numThreads) = Array("master:2181,slave1:2181,slave2:2181", "1", "test3", "1")
    val dateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss")
    val conf = new SparkConf().setAppName("ScannPlugins").setMaster("local[2]")
    conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    conf.set("spark.executor.memory","512m")
    conf.set("spark.cores.max","1")
    conf.set("spark.driver.cores","1")
    val sc = new SparkContext(conf)
    val ssc = new StreamingContext(sc, Milliseconds(10000))
    sc.setCheckpointDir("hdfs://master:9000/checkpoint")
    val sqlContext = new SQLContext(sc)
    val hc = new HiveContext(sc)


    val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap
    val kafkaParams = Map[String, String](
      "zookeeper.connect" -> zkQuorum,
      "group.id" -> group,
      "auto.offset.reset" -> "smallest"
    )
    val dstream = KafkaUtils.createStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topicMap, StorageLevel.MEMORY_AND_DISK_SER)
    println("***********")

    val lines = dstream.map(_._2)
    lines.print()
    println("***********")

    dstream.foreachRDD(rdd=>{
      var ratingResult:String=""
      var userResult:String=""
      var timeLineResult:String=""

      println("准备解析数据。。。")
      rdd.collect().length
      rdd.collect().foreach(yz => {   //集群可以拿取kafka数据

        println("查看数据" + yz._2)

        val jsonObject = JSON.parseObject(yz._2)
        println(jsonObject.get("user"))
        if (jsonObject.get("score")!=null){
          //Rating类
          val rating:Rating = JSON.parseObject(yz._2,classOf[Rating])
          println(rating)
          ratingResult=ratingResult+rating.getUser+"\t"+rating.getRowKey+"\t"+rating.getScore+"\n"
        }else if (jsonObject.get("school")!=null){
          val userInfo:UserInfo = JSON.parseObject(yz._2,classOf[UserInfo])
          userResult=userResult+userInfo.getId+"\t"+userInfo.getGender+"\t"+userInfo.getAge+"\t"+userInfo.getSchool+"\n"
        }else{
          val timeLine:StatusContent= JSON.parseObject(yz._2,classOf[StatusContent])
//          timeLineResult=timeLineResult+timeLine.getRow_key+"\t"+timeLine.getUserInfo.getId+"\t"+timeLine.getText+"\t"+timeLine.getType+"\n"
        }
      })
      //将result写入hdfs文件
      if (ratingResult!=""){
        println("***********************************")
        println(ratingResult)
        service.createFileByInputStream(new ByteArrayInputStream(ratingResult.getBytes()),"/data/rating")
        hc.sql("load data inpath 'hdfs://master:9000/data/rating' into table rating")
      }
      if (userResult!=""){
        println("***********************************")
        println(userResult)
        service.createFileByInputStream(new ByteArrayInputStream(userResult.getBytes()),"/data/rating")
        hc.sql("load data inpath 'hdfs://master:9000/data/user_info' into table user_info")
      }
      if (timeLineResult!=""){
        println("***********************************")
        println(timeLineResult)
        service.createFileByInputStream(new ByteArrayInputStream(timeLineResult.getBytes()),"/data/rating")
        hc.sql("load data inpath 'hdfs://master:9000/data/time_line' into table time_line")
      }

    })

    ssc.start()
    ssc.awaitTermination()

  }
}
