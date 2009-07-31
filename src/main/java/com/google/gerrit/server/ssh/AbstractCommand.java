begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2008 The Android Open Source Project
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
name|data
operator|.
name|ProjectCache
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
name|client
operator|.
name|reviewdb
operator|.
name|AccountGroup
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
name|client
operator|.
name|reviewdb
operator|.
name|ApprovalCategory
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
name|client
operator|.
name|reviewdb
operator|.
name|ReviewDb
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
name|client
operator|.
name|rpc
operator|.
name|Common
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
name|BaseServiceImplementation
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
name|GerritServer
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
name|RemotePeer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|OrmException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|SchemaFactory
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
name|CommandFactory
operator|.
name|SessionAware
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
name|net
operator|.
name|SocketAddress
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/** Basic command implementation invoked by {@link GerritCommandFactory}. */
end_comment

begin_class
DECL|class|AbstractCommand
specifier|public
specifier|abstract
class|class
name|AbstractCommand
implements|implements
name|Command
implements|,
name|SessionAware
block|{
DECL|field|ENC
specifier|private
specifier|static
specifier|final
name|String
name|ENC
init|=
literal|"UTF-8"
decl_stmt|;
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
name|AbstractCommand
operator|.
name|class
argument_list|)
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
specifier|protected
name|ExitCallback
name|exit
decl_stmt|;
DECL|field|session
specifier|protected
name|ServerSession
name|session
decl_stmt|;
annotation|@
name|Inject
DECL|field|server
specifier|protected
name|GerritServer
name|server
decl_stmt|;
annotation|@
name|Inject
DECL|field|schema
specifier|protected
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schema
decl_stmt|;
annotation|@
name|Inject
annotation|@
name|RemotePeer
DECL|field|remoteAddress
specifier|private
name|SocketAddress
name|remoteAddress
decl_stmt|;
annotation|@
name|Inject
DECL|field|currentUser
specifier|private
name|IdentifiedUser
name|currentUser
decl_stmt|;
DECL|field|db
specifier|protected
name|ReviewDb
name|db
decl_stmt|;
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|unsplitArguments
specifier|private
name|String
name|unsplitArguments
decl_stmt|;
DECL|field|userGroups
specifier|private
name|Set
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|userGroups
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
DECL|method|setSession (final ServerSession session)
specifier|public
name|void
name|setSession
parameter_list|(
specifier|final
name|ServerSession
name|session
parameter_list|)
block|{
name|this
operator|.
name|session
operator|=
name|session
expr_stmt|;
block|}
DECL|method|toPrintWriter (final OutputStream o)
specifier|protected
name|PrintWriter
name|toPrintWriter
parameter_list|(
specifier|final
name|OutputStream
name|o
parameter_list|)
throws|throws
name|UnsupportedEncodingException
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
DECL|method|getGerritServer ()
specifier|protected
name|GerritServer
name|getGerritServer
parameter_list|()
block|{
return|return
name|server
return|;
block|}
DECL|method|openReviewDb ()
specifier|protected
name|ReviewDb
name|openReviewDb
parameter_list|()
throws|throws
name|Failure
block|{
if|if
condition|(
name|db
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|db
operator|=
name|schema
operator|.
name|open
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
literal|1
argument_list|,
literal|"fatal: Gerrit database is offline"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|db
return|;
block|}
DECL|method|getAccountId ()
specifier|protected
name|Account
operator|.
name|Id
name|getAccountId
parameter_list|()
block|{
return|return
name|currentUser
operator|.
name|getAccountId
argument_list|()
return|;
block|}
DECL|method|getRemoteAddress ()
specifier|protected
name|SocketAddress
name|getRemoteAddress
parameter_list|()
block|{
return|return
name|remoteAddress
return|;
block|}
DECL|method|getGroups ()
specifier|protected
name|Set
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|getGroups
parameter_list|()
block|{
if|if
condition|(
name|userGroups
operator|==
literal|null
condition|)
block|{
name|userGroups
operator|=
name|Common
operator|.
name|getGroupCache
argument_list|()
operator|.
name|getEffectiveGroups
argument_list|(
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|userGroups
return|;
block|}
DECL|method|canRead (final ProjectCache.Entry project)
specifier|protected
name|boolean
name|canRead
parameter_list|(
specifier|final
name|ProjectCache
operator|.
name|Entry
name|project
parameter_list|)
block|{
return|return
name|canPerform
argument_list|(
name|project
argument_list|,
name|ApprovalCategory
operator|.
name|READ
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
return|;
block|}
DECL|method|canPerform (final ProjectCache.Entry project, final ApprovalCategory.Id actionId, final short val)
specifier|protected
name|boolean
name|canPerform
parameter_list|(
specifier|final
name|ProjectCache
operator|.
name|Entry
name|project
parameter_list|,
specifier|final
name|ApprovalCategory
operator|.
name|Id
name|actionId
parameter_list|,
specifier|final
name|short
name|val
parameter_list|)
block|{
return|return
name|BaseServiceImplementation
operator|.
name|canPerform
argument_list|(
name|getGroups
argument_list|()
argument_list|,
name|project
argument_list|,
name|actionId
argument_list|,
name|val
argument_list|)
return|;
block|}
DECL|method|assertIsAdministrator ()
specifier|protected
name|void
name|assertIsAdministrator
parameter_list|()
throws|throws
name|Failure
block|{
if|if
condition|(
operator|!
name|Common
operator|.
name|getGroupCache
argument_list|()
operator|.
name|isAdministrator
argument_list|(
name|getAccountId
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
literal|1
argument_list|,
literal|"fatal: Not a Gerrit administrator"
argument_list|)
throw|;
block|}
block|}
DECL|method|getName ()
specifier|protected
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getCommandLine ()
specifier|public
name|String
name|getCommandLine
parameter_list|()
block|{
return|return
name|unsplitArguments
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|?
name|name
operator|+
literal|" "
operator|+
name|unsplitArguments
else|:
name|name
return|;
block|}
DECL|method|setCommandLine (final String cmdName, final String line)
specifier|public
name|void
name|setCommandLine
parameter_list|(
specifier|final
name|String
name|cmdName
parameter_list|,
specifier|final
name|String
name|line
parameter_list|)
block|{
name|name
operator|=
name|cmdName
expr_stmt|;
name|unsplitArguments
operator|=
name|line
expr_stmt|;
block|}
DECL|method|parseArguments ()
specifier|private
name|void
name|parseArguments
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
name|unsplitArguments
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
name|unsplitArguments
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
name|unsplitArguments
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
name|unsplitArguments
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
operator|new
name|CmdLineParser
argument_list|(
name|this
argument_list|)
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
name|getName
argument_list|()
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
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|AbstractCommand
argument_list|>
name|list
init|=
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
name|String
name|who
init|=
name|session
operator|.
name|getUsername
argument_list|()
operator|+
literal|","
operator|+
name|getAccountId
argument_list|()
decl_stmt|;
specifier|final
name|AbstractCommand
name|cmd
init|=
name|this
decl_stmt|;
operator|new
name|Thread
argument_list|(
literal|"Execute "
operator|+
name|getName
argument_list|()
operator|+
literal|" ["
operator|+
name|who
operator|+
literal|"]"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|SshScopes
operator|.
name|invoke
argument_list|(
name|session
argument_list|,
name|cmd
argument_list|,
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
synchronized|synchronized
init|(
name|list
init|)
block|{
name|list
operator|.
name|add
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
name|runImp
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
synchronized|synchronized
init|(
name|list
init|)
block|{
name|list
operator|.
name|remove
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|runImp ()
specifier|private
name|void
name|runImp
parameter_list|()
block|{
name|int
name|rc
init|=
literal|0
decl_stmt|;
try|try
block|{
try|try
block|{
try|try
block|{
name|preRun
argument_list|()
expr_stmt|;
try|try
block|{
name|parseArguments
argument_list|()
expr_stmt|;
name|run
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|postRun
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
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
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
literal|127
argument_list|,
literal|"error: client went away"
argument_list|,
name|e
argument_list|)
throw|;
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
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
literal|127
argument_list|,
literal|"error: client went away"
argument_list|,
name|e
argument_list|)
throw|;
block|}
throw|throw
operator|new
name|Failure
argument_list|(
literal|128
argument_list|,
literal|"fatal: unexpected IO error"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
literal|128
argument_list|,
literal|"fatal: internal server error"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Error
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
literal|128
argument_list|,
literal|"fatal: internal server error"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|Failure
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|e
operator|instanceof
name|UnloggedFailure
operator|)
condition|)
block|{
specifier|final
name|StringBuilder
name|logmsg
init|=
name|beginLogMessage
argument_list|()
decl_stmt|;
name|logmsg
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
expr_stmt|;
name|logmsg
operator|.
name|append
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|!=
literal|null
condition|)
name|log
operator|.
name|error
argument_list|(
name|logmsg
operator|.
name|toString
argument_list|()
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|log
operator|.
name|error
argument_list|(
name|logmsg
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|rc
operator|=
name|e
operator|.
name|exitCode
expr_stmt|;
try|try
block|{
name|err
operator|.
name|write
argument_list|(
operator|(
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|'\n'
operator|)
operator|.
name|getBytes
argument_list|(
name|ENC
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|err
parameter_list|)
block|{         }
block|}
block|}
finally|finally
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
name|IOException
name|err
parameter_list|)
block|{       }
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
name|IOException
name|err
parameter_list|)
block|{       }
name|exit
operator|.
name|onExit
argument_list|(
name|rc
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|beginLogMessage ()
specifier|private
name|StringBuilder
name|beginLogMessage
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|logmsg
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|logmsg
operator|.
name|append
argument_list|(
literal|"sshd error (account "
argument_list|)
expr_stmt|;
name|logmsg
operator|.
name|append
argument_list|(
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
name|logmsg
operator|.
name|append
argument_list|(
literal|"): "
argument_list|)
expr_stmt|;
name|logmsg
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|logmsg
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|logmsg
operator|.
name|append
argument_list|(
name|unsplitArguments
argument_list|)
expr_stmt|;
return|return
name|logmsg
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
DECL|method|preRun ()
specifier|protected
name|void
name|preRun
parameter_list|()
throws|throws
name|Failure
block|{   }
DECL|method|run ()
specifier|protected
specifier|abstract
name|void
name|run
parameter_list|()
throws|throws
name|IOException
throws|,
name|Failure
function_decl|;
DECL|method|postRun ()
specifier|protected
name|void
name|postRun
parameter_list|()
block|{
name|closeDb
argument_list|()
expr_stmt|;
block|}
DECL|method|closeDb ()
specifier|protected
name|void
name|closeDb
parameter_list|()
block|{
if|if
condition|(
name|db
operator|!=
literal|null
condition|)
block|{
name|db
operator|.
name|close
argument_list|()
expr_stmt|;
name|db
operator|=
literal|null
expr_stmt|;
block|}
block|}
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

