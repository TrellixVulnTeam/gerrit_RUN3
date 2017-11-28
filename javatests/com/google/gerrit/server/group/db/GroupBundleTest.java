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
DECL|package|com.google.gerrit.server.group.db
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|group
operator|.
name|db
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
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|common
operator|.
name|TimeUtil
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
name|AccountGroupById
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
name|AccountGroupByIdAud
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
name|AccountGroupMember
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
name|AccountGroupMemberAudit
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
name|group
operator|.
name|db
operator|.
name|GroupBundle
operator|.
name|Source
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
name|testing
operator|.
name|GerritBaseTests
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
name|testing
operator|.
name|TestTimeUtil
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
name|util
operator|.
name|TimeZone
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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

begin_class
DECL|class|GroupBundleTest
specifier|public
class|class
name|GroupBundleTest
extends|extends
name|GerritBaseTests
block|{
comment|// This class just contains sanity checks that GroupBundle#compare correctly compares all parts of
comment|// the bundle. Most other test coverage should come via the slightly more realistic
comment|// GroupRebuilderTest.
DECL|field|TIMEZONE_ID
specifier|private
specifier|static
specifier|final
name|String
name|TIMEZONE_ID
init|=
literal|"US/Eastern"
decl_stmt|;
DECL|field|systemTimeZoneProperty
specifier|private
name|String
name|systemTimeZoneProperty
decl_stmt|;
DECL|field|systemTimeZone
specifier|private
name|TimeZone
name|systemTimeZone
decl_stmt|;
DECL|field|ts
specifier|private
name|Timestamp
name|ts
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|systemTimeZoneProperty
operator|=
name|System
operator|.
name|setProperty
argument_list|(
literal|"user.timezone"
argument_list|,
name|TIMEZONE_ID
argument_list|)
expr_stmt|;
name|systemTimeZone
operator|=
name|TimeZone
operator|.
name|getDefault
argument_list|()
expr_stmt|;
name|TimeZone
operator|.
name|setDefault
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
name|TIMEZONE_ID
argument_list|)
argument_list|)
expr_stmt|;
name|TestTimeUtil
operator|.
name|resetWithClockStep
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|ts
operator|=
name|TimeUtil
operator|.
name|nowTs
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|TestTimeUtil
operator|.
name|useSystemTime
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"user.timezone"
argument_list|,
name|systemTimeZoneProperty
argument_list|)
expr_stmt|;
name|TimeZone
operator|.
name|setDefault
argument_list|(
name|systemTimeZone
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|compareNonEqual ()
specifier|public
name|void
name|compareNonEqual
parameter_list|()
throws|throws
name|Exception
block|{
name|GroupBundle
name|reviewDbBundle
init|=
name|newBundle
argument_list|()
operator|.
name|source
argument_list|(
name|Source
operator|.
name|REVIEW_DB
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|AccountGroup
name|g2
init|=
operator|new
name|AccountGroup
argument_list|(
name|reviewDbBundle
operator|.
name|group
argument_list|()
argument_list|)
decl_stmt|;
name|g2
operator|.
name|setDescription
argument_list|(
literal|"Hello!"
argument_list|)
expr_stmt|;
name|GroupBundle
name|noteDbBundle
init|=
name|GroupBundle
operator|.
name|builder
argument_list|()
operator|.
name|source
argument_list|(
name|Source
operator|.
name|NOTE_DB
argument_list|)
operator|.
name|group
argument_list|(
name|g2
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|GroupBundle
operator|.
name|compare
argument_list|(
name|reviewDbBundle
argument_list|,
name|noteDbBundle
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"AccountGroups differ\n"
operator|+
operator|(
literal|"ReviewDb: AccountGroup{name=group, groupId=1, description=null,"
operator|+
literal|" visibleToAll=false, groupUUID=group-1, ownerGroupUUID=group-1,"
operator|+
literal|" createdOn=2009-09-30 17:00:00.0}\n"
operator|)
operator|+
operator|(
literal|"NoteDb  : AccountGroup{name=group, groupId=1, description=Hello!,"
operator|+
literal|" visibleToAll=false, groupUUID=group-1, ownerGroupUUID=group-1,"
operator|+
literal|" createdOn=2009-09-30 17:00:00.0}"
operator|)
argument_list|,
literal|"AccountGroupMembers differ\n"
operator|+
literal|"ReviewDb: [AccountGroupMember{key=1000,1}]\n"
operator|+
literal|"NoteDb  : []"
argument_list|,
literal|"AccountGroupMemberAudits differ\n"
operator|+
operator|(
literal|"ReviewDb: [AccountGroupMemberAudit{key=Key{groupId=1, accountId=1000,"
operator|+
literal|" addedOn=2009-09-30 17:00:00.0}, addedBy=2000, removedBy=null,"
operator|+
literal|" removedOn=null}]\n"
operator|)
operator|+
literal|"NoteDb  : []"
argument_list|,
literal|"AccountGroupByIds differ\n"
operator|+
literal|"ReviewDb: [AccountGroupById{key=1,subgroup}]\n"
operator|+
literal|"NoteDb  : []"
argument_list|,
literal|"AccountGroupByIdAudits differ\n"
operator|+
operator|(
literal|"ReviewDb: [AccountGroupByIdAud{key=Key{groupId=1, includeUUID=subgroup,"
operator|+
literal|" addedOn=2009-09-30 17:00:00.0}, addedBy=3000, removedBy=null,"
operator|+
literal|" removedOn=null}]\n"
operator|)
operator|+
literal|"NoteDb  : []"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|compareEqual ()
specifier|public
name|void
name|compareEqual
parameter_list|()
throws|throws
name|Exception
block|{
name|GroupBundle
name|reviewDbBundle
init|=
name|newBundle
argument_list|()
operator|.
name|source
argument_list|(
name|Source
operator|.
name|REVIEW_DB
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|GroupBundle
name|noteDbBundle
init|=
name|newBundle
argument_list|()
operator|.
name|source
argument_list|(
name|Source
operator|.
name|NOTE_DB
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|GroupBundle
operator|.
name|compare
argument_list|(
name|reviewDbBundle
argument_list|,
name|noteDbBundle
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
block|}
DECL|method|newBundle ()
specifier|private
name|GroupBundle
operator|.
name|Builder
name|newBundle
parameter_list|()
block|{
name|AccountGroup
name|group
init|=
operator|new
name|AccountGroup
argument_list|(
operator|new
name|AccountGroup
operator|.
name|NameKey
argument_list|(
literal|"group"
argument_list|)
argument_list|,
operator|new
name|AccountGroup
operator|.
name|Id
argument_list|(
literal|1
argument_list|)
argument_list|,
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
literal|"group-1"
argument_list|)
argument_list|,
name|ts
argument_list|)
decl_stmt|;
name|AccountGroupMember
name|member
init|=
operator|new
name|AccountGroupMember
argument_list|(
operator|new
name|AccountGroupMember
operator|.
name|Key
argument_list|(
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|1000
argument_list|)
argument_list|,
name|group
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|AccountGroupMemberAudit
name|memberAudit
init|=
operator|new
name|AccountGroupMemberAudit
argument_list|(
name|member
argument_list|,
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|2000
argument_list|)
argument_list|,
name|ts
argument_list|)
decl_stmt|;
name|AccountGroupById
name|byId
init|=
operator|new
name|AccountGroupById
argument_list|(
operator|new
name|AccountGroupById
operator|.
name|Key
argument_list|(
name|group
operator|.
name|getId
argument_list|()
argument_list|,
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
literal|"subgroup"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|AccountGroupByIdAud
name|byIdAudit
init|=
operator|new
name|AccountGroupByIdAud
argument_list|(
name|byId
argument_list|,
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|3000
argument_list|)
argument_list|,
name|ts
argument_list|)
decl_stmt|;
return|return
name|GroupBundle
operator|.
name|builder
argument_list|()
operator|.
name|group
argument_list|(
name|group
argument_list|)
operator|.
name|members
argument_list|(
name|member
argument_list|)
operator|.
name|memberAudit
argument_list|(
name|memberAudit
argument_list|)
operator|.
name|byId
argument_list|(
name|byId
argument_list|)
operator|.
name|byIdAudit
argument_list|(
name|byIdAudit
argument_list|)
return|;
block|}
block|}
end_class

end_unit

