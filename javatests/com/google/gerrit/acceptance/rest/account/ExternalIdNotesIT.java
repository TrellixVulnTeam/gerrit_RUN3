begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2018 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance.rest.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|rest
operator|.
name|account
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
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|instanceOf
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
name|acceptance
operator|.
name|AbstractDaemonTest
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
name|server
operator|.
name|account
operator|.
name|externalids
operator|.
name|ExternalId
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
name|account
operator|.
name|externalids
operator|.
name|ExternalIdNotes
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
name|account
operator|.
name|externalids
operator|.
name|ExternalIds
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
name|meta
operator|.
name|MetaDataUpdate
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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|lib
operator|.
name|Repository
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

begin_class
DECL|class|ExternalIdNotesIT
specifier|public
class|class
name|ExternalIdNotesIT
extends|extends
name|AbstractDaemonTest
block|{
DECL|field|externalIds
annotation|@
name|Inject
specifier|private
name|ExternalIds
name|externalIds
decl_stmt|;
DECL|field|externalIdNotesFactory
annotation|@
name|Inject
specifier|private
name|ExternalIdNotes
operator|.
name|Factory
name|externalIdNotesFactory
decl_stmt|;
annotation|@
name|Test
DECL|method|cannotAddExternalIdsWithSameEmail ()
specifier|public
name|void
name|cannotAddExternalIdsWithSameEmail
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|email
init|=
literal|"foo@example.com"
decl_stmt|;
name|ExternalId
name|extId1
init|=
name|ExternalId
operator|.
name|create
argument_list|(
name|ExternalId
operator|.
name|SCHEME_EXTERNAL
argument_list|,
literal|"abc"
argument_list|,
name|user
operator|.
name|id
argument_list|,
name|email
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|ExternalId
name|extId2
init|=
name|ExternalId
operator|.
name|create
argument_list|(
name|ExternalId
operator|.
name|SCHEME_EXTERNAL
argument_list|,
literal|"xyz"
argument_list|,
name|user
operator|.
name|id
argument_list|,
name|email
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
init|(
name|Repository
name|allUsersRepo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsers
argument_list|)
init|;
name|MetaDataUpdate
name|md
operator|=
name|metaDataUpdateFactory
operator|.
name|create
argument_list|(
name|allUsers
argument_list|)
init|)
block|{
name|ExternalIdNotes
name|extIdNotes
init|=
name|externalIdNotesFactory
operator|.
name|load
argument_list|(
name|allUsersRepo
argument_list|)
decl_stmt|;
name|extIdNotes
operator|.
name|insert
argument_list|(
name|extId1
argument_list|)
expr_stmt|;
name|extIdNotes
operator|.
name|insert
argument_list|(
name|extId2
argument_list|)
expr_stmt|;
name|expectException
argument_list|(
literal|"cannot assign email "
operator|+
name|email
operator|+
literal|" to multiple external IDs: ["
operator|+
name|extId1
operator|.
name|key
argument_list|()
operator|.
name|get
argument_list|()
operator|+
literal|", "
operator|+
name|extId2
operator|.
name|key
argument_list|()
operator|.
name|get
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|extIdNotes
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|cannotAddExternalIdWithEmailThatIsAlreadyUsed ()
specifier|public
name|void
name|cannotAddExternalIdWithEmailThatIsAlreadyUsed
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|email
init|=
literal|"foo@example.com"
decl_stmt|;
name|ExternalId
name|extId1
init|=
name|ExternalId
operator|.
name|create
argument_list|(
name|ExternalId
operator|.
name|SCHEME_EXTERNAL
argument_list|,
literal|"abc"
argument_list|,
name|user
operator|.
name|id
argument_list|,
name|email
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
init|(
name|Repository
name|allUsersRepo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsers
argument_list|)
init|;
name|MetaDataUpdate
name|md
operator|=
name|metaDataUpdateFactory
operator|.
name|create
argument_list|(
name|allUsers
argument_list|)
init|)
block|{
name|ExternalIdNotes
name|extIdNotes
init|=
name|externalIdNotesFactory
operator|.
name|load
argument_list|(
name|allUsersRepo
argument_list|)
decl_stmt|;
name|extIdNotes
operator|.
name|insert
argument_list|(
name|extId1
argument_list|)
expr_stmt|;
name|extIdNotes
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
block|}
name|ExternalId
name|extId2
init|=
name|ExternalId
operator|.
name|create
argument_list|(
name|ExternalId
operator|.
name|SCHEME_EXTERNAL
argument_list|,
literal|"xyz"
argument_list|,
name|user
operator|.
name|id
argument_list|,
name|email
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
init|(
name|Repository
name|allUsersRepo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsers
argument_list|)
init|;
name|MetaDataUpdate
name|md
operator|=
name|metaDataUpdateFactory
operator|.
name|create
argument_list|(
name|allUsers
argument_list|)
init|)
block|{
name|ExternalIdNotes
name|extIdNotes
init|=
name|externalIdNotesFactory
operator|.
name|load
argument_list|(
name|allUsersRepo
argument_list|)
decl_stmt|;
name|extIdNotes
operator|.
name|insert
argument_list|(
name|extId2
argument_list|)
expr_stmt|;
name|expectException
argument_list|(
literal|"cannot assign email "
operator|+
name|email
operator|+
literal|" to external ID(s) ["
operator|+
name|extId2
operator|.
name|key
argument_list|()
operator|.
name|get
argument_list|()
operator|+
literal|"],"
operator|+
literal|" it is already assigned to external ID(s) ["
operator|+
name|extId1
operator|.
name|key
argument_list|()
operator|.
name|get
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|extIdNotes
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|canAssignExistingEmailToNewExternalId ()
specifier|public
name|void
name|canAssignExistingEmailToNewExternalId
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|email
init|=
literal|"foo@example.com"
decl_stmt|;
name|ExternalId
name|extId1
init|=
name|ExternalId
operator|.
name|create
argument_list|(
name|ExternalId
operator|.
name|SCHEME_EXTERNAL
argument_list|,
literal|"abc"
argument_list|,
name|user
operator|.
name|id
argument_list|,
name|email
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
init|(
name|Repository
name|allUsersRepo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsers
argument_list|)
init|;
name|MetaDataUpdate
name|md
operator|=
name|metaDataUpdateFactory
operator|.
name|create
argument_list|(
name|allUsers
argument_list|)
init|)
block|{
name|ExternalIdNotes
name|extIdNotes
init|=
name|externalIdNotesFactory
operator|.
name|load
argument_list|(
name|allUsersRepo
argument_list|)
decl_stmt|;
name|extIdNotes
operator|.
name|insert
argument_list|(
name|extId1
argument_list|)
expr_stmt|;
name|extIdNotes
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
block|}
name|ExternalId
name|extId2
init|=
name|ExternalId
operator|.
name|create
argument_list|(
name|ExternalId
operator|.
name|SCHEME_EXTERNAL
argument_list|,
literal|"xyz"
argument_list|,
name|user
operator|.
name|id
argument_list|,
name|email
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
init|(
name|Repository
name|allUsersRepo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsers
argument_list|)
init|;
name|MetaDataUpdate
name|md
operator|=
name|metaDataUpdateFactory
operator|.
name|create
argument_list|(
name|allUsers
argument_list|)
init|)
block|{
name|ExternalIdNotes
name|extIdNotes
init|=
name|externalIdNotesFactory
operator|.
name|load
argument_list|(
name|allUsersRepo
argument_list|)
decl_stmt|;
name|extIdNotes
operator|.
name|upsert
argument_list|(
name|ExternalId
operator|.
name|create
argument_list|(
name|extId1
operator|.
name|key
argument_list|()
argument_list|,
name|user
operator|.
name|id
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|extIdNotes
operator|.
name|insert
argument_list|(
name|extId2
argument_list|)
expr_stmt|;
name|extIdNotes
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
block|}
name|assertExternalIdWithoutEmail
argument_list|(
name|extId1
operator|.
name|key
argument_list|()
argument_list|)
expr_stmt|;
name|assertExternalId
argument_list|(
name|extId2
operator|.
name|key
argument_list|()
argument_list|,
name|email
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|canAssignExistingEmailToDifferentExternalId ()
specifier|public
name|void
name|canAssignExistingEmailToDifferentExternalId
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|email
init|=
literal|"foo@example.com"
decl_stmt|;
name|ExternalId
name|extId1
init|=
name|ExternalId
operator|.
name|create
argument_list|(
name|ExternalId
operator|.
name|SCHEME_EXTERNAL
argument_list|,
literal|"abc"
argument_list|,
name|user
operator|.
name|id
argument_list|,
name|email
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|ExternalId
name|extId2
init|=
name|ExternalId
operator|.
name|create
argument_list|(
name|ExternalId
operator|.
name|SCHEME_EXTERNAL
argument_list|,
literal|"xyz"
argument_list|,
name|user
operator|.
name|id
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
init|(
name|Repository
name|allUsersRepo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsers
argument_list|)
init|;
name|MetaDataUpdate
name|md
operator|=
name|metaDataUpdateFactory
operator|.
name|create
argument_list|(
name|allUsers
argument_list|)
init|)
block|{
name|ExternalIdNotes
name|extIdNotes
init|=
name|externalIdNotesFactory
operator|.
name|load
argument_list|(
name|allUsersRepo
argument_list|)
decl_stmt|;
name|extIdNotes
operator|.
name|insert
argument_list|(
name|extId1
argument_list|)
expr_stmt|;
name|extIdNotes
operator|.
name|insert
argument_list|(
name|extId2
argument_list|)
expr_stmt|;
name|extIdNotes
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|Repository
name|allUsersRepo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsers
argument_list|)
init|;
name|MetaDataUpdate
name|md
operator|=
name|metaDataUpdateFactory
operator|.
name|create
argument_list|(
name|allUsers
argument_list|)
init|)
block|{
name|ExternalIdNotes
name|extIdNotes
init|=
name|externalIdNotesFactory
operator|.
name|load
argument_list|(
name|allUsersRepo
argument_list|)
decl_stmt|;
name|extIdNotes
operator|.
name|upsert
argument_list|(
name|ExternalId
operator|.
name|create
argument_list|(
name|extId1
operator|.
name|key
argument_list|()
argument_list|,
name|user
operator|.
name|id
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|extIdNotes
operator|.
name|upsert
argument_list|(
name|ExternalId
operator|.
name|create
argument_list|(
name|extId2
operator|.
name|key
argument_list|()
argument_list|,
name|user
operator|.
name|id
argument_list|,
name|email
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|extIdNotes
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
block|}
name|assertExternalIdWithoutEmail
argument_list|(
name|extId1
operator|.
name|key
argument_list|()
argument_list|)
expr_stmt|;
name|assertExternalId
argument_list|(
name|extId2
operator|.
name|key
argument_list|()
argument_list|,
name|email
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|cannotAssignExistingEmailToMultipleNewExternalIds ()
specifier|public
name|void
name|cannotAssignExistingEmailToMultipleNewExternalIds
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|email
init|=
literal|"foo@example.com"
decl_stmt|;
name|ExternalId
name|extId1
init|=
name|ExternalId
operator|.
name|create
argument_list|(
name|ExternalId
operator|.
name|SCHEME_EXTERNAL
argument_list|,
literal|"abc"
argument_list|,
name|user
operator|.
name|id
argument_list|,
name|email
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
init|(
name|Repository
name|allUsersRepo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsers
argument_list|)
init|;
name|MetaDataUpdate
name|md
operator|=
name|metaDataUpdateFactory
operator|.
name|create
argument_list|(
name|allUsers
argument_list|)
init|)
block|{
name|ExternalIdNotes
name|extIdNotes
init|=
name|externalIdNotesFactory
operator|.
name|load
argument_list|(
name|allUsersRepo
argument_list|)
decl_stmt|;
name|extIdNotes
operator|.
name|insert
argument_list|(
name|extId1
argument_list|)
expr_stmt|;
name|extIdNotes
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
block|}
name|ExternalId
name|extId2
init|=
name|ExternalId
operator|.
name|create
argument_list|(
name|ExternalId
operator|.
name|SCHEME_EXTERNAL
argument_list|,
literal|"efg"
argument_list|,
name|user
operator|.
name|id
argument_list|,
name|email
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|ExternalId
name|extId3
init|=
name|ExternalId
operator|.
name|create
argument_list|(
name|ExternalId
operator|.
name|SCHEME_EXTERNAL
argument_list|,
literal|"hij"
argument_list|,
name|user
operator|.
name|id
argument_list|,
name|email
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
init|(
name|Repository
name|allUsersRepo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsers
argument_list|)
init|;
name|MetaDataUpdate
name|md
operator|=
name|metaDataUpdateFactory
operator|.
name|create
argument_list|(
name|allUsers
argument_list|)
init|)
block|{
name|ExternalIdNotes
name|extIdNotes
init|=
name|externalIdNotesFactory
operator|.
name|load
argument_list|(
name|allUsersRepo
argument_list|)
decl_stmt|;
name|extIdNotes
operator|.
name|upsert
argument_list|(
name|ExternalId
operator|.
name|create
argument_list|(
name|extId1
operator|.
name|key
argument_list|()
argument_list|,
name|user
operator|.
name|id
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|extIdNotes
operator|.
name|insert
argument_list|(
name|extId2
argument_list|)
expr_stmt|;
name|extIdNotes
operator|.
name|insert
argument_list|(
name|extId3
argument_list|)
expr_stmt|;
name|expectException
argument_list|(
literal|"cannot assign email "
operator|+
name|email
operator|+
literal|" to multiple external IDs: ["
operator|+
name|extId2
operator|.
name|key
argument_list|()
operator|.
name|get
argument_list|()
operator|+
literal|", "
operator|+
name|extId3
operator|.
name|key
argument_list|()
operator|.
name|get
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|extIdNotes
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|canUpdateExternalIdsIfDuplicateEmailsAlreadyExist ()
specifier|public
name|void
name|canUpdateExternalIdsIfDuplicateEmailsAlreadyExist
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|email
init|=
literal|"foo@example.com"
decl_stmt|;
name|ExternalId
name|extId1
init|=
name|ExternalId
operator|.
name|create
argument_list|(
name|ExternalId
operator|.
name|SCHEME_EXTERNAL
argument_list|,
literal|"abc"
argument_list|,
name|user
operator|.
name|id
argument_list|,
name|email
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|ExternalId
name|extId2
init|=
name|ExternalId
operator|.
name|create
argument_list|(
name|ExternalId
operator|.
name|SCHEME_EXTERNAL
argument_list|,
literal|"efg"
argument_list|,
name|user
operator|.
name|id
argument_list|,
name|email
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
init|(
name|Repository
name|allUsersRepo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsers
argument_list|)
init|;
name|MetaDataUpdate
name|md
operator|=
name|metaDataUpdateFactory
operator|.
name|create
argument_list|(
name|allUsers
argument_list|)
init|)
block|{
name|ExternalIdNotes
name|extIdNotes
init|=
name|externalIdNotesFactory
operator|.
name|load
argument_list|(
name|allUsersRepo
argument_list|)
operator|.
name|setDisableCheckForNewDuplicateEmails
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|extIdNotes
operator|.
name|insert
argument_list|(
name|extId1
argument_list|)
expr_stmt|;
name|extIdNotes
operator|.
name|insert
argument_list|(
name|extId2
argument_list|)
expr_stmt|;
name|extIdNotes
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
block|}
name|String
name|email2
init|=
literal|"bar@example.com"
decl_stmt|;
name|ExternalId
name|extId3
init|=
name|ExternalId
operator|.
name|create
argument_list|(
name|ExternalId
operator|.
name|SCHEME_EXTERNAL
argument_list|,
literal|"hij"
argument_list|,
name|user
operator|.
name|id
argument_list|,
name|email2
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
init|(
name|Repository
name|allUsersRepo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsers
argument_list|)
init|;
name|MetaDataUpdate
name|md
operator|=
name|metaDataUpdateFactory
operator|.
name|create
argument_list|(
name|allUsers
argument_list|)
init|)
block|{
name|ExternalIdNotes
name|extIdNotes
init|=
name|externalIdNotesFactory
operator|.
name|load
argument_list|(
name|allUsersRepo
argument_list|)
decl_stmt|;
name|extIdNotes
operator|.
name|upsert
argument_list|(
name|ExternalId
operator|.
name|create
argument_list|(
name|extId1
operator|.
name|key
argument_list|()
argument_list|,
name|admin
operator|.
name|id
argument_list|,
name|email
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|extIdNotes
operator|.
name|insert
argument_list|(
name|extId3
argument_list|)
expr_stmt|;
name|extIdNotes
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
block|}
name|assertExternalId
argument_list|(
name|extId1
operator|.
name|key
argument_list|()
argument_list|,
name|email
argument_list|)
expr_stmt|;
name|assertExternalId
argument_list|(
name|extId2
operator|.
name|key
argument_list|()
argument_list|,
name|email
argument_list|)
expr_stmt|;
name|assertExternalId
argument_list|(
name|extId3
operator|.
name|key
argument_list|()
argument_list|,
name|email2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|cannotAddExistingDuplicateEmailToAnotherExternalId ()
specifier|public
name|void
name|cannotAddExistingDuplicateEmailToAnotherExternalId
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|email
init|=
literal|"foo@example.com"
decl_stmt|;
name|ExternalId
name|extId1
init|=
name|ExternalId
operator|.
name|create
argument_list|(
name|ExternalId
operator|.
name|SCHEME_EXTERNAL
argument_list|,
literal|"abc"
argument_list|,
name|user
operator|.
name|id
argument_list|,
name|email
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|ExternalId
name|extId2
init|=
name|ExternalId
operator|.
name|create
argument_list|(
name|ExternalId
operator|.
name|SCHEME_EXTERNAL
argument_list|,
literal|"efg"
argument_list|,
name|user
operator|.
name|id
argument_list|,
name|email
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
init|(
name|Repository
name|allUsersRepo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsers
argument_list|)
init|;
name|MetaDataUpdate
name|md
operator|=
name|metaDataUpdateFactory
operator|.
name|create
argument_list|(
name|allUsers
argument_list|)
init|)
block|{
name|ExternalIdNotes
name|extIdNotes
init|=
name|externalIdNotesFactory
operator|.
name|load
argument_list|(
name|allUsersRepo
argument_list|)
operator|.
name|setDisableCheckForNewDuplicateEmails
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|extIdNotes
operator|.
name|insert
argument_list|(
name|extId1
argument_list|)
expr_stmt|;
name|extIdNotes
operator|.
name|insert
argument_list|(
name|extId2
argument_list|)
expr_stmt|;
name|extIdNotes
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
block|}
name|ExternalId
name|extId3
init|=
name|ExternalId
operator|.
name|create
argument_list|(
name|ExternalId
operator|.
name|SCHEME_EXTERNAL
argument_list|,
literal|"hij"
argument_list|,
name|user
operator|.
name|id
argument_list|,
name|email
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
init|(
name|Repository
name|allUsersRepo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsers
argument_list|)
init|;
name|MetaDataUpdate
name|md
operator|=
name|metaDataUpdateFactory
operator|.
name|create
argument_list|(
name|allUsers
argument_list|)
init|)
block|{
name|ExternalIdNotes
name|extIdNotes
init|=
name|externalIdNotesFactory
operator|.
name|load
argument_list|(
name|allUsersRepo
argument_list|)
decl_stmt|;
name|extIdNotes
operator|.
name|insert
argument_list|(
name|extId3
argument_list|)
expr_stmt|;
name|expectException
argument_list|(
literal|"cannot assign email "
operator|+
name|email
operator|+
literal|" to external ID(s) ["
operator|+
name|extId3
operator|.
name|key
argument_list|()
operator|.
name|get
argument_list|()
operator|+
literal|"],"
operator|+
literal|" it is already assigned to external ID(s) ["
operator|+
name|extId1
operator|.
name|key
argument_list|()
operator|.
name|get
argument_list|()
operator|+
literal|", "
operator|+
name|extId2
operator|.
name|key
argument_list|()
operator|.
name|get
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|extIdNotes
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|canRemoveExistingDuplicateEmail ()
specifier|public
name|void
name|canRemoveExistingDuplicateEmail
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|email
init|=
literal|"foo@example.com"
decl_stmt|;
name|ExternalId
name|extId1
init|=
name|ExternalId
operator|.
name|create
argument_list|(
name|ExternalId
operator|.
name|SCHEME_EXTERNAL
argument_list|,
literal|"abc"
argument_list|,
name|user
operator|.
name|id
argument_list|,
name|email
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|ExternalId
name|extId2
init|=
name|ExternalId
operator|.
name|create
argument_list|(
name|ExternalId
operator|.
name|SCHEME_EXTERNAL
argument_list|,
literal|"efg"
argument_list|,
name|user
operator|.
name|id
argument_list|,
name|email
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
init|(
name|Repository
name|allUsersRepo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsers
argument_list|)
init|;
name|MetaDataUpdate
name|md
operator|=
name|metaDataUpdateFactory
operator|.
name|create
argument_list|(
name|allUsers
argument_list|)
init|)
block|{
name|ExternalIdNotes
name|extIdNotes
init|=
name|externalIdNotesFactory
operator|.
name|load
argument_list|(
name|allUsersRepo
argument_list|)
operator|.
name|setDisableCheckForNewDuplicateEmails
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|extIdNotes
operator|.
name|insert
argument_list|(
name|extId1
argument_list|)
expr_stmt|;
name|extIdNotes
operator|.
name|insert
argument_list|(
name|extId2
argument_list|)
expr_stmt|;
name|extIdNotes
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
block|}
name|String
name|email2
init|=
literal|"bar@example.com"
decl_stmt|;
try|try
init|(
name|Repository
name|allUsersRepo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsers
argument_list|)
init|;
name|MetaDataUpdate
name|md
operator|=
name|metaDataUpdateFactory
operator|.
name|create
argument_list|(
name|allUsers
argument_list|)
init|)
block|{
name|ExternalIdNotes
name|extIdNotes
init|=
name|externalIdNotesFactory
operator|.
name|load
argument_list|(
name|allUsersRepo
argument_list|)
decl_stmt|;
name|extIdNotes
operator|.
name|upsert
argument_list|(
name|ExternalId
operator|.
name|create
argument_list|(
name|extId2
operator|.
name|key
argument_list|()
argument_list|,
name|user
operator|.
name|id
argument_list|,
name|email2
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|extIdNotes
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
block|}
name|assertExternalId
argument_list|(
name|extId1
operator|.
name|key
argument_list|()
argument_list|,
name|email
argument_list|)
expr_stmt|;
name|assertExternalId
argument_list|(
name|extId2
operator|.
name|key
argument_list|()
argument_list|,
name|email2
argument_list|)
expr_stmt|;
block|}
DECL|method|assertExternalIdWithoutEmail (ExternalId.Key extIdKey)
specifier|private
name|void
name|assertExternalIdWithoutEmail
parameter_list|(
name|ExternalId
operator|.
name|Key
name|extIdKey
parameter_list|)
throws|throws
name|Exception
block|{
name|assertExternalId
argument_list|(
name|extIdKey
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|assertExternalId (ExternalId.Key extIdKey, @Nullable String expectedEmail)
specifier|private
name|void
name|assertExternalId
parameter_list|(
name|ExternalId
operator|.
name|Key
name|extIdKey
parameter_list|,
annotation|@
name|Nullable
name|String
name|expectedEmail
parameter_list|)
throws|throws
name|Exception
block|{
name|ExternalId
name|extId
init|=
name|externalIds
operator|.
name|get
argument_list|(
name|extIdKey
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|extId
argument_list|)
operator|.
name|named
argument_list|(
name|extIdKey
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|extId
operator|.
name|email
argument_list|()
argument_list|)
operator|.
name|named
argument_list|(
literal|"email of "
operator|+
name|extIdKey
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expectedEmail
argument_list|)
expr_stmt|;
block|}
DECL|method|expectException (String message)
specifier|private
name|void
name|expectException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|exception
operator|.
name|expect
argument_list|(
name|IOException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectCause
argument_list|(
name|instanceOf
argument_list|(
name|ConfigInvalidException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"Ambiguous emails:"
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

