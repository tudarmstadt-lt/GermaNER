TOMCAT_DIR="/Users/Jonas/Documents/Studium/6. Semester/QA Watson/Project/apache-tomcat-7.0.54"
WEB_URL="http://localhost:8080/de.tu-darmstadt.lt.lqa/"
START_DELAY="5s"

if [ ! -f "${TOMCAT_DIR}/bin/startup.sh" ];
then
	echo "Please specify your TOMCAT installation path within line 1 of this file."
	exit 2
fi

sh "${TOMCAT_DIR}/bin/shutdown.sh">/dev/null

BASEDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

cd "${BASEDIR}"
mvn3 clean install>/dev/null

cd "target"
cp *.war "${TOMCAT_DIR}/webapps"

chmod +x "${TOMCAT_DIR}/bin/"*
sh "${TOMCAT_DIR}/bin/startup.sh">/dev/null

echo "Starting in ${START_DELAY}"
sleep $START_DELAY

open "${WEB_URL}"

echo "Done!"