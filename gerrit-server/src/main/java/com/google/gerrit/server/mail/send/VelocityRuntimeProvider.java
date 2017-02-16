begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2011 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.mail.send
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|mail
operator|.
name|send
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
name|server
operator|.
name|config
operator|.
name|SitePaths
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|ProvisionException
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
name|Singleton
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|runtime
operator|.
name|RuntimeConstants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|runtime
operator|.
name|RuntimeInstance
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|runtime
operator|.
name|RuntimeServices
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|runtime
operator|.
name|log
operator|.
name|LogChute
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

begin_comment
comment|/** Configures Velocity template engine for sending email. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|VelocityRuntimeProvider
specifier|public
class|class
name|VelocityRuntimeProvider
implements|implements
name|Provider
argument_list|<
name|RuntimeInstance
argument_list|>
block|{
DECL|field|site
specifier|private
specifier|final
name|SitePaths
name|site
decl_stmt|;
annotation|@
name|Inject
DECL|method|VelocityRuntimeProvider (SitePaths site)
name|VelocityRuntimeProvider
parameter_list|(
name|SitePaths
name|site
parameter_list|)
block|{
name|this
operator|.
name|site
operator|=
name|site
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|RuntimeInstance
name|get
parameter_list|()
block|{
name|String
name|rl
init|=
literal|"resource.loader"
decl_stmt|;
name|String
name|pkg
init|=
literal|"org.apache.velocity.runtime.resource.loader"
decl_stmt|;
name|Properties
name|p
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
name|RuntimeConstants
operator|.
name|VM_PERM_INLINE_LOCAL
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
name|RuntimeConstants
operator|.
name|RUNTIME_LOG_LOGSYSTEM_CLASS
argument_list|,
name|Slf4jLogChute
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
name|RuntimeConstants
operator|.
name|RUNTIME_REFERENCES_STRICT
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"runtime.log.logsystem.log4j.category"
argument_list|,
literal|"velocity"
argument_list|)
expr_stmt|;
if|if
condition|(
name|Files
operator|.
name|isDirectory
argument_list|(
name|site
operator|.
name|mail_dir
argument_list|)
condition|)
block|{
name|p
operator|.
name|setProperty
argument_list|(
name|rl
argument_list|,
literal|"file, class"
argument_list|)
expr_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"file."
operator|+
name|rl
operator|+
literal|".class"
argument_list|,
name|pkg
operator|+
literal|".FileResourceLoader"
argument_list|)
expr_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"file."
operator|+
name|rl
operator|+
literal|".path"
argument_list|,
name|site
operator|.
name|mail_dir
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"class."
operator|+
name|rl
operator|+
literal|".class"
argument_list|,
name|pkg
operator|+
literal|".ClasspathResourceLoader"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|p
operator|.
name|setProperty
argument_list|(
name|rl
argument_list|,
literal|"class"
argument_list|)
expr_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"class."
operator|+
name|rl
operator|+
literal|".class"
argument_list|,
name|pkg
operator|+
literal|".ClasspathResourceLoader"
argument_list|)
expr_stmt|;
block|}
name|RuntimeInstance
name|ri
init|=
operator|new
name|RuntimeInstance
argument_list|()
decl_stmt|;
try|try
block|{
name|ri
operator|.
name|init
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|err
parameter_list|)
block|{
throw|throw
operator|new
name|ProvisionException
argument_list|(
literal|"Cannot configure Velocity templates"
argument_list|,
name|err
argument_list|)
throw|;
block|}
return|return
name|ri
return|;
block|}
comment|/** Connects Velocity to sfl4j. */
DECL|class|Slf4jLogChute
specifier|public
specifier|static
class|class
name|Slf4jLogChute
implements|implements
name|LogChute
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
name|Slf4jLogChute
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|init (RuntimeServices rs)
specifier|public
name|void
name|init
parameter_list|(
name|RuntimeServices
name|rs
parameter_list|)
block|{}
annotation|@
name|Override
DECL|method|isLevelEnabled (int level)
specifier|public
name|boolean
name|isLevelEnabled
parameter_list|(
name|int
name|level
parameter_list|)
block|{
switch|switch
condition|(
name|level
condition|)
block|{
default|default:
case|case
name|DEBUG_ID
case|:
return|return
name|log
operator|.
name|isDebugEnabled
argument_list|()
return|;
case|case
name|INFO_ID
case|:
return|return
name|log
operator|.
name|isInfoEnabled
argument_list|()
return|;
case|case
name|WARN_ID
case|:
return|return
name|log
operator|.
name|isWarnEnabled
argument_list|()
return|;
case|case
name|ERROR_ID
case|:
return|return
name|log
operator|.
name|isErrorEnabled
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|log (int level, String message)
specifier|public
name|void
name|log
parameter_list|(
name|int
name|level
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|log
argument_list|(
name|level
argument_list|,
name|message
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|log (int level, String msg, Throwable err)
specifier|public
name|void
name|log
parameter_list|(
name|int
name|level
parameter_list|,
name|String
name|msg
parameter_list|,
name|Throwable
name|err
parameter_list|)
block|{
switch|switch
condition|(
name|level
condition|)
block|{
default|default:
case|case
name|DEBUG_ID
case|:
name|log
operator|.
name|debug
argument_list|(
name|msg
argument_list|,
name|err
argument_list|)
expr_stmt|;
break|break;
case|case
name|INFO_ID
case|:
name|log
operator|.
name|info
argument_list|(
name|msg
argument_list|,
name|err
argument_list|)
expr_stmt|;
break|break;
case|case
name|WARN_ID
case|:
name|log
operator|.
name|warn
argument_list|(
name|msg
argument_list|,
name|err
argument_list|)
expr_stmt|;
break|break;
case|case
name|ERROR_ID
case|:
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|err
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
end_class

end_unit

