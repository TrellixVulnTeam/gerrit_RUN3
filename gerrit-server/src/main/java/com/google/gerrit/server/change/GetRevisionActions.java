begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2015 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|change
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
name|hash
operator|.
name|Hasher
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
name|hash
operator|.
name|Hashing
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
name|common
operator|.
name|ActionInfo
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
name|restapi
operator|.
name|ETagView
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
name|restapi
operator|.
name|Response
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
name|server
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
name|config
operator|.
name|GerritServerConfig
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
name|ChangeSet
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
name|MergeSuperSet
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
name|query
operator|.
name|change
operator|.
name|ChangeData
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
name|server
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
name|server
operator|.
name|OrmRuntimeException
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
name|Singleton
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
name|util
operator|.
name|Map
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|GetRevisionActions
specifier|public
class|class
name|GetRevisionActions
implements|implements
name|ETagView
argument_list|<
name|RevisionResource
argument_list|>
block|{
DECL|field|delegate
specifier|private
specifier|final
name|ActionJson
name|delegate
decl_stmt|;
DECL|field|config
specifier|private
specifier|final
name|Config
name|config
decl_stmt|;
DECL|field|dbProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
decl_stmt|;
DECL|field|mergeSuperSet
specifier|private
specifier|final
name|MergeSuperSet
name|mergeSuperSet
decl_stmt|;
annotation|@
name|Inject
DECL|method|GetRevisionActions ( ActionJson delegate, Provider<ReviewDb> dbProvider, MergeSuperSet mergeSuperSet, @GerritServerConfig Config config)
name|GetRevisionActions
parameter_list|(
name|ActionJson
name|delegate
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
parameter_list|,
name|MergeSuperSet
name|mergeSuperSet
parameter_list|,
annotation|@
name|GerritServerConfig
name|Config
name|config
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|dbProvider
operator|=
name|dbProvider
expr_stmt|;
name|this
operator|.
name|mergeSuperSet
operator|=
name|mergeSuperSet
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (RevisionResource rsrc)
specifier|public
name|Response
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|ActionInfo
argument_list|>
argument_list|>
name|apply
parameter_list|(
name|RevisionResource
name|rsrc
parameter_list|)
block|{
return|return
name|Response
operator|.
name|withMustRevalidate
argument_list|(
name|delegate
operator|.
name|format
argument_list|(
name|rsrc
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getETag (RevisionResource rsrc)
specifier|public
name|String
name|getETag
parameter_list|(
name|RevisionResource
name|rsrc
parameter_list|)
block|{
name|Hasher
name|h
init|=
name|Hashing
operator|.
name|md5
argument_list|()
operator|.
name|newHasher
argument_list|()
decl_stmt|;
name|CurrentUser
name|user
init|=
name|rsrc
operator|.
name|getControl
argument_list|()
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
try|try
block|{
name|rsrc
operator|.
name|getChangeResource
argument_list|()
operator|.
name|prepareETag
argument_list|(
name|h
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|h
operator|.
name|putBoolean
argument_list|(
name|Submit
operator|.
name|wholeTopicEnabled
argument_list|(
name|config
argument_list|)
argument_list|)
expr_stmt|;
name|ReviewDb
name|db
init|=
name|dbProvider
operator|.
name|get
argument_list|()
decl_stmt|;
name|ChangeSet
name|cs
init|=
name|mergeSuperSet
operator|.
name|completeChangeSet
argument_list|(
name|db
argument_list|,
name|rsrc
operator|.
name|getChange
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|ChangeData
name|cd
range|:
name|cs
operator|.
name|changes
argument_list|()
control|)
block|{
operator|new
name|ChangeResource
argument_list|(
name|cd
operator|.
name|changeControl
argument_list|()
argument_list|)
operator|.
name|prepareETag
argument_list|(
name|h
argument_list|,
name|user
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|OrmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|OrmRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|h
operator|.
name|hash
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

