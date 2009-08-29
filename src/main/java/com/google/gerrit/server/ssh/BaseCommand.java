begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// Licensed under the Apache License, Version 2.0 (the "License");
end_comment

begin_comment
comment|// you may not use this file except in compliance with the License.
end_comment

begin_comment
comment|// You may obtain a copy of the License at
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// http://www.apache.org/licenses/LICENSE-2.0
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// Unless required by applicable law or agreed to in writing, software
end_comment

begin_comment
comment|// distributed under the License is distributed on an "AS IS" BASIS,
end_comment

begin_comment
comment|// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
end_comment

begin_comment
comment|// See the License for the specific language governing permissions and
end_comment

begin_comment
comment|// limitations under the License.
end_comment

begin_package
DECL|package|com.google.gerrit.server.ssh
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|ssh
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|reviewdb
operator|.
name|Account
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
operator|.
name|CmdLineParser
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|RequestCleanup
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|project
operator|.
name|NoSuchChangeException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|project
operator|.
name|NoSuchProjectException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|ssh
operator|.
name|SshScopes
operator|.
name|Context
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sshd
operator|.
name|common
operator|.
name|SshException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sshd
operator|.
name|server
operator|.
name|CommandFactory
operator|.
name|Command
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sshd
operator|.
name|server
operator|.
name|CommandFactory
operator|.
name|ExitCallback
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sshd
operator|.
name|server
operator|.
name|session
operator|.
name|ServerSession
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|Argument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|CmdLineException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|Option
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_class
DECL|class|BaseCommand
specifier|public
specifier|abstract
class|class
name|BaseCommand
implements|implements
name|Command
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|BaseCommand
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ENC
specifier|public
specifier|static
specifier|final
name|String
name|ENC
init|=
literal|"UTF-8"
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--help"
argument_list|,
name|usage
operator|=
literal|"display this help text"
argument_list|,
name|aliases
operator|=
block|{
literal|"-h"
block|}
argument_list|)
DECL|field|help
specifier|private
name|boolean
name|help
decl_stmt|;
DECL|field|in
specifier|protected
name|InputStream
name|in
decl_stmt|;
DECL|field|out
specifier|protected
name|OutputStream
name|out
decl_stmt|;
DECL|field|err
specifier|protected
name|OutputStream
name|err
decl_stmt|;
DECL|field|exit
specifier|private
name|ExitCallback
name|exit
decl_stmt|;
annotation|@
name|Inject
DECL|field|cmdLineParserFactory
specifier|private
name|CmdLineParser
operator|.
name|Factory
name|cmdLineParserFactory
decl_stmt|;
annotation|@
name|Inject
DECL|field|cleanup
specifier|private
name|RequestCleanup
name|cleanup
decl_stmt|;
comment|/** Text of the command line which lead up to invoking this instance. */
DECL|field|commandPrefix
specifier|protected
name|String
name|commandPrefix
init|=
literal|""
decl_stmt|;
comment|/** Unparsed rest of the command line. */
DECL|field|commandLine
specifier|protected
name|String
name|commandLine
init|=
literal|""
decl_stmt|;
DECL|method|setInputStream (final InputStream in)
specifier|public
name|void
name|setInputStream
parameter_list|(
specifier|final
name|InputStream
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
DECL|method|setOutputStream (final OutputStream out)
specifier|public
name|void
name|setOutputStream
parameter_list|(
specifier|final
name|OutputStream
name|out
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
block|}
DECL|method|setErrorStream (final OutputStream err)
specifier|public
name|void
name|setErrorStream
parameter_list|(
specifier|final
name|OutputStream
name|err
parameter_list|)
block|{
name|this
operator|.
name|err
operator|=
name|err
expr_stmt|;
block|}
DECL|method|setExitCallback (final ExitCallback callback)
specifier|public
name|void
name|setExitCallback
parameter_list|(
specifier|final
name|ExitCallback
name|callback
parameter_list|)
block|{
name|this
operator|.
name|exit
operator|=
name|callback
expr_stmt|;
block|}
DECL|method|setCommandPrefix (final String prefix)
specifier|public
name|void
name|setCommandPrefix
parameter_list|(
specifier|final
name|String
name|prefix
parameter_list|)
block|{
name|this
operator|.
name|commandPrefix
operator|=
name|prefix
expr_stmt|;
block|}
comment|/**    * Set the command line to be evaluated by this command.    *<p>    * If this command is being invoked from a higher level    * {@link DispatchCommand} then only the portion after the command name (that    * is, the arguments) is supplied.    *    * @param line the command line received from the client.    */
DECL|method|setCommandLine (final String line)
specifier|public
name|void
name|setCommandLine
parameter_list|(
specifier|final
name|String
name|line
parameter_list|)
block|{
name|this
operator|.
name|commandLine
operator|=
name|line
expr_stmt|;
block|}
comment|/**    * Pass all state into the command, then run its start method.    *<p>    * This method copies all critical state, like the input and output streams,    * into the supplied command. The caller must still invoke {@code cmd.start()}    * if wants to pass control to the command.    *    * @param cmd the command that will receive the current state.    */
DECL|method|provideStateTo (final Command cmd)
specifier|protected
name|void
name|provideStateTo
parameter_list|(
specifier|final
name|Command
name|cmd
parameter_list|)
block|{
name|cmd
operator|.
name|setInputStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|setOutputStream
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|setErrorStream
argument_list|(
name|err
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|setExitCallback
argument_list|(
name|exit
argument_list|)
expr_stmt|;
block|}
comment|/**    * Parses the command line argument, injecting parsed values into fields.    *<p>    * This method must be explicitly invoked to cause a parse. When parsing,    * arguments are split out of and read from the {@link #commandLine} field.    *    * @throws Failure if the command line arguments were invalid.    * @see Option    * @see Argument    */
DECL|method|parseCommandLine ()
specifier|protected
name|void
name|parseCommandLine
parameter_list|()
throws|throws
name|Failure
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|boolean
name|inquote
init|=
literal|false
decl_stmt|;
name|StringBuilder
name|r
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|ip
init|=
literal|0
init|;
name|ip
operator|<
name|commandLine
operator|.
name|length
argument_list|()
condition|;
control|)
block|{
specifier|final
name|char
name|b
init|=
name|commandLine
operator|.
name|charAt
argument_list|(
name|ip
operator|++
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|b
condition|)
block|{
case|case
literal|'\t'
case|:
case|case
literal|' '
case|:
if|if
condition|(
name|inquote
condition|)
name|r
operator|.
name|append
argument_list|(
name|b
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|r
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|r
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
block|}
continue|continue;
case|case
literal|'\''
case|:
name|inquote
operator|=
operator|!
name|inquote
expr_stmt|;
continue|continue;
case|case
literal|'\\'
case|:
if|if
condition|(
name|inquote
operator|||
name|ip
operator|==
name|commandLine
operator|.
name|length
argument_list|()
condition|)
name|r
operator|.
name|append
argument_list|(
name|b
argument_list|)
expr_stmt|;
comment|// literal within a quote
else|else
name|r
operator|.
name|append
argument_list|(
name|commandLine
operator|.
name|charAt
argument_list|(
name|ip
operator|++
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
default|default:
name|r
operator|.
name|append
argument_list|(
name|b
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
if|if
condition|(
name|r
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|r
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|CmdLineParser
name|clp
init|=
name|newCmdLineParser
argument_list|()
decl_stmt|;
try|try
block|{
name|clp
operator|.
name|parseArgument
argument_list|(
name|list
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|list
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|err
parameter_list|)
block|{
if|if
condition|(
operator|!
name|help
condition|)
block|{
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
literal|1
argument_list|,
literal|"fatal: "
operator|+
name|err
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|CmdLineException
name|err
parameter_list|)
block|{
if|if
condition|(
operator|!
name|help
condition|)
block|{
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
literal|1
argument_list|,
literal|"fatal: "
operator|+
name|err
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|help
condition|)
block|{
specifier|final
name|StringWriter
name|msg
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|msg
operator|.
name|write
argument_list|(
name|commandPrefix
argument_list|)
expr_stmt|;
name|clp
operator|.
name|printSingleLineUsage
argument_list|(
name|msg
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|msg
operator|.
name|write
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|msg
operator|.
name|write
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|clp
operator|.
name|printUsage
argument_list|(
name|msg
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|msg
operator|.
name|write
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
literal|1
argument_list|,
name|msg
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/** Construct a new parser for this command's received command line. */
DECL|method|newCmdLineParser ()
specifier|protected
name|CmdLineParser
name|newCmdLineParser
parameter_list|()
block|{
return|return
name|cmdLineParserFactory
operator|.
name|create
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * Spawn a function into its own thread.    *<p>    * Typically this should be invoked within {@link Command#start()}, such as:    *    *<pre>    * startThread(new Runnable() {    *   public void run() {    *     runImp();    *   }    * });    *</pre>    *    * @param thunk the runnable to execute on the thread, performing the    *        command's logic.    */
DECL|method|startThread (final Runnable thunk)
specifier|protected
name|void
name|startThread
parameter_list|(
specifier|final
name|Runnable
name|thunk
parameter_list|)
block|{
name|startThread
argument_list|(
operator|new
name|CommandRunnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|thunk
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Spawn a function into its own thread.    *<p>    * Typically this should be invoked within {@link Command#start()}, such as:    *    *<pre>    * startThread(new CommandRunnable() {    *   public void run() throws Exception {    *     runImp();    *   }    * });    *</pre>    *<p>    * If the function throws an exception, it is translated to a simple message    * for the client, a non-zero exit code, and the stack trace is logged.    *    * @param thunk the runnable to execute on the thread, performing the    *        command's logic.    */
DECL|method|startThread (final CommandRunnable thunk)
specifier|protected
name|void
name|startThread
parameter_list|(
specifier|final
name|CommandRunnable
name|thunk
parameter_list|)
block|{
specifier|final
name|Context
name|context
init|=
name|SshScopes
operator|.
name|getContext
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Command
argument_list|>
name|active
init|=
name|context
operator|.
name|session
operator|.
name|getAttribute
argument_list|(
name|SshUtil
operator|.
name|ACTIVE
argument_list|)
decl_stmt|;
specifier|final
name|Command
name|cmd
init|=
name|this
decl_stmt|;
operator|new
name|Thread
argument_list|(
name|threadName
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|int
name|rc
init|=
literal|0
decl_stmt|;
try|try
block|{
synchronized|synchronized
init|(
name|active
init|)
block|{
name|active
operator|.
name|add
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
name|SshScopes
operator|.
name|current
operator|.
name|set
argument_list|(
name|context
argument_list|)
expr_stmt|;
try|try
block|{
name|thunk
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchProjectException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
literal|1
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|" no such project"
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|NoSuchChangeException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
literal|1
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|" no such change"
argument_list|)
throw|;
block|}
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|err
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
try|try
block|{
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e2
parameter_list|)
block|{           }
try|try
block|{
name|err
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e2
parameter_list|)
block|{           }
name|rc
operator|=
name|handleError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
synchronized|synchronized
init|(
name|active
init|)
block|{
name|active
operator|.
name|remove
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
name|onExit
argument_list|(
name|rc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**    * Terminate this command and return a result code to the remote client.    *<p>    * Commands should invoke this at most once. Once invoked, the command may    * lose access to request based resources as any callbacks previously    * registered with {@link RequestCleanup} will fire.    *    * @param rc exit code for the remote client.    */
DECL|method|onExit (final int rc)
specifier|protected
name|void
name|onExit
parameter_list|(
specifier|final
name|int
name|rc
parameter_list|)
block|{
name|exit
operator|.
name|onExit
argument_list|(
name|rc
argument_list|)
expr_stmt|;
name|cleanup
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
comment|/** Wrap the supplied output stream in a UTF-8 encoded PrintWriter. */
DECL|method|toPrintWriter (final OutputStream o)
specifier|protected
specifier|static
name|PrintWriter
name|toPrintWriter
parameter_list|(
specifier|final
name|OutputStream
name|o
parameter_list|)
block|{
try|try
block|{
return|return
operator|new
name|PrintWriter
argument_list|(
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|o
argument_list|,
name|ENC
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
comment|// Our default encoding is required by the specifications for the
comment|// runtime APIs, this should never, ever happen.
comment|//
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"JVM lacks "
operator|+
name|ENC
operator|+
literal|" encoding"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|threadName ()
specifier|private
name|String
name|threadName
parameter_list|()
block|{
specifier|final
name|ServerSession
name|session
init|=
name|SshScopes
operator|.
name|getContext
argument_list|()
operator|.
name|session
decl_stmt|;
specifier|final
name|String
name|who
init|=
name|session
operator|.
name|getUsername
argument_list|()
decl_stmt|;
specifier|final
name|Account
operator|.
name|Id
name|id
init|=
name|session
operator|.
name|getAttribute
argument_list|(
name|SshUtil
operator|.
name|CURRENT_ACCOUNT
argument_list|)
decl_stmt|;
return|return
literal|"SSH "
operator|+
name|getFullCommandLine
argument_list|()
operator|+
literal|" / "
operator|+
name|who
operator|+
literal|" "
operator|+
name|id
return|;
block|}
DECL|method|handleError (final Throwable e)
specifier|private
name|int
name|handleError
parameter_list|(
specifier|final
name|Throwable
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getClass
argument_list|()
operator|==
name|IOException
operator|.
name|class
operator|&&
literal|"Pipe closed"
operator|.
name|equals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
condition|)
block|{
comment|// This is sshd telling us the client just dropped off while
comment|// we were waiting for a read or a write to complete. Either
comment|// way its not really a fatal error. Don't log it.
comment|//
return|return
literal|127
return|;
block|}
if|if
condition|(
name|e
operator|.
name|getClass
argument_list|()
operator|==
name|SshException
operator|.
name|class
operator|&&
literal|"Already closed"
operator|.
name|equals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
condition|)
block|{
comment|// This is sshd telling us the client just dropped off while
comment|// we were waiting for a read or a write to complete. Either
comment|// way its not really a fatal error. Don't log it.
comment|//
return|return
literal|127
return|;
block|}
if|if
condition|(
name|e
operator|instanceof
name|UnloggedFailure
condition|)
block|{     }
else|else
block|{
specifier|final
name|ServerSession
name|session
init|=
name|SshScopes
operator|.
name|getContext
argument_list|()
operator|.
name|session
decl_stmt|;
specifier|final
name|StringBuilder
name|m
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|m
operator|.
name|append
argument_list|(
literal|"Internal server error ("
argument_list|)
expr_stmt|;
name|m
operator|.
name|append
argument_list|(
literal|"user "
argument_list|)
expr_stmt|;
name|m
operator|.
name|append
argument_list|(
name|session
operator|.
name|getUsername
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|append
argument_list|(
literal|" account "
argument_list|)
expr_stmt|;
name|m
operator|.
name|append
argument_list|(
name|session
operator|.
name|getAttribute
argument_list|(
name|SshUtil
operator|.
name|CURRENT_ACCOUNT
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|.
name|append
argument_list|(
literal|") during "
argument_list|)
expr_stmt|;
name|m
operator|.
name|append
argument_list|(
name|getFullCommandLine
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
name|m
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|e
operator|instanceof
name|Failure
condition|)
block|{
specifier|final
name|Failure
name|f
init|=
operator|(
name|Failure
operator|)
name|e
decl_stmt|;
try|try
block|{
name|err
operator|.
name|write
argument_list|(
operator|(
name|f
operator|.
name|getMessage
argument_list|()
operator|+
literal|"\n"
operator|)
operator|.
name|getBytes
argument_list|(
name|ENC
argument_list|)
argument_list|)
expr_stmt|;
name|err
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e2
parameter_list|)
block|{       }
catch|catch
parameter_list|(
name|Throwable
name|e2
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Cannot send failure message to client"
argument_list|,
name|e2
argument_list|)
expr_stmt|;
block|}
return|return
name|f
operator|.
name|exitCode
return|;
block|}
else|else
block|{
try|try
block|{
name|err
operator|.
name|write
argument_list|(
literal|"fatal: internal server error\n"
operator|.
name|getBytes
argument_list|(
name|ENC
argument_list|)
argument_list|)
expr_stmt|;
name|err
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e2
parameter_list|)
block|{       }
catch|catch
parameter_list|(
name|Throwable
name|e2
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Cannot send internal server error message to client"
argument_list|,
name|e2
argument_list|)
expr_stmt|;
block|}
return|return
literal|128
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getFullCommandLine
argument_list|()
return|;
block|}
DECL|method|getFullCommandLine ()
specifier|private
name|String
name|getFullCommandLine
parameter_list|()
block|{
if|if
condition|(
name|commandPrefix
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|commandLine
return|;
elseif|else
if|if
condition|(
name|commandLine
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|commandPrefix
return|;
else|else
return|return
name|commandPrefix
operator|+
literal|" "
operator|+
name|commandLine
return|;
block|}
comment|/** Runnable function which can throw an exception. */
DECL|interface|CommandRunnable
specifier|public
specifier|static
interface|interface
name|CommandRunnable
block|{
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
function_decl|;
block|}
comment|/** Thrown from {@link CommandRunnable#run()} with client message and code. */
DECL|class|Failure
specifier|public
specifier|static
class|class
name|Failure
extends|extends
name|Exception
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|exitCode
specifier|final
name|int
name|exitCode
decl_stmt|;
comment|/**      * Create a new failure.      *      * @param exitCode exit code to return the client, which indicates the      *        failure status of this command. Should be between 1 and 255,      *        inclusive.      * @param msg message to also send to the client's stderr.      */
DECL|method|Failure (final int exitCode, final String msg)
specifier|public
name|Failure
parameter_list|(
specifier|final
name|int
name|exitCode
parameter_list|,
specifier|final
name|String
name|msg
parameter_list|)
block|{
name|this
argument_list|(
name|exitCode
argument_list|,
name|msg
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create a new failure.      *      * @param exitCode exit code to return the client, which indicates the      *        failure status of this command. Should be between 1 and 255,      *        inclusive.      * @param msg message to also send to the client's stderr.      * @param why stack trace to include in the server's log, but is not sent to      *        the client's stderr.      */
DECL|method|Failure (final int exitCode, final String msg, final Throwable why)
specifier|public
name|Failure
parameter_list|(
specifier|final
name|int
name|exitCode
parameter_list|,
specifier|final
name|String
name|msg
parameter_list|,
specifier|final
name|Throwable
name|why
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|,
name|why
argument_list|)
expr_stmt|;
name|this
operator|.
name|exitCode
operator|=
name|exitCode
expr_stmt|;
block|}
block|}
comment|/** Thrown from {@link CommandRunnable#run()} with client message and code. */
DECL|class|UnloggedFailure
specifier|public
specifier|static
class|class
name|UnloggedFailure
extends|extends
name|Failure
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
comment|/**      * Create a new failure.      *      * @param exitCode exit code to return the client, which indicates the      *        failure status of this command. Should be between 1 and 255,      *        inclusive.      * @param msg message to also send to the client's stderr.      */
DECL|method|UnloggedFailure (final int exitCode, final String msg)
specifier|public
name|UnloggedFailure
parameter_list|(
specifier|final
name|int
name|exitCode
parameter_list|,
specifier|final
name|String
name|msg
parameter_list|)
block|{
name|this
argument_list|(
name|exitCode
argument_list|,
name|msg
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create a new failure.      *      * @param exitCode exit code to return the client, which indicates the      *        failure status of this command. Should be between 1 and 255,      *        inclusive.      * @param msg message to also send to the client's stderr.      * @param why stack trace to include in the server's log, but is not sent to      *        the client's stderr.      */
DECL|method|UnloggedFailure (final int exitCode, final String msg, final Throwable why)
specifier|public
name|UnloggedFailure
parameter_list|(
specifier|final
name|int
name|exitCode
parameter_list|,
specifier|final
name|String
name|msg
parameter_list|,
specifier|final
name|Throwable
name|why
parameter_list|)
block|{
name|super
argument_list|(
name|exitCode
argument_list|,
name|msg
argument_list|,
name|why
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

