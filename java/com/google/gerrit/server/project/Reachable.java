begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.project
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|project
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
name|flogger
operator|.
name|FluentLogger
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
name|change
operator|.
name|IncludedInResolver
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
name|permissions
operator|.
name|PermissionBackend
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
name|permissions
operator|.
name|PermissionBackend
operator|.
name|RefFilterOptions
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
name|permissions
operator|.
name|PermissionBackendException
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
name|Singleton
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
name|List
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
name|Ref
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
name|Repository
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
name|revwalk
operator|.
name|RevCommit
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
name|revwalk
operator|.
name|RevWalk
import|;
end_import

begin_comment
comment|/**  * Report whether a commit is reachable from a set of commits. This is used for checking if a user  * has read permissions on a commit.  */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|Reachable
specifier|public
class|class
name|Reachable
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|FluentLogger
name|logger
init|=
name|FluentLogger
operator|.
name|forEnclosingClass
argument_list|()
decl_stmt|;
DECL|field|permissionBackend
specifier|private
specifier|final
name|PermissionBackend
name|permissionBackend
decl_stmt|;
annotation|@
name|Inject
DECL|method|Reachable (PermissionBackend permissionBackend)
name|Reachable
parameter_list|(
name|PermissionBackend
name|permissionBackend
parameter_list|)
block|{
name|this
operator|.
name|permissionBackend
operator|=
name|permissionBackend
expr_stmt|;
block|}
comment|/**    * @return true if a commit is reachable from a given set of refs. This method enforces    *     permissions on the given set of refs and performs a reachability check.    */
DECL|method|fromRefs ( Project.NameKey project, Repository repo, RevCommit commit, List<Ref> refs)
specifier|public
name|boolean
name|fromRefs
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|Repository
name|repo
parameter_list|,
name|RevCommit
name|commit
parameter_list|,
name|List
argument_list|<
name|Ref
argument_list|>
name|refs
parameter_list|)
block|{
try|try
init|(
name|RevWalk
name|rw
init|=
operator|new
name|RevWalk
argument_list|(
name|repo
argument_list|)
init|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|filtered
init|=
name|permissionBackend
operator|.
name|currentUser
argument_list|()
operator|.
name|project
argument_list|(
name|project
argument_list|)
operator|.
name|filter
argument_list|(
name|refs
argument_list|,
name|repo
argument_list|,
name|RefFilterOptions
operator|.
name|builder
argument_list|()
operator|.
name|setFilterTagsSeparately
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|IncludedInResolver
operator|.
name|includedInAny
argument_list|(
name|repo
argument_list|,
name|rw
argument_list|,
name|commit
argument_list|,
name|filtered
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|PermissionBackendException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|atSevere
argument_list|()
operator|.
name|withCause
argument_list|(
name|e
argument_list|)
operator|.
name|log
argument_list|(
literal|"Cannot verify permissions to commit object %s in repository %s"
argument_list|,
name|commit
operator|.
name|name
argument_list|()
argument_list|,
name|project
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

