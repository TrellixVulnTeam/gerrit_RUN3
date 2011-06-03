begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2010 The Android Open Source Project
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
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|reviewdb
operator|.
name|ApprovalCategory
operator|.
name|OWN
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|reviewdb
operator|.
name|ApprovalCategory
operator|.
name|READ
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|reviewdb
operator|.
name|ApprovalCategory
operator|.
name|SUBMIT
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
name|AccountProjectWatch
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
name|reviewdb
operator|.
name|Change
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
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|reviewdb
operator|.
name|RefRight
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
name|SystemConfig
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
name|RefRight
operator|.
name|RefPattern
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
name|AccessPath
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
name|AnonymousUser
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
name|AuthConfig
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
name|inject
operator|.
name|AbstractModule
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
name|Guice
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
name|Injector
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|binary
operator|.
name|Base64
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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

begin_class
DECL|class|RefControlTest
specifier|public
class|class
name|RefControlTest
extends|extends
name|TestCase
block|{
DECL|method|testOwnerProject ()
specifier|public
name|void
name|testOwnerProject
parameter_list|()
block|{
name|grant
argument_list|(
name|local
argument_list|,
name|OWN
argument_list|,
name|admin
argument_list|,
literal|"refs/*"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|ProjectControl
name|uBlah
init|=
name|user
argument_list|(
name|devs
argument_list|)
decl_stmt|;
name|ProjectControl
name|uAdmin
init|=
name|user
argument_list|(
name|devs
argument_list|,
name|admin
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"not owner"
argument_list|,
name|uBlah
operator|.
name|isOwner
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"is owner"
argument_list|,
name|uAdmin
operator|.
name|isOwner
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testBranchDelegation1 ()
specifier|public
name|void
name|testBranchDelegation1
parameter_list|()
block|{
name|grant
argument_list|(
name|local
argument_list|,
name|OWN
argument_list|,
name|admin
argument_list|,
literal|"refs/*"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|grant
argument_list|(
name|local
argument_list|,
name|OWN
argument_list|,
name|devs
argument_list|,
literal|"refs/heads/x/*"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|ProjectControl
name|uDev
init|=
name|user
argument_list|(
name|devs
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"not owner"
argument_list|,
name|uDev
operator|.
name|isOwner
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"owns ref"
argument_list|,
name|uDev
operator|.
name|isOwnerAnyRef
argument_list|()
argument_list|)
expr_stmt|;
name|assertOwner
argument_list|(
literal|"refs/heads/x/*"
argument_list|,
name|uDev
argument_list|)
expr_stmt|;
name|assertOwner
argument_list|(
literal|"refs/heads/x/y"
argument_list|,
name|uDev
argument_list|)
expr_stmt|;
name|assertOwner
argument_list|(
literal|"refs/heads/x/y/*"
argument_list|,
name|uDev
argument_list|)
expr_stmt|;
name|assertNotOwner
argument_list|(
literal|"refs/*"
argument_list|,
name|uDev
argument_list|)
expr_stmt|;
name|assertNotOwner
argument_list|(
literal|"refs/heads/master"
argument_list|,
name|uDev
argument_list|)
expr_stmt|;
block|}
DECL|method|testBranchDelegation2 ()
specifier|public
name|void
name|testBranchDelegation2
parameter_list|()
block|{
name|grant
argument_list|(
name|local
argument_list|,
name|OWN
argument_list|,
name|admin
argument_list|,
literal|"refs/*"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|grant
argument_list|(
name|local
argument_list|,
name|OWN
argument_list|,
name|devs
argument_list|,
literal|"refs/heads/x/*"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|grant
argument_list|(
name|local
argument_list|,
name|OWN
argument_list|,
name|fixers
argument_list|,
literal|"-refs/heads/x/y/*"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|ProjectControl
name|uDev
init|=
name|user
argument_list|(
name|devs
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"not owner"
argument_list|,
name|uDev
operator|.
name|isOwner
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"owns ref"
argument_list|,
name|uDev
operator|.
name|isOwnerAnyRef
argument_list|()
argument_list|)
expr_stmt|;
name|assertOwner
argument_list|(
literal|"refs/heads/x/*"
argument_list|,
name|uDev
argument_list|)
expr_stmt|;
name|assertOwner
argument_list|(
literal|"refs/heads/x/y"
argument_list|,
name|uDev
argument_list|)
expr_stmt|;
name|assertOwner
argument_list|(
literal|"refs/heads/x/y/*"
argument_list|,
name|uDev
argument_list|)
expr_stmt|;
name|assertNotOwner
argument_list|(
literal|"refs/*"
argument_list|,
name|uDev
argument_list|)
expr_stmt|;
name|assertNotOwner
argument_list|(
literal|"refs/heads/master"
argument_list|,
name|uDev
argument_list|)
expr_stmt|;
name|ProjectControl
name|uFix
init|=
name|user
argument_list|(
name|fixers
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"not owner"
argument_list|,
name|uFix
operator|.
name|isOwner
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"owns ref"
argument_list|,
name|uFix
operator|.
name|isOwnerAnyRef
argument_list|()
argument_list|)
expr_stmt|;
name|assertOwner
argument_list|(
literal|"refs/heads/x/y/*"
argument_list|,
name|uFix
argument_list|)
expr_stmt|;
name|assertOwner
argument_list|(
literal|"refs/heads/x/y/bar"
argument_list|,
name|uFix
argument_list|)
expr_stmt|;
name|assertNotOwner
argument_list|(
literal|"refs/heads/x/*"
argument_list|,
name|uFix
argument_list|)
expr_stmt|;
name|assertNotOwner
argument_list|(
literal|"refs/heads/x/y"
argument_list|,
name|uFix
argument_list|)
expr_stmt|;
name|assertNotOwner
argument_list|(
literal|"refs/*"
argument_list|,
name|uFix
argument_list|)
expr_stmt|;
name|assertNotOwner
argument_list|(
literal|"refs/heads/master"
argument_list|,
name|uFix
argument_list|)
expr_stmt|;
block|}
DECL|method|testInheritRead_SingleBranchDeniesUpload ()
specifier|public
name|void
name|testInheritRead_SingleBranchDeniesUpload
parameter_list|()
block|{
name|grant
argument_list|(
name|parent
argument_list|,
name|READ
argument_list|,
name|registered
argument_list|,
literal|"refs/*"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|grant
argument_list|(
name|local
argument_list|,
name|READ
argument_list|,
name|registered
argument_list|,
literal|"-refs/heads/foobar"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|ProjectControl
name|u
init|=
name|user
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"can upload"
argument_list|,
name|u
operator|.
name|canPushToAtLeastOneRef
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"can upload refs/heads/master"
argument_list|,
comment|//
name|u
operator|.
name|controlForRef
argument_list|(
literal|"refs/heads/master"
argument_list|)
operator|.
name|canUpload
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"deny refs/heads/foobar"
argument_list|,
comment|//
name|u
operator|.
name|controlForRef
argument_list|(
literal|"refs/heads/foobar"
argument_list|)
operator|.
name|canUpload
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testInheritRead_SingleBranchDoesNotOverrideInherited ()
specifier|public
name|void
name|testInheritRead_SingleBranchDoesNotOverrideInherited
parameter_list|()
block|{
name|grant
argument_list|(
name|parent
argument_list|,
name|READ
argument_list|,
name|registered
argument_list|,
literal|"refs/*"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|grant
argument_list|(
name|local
argument_list|,
name|READ
argument_list|,
name|registered
argument_list|,
literal|"refs/heads/foobar"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|ProjectControl
name|u
init|=
name|user
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"can upload"
argument_list|,
name|u
operator|.
name|canPushToAtLeastOneRef
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"can upload refs/heads/master"
argument_list|,
comment|//
name|u
operator|.
name|controlForRef
argument_list|(
literal|"refs/heads/master"
argument_list|)
operator|.
name|canUpload
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"can upload refs/heads/foobar"
argument_list|,
comment|//
name|u
operator|.
name|controlForRef
argument_list|(
literal|"refs/heads/foobar"
argument_list|)
operator|.
name|canUpload
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testInheritRead_OverrideWithDeny ()
specifier|public
name|void
name|testInheritRead_OverrideWithDeny
parameter_list|()
block|{
name|grant
argument_list|(
name|parent
argument_list|,
name|READ
argument_list|,
name|registered
argument_list|,
literal|"refs/*"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|grant
argument_list|(
name|local
argument_list|,
name|READ
argument_list|,
name|registered
argument_list|,
literal|"refs/*"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|ProjectControl
name|u
init|=
name|user
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
literal|"can't read"
argument_list|,
name|u
operator|.
name|isVisible
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testInheritRead_AppendWithDenyOfRef ()
specifier|public
name|void
name|testInheritRead_AppendWithDenyOfRef
parameter_list|()
block|{
name|grant
argument_list|(
name|parent
argument_list|,
name|READ
argument_list|,
name|registered
argument_list|,
literal|"refs/*"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|grant
argument_list|(
name|local
argument_list|,
name|READ
argument_list|,
name|registered
argument_list|,
literal|"refs/heads/*"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|ProjectControl
name|u
init|=
name|user
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"can read"
argument_list|,
name|u
operator|.
name|isVisible
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"can read"
argument_list|,
name|u
operator|.
name|controlForRef
argument_list|(
literal|"refs/master"
argument_list|)
operator|.
name|isVisible
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"can read"
argument_list|,
name|u
operator|.
name|controlForRef
argument_list|(
literal|"refs/tags/foobar"
argument_list|)
operator|.
name|isVisible
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"no master"
argument_list|,
name|u
operator|.
name|controlForRef
argument_list|(
literal|"refs/heads/master"
argument_list|)
operator|.
name|isVisible
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testInheritRead_OverridesAndDeniesOfRef ()
specifier|public
name|void
name|testInheritRead_OverridesAndDeniesOfRef
parameter_list|()
block|{
name|grant
argument_list|(
name|parent
argument_list|,
name|READ
argument_list|,
name|registered
argument_list|,
literal|"refs/*"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|grant
argument_list|(
name|local
argument_list|,
name|READ
argument_list|,
name|registered
argument_list|,
literal|"refs/*"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|grant
argument_list|(
name|local
argument_list|,
name|READ
argument_list|,
name|registered
argument_list|,
literal|"refs/heads/*"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|ProjectControl
name|u
init|=
name|user
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"can read"
argument_list|,
name|u
operator|.
name|isVisible
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"can't read"
argument_list|,
name|u
operator|.
name|controlForRef
argument_list|(
literal|"refs/foobar"
argument_list|)
operator|.
name|isVisible
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"can't read"
argument_list|,
name|u
operator|.
name|controlForRef
argument_list|(
literal|"refs/tags/foobar"
argument_list|)
operator|.
name|isVisible
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"can read"
argument_list|,
name|u
operator|.
name|controlForRef
argument_list|(
literal|"refs/heads/foobar"
argument_list|)
operator|.
name|isVisible
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testInheritSubmit_OverridesAndDeniesOfRef ()
specifier|public
name|void
name|testInheritSubmit_OverridesAndDeniesOfRef
parameter_list|()
block|{
name|grant
argument_list|(
name|parent
argument_list|,
name|SUBMIT
argument_list|,
name|registered
argument_list|,
literal|"refs/*"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|grant
argument_list|(
name|local
argument_list|,
name|SUBMIT
argument_list|,
name|registered
argument_list|,
literal|"refs/*"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|grant
argument_list|(
name|local
argument_list|,
name|SUBMIT
argument_list|,
name|registered
argument_list|,
literal|"refs/heads/*"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|ProjectControl
name|u
init|=
name|user
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
literal|"can't submit"
argument_list|,
name|u
operator|.
name|controlForRef
argument_list|(
literal|"refs/foobar"
argument_list|)
operator|.
name|canSubmit
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"can't submit"
argument_list|,
name|u
operator|.
name|controlForRef
argument_list|(
literal|"refs/tags/foobar"
argument_list|)
operator|.
name|canSubmit
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"can submit"
argument_list|,
name|u
operator|.
name|controlForRef
argument_list|(
literal|"refs/heads/foobar"
argument_list|)
operator|.
name|canSubmit
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testCannotUploadToAnyRef ()
specifier|public
name|void
name|testCannotUploadToAnyRef
parameter_list|()
block|{
name|grant
argument_list|(
name|parent
argument_list|,
name|READ
argument_list|,
name|registered
argument_list|,
literal|"refs/*"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|grant
argument_list|(
name|local
argument_list|,
name|READ
argument_list|,
name|devs
argument_list|,
literal|"refs/heads/*"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|ProjectControl
name|u
init|=
name|user
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
literal|"cannot upload"
argument_list|,
name|u
operator|.
name|canPushToAtLeastOneRef
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"cannot upload refs/heads/master"
argument_list|,
comment|//
name|u
operator|.
name|controlForRef
argument_list|(
literal|"refs/heads/master"
argument_list|)
operator|.
name|canUpload
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// -----------------------------------------------------------------------
DECL|field|local
specifier|private
specifier|final
name|Project
operator|.
name|NameKey
name|local
init|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
DECL|field|parent
specifier|private
specifier|final
name|Project
operator|.
name|NameKey
name|parent
init|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"parent"
argument_list|)
decl_stmt|;
DECL|field|admin
specifier|private
specifier|final
name|AccountGroup
operator|.
name|Id
name|admin
init|=
operator|new
name|AccountGroup
operator|.
name|Id
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|anonymous
specifier|private
specifier|final
name|AccountGroup
operator|.
name|Id
name|anonymous
init|=
operator|new
name|AccountGroup
operator|.
name|Id
argument_list|(
literal|2
argument_list|)
decl_stmt|;
DECL|field|registered
specifier|private
specifier|final
name|AccountGroup
operator|.
name|Id
name|registered
init|=
operator|new
name|AccountGroup
operator|.
name|Id
argument_list|(
literal|3
argument_list|)
decl_stmt|;
DECL|field|owners
specifier|private
specifier|final
name|AccountGroup
operator|.
name|Id
name|owners
init|=
operator|new
name|AccountGroup
operator|.
name|Id
argument_list|(
literal|4
argument_list|)
decl_stmt|;
DECL|field|devs
specifier|private
specifier|final
name|AccountGroup
operator|.
name|Id
name|devs
init|=
operator|new
name|AccountGroup
operator|.
name|Id
argument_list|(
literal|5
argument_list|)
decl_stmt|;
DECL|field|fixers
specifier|private
specifier|final
name|AccountGroup
operator|.
name|Id
name|fixers
init|=
operator|new
name|AccountGroup
operator|.
name|Id
argument_list|(
literal|6
argument_list|)
decl_stmt|;
DECL|field|systemConfig
specifier|private
specifier|final
name|SystemConfig
name|systemConfig
decl_stmt|;
DECL|field|authConfig
specifier|private
specifier|final
name|AuthConfig
name|authConfig
decl_stmt|;
DECL|field|anonymousUser
specifier|private
specifier|final
name|AnonymousUser
name|anonymousUser
decl_stmt|;
DECL|method|RefControlTest ()
specifier|public
name|RefControlTest
parameter_list|()
block|{
name|systemConfig
operator|=
name|SystemConfig
operator|.
name|create
argument_list|()
expr_stmt|;
name|systemConfig
operator|.
name|adminGroupId
operator|=
name|admin
expr_stmt|;
name|systemConfig
operator|.
name|anonymousGroupId
operator|=
name|anonymous
expr_stmt|;
name|systemConfig
operator|.
name|registeredGroupId
operator|=
name|registered
expr_stmt|;
name|systemConfig
operator|.
name|ownerGroupId
operator|=
name|owners
expr_stmt|;
name|systemConfig
operator|.
name|batchUsersGroupId
operator|=
name|anonymous
expr_stmt|;
try|try
block|{
name|byte
index|[]
name|bin
init|=
literal|"abcdefghijklmnopqrstuvwxyz"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|systemConfig
operator|.
name|registerEmailPrivateKey
operator|=
name|Base64
operator|.
name|encodeBase64String
argument_list|(
name|bin
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|err
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot encode key"
argument_list|,
name|err
argument_list|)
throw|;
block|}
name|Injector
name|injector
init|=
name|Guice
operator|.
name|createInjector
argument_list|(
operator|new
name|AbstractModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|Config
operator|.
name|class
argument_list|)
comment|//
operator|.
name|annotatedWith
argument_list|(
name|GerritServerConfig
operator|.
name|class
argument_list|)
comment|//
operator|.
name|toInstance
argument_list|(
operator|new
name|Config
argument_list|()
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|SystemConfig
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|systemConfig
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|AuthConfig
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|AnonymousUser
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|authConfig
operator|=
name|injector
operator|.
name|getInstance
argument_list|(
name|AuthConfig
operator|.
name|class
argument_list|)
expr_stmt|;
name|anonymousUser
operator|=
name|injector
operator|.
name|getInstance
argument_list|(
name|AnonymousUser
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|field|localRights
specifier|private
name|List
argument_list|<
name|RefRight
argument_list|>
name|localRights
decl_stmt|;
DECL|field|inheritedRights
specifier|private
name|List
argument_list|<
name|RefRight
argument_list|>
name|inheritedRights
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp ()
specifier|protected
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
name|localRights
operator|=
operator|new
name|ArrayList
argument_list|<
name|RefRight
argument_list|>
argument_list|()
expr_stmt|;
name|inheritedRights
operator|=
operator|new
name|ArrayList
argument_list|<
name|RefRight
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|assertOwner (String ref, ProjectControl u)
specifier|private
specifier|static
name|void
name|assertOwner
parameter_list|(
name|String
name|ref
parameter_list|,
name|ProjectControl
name|u
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"OWN "
operator|+
name|ref
argument_list|,
name|u
operator|.
name|controlForRef
argument_list|(
name|ref
argument_list|)
operator|.
name|isOwner
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertNotOwner (String ref, ProjectControl u)
specifier|private
specifier|static
name|void
name|assertNotOwner
parameter_list|(
name|String
name|ref
parameter_list|,
name|ProjectControl
name|u
parameter_list|)
block|{
name|assertFalse
argument_list|(
literal|"NOT OWN "
operator|+
name|ref
argument_list|,
name|u
operator|.
name|controlForRef
argument_list|(
name|ref
argument_list|)
operator|.
name|isOwner
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|grant (Project.NameKey project, ApprovalCategory.Id categoryId, AccountGroup.Id group, String ref, int maxValue)
specifier|private
name|void
name|grant
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|ApprovalCategory
operator|.
name|Id
name|categoryId
parameter_list|,
name|AccountGroup
operator|.
name|Id
name|group
parameter_list|,
name|String
name|ref
parameter_list|,
name|int
name|maxValue
parameter_list|)
block|{
name|grant
argument_list|(
name|project
argument_list|,
name|categoryId
argument_list|,
name|group
argument_list|,
name|ref
argument_list|,
name|maxValue
argument_list|,
name|maxValue
argument_list|)
expr_stmt|;
block|}
DECL|method|grant (Project.NameKey project, ApprovalCategory.Id categoryId, AccountGroup.Id group, String ref, int minValue, int maxValue)
specifier|private
name|void
name|grant
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|ApprovalCategory
operator|.
name|Id
name|categoryId
parameter_list|,
name|AccountGroup
operator|.
name|Id
name|group
parameter_list|,
name|String
name|ref
parameter_list|,
name|int
name|minValue
parameter_list|,
name|int
name|maxValue
parameter_list|)
block|{
name|RefRight
name|right
init|=
operator|new
name|RefRight
argument_list|(
operator|new
name|RefRight
operator|.
name|Key
argument_list|(
name|project
argument_list|,
operator|new
name|RefPattern
argument_list|(
name|ref
argument_list|)
argument_list|,
name|categoryId
argument_list|,
name|group
argument_list|)
argument_list|)
decl_stmt|;
name|right
operator|.
name|setMinValue
argument_list|(
operator|(
name|short
operator|)
name|minValue
argument_list|)
expr_stmt|;
name|right
operator|.
name|setMaxValue
argument_list|(
operator|(
name|short
operator|)
name|maxValue
argument_list|)
expr_stmt|;
if|if
condition|(
name|project
operator|==
name|parent
condition|)
block|{
name|inheritedRights
operator|.
name|add
argument_list|(
name|right
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|project
operator|==
name|local
condition|)
block|{
name|localRights
operator|.
name|add
argument_list|(
name|right
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"Unknown project key: "
operator|+
name|project
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|user (AccountGroup.Id... memberOf)
specifier|private
name|ProjectControl
name|user
parameter_list|(
name|AccountGroup
operator|.
name|Id
modifier|...
name|memberOf
parameter_list|)
block|{
name|RefControl
operator|.
name|Factory
name|refControlFactory
init|=
operator|new
name|RefControl
operator|.
name|Factory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RefControl
name|create
parameter_list|(
specifier|final
name|ProjectControl
name|projectControl
parameter_list|,
specifier|final
name|String
name|ref
parameter_list|)
block|{
return|return
operator|new
name|RefControl
argument_list|(
name|systemConfig
argument_list|,
name|projectControl
argument_list|,
name|ref
argument_list|)
return|;
block|}
block|}
decl_stmt|;
return|return
operator|new
name|ProjectControl
argument_list|(
name|systemConfig
argument_list|,
name|Collections
operator|.
expr|<
name|AccountGroup
operator|.
name|Id
operator|>
name|emptySet
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|AccountGroup
operator|.
name|Id
operator|>
name|emptySet
argument_list|()
argument_list|,
name|refControlFactory
argument_list|,
operator|new
name|MockUser
argument_list|(
name|memberOf
argument_list|)
argument_list|,
name|newProjectState
argument_list|()
argument_list|)
return|;
block|}
DECL|method|newProjectState ()
specifier|private
name|ProjectState
name|newProjectState
parameter_list|()
block|{
name|ProjectCache
name|projectCache
init|=
literal|null
decl_stmt|;
name|Project
operator|.
name|NameKey
name|wildProject
init|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"-- All Projects --"
argument_list|)
decl_stmt|;
name|ProjectControl
operator|.
name|AssistedFactory
name|projectControlFactory
init|=
literal|null
decl_stmt|;
name|ProjectState
name|ps
init|=
operator|new
name|ProjectState
argument_list|(
name|anonymousUser
argument_list|,
name|projectCache
argument_list|,
name|wildProject
argument_list|,
name|projectControlFactory
argument_list|,
operator|new
name|Project
argument_list|(
name|parent
argument_list|)
argument_list|,
name|localRights
argument_list|)
decl_stmt|;
name|ps
operator|.
name|setInheritedRights
argument_list|(
name|inheritedRights
argument_list|)
expr_stmt|;
return|return
name|ps
return|;
block|}
DECL|class|MockUser
specifier|private
class|class
name|MockUser
extends|extends
name|CurrentUser
block|{
DECL|field|groups
specifier|private
specifier|final
name|Set
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|groups
decl_stmt|;
DECL|method|MockUser (AccountGroup.Id[] groupId)
name|MockUser
parameter_list|(
name|AccountGroup
operator|.
name|Id
index|[]
name|groupId
parameter_list|)
block|{
name|super
argument_list|(
name|AccessPath
operator|.
name|UNKNOWN
argument_list|,
name|RefControlTest
operator|.
name|this
operator|.
name|authConfig
argument_list|)
expr_stmt|;
name|groups
operator|=
operator|new
name|HashSet
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|groupId
argument_list|)
argument_list|)
expr_stmt|;
name|groups
operator|.
name|add
argument_list|(
name|registered
argument_list|)
expr_stmt|;
name|groups
operator|.
name|add
argument_list|(
name|anonymous
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getEffectiveGroups ()
specifier|public
name|Set
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|getEffectiveGroups
parameter_list|()
block|{
return|return
name|groups
return|;
block|}
annotation|@
name|Override
DECL|method|getStarredChanges ()
specifier|public
name|Set
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|getStarredChanges
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getNotificationFilters ()
specifier|public
name|Collection
argument_list|<
name|AccountProjectWatch
argument_list|>
name|getNotificationFilters
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

