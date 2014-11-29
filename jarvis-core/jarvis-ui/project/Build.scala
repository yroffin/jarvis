import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "jarvis-ui"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    javaCore,
    javaJdbc,
    javaEbean,
	"org.springframework"    %    "spring-context"         % "3.2.4.RELEASE",
	"org.springframework"    %    "spring-core"            % "3.2.4.RELEASE",
	"org.springframework"    %    "spring-beans"           % "3.2.4.RELEASE",
	"org.yroffin.com.bob"    %    "bob"                    % "0.0.1-SNAPSHOT",
	"org.yroffin.com.bob"    %    "jarvis-aiml"            % "0.0.1-SNAPSHOT",
	"org.yroffin.com.bob"    %    "jarvis-core"            % "0.0.1-SNAPSHOT",
	"org.yroffin.com.bob"    %    "jarvis-gtranslate-libs" % "0.0.1-SNAPSHOT"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
	resolvers += "Default repository" at "http://192.168.0.130:9081/nexus/content/groups/public/"
  )
}
