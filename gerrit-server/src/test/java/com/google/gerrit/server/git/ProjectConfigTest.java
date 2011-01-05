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
DECL|package|com.google.gerrit.server.git
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|git
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
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
name|data
operator|.
name|AccessSection
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
name|data
operator|.
name|GroupReference
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
name|data
operator|.
name|Permission
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
name|data
operator|.
name|PermissionRule
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
name|reviewdb
operator|.
name|Project
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
name|errors
operator|.
name|ConfigInvalidException
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
name|errors
operator|.
name|IncorrectObjectTypeException
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
name|errors
operator|.
name|MissingObjectException
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
name|junit
operator|.
name|LocalDiskRepositoryTestCase
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
name|junit
operator|.
name|TestRepository
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
name|RefUpdate
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
name|RevObject
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
name|RawParseUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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

begin_class
DECL|class|ProjectConfigTest
specifier|public
class|class
name|ProjectConfigTest
extends|extends
name|LocalDiskRepositoryTestCase
block|{
DECL|field|developers
specifier|private
specifier|final
name|GroupReference
name|developers
init|=
operator|new
name|GroupReference
argument_list|(
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
literal|"X"
argument_list|)
argument_list|,
literal|"Developers"
argument_list|)
decl_stmt|;
DECL|field|staff
specifier|private
specifier|final
name|GroupReference
name|staff
init|=
operator|new
name|GroupReference
argument_list|(
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
literal|"Y"
argument_list|)
argument_list|,
literal|"Staff"
argument_list|)
decl_stmt|;
DECL|field|db
specifier|private
name|Repository
name|db
decl_stmt|;
DECL|field|util
specifier|private
name|TestRepository
argument_list|<
name|Repository
argument_list|>
name|util
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|db
operator|=
name|createBareRepository
argument_list|()
expr_stmt|;
name|util
operator|=
operator|new
name|TestRepository
argument_list|<
name|Repository
argument_list|>
argument_list|(
name|db
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReadConfig ()
specifier|public
name|void
name|testReadConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|RevCommit
name|rev
init|=
name|util
operator|.
name|commit
argument_list|(
name|util
operator|.
name|tree
argument_list|(
comment|//
name|util
operator|.
name|file
argument_list|(
literal|"groups"
argument_list|,
name|util
operator|.
name|blob
argument_list|(
name|group
argument_list|(
name|developers
argument_list|)
argument_list|)
argument_list|)
argument_list|,
comment|//
name|util
operator|.
name|file
argument_list|(
literal|"project.config"
argument_list|,
name|util
operator|.
name|blob
argument_list|(
literal|""
comment|//
operator|+
literal|"[access \"refs/heads/*\"]\n"
comment|//
operator|+
literal|"  exclusiveGroupPermissions = read submit create\n"
comment|//
operator|+
literal|"  submit = group Developers\n"
comment|//
operator|+
literal|"  push = group Developers\n"
comment|//
operator|+
literal|"  read = group Developers\n"
argument_list|)
argument_list|)
comment|//
argument_list|)
argument_list|)
decl_stmt|;
name|ProjectConfig
name|cfg
init|=
name|read
argument_list|(
name|rev
argument_list|)
decl_stmt|;
name|AccessSection
name|section
init|=
name|cfg
operator|.
name|getAccessSection
argument_list|(
literal|"refs/heads/*"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"has refs/heads/*"
argument_list|,
name|section
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"no refs/*"
argument_list|,
name|cfg
operator|.
name|getAccessSection
argument_list|(
literal|"refs/*"
argument_list|)
argument_list|)
expr_stmt|;
name|Permission
name|create
init|=
name|section
operator|.
name|getPermission
argument_list|(
name|Permission
operator|.
name|CREATE
argument_list|)
decl_stmt|;
name|Permission
name|submit
init|=
name|section
operator|.
name|getPermission
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|)
decl_stmt|;
name|Permission
name|read
init|=
name|section
operator|.
name|getPermission
argument_list|(
name|Permission
operator|.
name|READ
argument_list|)
decl_stmt|;
name|Permission
name|push
init|=
name|section
operator|.
name|getPermission
argument_list|(
name|Permission
operator|.
name|PUSH
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|create
operator|.
name|getExclusiveGroup
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|submit
operator|.
name|getExclusiveGroup
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|read
operator|.
name|getExclusiveGroup
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|push
operator|.
name|getExclusiveGroup
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEditConfig ()
specifier|public
name|void
name|testEditConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|RevCommit
name|rev
init|=
name|util
operator|.
name|commit
argument_list|(
name|util
operator|.
name|tree
argument_list|(
comment|//
name|util
operator|.
name|file
argument_list|(
literal|"groups"
argument_list|,
name|util
operator|.
name|blob
argument_list|(
name|group
argument_list|(
name|developers
argument_list|)
argument_list|)
argument_list|)
argument_list|,
comment|//
name|util
operator|.
name|file
argument_list|(
literal|"project.config"
argument_list|,
name|util
operator|.
name|blob
argument_list|(
literal|""
comment|//
operator|+
literal|"[access \"refs/heads/*\"]\n"
comment|//
operator|+
literal|"  exclusiveGroupPermissions = read submit\n"
comment|//
operator|+
literal|"  submit = group Developers\n"
comment|//
operator|+
literal|"  upload = group Developers\n"
comment|//
operator|+
literal|"  read = group Developers\n"
argument_list|)
argument_list|)
comment|//
argument_list|)
argument_list|)
decl_stmt|;
name|update
argument_list|(
name|rev
argument_list|)
expr_stmt|;
name|ProjectConfig
name|cfg
init|=
name|read
argument_list|(
name|rev
argument_list|)
decl_stmt|;
name|AccessSection
name|section
init|=
name|cfg
operator|.
name|getAccessSection
argument_list|(
literal|"refs/heads/*"
argument_list|)
decl_stmt|;
name|Permission
name|submit
init|=
name|section
operator|.
name|getPermission
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|)
decl_stmt|;
name|submit
operator|.
name|add
argument_list|(
operator|new
name|PermissionRule
argument_list|(
name|cfg
operator|.
name|resolve
argument_list|(
name|staff
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|rev
operator|=
name|commit
argument_list|(
name|cfg
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
comment|//
operator|+
literal|"[access \"refs/heads/*\"]\n"
comment|//
operator|+
literal|"  exclusiveGroupPermissions = read submit\n"
comment|//
operator|+
literal|"  submit = group Developers\n"
comment|//
operator|+
literal|"\tsubmit = group Staff\n"
comment|//
operator|+
literal|"  upload = group Developers\n"
comment|//
operator|+
literal|"  read = group Developers\n"
argument_list|,
name|text
argument_list|(
name|rev
argument_list|,
literal|"project.config"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEditConfigMissingGroupTableEntry ()
specifier|public
name|void
name|testEditConfigMissingGroupTableEntry
parameter_list|()
throws|throws
name|Exception
block|{
name|RevCommit
name|rev
init|=
name|util
operator|.
name|commit
argument_list|(
name|util
operator|.
name|tree
argument_list|(
comment|//
name|util
operator|.
name|file
argument_list|(
literal|"groups"
argument_list|,
name|util
operator|.
name|blob
argument_list|(
name|group
argument_list|(
name|developers
argument_list|)
argument_list|)
argument_list|)
argument_list|,
comment|//
name|util
operator|.
name|file
argument_list|(
literal|"project.config"
argument_list|,
name|util
operator|.
name|blob
argument_list|(
literal|""
comment|//
operator|+
literal|"[access \"refs/heads/*\"]\n"
comment|//
operator|+
literal|"  exclusiveGroupPermissions = read submit\n"
comment|//
operator|+
literal|"  submit = group People Who Can Submit\n"
comment|//
operator|+
literal|"  upload = group Developers\n"
comment|//
operator|+
literal|"  read = group Developers\n"
argument_list|)
argument_list|)
comment|//
argument_list|)
argument_list|)
decl_stmt|;
name|update
argument_list|(
name|rev
argument_list|)
expr_stmt|;
name|ProjectConfig
name|cfg
init|=
name|read
argument_list|(
name|rev
argument_list|)
decl_stmt|;
name|AccessSection
name|section
init|=
name|cfg
operator|.
name|getAccessSection
argument_list|(
literal|"refs/heads/*"
argument_list|)
decl_stmt|;
name|Permission
name|submit
init|=
name|section
operator|.
name|getPermission
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|)
decl_stmt|;
name|submit
operator|.
name|add
argument_list|(
operator|new
name|PermissionRule
argument_list|(
name|cfg
operator|.
name|resolve
argument_list|(
name|staff
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|rev
operator|=
name|commit
argument_list|(
name|cfg
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
comment|//
operator|+
literal|"[access \"refs/heads/*\"]\n"
comment|//
operator|+
literal|"  exclusiveGroupPermissions = read submit\n"
comment|//
operator|+
literal|"  submit = group People Who Can Submit\n"
comment|//
operator|+
literal|"\tsubmit = group Staff\n"
comment|//
operator|+
literal|"  upload = group Developers\n"
comment|//
operator|+
literal|"  read = group Developers\n"
argument_list|,
name|text
argument_list|(
name|rev
argument_list|,
literal|"project.config"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|read (RevCommit rev)
specifier|private
name|ProjectConfig
name|read
parameter_list|(
name|RevCommit
name|rev
parameter_list|)
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|ProjectConfig
name|cfg
init|=
operator|new
name|ProjectConfig
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"test"
argument_list|)
argument_list|)
decl_stmt|;
name|cfg
operator|.
name|load
argument_list|(
name|db
argument_list|,
name|rev
argument_list|)
expr_stmt|;
return|return
name|cfg
return|;
block|}
DECL|method|commit (ProjectConfig cfg)
specifier|private
name|RevCommit
name|commit
parameter_list|(
name|ProjectConfig
name|cfg
parameter_list|)
throws|throws
name|IOException
throws|,
name|MissingObjectException
throws|,
name|IncorrectObjectTypeException
block|{
name|MetaDataUpdate
name|md
init|=
operator|new
name|MetaDataUpdate
argument_list|(
operator|new
name|NoReplication
argument_list|()
argument_list|,
comment|//
name|cfg
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
argument_list|,
comment|//
name|db
argument_list|)
decl_stmt|;
name|util
operator|.
name|tick
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|util
operator|.
name|setAuthorAndCommitter
argument_list|(
name|md
operator|.
name|getCommitBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|md
operator|.
name|setMessage
argument_list|(
literal|"Edit\n"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"commit finished"
argument_list|,
name|cfg
operator|.
name|commit
argument_list|(
name|md
argument_list|)
argument_list|)
expr_stmt|;
name|Ref
name|ref
init|=
name|db
operator|.
name|getRef
argument_list|(
name|GitRepositoryManager
operator|.
name|REF_CONFIG
argument_list|)
decl_stmt|;
return|return
name|util
operator|.
name|getRevWalk
argument_list|()
operator|.
name|parseCommit
argument_list|(
name|ref
operator|.
name|getObjectId
argument_list|()
argument_list|)
return|;
block|}
DECL|method|update (RevCommit rev)
specifier|private
name|void
name|update
parameter_list|(
name|RevCommit
name|rev
parameter_list|)
throws|throws
name|Exception
block|{
name|RefUpdate
name|u
init|=
name|db
operator|.
name|updateRef
argument_list|(
name|GitRepositoryManager
operator|.
name|REF_CONFIG
argument_list|)
decl_stmt|;
name|u
operator|.
name|disableRefLog
argument_list|()
expr_stmt|;
name|u
operator|.
name|setNewObjectId
argument_list|(
name|rev
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|u
operator|.
name|forceUpdate
argument_list|()
condition|)
block|{
case|case
name|FAST_FORWARD
case|:
case|case
name|FORCED
case|:
case|case
name|NEW
case|:
case|case
name|NO_CHANGE
case|:
break|break;
default|default:
name|fail
argument_list|(
literal|"Cannot update ref for test: "
operator|+
name|u
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|text (RevCommit rev, String path)
specifier|private
name|String
name|text
parameter_list|(
name|RevCommit
name|rev
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
name|RevObject
name|blob
init|=
name|util
operator|.
name|get
argument_list|(
name|rev
operator|.
name|getTree
argument_list|()
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|db
operator|.
name|open
argument_list|(
name|blob
argument_list|)
operator|.
name|getCachedBytes
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
return|return
name|RawParseUtils
operator|.
name|decode
argument_list|(
name|data
argument_list|)
return|;
block|}
DECL|method|group (GroupReference g)
specifier|private
specifier|static
name|String
name|group
parameter_list|(
name|GroupReference
name|g
parameter_list|)
block|{
return|return
name|g
operator|.
name|getUUID
argument_list|()
operator|.
name|get
argument_list|()
operator|+
literal|"\t"
operator|+
name|g
operator|.
name|getName
argument_list|()
operator|+
literal|"\n"
return|;
block|}
block|}
end_class

end_unit

