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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|truth
operator|.
name|Truth
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|truth
operator|.
name|Truth
operator|.
name|assert_
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
name|collect
operator|.
name|Iterables
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
name|ContributorAgreement
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
name|LabelType
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
name|client
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
name|reviewdb
operator|.
name|client
operator|.
name|RefNames
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
name|extensions
operator|.
name|events
operator|.
name|GitReferenceUpdated
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
name|RefUpdate
operator|.
name|Result
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
name|Map
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
argument_list|<>
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
comment|//
operator|+
literal|"[accounts]\n"
comment|//
operator|+
literal|"  sameGroupVisibility = deny group Developers\n"
comment|//
operator|+
literal|"  sameGroupVisibility = block group Staff\n"
comment|//
operator|+
literal|"[contributor-agreement \"Individual\"]\n"
comment|//
operator|+
literal|"  description = A simple description\n"
comment|//
operator|+
literal|"  accepted = group Developers\n"
comment|//
operator|+
literal|"  accepted = group Staff\n"
comment|//
operator|+
literal|"  autoVerify = group Developers\n"
comment|//
operator|+
literal|"  agreementUrl = http://www.example.com/agree\n"
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
name|assertThat
argument_list|(
name|cfg
operator|.
name|getAccountsSection
argument_list|()
operator|.
name|getSameGroupVisibility
argument_list|()
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|ContributorAgreement
name|ca
init|=
name|cfg
operator|.
name|getContributorAgreement
argument_list|(
literal|"Individual"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|ca
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"Individual"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ca
operator|.
name|getDescription
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"A simple description"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ca
operator|.
name|getAgreementUrl
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"http://www.example.com/agree"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ca
operator|.
name|getAccepted
argument_list|()
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ca
operator|.
name|getAccepted
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getGroup
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|developers
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ca
operator|.
name|getAccepted
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getGroup
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"Staff"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ca
operator|.
name|getAutoVerify
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"Developers"
argument_list|)
expr_stmt|;
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
name|assertThat
argument_list|(
name|section
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|cfg
operator|.
name|getAccessSection
argument_list|(
literal|"refs/*"
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
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
name|assertThat
argument_list|(
name|create
operator|.
name|getExclusiveGroup
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|submit
operator|.
name|getExclusiveGroup
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|read
operator|.
name|getExclusiveGroup
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|push
operator|.
name|getExclusiveGroup
argument_list|()
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReadConfigLabelDefaultValue ()
specifier|public
name|void
name|testReadConfigLabelDefaultValue
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
literal|"[label \"CustomLabel\"]\n"
comment|//
operator|+
literal|"  value = -1 Negative\n"
comment|//
operator|+
literal|"  value =  0 No Score\n"
comment|//
operator|+
literal|"  value =  1 Positive\n"
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
name|Map
argument_list|<
name|String
argument_list|,
name|LabelType
argument_list|>
name|labels
init|=
name|cfg
operator|.
name|getLabelSections
argument_list|()
decl_stmt|;
name|Short
name|dv
init|=
name|labels
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getValue
argument_list|()
operator|.
name|getDefaultValue
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
operator|(
name|int
operator|)
name|dv
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReadConfigLabelDefaultValueInRange ()
specifier|public
name|void
name|testReadConfigLabelDefaultValueInRange
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
literal|"[label \"CustomLabel\"]\n"
comment|//
operator|+
literal|"  value = -1 Negative\n"
comment|//
operator|+
literal|"  value =  0 No Score\n"
comment|//
operator|+
literal|"  value =  1 Positive\n"
comment|//
operator|+
literal|"  defaultValue = -1\n"
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
name|Map
argument_list|<
name|String
argument_list|,
name|LabelType
argument_list|>
name|labels
init|=
name|cfg
operator|.
name|getLabelSections
argument_list|()
decl_stmt|;
name|Short
name|dv
init|=
name|labels
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getValue
argument_list|()
operator|.
name|getDefaultValue
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
operator|(
name|int
operator|)
name|dv
argument_list|)
operator|.
name|isEqualTo
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReadConfigLabelDefaultValueNotInRange ()
specifier|public
name|void
name|testReadConfigLabelDefaultValueNotInRange
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
literal|"[label \"CustomLabel\"]\n"
comment|//
operator|+
literal|"  value = -1 Negative\n"
comment|//
operator|+
literal|"  value =  0 No Score\n"
comment|//
operator|+
literal|"  value =  1 Positive\n"
comment|//
operator|+
literal|"  defaultValue = -2\n"
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
name|assertThat
argument_list|(
name|cfg
operator|.
name|getValidationErrors
argument_list|()
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|cfg
operator|.
name|getValidationErrors
argument_list|()
argument_list|)
operator|.
name|getMessage
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"project.config: Invalid defaultValue \"-2\" "
operator|+
literal|"for label \"CustomLabel\""
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
comment|//
operator|+
literal|"[accounts]\n"
comment|//
operator|+
literal|"  sameGroupVisibility = deny group Developers\n"
comment|//
operator|+
literal|"  sameGroupVisibility = block group Staff\n"
comment|//
operator|+
literal|"[contributor-agreement \"Individual\"]\n"
comment|//
operator|+
literal|"  description = A simple description\n"
comment|//
operator|+
literal|"  accepted = group Developers\n"
comment|//
operator|+
literal|"  autoVerify = group Developers\n"
comment|//
operator|+
literal|"  agreementUrl = http://www.example.com/agree\n"
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
name|cfg
operator|.
name|getAccountsSection
argument_list|()
operator|.
name|setSameGroupVisibility
argument_list|(
name|Collections
operator|.
name|singletonList
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
argument_list|)
expr_stmt|;
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
name|ContributorAgreement
name|ca
init|=
name|cfg
operator|.
name|getContributorAgreement
argument_list|(
literal|"Individual"
argument_list|)
decl_stmt|;
name|ca
operator|.
name|setAccepted
argument_list|(
name|Collections
operator|.
name|singletonList
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
argument_list|)
expr_stmt|;
name|ca
operator|.
name|setAutoVerify
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|ca
operator|.
name|setDescription
argument_list|(
literal|"A new description"
argument_list|)
expr_stmt|;
name|rev
operator|=
name|commit
argument_list|(
name|cfg
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|text
argument_list|(
name|rev
argument_list|,
literal|"project.config"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
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
comment|//
operator|+
literal|"[accounts]\n"
comment|//
operator|+
literal|"  sameGroupVisibility = group Staff\n"
comment|//
operator|+
literal|"[contributor-agreement \"Individual\"]\n"
comment|//
operator|+
literal|"  description = A new description\n"
comment|//
operator|+
literal|"  accepted = group Staff\n"
comment|//
operator|+
literal|"  agreementUrl = http://www.example.com/agree\n"
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
name|assertThat
argument_list|(
name|text
argument_list|(
name|rev
argument_list|,
literal|"project.config"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
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
name|GitReferenceUpdated
operator|.
name|DISABLED
argument_list|,
name|cfg
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
argument_list|,
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
name|cfg
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
name|Ref
name|ref
init|=
name|db
operator|.
name|exactRef
argument_list|(
name|RefNames
operator|.
name|REFS_CONFIG
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
name|RefNames
operator|.
name|REFS_CONFIG
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
name|Result
name|result
init|=
name|u
operator|.
name|forceUpdate
argument_list|()
decl_stmt|;
name|assert_
argument_list|()
operator|.
name|withFailureMessage
argument_list|(
literal|"Cannot update ref for test: "
operator|+
name|result
argument_list|)
operator|.
name|that
argument_list|(
name|result
argument_list|)
operator|.
name|isAnyOf
argument_list|(
name|Result
operator|.
name|FAST_FORWARD
argument_list|,
name|Result
operator|.
name|FORCED
argument_list|,
name|Result
operator|.
name|NEW
argument_list|,
name|Result
operator|.
name|NO_CHANGE
argument_list|)
expr_stmt|;
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

