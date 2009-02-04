#!/bin/sh

# Builds and deploys into Jetty; primarily for debugging

jetty=$1
if [ -z "$jetty" ]
then
	echo >&2 "usage: $0 jettydir"
	exit 1
fi
if ! [ -f "$jetty/etc/jetty.xml" ]
then
	echo >&2 "error: $jetty is not a Jetty installation"
	exit 1
fi

ctx="$jetty/contexts/gerrit.xml" &&

mvn package &&
war=target/gerrit-*.war &&

cp $war "$jetty/webapps/gerrit.war" &&
if [ -f "$ctx" ]
then
	touch "$ctx"
else
	for f in postgresql-8.3-603.jdbc3.jar c3p0-0.9.1.2.jar
	do
		java -jar $war --cat lib/$f >"$jetty/lib/plus/$f"
	done

	rm -f "$jetty/contexts/test.xml" &&
	java -jar $war --cat extra/jetty_gerrit.xml >"$ctx" &&

	echo >&2 &&
	echo >&2 "You need to edit and configure $ctx"
fi
