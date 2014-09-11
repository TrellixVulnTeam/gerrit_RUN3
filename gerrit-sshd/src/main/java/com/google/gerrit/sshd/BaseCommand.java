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
DECL|package|com.google.gerrit.sshd
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|sshd
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Atomics
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
name|common
operator|.
name|Nullable
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
name|extensions
operator|.
name|annotations
operator|.
name|PluginName
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
name|reviewdb
operator|.
name|client
operator|.
name|Project
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
name|CurrentUser
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
name|IdentifiedUser
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
name|git
operator|.
name|ProjectRunnable
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
name|git
operator|.
name|WorkQueue
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
name|git
operator|.
name|WorkQueue
operator|.
name|CancelableRunnable
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
name|util
operator|.
name|TimeUtil
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
name|sshd
operator|.
name|SshScope
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
name|gerrit
operator|.
name|util
operator|.
name|cli
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
name|util
operator|.
name|cli
operator|.
name|EndOfOptionsHandler
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Provider
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
name|Environment
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
name|ExitCallback
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
name|InterruptedIOException
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
name|concurrent
operator|.
name|Future
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
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
DECL|field|PRIVATE_STATUS
specifier|private
specifier|static
specifier|final
name|int
name|PRIVATE_STATUS
init|=
literal|1
operator|<<
literal|30
decl_stmt|;
DECL|field|STATUS_CANCEL
specifier|static
specifier|final
name|int
name|STATUS_CANCEL
init|=
name|PRIVATE_STATUS
operator||
literal|1
decl_stmt|;
DECL|field|STATUS_NOT_FOUND
specifier|static
specifier|final
name|int
name|STATUS_NOT_FOUND
init|=
name|PRIVATE_STATUS
operator||
literal|2
decl_stmt|;
DECL|field|STATUS_NOT_ADMIN
specifier|public
specifier|static
specifier|final
name|int
name|STATUS_NOT_ADMIN
init|=
name|PRIVATE_STATUS
operator||
literal|3
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--"
argument_list|,
name|usage
operator|=
literal|"end of options"
argument_list|,
name|handler
operator|=
name|EndOfOptionsHandler
operator|.
name|class
argument_list|)
DECL|field|endOfOptions
specifier|private
name|boolean
name|endOfOptions
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
DECL|field|sshScope
specifier|private
name|SshScope
name|sshScope
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
annotation|@
name|Inject
annotation|@
name|CommandExecutor
DECL|field|executor
specifier|private
name|WorkQueue
operator|.
name|Executor
name|executor
decl_stmt|;
annotation|@
name|Inject
DECL|field|userProvider
specifier|private
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|userProvider
decl_stmt|;
annotation|@
name|Inject
DECL|field|contextProvider
specifier|private
name|Provider
argument_list|<
name|SshScope
operator|.
name|Context
argument_list|>
name|contextProvider
decl_stmt|;
comment|/** Commands declared by a plugin can be scoped by the plugin name. */
annotation|@
name|Inject
argument_list|(
name|optional
operator|=
literal|true
argument_list|)
annotation|@
name|PluginName
DECL|field|pluginName
specifier|private
name|String
name|pluginName
decl_stmt|;
comment|/** The task, as scheduled on a worker thread. */
DECL|field|task
specifier|private
specifier|final
name|AtomicReference
argument_list|<
name|Future
argument_list|<
name|?
argument_list|>
argument_list|>
name|task
decl_stmt|;
comment|/** Text of the command line which lead up to invoking this instance. */
DECL|field|commandName
specifier|private
name|String
name|commandName
init|=
literal|""
decl_stmt|;
comment|/** Unparsed command line options. */
DECL|field|argv
specifier|private
name|String
index|[]
name|argv
decl_stmt|;
DECL|method|BaseCommand ()
specifier|public
name|BaseCommand
parameter_list|()
block|{
name|task
operator|=
name|Atomics
operator|.
name|newReference
argument_list|()
expr_stmt|;
block|}
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
annotation|@
name|Nullable
DECL|method|getPluginName ()
specifier|protected
name|String
name|getPluginName
parameter_list|()
block|{
return|return
name|pluginName
return|;
block|}
DECL|method|getName ()
specifier|protected
name|String
name|getName
parameter_list|()
block|{
return|return
name|commandName
return|;
block|}
DECL|method|setName (final String prefix)
name|void
name|setName
parameter_list|(
specifier|final
name|String
name|prefix
parameter_list|)
block|{
name|this
operator|.
name|commandName
operator|=
name|prefix
expr_stmt|;
block|}
DECL|method|getArguments ()
specifier|public
name|String
index|[]
name|getArguments
parameter_list|()
block|{
return|return
name|argv
return|;
block|}
DECL|method|setArguments (final String[] argv)
specifier|public
name|void
name|setArguments
parameter_list|(
specifier|final
name|String
index|[]
name|argv
parameter_list|)
block|{
name|this
operator|.
name|argv
operator|=
name|argv
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
block|{
name|Future
argument_list|<
name|?
argument_list|>
name|future
init|=
name|task
operator|.
name|getAndSet
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|future
operator|!=
literal|null
operator|&&
operator|!
name|future
operator|.
name|isDone
argument_list|()
condition|)
block|{
name|future
operator|.
name|cancel
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
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
comment|/**    * Parses the command line argument, injecting parsed values into fields.    *<p>    * This method must be explicitly invoked to cause a parse.    *    * @throws UnloggedFailure if the command line arguments were invalid.    * @see Option    * @see Argument    */
DECL|method|parseCommandLine ()
specifier|protected
name|void
name|parseCommandLine
parameter_list|()
throws|throws
name|UnloggedFailure
block|{
name|parseCommandLine
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|/**    * Parses the command line argument, injecting parsed values into fields.    *<p>    * This method must be explicitly invoked to cause a parse.    *    * @param options object whose fields declare Option and Argument annotations    *        to describe the parameters of the command. Usually {@code this}.    * @throws UnloggedFailure if the command line arguments were invalid.    * @see Option    * @see Argument    */
DECL|method|parseCommandLine (Object options)
specifier|protected
name|void
name|parseCommandLine
parameter_list|(
name|Object
name|options
parameter_list|)
throws|throws
name|UnloggedFailure
block|{
specifier|final
name|CmdLineParser
name|clp
init|=
name|newCmdLineParser
argument_list|(
name|options
argument_list|)
decl_stmt|;
try|try
block|{
name|clp
operator|.
name|parseArgument
argument_list|(
name|argv
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
name|clp
operator|.
name|wasHelpRequestedByOption
argument_list|()
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
name|clp
operator|.
name|wasHelpRequestedByOption
argument_list|()
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
name|clp
operator|.
name|wasHelpRequestedByOption
argument_list|()
condition|)
block|{
name|StringWriter
name|msg
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|clp
operator|.
name|printDetailedUsage
argument_list|(
name|commandName
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|msg
operator|.
name|write
argument_list|(
name|usage
argument_list|()
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
DECL|method|usage ()
specifier|protected
name|String
name|usage
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
comment|/** Construct a new parser for this command's received command line. */
DECL|method|newCmdLineParser (Object options)
specifier|protected
name|CmdLineParser
name|newCmdLineParser
parameter_list|(
name|Object
name|options
parameter_list|)
block|{
return|return
name|cmdLineParserFactory
operator|.
name|create
argument_list|(
name|options
argument_list|)
return|;
block|}
comment|/**    * Spawn a function into its own thread.    *<p>    * Typically this should be invoked within {@link Command#start(Environment)},    * such as:    *    *<pre>    * startThread(new Runnable() {    *   public void run() {    *     runImp();    *   }    * });    *</pre>    *    * @param thunk the runnable to execute on the thread, performing the    *        command's logic.    */
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
comment|/**    * Spawn a function into its own thread.    *<p>    * Typically this should be invoked within {@link Command#start(Environment)},    * such as:    *    *<pre>    * startThread(new CommandRunnable() {    *   public void run() throws Exception {    *     runImp();    *   }    * });    *</pre>    *<p>    * If the function throws an exception, it is translated to a simple message    * for the client, a non-zero exit code, and the stack trace is logged.    *    * @param thunk the runnable to execute on the thread, performing the    *        command's logic.    */
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
name|TaskThunk
name|tt
init|=
operator|new
name|TaskThunk
argument_list|(
name|thunk
argument_list|)
decl_stmt|;
if|if
condition|(
name|isAdminHighPriorityCommand
argument_list|()
operator|&&
name|userProvider
operator|.
name|get
argument_list|()
operator|.
name|getCapabilities
argument_list|()
operator|.
name|canAdministrateServer
argument_list|()
condition|)
block|{
comment|// Admin commands should not block the main work threads (there
comment|// might be an interactive shell there), nor should they wait
comment|// for the main work threads.
comment|//
operator|new
name|Thread
argument_list|(
name|tt
argument_list|,
name|tt
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|task
operator|.
name|set
argument_list|(
name|executor
operator|.
name|submit
argument_list|(
name|tt
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|isAdminHighPriorityCommand ()
specifier|private
specifier|final
name|boolean
name|isAdminHighPriorityCommand
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getAnnotation
argument_list|(
name|AdminHighPriorityCommand
operator|.
name|class
argument_list|)
operator|!=
literal|null
return|;
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
if|if
condition|(
name|cleanup
operator|!=
literal|null
condition|)
block|{
name|cleanup
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
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
operator|(
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
operator|)
operator|||
comment|//
operator|(
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
operator|)
operator|||
comment|//
name|e
operator|.
name|getClass
argument_list|()
operator|==
name|InterruptedIOException
operator|.
name|class
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
literal|"Internal server error"
argument_list|)
expr_stmt|;
if|if
condition|(
name|userProvider
operator|.
name|get
argument_list|()
operator|.
name|isIdentifiedUser
argument_list|()
condition|)
block|{
specifier|final
name|IdentifiedUser
name|u
init|=
operator|(
name|IdentifiedUser
operator|)
name|userProvider
operator|.
name|get
argument_list|()
decl_stmt|;
name|m
operator|.
name|append
argument_list|(
literal|" (user "
argument_list|)
expr_stmt|;
name|m
operator|.
name|append
argument_list|(
name|u
operator|.
name|getAccount
argument_list|()
operator|.
name|getUserName
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
name|u
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
name|m
operator|.
name|append
argument_list|(
literal|" during "
argument_list|)
expr_stmt|;
name|m
operator|.
name|append
argument_list|(
name|contextProvider
operator|.
name|get
argument_list|()
operator|.
name|getCommandLine
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
DECL|method|die (String msg)
specifier|protected
name|UnloggedFailure
name|die
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
return|return
operator|new
name|UnloggedFailure
argument_list|(
literal|1
argument_list|,
literal|"fatal: "
operator|+
name|msg
argument_list|)
return|;
block|}
DECL|method|die (Throwable why)
specifier|protected
name|UnloggedFailure
name|die
parameter_list|(
name|Throwable
name|why
parameter_list|)
block|{
return|return
operator|new
name|UnloggedFailure
argument_list|(
literal|1
argument_list|,
literal|"fatal: "
operator|+
name|why
operator|.
name|getMessage
argument_list|()
argument_list|,
name|why
argument_list|)
return|;
block|}
DECL|method|checkExclusivity (final Object arg1, final String arg1name, final Object arg2, final String arg2name)
specifier|public
name|void
name|checkExclusivity
parameter_list|(
specifier|final
name|Object
name|arg1
parameter_list|,
specifier|final
name|String
name|arg1name
parameter_list|,
specifier|final
name|Object
name|arg2
parameter_list|,
specifier|final
name|String
name|arg2name
parameter_list|)
throws|throws
name|UnloggedFailure
block|{
if|if
condition|(
name|arg1
operator|!=
literal|null
operator|&&
name|arg2
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s and %s options are mutually exclusive."
argument_list|,
name|arg1name
argument_list|,
name|arg2name
argument_list|)
argument_list|)
throw|;
block|}
block|}
DECL|class|TaskThunk
specifier|private
specifier|final
class|class
name|TaskThunk
implements|implements
name|CancelableRunnable
implements|,
name|ProjectRunnable
block|{
DECL|field|thunk
specifier|private
specifier|final
name|CommandRunnable
name|thunk
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|Context
name|context
decl_stmt|;
DECL|field|taskName
specifier|private
specifier|final
name|String
name|taskName
decl_stmt|;
DECL|field|projectName
specifier|private
name|Project
operator|.
name|NameKey
name|projectName
decl_stmt|;
DECL|method|TaskThunk (final CommandRunnable thunk)
specifier|private
name|TaskThunk
parameter_list|(
specifier|final
name|CommandRunnable
name|thunk
parameter_list|)
block|{
name|this
operator|.
name|thunk
operator|=
name|thunk
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|contextProvider
operator|.
name|get
argument_list|()
expr_stmt|;
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
name|context
operator|.
name|getCommandLine
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|userProvider
operator|.
name|get
argument_list|()
operator|.
name|isIdentifiedUser
argument_list|()
condition|)
block|{
name|IdentifiedUser
name|u
init|=
operator|(
name|IdentifiedUser
operator|)
name|userProvider
operator|.
name|get
argument_list|()
decl_stmt|;
name|m
operator|.
name|append
argument_list|(
literal|" ("
argument_list|)
operator|.
name|append
argument_list|(
name|u
operator|.
name|getAccount
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|taskName
operator|=
name|m
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|cancel ()
specifier|public
name|void
name|cancel
parameter_list|()
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
specifier|final
name|Context
name|old
init|=
name|sshScope
operator|.
name|set
argument_list|(
name|context
argument_list|)
decl_stmt|;
try|try
block|{
name|onExit
argument_list|(
name|STATUS_CANCEL
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|sshScope
operator|.
name|set
argument_list|(
name|old
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
specifier|final
name|Thread
name|thisThread
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
decl_stmt|;
specifier|final
name|String
name|thisName
init|=
name|thisThread
operator|.
name|getName
argument_list|()
decl_stmt|;
name|int
name|rc
init|=
literal|0
decl_stmt|;
specifier|final
name|Context
name|old
init|=
name|sshScope
operator|.
name|set
argument_list|(
name|context
argument_list|)
decl_stmt|;
try|try
block|{
name|context
operator|.
name|started
operator|=
name|TimeUtil
operator|.
name|nowMs
argument_list|()
expr_stmt|;
name|thisThread
operator|.
name|setName
argument_list|(
literal|"SSH "
operator|+
name|taskName
argument_list|)
expr_stmt|;
if|if
condition|(
name|thunk
operator|instanceof
name|ProjectCommandRunnable
condition|)
block|{
operator|(
operator|(
name|ProjectCommandRunnable
operator|)
name|thunk
operator|)
operator|.
name|executeParseCommand
argument_list|()
expr_stmt|;
name|projectName
operator|=
operator|(
operator|(
name|ProjectCommandRunnable
operator|)
name|thunk
operator|)
operator|.
name|getProjectName
argument_list|()
expr_stmt|;
block|}
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
try|try
block|{
name|onExit
argument_list|(
name|rc
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|sshScope
operator|.
name|set
argument_list|(
name|old
argument_list|)
expr_stmt|;
name|thisThread
operator|.
name|setName
argument_list|(
name|thisName
argument_list|)
expr_stmt|;
block|}
block|}
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
name|taskName
return|;
block|}
annotation|@
name|Override
DECL|method|getProjectNameKey ()
specifier|public
name|Project
operator|.
name|NameKey
name|getProjectNameKey
parameter_list|()
block|{
return|return
name|projectName
return|;
block|}
annotation|@
name|Override
DECL|method|getRemoteName ()
specifier|public
name|String
name|getRemoteName
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|hasCustomizedPrint ()
specifier|public
name|boolean
name|hasCustomizedPrint
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
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
comment|/** Runnable function which can retrieve a project name related to the task */
DECL|interface|ProjectCommandRunnable
specifier|public
specifier|static
interface|interface
name|ProjectCommandRunnable
extends|extends
name|CommandRunnable
block|{
comment|// execute parser command before running, in order to be able to retrieve
comment|// project name
DECL|method|executeParseCommand ()
specifier|public
name|void
name|executeParseCommand
parameter_list|()
throws|throws
name|Exception
function_decl|;
DECL|method|getProjectName ()
specifier|public
name|Project
operator|.
name|NameKey
name|getProjectName
parameter_list|()
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
comment|/**      * Create a new failure.      *      * @param msg message to also send to the client's stderr.      */
DECL|method|UnloggedFailure (final String msg)
specifier|public
name|UnloggedFailure
parameter_list|(
specifier|final
name|String
name|msg
parameter_list|)
block|{
name|this
argument_list|(
literal|1
argument_list|,
name|msg
argument_list|)
expr_stmt|;
block|}
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

