begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright 2008 Google Inc.
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
name|git
operator|.
name|RepositoryCache
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
name|gwtjsonrpc
operator|.
name|server
operator|.
name|XsrfException
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

begin_comment
comment|/** Basic command implementation invoked by {@link GerritCommandFactory}. */
end_comment

begin_class
DECL|class|AbstractCommand
specifier|abstract
class|class
name|AbstractCommand
implements|implements
name|Command
implements|,
name|SessionAware
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
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|args
specifier|private
name|String
index|[]
name|args
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
DECL|method|getGerritServer ()
specifier|protected
name|GerritServer
name|getGerritServer
parameter_list|()
throws|throws
name|Failure
block|{
try|try
block|{
return|return
name|GerritServer
operator|.
name|getInstance
argument_list|()
return|;
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
literal|128
argument_list|,
literal|"fatal: Gerrit is not available"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|XsrfException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
literal|128
argument_list|,
literal|"fatal: Gerrit is not available"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getRepositoryCache ()
specifier|protected
name|RepositoryCache
name|getRepositoryCache
parameter_list|()
throws|throws
name|Failure
block|{
specifier|final
name|RepositoryCache
name|rc
init|=
name|getGerritServer
argument_list|()
operator|.
name|getRepositoryCache
argument_list|()
decl_stmt|;
if|if
condition|(
name|rc
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
literal|128
argument_list|,
literal|"fatal: Gerrit repositories are not available"
argument_list|,
operator|new
name|IllegalStateException
argument_list|(
literal|"git_base_path not set in system_config"
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|rc
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
try|try
block|{
return|return
name|Common
operator|.
name|getSchemaFactory
argument_list|()
operator|.
name|open
argument_list|()
return|;
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
DECL|method|getAccountId ()
specifier|protected
name|Account
operator|.
name|Id
name|getAccountId
parameter_list|()
block|{
return|return
name|session
operator|.
name|getAttribute
argument_list|(
name|SshUtil
operator|.
name|CURRENT_ACCOUNT
argument_list|)
return|;
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
DECL|method|parseArguments (final String cmdName, final String line)
name|void
name|parseArguments
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
name|line
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
name|line
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
name|line
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
name|line
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
name|name
operator|=
name|cmdName
expr_stmt|;
name|args
operator|=
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
expr_stmt|;
block|}
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
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
name|runImp
argument_list|()
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
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
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
literal|"UTF-8"
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
name|err
parameter_list|)
block|{         }
block|}
block|}
finally|finally
block|{
try|try
block|{
name|in
operator|.
name|close
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
name|out
operator|.
name|close
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
name|close
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
for|for
control|(
specifier|final
name|String
name|a
range|:
name|args
control|)
block|{
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
name|a
argument_list|)
expr_stmt|;
block|}
return|return
name|logmsg
return|;
block|}
DECL|method|run (final String args[])
specifier|protected
specifier|abstract
name|void
name|run
parameter_list|(
specifier|final
name|String
name|args
index|[]
parameter_list|)
throws|throws
name|IOException
throws|,
name|Failure
function_decl|;
DECL|class|Failure
specifier|public
specifier|static
class|class
name|Failure
extends|extends
name|Exception
block|{
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
block|}
end_class

end_unit

