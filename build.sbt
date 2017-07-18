name := "graph_visualization"

version := "1.0"

scalaVersion := "2.10.4"

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

resolvers += Resolver.mavenLocal

// resolvers += "TalkingData Maven Repository" at "https://maven.tenddata.com/nexus/content/groups/public/"

resolvers += "Aliyun Repository" at "http://maven.aliyun.com/nexus/content/groups/public/"

resolvers += "Maven Central Repository" at "http://repo1.maven.org/maven2/"

//resolvers += "Cloudera Maven Repository" at "https://repository.cloudera.com/artifactory/cloudera-repos/"

resolvers += "Spark Packages Repo" at "http://dl.bintray.com/spark-packages/maven"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.6.1" withSources(),
  "org.apache.spark" %% "spark-sql" % "1.6.1" withSources(),
  "org.apache.spark" %% "spark-graphx" % "1.6.1" withSources(),
  "graphframes" % "graphframes" % "0.5.0-spark1.6-s_2.10"
)
