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
DECL|package|com.google.gerrit.acceptance.rest.config
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
name|config
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
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
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
name|acceptance
operator|.
name|GerritConfig
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
name|GerritConfigs
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
name|RestResponse
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
name|AuthType
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
name|AllProjectsNameProvider
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
name|AllUsersNameProvider
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
name|AnonymousCowardNameProvider
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
name|GetServerInfo
operator|.
name|ServerInfo
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
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_class
DECL|class|ServerInfoIT
specifier|public
class|class
name|ServerInfoIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|Test
annotation|@
name|GerritConfigs
argument_list|(
block|{
comment|// auth
annotation|@
name|GerritConfig
argument_list|(
name|name
operator|=
literal|"auth.type"
argument_list|,
name|value
operator|=
literal|"HTTP"
argument_list|)
block|,
annotation|@
name|GerritConfig
argument_list|(
name|name
operator|=
literal|"auth.contributorAgreements"
argument_list|,
name|value
operator|=
literal|"true"
argument_list|)
block|,
annotation|@
name|GerritConfig
argument_list|(
name|name
operator|=
literal|"auth.loginUrl"
argument_list|,
name|value
operator|=
literal|"https://example.com/login"
argument_list|)
block|,
annotation|@
name|GerritConfig
argument_list|(
name|name
operator|=
literal|"auth.loginText"
argument_list|,
name|value
operator|=
literal|"LOGIN"
argument_list|)
block|,
annotation|@
name|GerritConfig
argument_list|(
name|name
operator|=
literal|"auth.switchAccountUrl"
argument_list|,
name|value
operator|=
literal|"https://example.com/switch"
argument_list|)
block|,
comment|// auth fields ignored when auth == HTTP
annotation|@
name|GerritConfig
argument_list|(
name|name
operator|=
literal|"auth.registerUrl"
argument_list|,
name|value
operator|=
literal|"https://example.com/register"
argument_list|)
block|,
annotation|@
name|GerritConfig
argument_list|(
name|name
operator|=
literal|"auth.registerText"
argument_list|,
name|value
operator|=
literal|"REGISTER"
argument_list|)
block|,
annotation|@
name|GerritConfig
argument_list|(
name|name
operator|=
literal|"auth.editFullNameUrl"
argument_list|,
name|value
operator|=
literal|"https://example.com/editname"
argument_list|)
block|,
annotation|@
name|GerritConfig
argument_list|(
name|name
operator|=
literal|"auth.httpPasswordUrl"
argument_list|,
name|value
operator|=
literal|"https://example.com/password"
argument_list|)
block|,
comment|// change
annotation|@
name|GerritConfig
argument_list|(
name|name
operator|=
literal|"change.allowDrafts"
argument_list|,
name|value
operator|=
literal|"false"
argument_list|)
block|,
annotation|@
name|GerritConfig
argument_list|(
name|name
operator|=
literal|"change.largeChange"
argument_list|,
name|value
operator|=
literal|"300"
argument_list|)
block|,
annotation|@
name|GerritConfig
argument_list|(
name|name
operator|=
literal|"change.replyTooltip"
argument_list|,
name|value
operator|=
literal|"Publish votes and draft comments"
argument_list|)
block|,
annotation|@
name|GerritConfig
argument_list|(
name|name
operator|=
literal|"change.replyLabel"
argument_list|,
name|value
operator|=
literal|"Vote"
argument_list|)
block|,
annotation|@
name|GerritConfig
argument_list|(
name|name
operator|=
literal|"change.updateDelay"
argument_list|,
name|value
operator|=
literal|"50s"
argument_list|)
block|,
comment|// download
annotation|@
name|GerritConfig
argument_list|(
name|name
operator|=
literal|"download.archive"
argument_list|,
name|values
operator|=
block|{
literal|"tar"
block|,
literal|"tbz2"
block|,
literal|"tgz"
block|,
literal|"txz"
block|}
argument_list|)
block|,
comment|// gerrit
annotation|@
name|GerritConfig
argument_list|(
name|name
operator|=
literal|"gerrit.allProjects"
argument_list|,
name|value
operator|=
literal|"Root"
argument_list|)
block|,
annotation|@
name|GerritConfig
argument_list|(
name|name
operator|=
literal|"gerrit.allUsers"
argument_list|,
name|value
operator|=
literal|"Users"
argument_list|)
block|,
annotation|@
name|GerritConfig
argument_list|(
name|name
operator|=
literal|"gerrit.reportBugUrl"
argument_list|,
name|value
operator|=
literal|"https://example.com/report"
argument_list|)
block|,
annotation|@
name|GerritConfig
argument_list|(
name|name
operator|=
literal|"gerrit.reportBugText"
argument_list|,
name|value
operator|=
literal|"REPORT BUG"
argument_list|)
block|,
comment|// suggest
annotation|@
name|GerritConfig
argument_list|(
name|name
operator|=
literal|"suggest.from"
argument_list|,
name|value
operator|=
literal|"3"
argument_list|)
block|,
comment|// user
annotation|@
name|GerritConfig
argument_list|(
name|name
operator|=
literal|"user.anonymousCoward"
argument_list|,
name|value
operator|=
literal|"Unnamed User"
argument_list|)
block|,
block|}
argument_list|)
DECL|method|serverConfig ()
specifier|public
name|void
name|serverConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|ServerInfo
name|i
init|=
name|getServerConfig
argument_list|()
decl_stmt|;
comment|// auth
name|assertThat
argument_list|(
name|i
operator|.
name|auth
operator|.
name|authType
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|AuthType
operator|.
name|HTTP
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|auth
operator|.
name|editableAccountFields
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|Account
operator|.
name|FieldName
operator|.
name|REGISTER_NEW_EMAIL
argument_list|,
name|Account
operator|.
name|FieldName
operator|.
name|FULL_NAME
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|auth
operator|.
name|useContributorAgreements
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|auth
operator|.
name|loginUrl
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"https://example.com/login"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|auth
operator|.
name|loginText
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"LOGIN"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|auth
operator|.
name|switchAccountUrl
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"https://example.com/switch"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|auth
operator|.
name|registerUrl
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|auth
operator|.
name|registerText
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|auth
operator|.
name|editFullNameUrl
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|auth
operator|.
name|httpPasswordUrl
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|auth
operator|.
name|isGitBasicAuth
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
comment|// change
name|assertThat
argument_list|(
name|i
operator|.
name|change
operator|.
name|allowDrafts
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|change
operator|.
name|largeChange
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|300
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|change
operator|.
name|replyTooltip
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"Publish votes and draft comments"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|change
operator|.
name|replyLabel
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"Vote\u2026"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|change
operator|.
name|updateDelay
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|50
argument_list|)
expr_stmt|;
comment|// download
name|assertThat
argument_list|(
name|i
operator|.
name|download
operator|.
name|archives
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"tar"
argument_list|,
literal|"tbz2"
argument_list|,
literal|"tgz"
argument_list|,
literal|"txz"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|download
operator|.
name|schemes
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
comment|// gerrit
name|assertThat
argument_list|(
name|i
operator|.
name|gerrit
operator|.
name|allProjects
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"Root"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|gerrit
operator|.
name|allUsers
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"Users"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|gerrit
operator|.
name|reportBugUrl
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"https://example.com/report"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|gerrit
operator|.
name|reportBugText
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"REPORT BUG"
argument_list|)
expr_stmt|;
comment|// plugin
name|assertThat
argument_list|(
name|i
operator|.
name|plugin
operator|.
name|jsResourcePaths
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
comment|// sshd
name|assertThat
argument_list|(
name|i
operator|.
name|sshd
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
comment|// suggest
name|assertThat
argument_list|(
name|i
operator|.
name|suggest
operator|.
name|from
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|3
argument_list|)
expr_stmt|;
comment|// user
name|assertThat
argument_list|(
name|i
operator|.
name|user
operator|.
name|anonymousCowardName
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"Unnamed User"
argument_list|)
expr_stmt|;
comment|// notedb
name|notesMigration
operator|.
name|setReadChanges
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|getServerConfig
argument_list|()
operator|.
name|noteDbEnabled
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|notesMigration
operator|.
name|setReadChanges
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|getServerConfig
argument_list|()
operator|.
name|noteDbEnabled
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|GerritConfig
argument_list|(
name|name
operator|=
literal|"plugins.allowRemoteAdmin"
argument_list|,
name|value
operator|=
literal|"true"
argument_list|)
DECL|method|serverConfigWithPlugin ()
specifier|public
name|void
name|serverConfigWithPlugin
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|plugins
init|=
name|tempSiteDir
operator|.
name|newFolder
argument_list|(
literal|"plugins"
argument_list|)
operator|.
name|toPath
argument_list|()
decl_stmt|;
name|Path
name|jsplugin
init|=
name|plugins
operator|.
name|resolve
argument_list|(
literal|"js-plugin-1.js"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|write
argument_list|(
name|jsplugin
argument_list|,
literal|"Gerrit.install(function(self){});\n"
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|adminSshSession
operator|.
name|exec
argument_list|(
literal|"gerrit plugin reload"
argument_list|)
expr_stmt|;
name|ServerInfo
name|i
init|=
name|getServerConfig
argument_list|()
decl_stmt|;
comment|// plugin
name|assertThat
argument_list|(
name|i
operator|.
name|plugin
operator|.
name|jsResourcePaths
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|serverConfigWithDefaults ()
specifier|public
name|void
name|serverConfigWithDefaults
parameter_list|()
throws|throws
name|Exception
block|{
name|ServerInfo
name|i
init|=
name|getServerConfig
argument_list|()
decl_stmt|;
comment|// auth
name|assertThat
argument_list|(
name|i
operator|.
name|auth
operator|.
name|authType
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|AuthType
operator|.
name|OPENID
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|auth
operator|.
name|editableAccountFields
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|Account
operator|.
name|FieldName
operator|.
name|REGISTER_NEW_EMAIL
argument_list|,
name|Account
operator|.
name|FieldName
operator|.
name|FULL_NAME
argument_list|,
name|Account
operator|.
name|FieldName
operator|.
name|USER_NAME
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|auth
operator|.
name|useContributorAgreements
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|auth
operator|.
name|loginUrl
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|auth
operator|.
name|loginText
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|auth
operator|.
name|switchAccountUrl
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|auth
operator|.
name|registerUrl
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|auth
operator|.
name|registerText
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|auth
operator|.
name|editFullNameUrl
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|auth
operator|.
name|httpPasswordUrl
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|auth
operator|.
name|isGitBasicAuth
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
comment|// change
name|assertThat
argument_list|(
name|i
operator|.
name|change
operator|.
name|allowDrafts
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|change
operator|.
name|largeChange
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|change
operator|.
name|replyTooltip
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"Reply and score"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|change
operator|.
name|replyLabel
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"Reply\u2026"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|change
operator|.
name|updateDelay
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|30
argument_list|)
expr_stmt|;
comment|// download
name|assertThat
argument_list|(
name|i
operator|.
name|download
operator|.
name|archives
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"tar"
argument_list|,
literal|"tbz2"
argument_list|,
literal|"tgz"
argument_list|,
literal|"txz"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|download
operator|.
name|schemes
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
comment|// gerrit
name|assertThat
argument_list|(
name|i
operator|.
name|gerrit
operator|.
name|allProjects
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|AllProjectsNameProvider
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|gerrit
operator|.
name|allUsers
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|AllUsersNameProvider
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|gerrit
operator|.
name|reportBugUrl
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|gerrit
operator|.
name|reportBugText
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
comment|// plugin
name|assertThat
argument_list|(
name|i
operator|.
name|plugin
operator|.
name|jsResourcePaths
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
comment|// sshd
name|assertThat
argument_list|(
name|i
operator|.
name|sshd
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
comment|// suggest
name|assertThat
argument_list|(
name|i
operator|.
name|suggest
operator|.
name|from
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// user
name|assertThat
argument_list|(
name|i
operator|.
name|user
operator|.
name|anonymousCowardName
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|AnonymousCowardNameProvider
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
DECL|method|getServerConfig ()
specifier|private
name|ServerInfo
name|getServerConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|RestResponse
name|r
init|=
name|adminRestSession
operator|.
name|get
argument_list|(
literal|"/config/server/info/"
argument_list|)
decl_stmt|;
name|r
operator|.
name|assertOK
argument_list|()
expr_stmt|;
return|return
name|newGson
argument_list|()
operator|.
name|fromJson
argument_list|(
name|r
operator|.
name|getReader
argument_list|()
argument_list|,
name|ServerInfo
operator|.
name|class
argument_list|)
return|;
block|}
block|}
end_class

end_unit

