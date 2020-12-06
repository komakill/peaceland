package alerts

import java.io.FileReader

import org.apache.spark.sql._
import models._
import org.apache.spark._
import org.apache.spark.sql.types._
import com.google.gson.Gson

object Main {

	val spark: SparkSession = SparkSession
		.builder()
		.config("spark.master", "local")
		.getOrCreate()

	def main(args: Array[String]): Unit = {

		val cfg: Config = new Config(
			sys.env("kafkaHost"),
			sys.env("kafkaPort"),
			null,
			null,
			sys.env("kafkaAlertTopic"),
			null
		)
		startup(cfg)
	}

	def startup(cfg: Config): Unit = {

		val kafkaStreamDF = spark.readStream
			.format("kafka")
			.option(
				"kafka.bootstrap.servers",
				String.format("%s:%s", cfg.kafkaHost, cfg.kafkaPort)
			)
			.option("subscribe", cfg.kafkaAlertTopic)
			.option("startingOffsets", "latest")
			.load()
			.selectExpr("CAST(value AS STRING)")

		val gson = new Gson

		val query = kafkaStreamDF.writeStream
			.foreachBatch { 
				(data: DataFrame, batchId: Long) =>
				val events = data.collect().map(row => row.getString(0)).map(x => gson.fromJson(x, classOf[Event]))
				val df = spark.createDataFrame(events)
				df.show()
			}
			.start()
		query.awaitTermination()

	}

}
