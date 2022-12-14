import com.github.plokhotnyuk.jsoniter_scala.core.{JsonValueCodec, readFromString}
import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker
import org.apache.spark.sql.functions.{col, expr}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.{DataStreamWriter, GroupState, GroupStateTimeout, Trigger}
import org.apache.spark.sql.types.{StringType, StructField, StructType, TimestampType}

import scala.concurrent.duration.DurationInt


object StateFullPipeline extends App {

  val spark = SparkSession
    .builder()
    .appName("Integrating Kafka")
    .master("local")
    .getOrCreate()

  val schema = StructType(
    Array(
      StructField("timestamp", StringType),
      StructField("page", StringType)
    )
  )

  def readFromKafka(): DataFrame =
    spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "input-click")
      .load()

  def transform(in: DataFrame): DataFrame =
    in
      .select(expr("cast(value as string) as actualValue"))
      .select(from_json(col("actualValue"), schema).as("page")) // composite column (struct)
      .selectExpr("page.timestamp as timestamp", "page.page as page")
      .select(
        date_format(to_timestamp(col("timestamp"), "dd-MM-yyyy HH:mm:ss:SSS"), "HH:mm:ss:SSS")
          .as("time"),
        col("page")
      )

  case class ClickRecord(timestamp: String, page: String, userId: Long, duration: Int)
  //  case class ClickRecord(timestamp: String, custom_atr: Map[String, (String, String)], userId: Long, duration: Int)
  //                                                     attr_nam, attr_type, attr_value
  //  {timestamp: "2202-09-11", custom_atr: "help", userId: 213123123, duration: Int}
  //  {timestamp: "2202-09-11", custom_atr: true, userId: 213123123, duration: Int}
  //  {timestamp: "2202-09-11", custom_atr: {"sada": 09809, "asds": 09809}, userId: 213123123, duration: Int}



  case class ClickBulk(pageType: String, count: Int, totalDuration: Int)

  case class ClickAverage(page: String, averageDuration: Double)

  import spark.implicits._

  implicit val codec: JsonValueCodec[ClickRecord] = JsonCodecMaker.make[ClickRecord]

  def transformToCaseClass(in: DataFrame) =
    in
      .select(expr("cast(value as string) as actualValue"))
      .as[String]
      .map(readFromString[ClickRecord](_))

  def updateCountState( // ???????????????????? ???????? ?????? ?????? ???????????? ???????????? ?? ????????????????????
                        postKey: String,
                        group: Iterator[ClickRecord],
                        state: GroupState[ClickBulk] // ???? ???? null IllegalArgumentException
                      ): ClickAverage = { // ???? ???????????? ???????????? ???????? ????????????
    val prevState = if (state.exists) state.get else ClickBulk(postKey, 0, 0)

    val totalAggData = group.foldLeft((0, 0)) {
      case (acc, rec) =>
        val (count, duration) = acc
        (count + 1, duration + rec.duration)
    }

    val (totalCount, totalDuration) = totalAggData
    val newPostBulk =
      ClickBulk(postKey, prevState.count + totalCount, prevState.totalDuration + totalDuration)

    state.update(newPostBulk) // ????????????????????????????????
    ClickAverage(postKey, newPostBulk.totalDuration * 1.0 / newPostBulk.count)
  }

  val frame = readFromKafka()



  println(frame.isStreaming)

  transformToCaseClass(frame)
  .writeStream.foreachBatch{(b: Dataset[ClickRecord], _: Long) =>
    b.show(10, false)
  }
    .trigger(Trigger.ProcessingTime(1.seconds))
    .start()
    .awaitTermination()

/*

    val transformed = transformToCaseClass(frame)

    val averageDS: Dataset[ClickAverage] = transformed
      .groupByKey(_.page)
      .mapGroupsWithState(GroupStateTimeout.NoTimeout())(updateCountState)

    averageDS
      .writeStream
      .outputMode("update")
      .foreachBatch { (batch: Dataset[ClickAverage], _: Long) =>
        batch.show()
      }
      .trigger(Trigger.ProcessingTime(2.seconds))
      .start()
      .awaitTermination()

*/


}
