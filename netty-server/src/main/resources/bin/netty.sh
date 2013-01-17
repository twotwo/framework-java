#!/bin/sh

# -----------------------------------------------------------------------------
# Start/Stop Script for the NETTY Server
#
# Environment Variable Prequisites
#
#   NETTY_HOME   May point at your netty "build" directory.
#
#   NETTY_BASE   (Optional) Base directory for resolving dynamic portions
#                   of a netty installation.  If not present, resolves to
#                   the same directory that NETTY_HOME points to.
#
#   NETTY_OPTS   (Optional) Java runtime options used when the "start",
#                   or "run" command is executed.
#
#   NETTY_TMPDIR (Optional) Directory path location of temporary directory
#                   the JVM should use (java.io.tmpdir).  Defaults to
#                   $NETTY_BASE/temp.
#
#
#   JAVA_OPTS       (Optional) Java runtime options used when the "start",
#                   "stop", or "run" command is executed.
#
#   JPDA_TRANSPORT  (Optional) JPDA transport used when the "jpda start"
#                   command is executed. The default is "dt_socket".
#
#   JPDA_ADDRESS    (Optional) Java runtime options used when the "jpda start"
#                   command is executed. The default is 8000.
#
#   JPDA_SUSPEND    (Optional) Java runtime options used when the "jpda start"
#                   command is executed. Specifies whether JVM should suspend
#                   execution immediately after startup. Default is "n".
#
#   JPDA_OPTS       (Optional) Java runtime options used when the "jpda start"
#                   command is executed. If used, JPDA_TRANSPORT, JPDA_ADDRESS,
#                   and JPDA_SUSPEND are ignored. Thus, all required jpda
#                   options MUST be specified. The default is:
#
#                   -Xdebug -Xrunjdwp:transport=$JPDA_TRANSPORT,
#                       address=$JPDA_ADDRESS,server=y,suspend=$JPDA_SUSPEND
#
#
#   NETTY_PID    (Optional) Path of the file which should contains the pid
#                   of netty startup java process, when start (fork) is used
#
# $Id: netty.sh 600664 2007-12-03 20:24:19Z jim $
# -----------------------------------------------------------------------------

# Business Server Port
export PORT_BUSINESS=8080

#Server Class for NETTY
_ServerClass=com.li3huo.netty.DemoServer

# OS specific support.  $var _must_ be set to either true or false.
cygwin=false
os400=false
darwin=false
case "`uname`" in
CYGWIN*) cygwin=true;;
OS400*) os400=true;;
Darwin*) darwin=true;;
esac

# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

# Only set NETTY_HOME if not already set
[ -z "$NETTY_HOME" ] && NETTY_HOME=`cd "$PRGDIR/.." ; pwd`


# Make sure prerequisite environment variables are set
if [ -z "$JAVA_HOME" -a -z "$JRE_HOME" ]; then
  # Bugzilla 37284 (reviewed).
  if $darwin; then
    if [ -d "/System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Home" ]; then
      export JAVA_HOME="/System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Home"
    fi
  else
    JAVA_PATH=`which java 2>/dev/null`
    if [ "x$JAVA_PATH" != "x" ]; then
      JAVA_PATH=`dirname $JAVA_PATH 2>/dev/null`
      JRE_HOME=`dirname $JAVA_PATH 2>/dev/null`
    fi
    if [ "x$JRE_HOME" = "x" ]; then
      # XXX: Should we try other locations?
      if [ -x /usr/bin/java ]; then
        JRE_HOME=/usr
      fi
    fi
  fi
  if [ -z "$JAVA_HOME" -a -z "$JRE_HOME" ]; then
    echo "Neither the JAVA_HOME nor the JRE_HOME environment variable is defined"
    echo "At least one of these environment variable is needed to run this program"
    exit 1
  fi
fi
if [ -z "$JAVA_HOME" -a "$1" = "debug" ]; then
  echo "JAVA_HOME should point to a JDK in order to run in debug mode."
  exit 1
fi
if [ -z "$JRE_HOME" ]; then
  JRE_HOME="$JAVA_HOME"
fi

# Set standard commands for invoking Java.
  _RUNJAVA="$JRE_HOME"/bin/java

if [ -z "$NETTY_BASE" ] ; then
  NETTY_BASE="$NETTY_HOME"
fi

NETTY_PID="$NETTY_BASE"/pid

if [ -z "$NETTY_TMPDIR" ] ; then
  # Define the java.io.tmpdir to use for netty
  NETTY_TMPDIR="$NETTY_BASE"/temp
fi

# Bugzilla 37848: When no TTY is available, don't output to console
have_tty=0
if [ "`tty`" != "not a tty" ]; then
    have_tty=1
fi

# ----- Execute The Requested Command -----------------------------------------

# Bugzilla 37848: only output this if we have a TTY
if [ $have_tty -eq 1 ]; then
  echo "Using NETTY_BASE:   $NETTY_BASE"
  echo "Using NETTY_HOME:   $NETTY_HOME"
  echo "Using NETTY_TMPDIR: $NETTY_TMPDIR"
  echo "Using JAVA_HOME:       $JAVA_HOME"
fi

# Add dependency library to CLASSPATH
if [ -r "$NETTY_HOME"/lib ]; then
	for x in "$NETTY_HOME"/lib/*
	do
	    CLASSPATH="$CLASSPATH":$x 
	done
else
	echo "debug mode. loading lib from project"
	if [ -r "$NETTY_HOME"/../lib ]; then
		cd "$NETTY_HOME"/../
		NETTY_HOME=`pwd`
		CLASSPATH="$NETTY_HOME"/bin
		for x in "$NETTY_HOME"/lib/*
		do
		    CLASSPATH="$CLASSPATH":$x 
		done
	else
		echo "loading lib failed"
		exit 1
	fi
fi
CLASSPATH="$CLASSPATH":"$NETTY_HOME"/lib/netty.jar:"$NETTY_HOME"/conf

if [ "$1" = "run" ]; then

  shift
  "$_RUNJAVA" $JAVA_OPTS $NETTY_OPTS \
    -Djava.endorsed.dirs="$JAVA_ENDORSED_DIRS" -classpath "$CLASSPATH" \
    -Dnetty.base="$NETTY_BASE" \
    -Dnetty.home="$NETTY_HOME" \
    -Djava.io.tmpdir="$NETTY_TMPDIR" \
    "$_ServerClass" "$@" start

elif [ "$1" = "start" ] ; then

  if [ -e $NETTY_PID -a ! -z $NETTY_PID ];then  
    echo "server already running...."  
    exit 1  
  fi

  shift
  touch "$NETTY_BASE"/logs/netty.out
  "$_RUNJAVA" $JAVA_OPTS $NETTY_OPTS \
    -Djava.endorsed.dirs="$JAVA_ENDORSED_DIRS" -classpath "$CLASSPATH" \
    -Dnetty.base="$NETTY_BASE" \
    -Dnetty.home="$NETTY_HOME" \
    -Djava.io.tmpdir="$NETTY_TMPDIR" \
    "$_ServerClass" "$@" start \
    >> "$NETTY_BASE"/logs/netty.out 2>&1 &

    if [ ! -z "$NETTY_PID" ]; then
      echo $! > $NETTY_PID
    fi

elif [ "$1" = "stop" ] ; then

  shift
  FORCE=1
  if [ "$1" = "-force" ]; then
    shift
    FORCE=1
  fi

  "$_RUNJAVA" $JAVA_OPTS \
    -Djava.endorsed.dirs="$JAVA_ENDORSED_DIRS" -classpath "$CLASSPATH" \
    -Dnetty.base="$NETTY_BASE" \
    -Dnetty.home="$NETTY_HOME" \
    -Djava.io.tmpdir="$NETTY_TMPDIR" \
    "$_ServerClass" "$@" stop

  if [ $FORCE -eq 1 ]; then
    #if [ ! -z "$NETTY_PID" ]; then
    if [ -e $NETTY_PID -a ! -z $NETTY_PID ];then  
       echo "Killing: `cat $NETTY_PID`"
       kill -9 `cat $NETTY_PID`
       RETVAL=$?  
       echo  
       [ $RETVAL = 0 ] && rm -f $NETTY_PID
    else
       echo "Kill failed: \$NETTY_PID not set or don't exist!"
    fi
  fi

elif [ "$1" = "version" ] ; then

    "$_RUNJAVA"   \
      -classpath "$CLASSPATH" \
      com.li3huo.netty.util.ServerInfo

elif [ "$1" = "hardware" ] ; then

    echo "===========Hardware Status==========="
    echo "CPU core number:        `grep processor /proc/cpuinfo | wc -l` "
    echo `grep MemTotal /proc/meminfo`
    echo "Current TCP Connections:        `netstat -tn |wc -l`"

else

  echo "Usage: netty.sh ( commands ... )"
  echo "commands:"
  echo "  run               Start netty in the current window"
  echo "  start             Start netty in a separate window"
  echo "  stop              Stop netty"
  echo "  stop -force       Stop netty (followed by kill -KILL)"
  echo "  version           What version of netty server are you running?"
  echo "  hardware          Show hardware info"
  exit 1

fi