begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2013 The Android Open Source Project
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
DECL|package|com.google.gerrit.common
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|common
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
name|annotations
operator|.
name|GwtIncompatible
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Timestamp
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|Instant
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|LongSupplier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Config
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|storage
operator|.
name|file
operator|.
name|FileBasedConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|util
operator|.
name|FS
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|util
operator|.
name|SystemReader
import|;
end_import

begin_comment
comment|/** Static utility methods for dealing with dates and times. */
end_comment

begin_class
annotation|@
name|GwtIncompatible
argument_list|(
literal|"Unemulated Java 8 functionalities"
argument_list|)
DECL|class|TimeUtil
specifier|public
class|class
name|TimeUtil
block|{
DECL|field|SYSTEM_CURRENT_MILLIS_SUPPLIER
specifier|private
specifier|static
specifier|final
name|LongSupplier
name|SYSTEM_CURRENT_MILLIS_SUPPLIER
init|=
name|System
operator|::
name|currentTimeMillis
decl_stmt|;
DECL|field|currentMillisSupplier
specifier|private
specifier|static
specifier|volatile
name|LongSupplier
name|currentMillisSupplier
init|=
name|SYSTEM_CURRENT_MILLIS_SUPPLIER
decl_stmt|;
DECL|method|nowMs ()
specifier|public
specifier|static
name|long
name|nowMs
parameter_list|()
block|{
comment|// We should rather use Instant.now(Clock).toEpochMilli() instead but this would require some
comment|// changes in our testing code as we wouldn't have clock steps anymore.
return|return
name|currentMillisSupplier
operator|.
name|getAsLong
argument_list|()
return|;
block|}
DECL|method|now ()
specifier|public
specifier|static
name|Instant
name|now
parameter_list|()
block|{
return|return
name|Instant
operator|.
name|ofEpochMilli
argument_list|(
name|nowMs
argument_list|()
argument_list|)
return|;
block|}
DECL|method|nowTs ()
specifier|public
specifier|static
name|Timestamp
name|nowTs
parameter_list|()
block|{
return|return
operator|new
name|Timestamp
argument_list|(
name|nowMs
argument_list|()
argument_list|)
return|;
block|}
DECL|method|truncateToSecond (Timestamp t)
specifier|public
specifier|static
name|Timestamp
name|truncateToSecond
parameter_list|(
name|Timestamp
name|t
parameter_list|)
block|{
return|return
operator|new
name|Timestamp
argument_list|(
operator|(
name|t
operator|.
name|getTime
argument_list|()
operator|/
literal|1000
operator|)
operator|*
literal|1000
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setCurrentMillisSupplier (LongSupplier customCurrentMillisSupplier)
specifier|public
specifier|static
name|void
name|setCurrentMillisSupplier
parameter_list|(
name|LongSupplier
name|customCurrentMillisSupplier
parameter_list|)
block|{
name|currentMillisSupplier
operator|=
name|customCurrentMillisSupplier
expr_stmt|;
name|SystemReader
name|oldSystemReader
init|=
name|SystemReader
operator|.
name|getInstance
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|oldSystemReader
operator|instanceof
name|GerritSystemReader
operator|)
condition|)
block|{
name|SystemReader
operator|.
name|setInstance
argument_list|(
operator|new
name|GerritSystemReader
argument_list|(
name|oldSystemReader
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|resetCurrentMillisSupplier ()
specifier|public
specifier|static
name|void
name|resetCurrentMillisSupplier
parameter_list|()
block|{
name|currentMillisSupplier
operator|=
name|SYSTEM_CURRENT_MILLIS_SUPPLIER
expr_stmt|;
name|SystemReader
operator|.
name|setInstance
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|class|GerritSystemReader
specifier|private
specifier|static
class|class
name|GerritSystemReader
extends|extends
name|SystemReader
block|{
DECL|field|delegate
name|SystemReader
name|delegate
decl_stmt|;
DECL|method|GerritSystemReader (SystemReader delegate)
name|GerritSystemReader
parameter_list|(
name|SystemReader
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getHostname ()
specifier|public
name|String
name|getHostname
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getHostname
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getenv (String variable)
specifier|public
name|String
name|getenv
parameter_list|(
name|String
name|variable
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|getenv
argument_list|(
name|variable
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getProperty (String key)
specifier|public
name|String
name|getProperty
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|getProperty
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|openUserConfig (Config parent, FS fs)
specifier|public
name|FileBasedConfig
name|openUserConfig
parameter_list|(
name|Config
name|parent
parameter_list|,
name|FS
name|fs
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|openUserConfig
argument_list|(
name|parent
argument_list|,
name|fs
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|openSystemConfig (Config parent, FS fs)
specifier|public
name|FileBasedConfig
name|openSystemConfig
parameter_list|(
name|Config
name|parent
parameter_list|,
name|FS
name|fs
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|openSystemConfig
argument_list|(
name|parent
argument_list|,
name|fs
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getCurrentTime ()
specifier|public
name|long
name|getCurrentTime
parameter_list|()
block|{
return|return
name|currentMillisSupplier
operator|.
name|getAsLong
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getTimezone (long when)
specifier|public
name|int
name|getTimezone
parameter_list|(
name|long
name|when
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|getTimezone
argument_list|(
name|when
argument_list|)
return|;
block|}
block|}
DECL|method|TimeUtil ()
specifier|private
name|TimeUtil
parameter_list|()
block|{}
block|}
end_class

end_unit

